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
import de.seerheinlab.micgwaf.component.EmptyComponent;
import de.seerheinlab.micgwaf.parser.contenthandler.ContentHandler;
import de.seerheinlab.micgwaf.parser.contenthandler.ContentHandlerFactory;
import de.seerheinlab.micgwaf.parser.contenthandler.ContentHandlerRegistry;
import de.seerheinlab.micgwaf.parser.contenthandler.PartListContentHandler;
import de.seerheinlab.micgwaf.util.Constants;

/**
 * A SAX Handler which creates the component tree from a XHTML-like input file.
 */
public class DelegatingContentHandler extends DefaultHandler
{
  public static final String REMOVE_ELEMENT_NAME = "remove";

  public List<DelegateReference> delegateList = new ArrayList<>();

  public DelegateReference currentDelegate;

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
    if (!currentDelegate.startedOnText || contentDepth != currentDelegate.startDepth)
    {
      currentDelegate.contentHandler.endElement(uri, localName, qName);
      endElementCalled = true;
    }
    while (contentDepth == currentDelegate.startDepth && !delegateList.isEmpty())
    {
      currentResult = currentDelegate.contentHandler.finished();
      for (Component child : currentResult.getChildren())
      {
        child.setParent(currentResult);
      }
      currentDelegate = delegateList.remove(delegateList.size() - 1);
      if (!(currentResult instanceof EmptyComponent))
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
    for (Component child : currentResult.getChildren())
    {
      child.setParent(currentResult);
    }
  }

  @Override
  public void characters (char[] ch, int start, int length)
      throws SAXException
  {
    if (!(currentDelegate.contentHandler instanceof PartListContentHandler))
    {
      ContentHandler contentHandler = new PartListContentHandler();
      delegateList.add(currentDelegate);
      // -1 because text is same content depth as start of previous element
      currentDelegate = new DelegateReference(contentDepth - 1, contentHandler, true);
    }
    currentDelegate.contentHandler.characters(ch, start, length);
  }

  @Override
  public void ignorableWhitespace (char ch[], int start, int length)
      throws SAXException
  {
    currentDelegate.contentHandler.ignorableWhitespace(ch, start, length);
  }

  private static class DelegateReference
  {
    public int startDepth;
    public boolean startedOnText;
    public ContentHandler contentHandler;

    public DelegateReference(int startDepth, ContentHandler contentHandler, boolean startedOnText)
    {
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
