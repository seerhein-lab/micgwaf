package de.seerheinlab.micgwaf.parser.contenthandler;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import de.seerheinlab.micgwaf.component.Component;
import de.seerheinlab.micgwaf.component.Composition;
import de.seerheinlab.micgwaf.component.DefineComponent;
import de.seerheinlab.micgwaf.component.PartListComponent;
import de.seerheinlab.micgwaf.util.Constants;

public class CompositionContentHandler extends ContentHandler
{
  public static final String TEMPLATE_ID_ATTR = "templateId";

  public static final String COMPOSITION_ELEMENT_NAME = "composition";
  
  public Composition result = new Composition(null);
  
  public String templateId;
  
  @Override
  public void startElement(
        String uri,
        String localName,
        String qName, 
        Attributes attributes) 
      throws SAXException 
  {
    // check for composition elements
    if (!Constants.XML_NAMESPACE.equals(uri) 
        || !CompositionContentHandler.COMPOSITION_ELEMENT_NAME.equals(localName)) 
    {
      throw new SAXException("unknown Element " + uri + ":" + localName);
    }
    templateId = attributes.getValue(TEMPLATE_ID_ATTR);
    if (templateId == null || "".equals(templateId.trim()))
    {
      throw new SAXException("Attribute " + TEMPLATE_ID_ATTR 
          + " is required on element " + qName);
    }
  }
  
  
  @Override
  public void child(Component child) throws SAXException
  {
    if (child instanceof PartListComponent)
    {
      PartListComponent snippetListChild = (PartListComponent) child;
      for (PartListComponent.ComponentPart part : snippetListChild.parts)
      {
        if (part.htmlSnippet != null)
        {
          if (!"".equals(part.htmlSnippet.trim()))
          {
            throw new IllegalArgumentException("Children of composition elements must not be markup, found "
                + part.htmlSnippet);
          }
          // ignore whitespace
          continue;
        }
        else
        {
          handleComponentChild(part.component);
        }
      }
      return;
    }
    handleComponentChild(child);
  }


  private void handleComponentChild(Component child)
  {
    if (!(child instanceof DefineComponent))
    {
      throw new IllegalArgumentException("Children of composition elements must be define elements, found "
          + child.getClass() + " component instead");
    }
    DefineComponent defineComponentChild = (DefineComponent) child;
    result.definitions.put(defineComponentChild.name, child);
  }

  @Override
  public Composition finished() throws SAXException
  {
    result.templateId = templateId;
    return result;
  }
}