package de.seerheinlab.micgwaf.parser.contenthandler;

import java.util.HashMap;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import de.seerheinlab.micgwaf.component.Component;
import de.seerheinlab.micgwaf.component.RefComponent;
import de.seerheinlab.micgwaf.util.Constants;

public class ComponentRefContentHandler extends ContentHandler
{
  public static final String COMPONENT_REF_REFID_ATTR = "refid";

  public static final String COMPONENT_REF_ID_ATTR = "id";

  public static final String COMPONENT_REF_ELEMENT_NAME = "componentRef";

  public String refid;

  public String id;

  public Map<String, String> variableValues = new HashMap<>();

  @Override
  public void startElement(
        String uri,
        String localName,
        String qName,
        Attributes attributes)
      throws SAXException
  {
    if (!Constants.XML_NAMESPACE.equals(uri)
        || !ComponentRefContentHandler.COMPONENT_REF_ELEMENT_NAME.equals(localName))
    {
      throw new SAXException("unknown Element " + uri + ":" + localName);
    }
    for (int i = 0; i < attributes.getLength(); ++i)
    {
      String attributeQName = attributes.getQName(i);
      String value = attributes.getValue(i);
      if (ComponentRefContentHandler.COMPONENT_REF_REFID_ATTR.equals(attributeQName))
      {
        refid = value;
      }
      else if (ComponentRefContentHandler.COMPONENT_REF_ID_ATTR.equals(attributeQName))
      {
        id = value;
      }
      else
      {
        variableValues.put(attributeQName, value);
      }
    }
    if (refid == null || "".equals(refid.trim()))
    {
      throw new SAXException("Attribute " + ComponentRefContentHandler.COMPONENT_REF_REFID_ATTR
          + " is required on element " + qName);
    }
  }


  @Override
  public void child(Component child) throws SAXException
  {
    // ignore all content in componentRef element;
    // TODO write log message
  }

  @Override
  public RefComponent finished() throws SAXException
  {
    RefComponent result = new RefComponent(refid, id, null);
    result.variableValues = variableValues;
    return result;
  }
}