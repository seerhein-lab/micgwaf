package de.seerheinlab.micgwaf.parser;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import de.seerheinlab.micgwaf.component.Component;
import de.seerheinlab.micgwaf.component.parse.RemoveComponent;
import de.seerheinlab.micgwaf.parser.contenthandler.ContentHandler;
import de.seerheinlab.micgwaf.parser.contenthandler.ContentHandlerFactory;
import de.seerheinlab.micgwaf.parser.contenthandler.ContentHandlerRegistry;
import de.seerheinlab.micgwaf.parser.contenthandler.PartListContentHandler;
import de.seerheinlab.micgwaf.util.Assertions;
import de.seerheinlab.micgwaf.util.Constants;

/**
 * A SAX Handler which creates the component tree from a XHTML-like input file.
 */
public class DelegatingContentHandler extends DefaultHandler
{
  /** The stack of handlers which handle started but not yet closed XML elements. */
  public List<DelegateReference> delegateList = new ArrayList<>();

  public DelegateReference currentDelegate;

  /**
   * How deep the current stack of open XML elements is, minus one.
   * -1 signifies "outside the document".
   * 0 means we are inside the root XML element.
   */
  public int contentDepth = -1;

  public Component currentResult;

  @Override
  public void startElement(
        String uri,
        String localName,
        String qName,
        Attributes attributes)
      throws SAXException
  {
    boolean newHandlerFound = false;
    // check whether this is an element with our namespace
    if (Constants.XML_NAMESPACE.equals(uri))
    {
      ContentHandlerFactory factory = ContentHandlerRegistry.elementHandlerMap.get(localName);
      if (factory == null)
      {
        throw new SAXException("unknown element " + localName);
      }
      ContentHandler contentHandler = factory.create(uri, localName, qName, attributes);
      if (currentDelegate != null)
      {
        delegateList.add(currentDelegate);
      }
      currentDelegate = new DelegateReference(contentDepth, contentHandler, false);
      newHandlerFound = true;
    }
    // check for attributes with our namespace
    if (attributes != null && !newHandlerFound)
    {
      for (int i = 0; i < attributes.getLength(); i++)
      {
        String attributeNamespace = attributes.getURI(i);
        if (Constants.XML_NAMESPACE.equals(attributeNamespace))
        {
          String attributeName = attributes.getLocalName(i);
          if (ContentHandlerRegistry.MULTIPLE_ATTR.equals(attributeName)
              || ContentHandlerRegistry.DEFAULT_RENDER_ATTR.equals(attributeName)
              || ContentHandlerRegistry.DEFAULT_RENDER_SELF_ATTR.equals(attributeName)
              || ContentHandlerRegistry.DEFAULT_RENDER_CHILDREN_ATTR.equals(attributeName)
              || ContentHandlerRegistry.GENRATE_EXTENSION_CLASS_ATTR.equals(attributeName))
          {
            // these attributes define the behaviour of a handler and are irrelevant for choosing a handler
            continue;
          }
          ContentHandlerFactory factory = ContentHandlerRegistry.attributeHandlerMap.get(attributeName);
          if (factory == null)
          {
            throw new SAXException("unknown attribute " + attributeName);
          }
          ContentHandler contentHandler = factory.create(uri, localName, qName, attributes);
          if (currentDelegate != null)
          {
            delegateList.add(currentDelegate);
          }
          currentDelegate = new DelegateReference(contentDepth, contentHandler, false);
          newHandlerFound = true;
        }
      }
    }
    if (!newHandlerFound
        && ((currentDelegate == null) || !(currentDelegate.contentHandler instanceof PartListContentHandler)))
    {
      ContentHandler contentHandler = new PartListContentHandler();
      if (currentDelegate != null)
      {
        delegateList.add(currentDelegate);
      }
      currentDelegate = new DelegateReference(contentDepth, contentHandler, false);
      newHandlerFound = true;
    }

    currentDelegate.contentHandler.startElement(uri, localName, qName, attributes);
    contentDepth++;
  }

  @Override
  public void endElement(String uri, String localName, String qName)
      throws SAXException
  {
    contentDepth--;
    boolean endElementCalled = false;
    // check whether to pass the end element to the active handler
    // this is the case if the element is a child element of the element which started the content handler
    // (if such an element exists)
    if (!currentDelegate.startedOnText || contentDepth != currentDelegate.startDepth)
    {
      currentDelegate.contentHandler.endElement(uri, localName, qName);
      endElementCalled = true;
    }
    // while the start depth of the active handler is reached
    // and there are still any remaining content handlers parked,
    // finish the active handler, establish parent-child relations
    // and store the result of the old active handler as child in the new active handler
    // (as long as the parsed result is not an RemoveComponent)
    //
    // Use while loop because nested PartListContentHandler can have same depth
    // as surrounding content handler
    while (contentDepth == currentDelegate.startDepth && !delegateList.isEmpty())
    {
      currentResult = currentDelegate.contentHandler.finished();
      currentDelegate = delegateList.remove(delegateList.size() - 1);
      if (!(currentResult instanceof RemoveComponent))
      {
        currentDelegate.contentHandler.child(currentResult);
      }
      if (!endElementCalled)
      {
        currentDelegate.contentHandler.endElement(uri, localName, qName);
        endElementCalled = true;
      }
    }
  }

  @Override
  public void startDocument() throws SAXException
  {
    // do nothing
  }

  @Override
  public void endDocument() throws SAXException
  {
    currentResult = currentDelegate.contentHandler.finished();
  }

  @Override
  public void characters(char[] ch, int start, int length)
      throws SAXException
  {
    if (!(currentDelegate.contentHandler instanceof PartListContentHandler))
    {
      ContentHandler contentHandler = new PartListContentHandler();
      delegateList.add(currentDelegate);
      // -1 because text is same content depth as start of previous element
      // So the PartListContentHandler is exited when the surrounded element is exited
      currentDelegate = new DelegateReference(contentDepth - 1, contentHandler, true);
    }
    currentDelegate.contentHandler.characters(ch, start, length);
  }

  @Override
  public void ignorableWhitespace(char ch[], int start, int length)
      throws SAXException
  {
    currentDelegate.contentHandler.ignorableWhitespace(ch, start, length);
  }

  /**
   * Contains a content handler plus additional information about which XML part is handled
   * by the content handler.
   */
  private static class DelegateReference
  {
    /**
     * The XML content depth just before the XML Element
     * which is handled by this reference's content handler.
     */
    public int startDepth;
    /** Whether the handler was started on raw text. */
    public boolean startedOnText;
    /** The content handler stored in this reference. */
    public ContentHandler contentHandler;

    /**
     * Constructor.
     * @param startDepth The XML content depth just before the XML Element
     *        which is handled by this reference's content handler.
     * @param contentHandler The content handler stored in this reference, not null.
     * @param startedOnText Whether the handler was started on raw text.
     *
     * @throws NullPointerException if null was passed for <code>contentHandler</code>.
     */
    public DelegateReference(int startDepth, ContentHandler contentHandler, boolean startedOnText)
    {
      Assertions.assertNotNull(contentHandler, "contentHandler");
      this.startDepth = startDepth;
      this.contentHandler = contentHandler;
      this.startedOnText = startedOnText;
    }

    @Override
    public String toString()
    {
      return "DelegateReference [startDepth=" + startDepth + ", startedOnText="
          + startedOnText + ", contentHandler=" + contentHandler + "]";
    }
  }

  @Override
  public InputSource resolveEntity(String publicId, String systemId)
      throws SAXException, IOException
  {
    if ("-//W3C//DTD XHTML 1.0 Strict//EN".equals(publicId))
    {
      return new InputSource(new StringReader(""));
    }
    return null;
  }

  @Override
  public void notationDecl(String name, String publicId, String systemId)
      throws SAXException
  {
    currentDelegate.contentHandler.notationDecl(name, publicId, systemId);
  }
}
