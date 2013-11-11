package com.seitenbau.micgwaf.parser.contenthandler;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.seitenbau.micgwaf.component.Component;
import com.seitenbau.micgwaf.component.Composition;
import com.seitenbau.micgwaf.component.SnippetListComponent;
import com.seitenbau.micgwaf.util.Constants;

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
    // check for componentRef elements
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
    if (!(child instanceof Component)) // TODO
    {
      throw new IllegalArgumentException("Children of composition elements must be define elements, found "
          + child.getClass() + " component instead");
    }
    result.definitions.put("TODO", child); // TODO
  }

  @Override
  public Composition finished() throws SAXException
  {
    result.templateId = templateId;
    return result;
  }
}