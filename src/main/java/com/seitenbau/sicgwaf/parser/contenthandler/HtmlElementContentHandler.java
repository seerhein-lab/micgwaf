package com.seitenbau.sicgwaf.parser.contenthandler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.seitenbau.sicgwaf.component.ChildListComponent;
import com.seitenbau.sicgwaf.component.Component;
import com.seitenbau.sicgwaf.component.HtmlElementComponent;
import com.seitenbau.sicgwaf.component.InputComponent;
import com.seitenbau.sicgwaf.util.Constants;

public class HtmlElementContentHandler extends ContentHandler
{
  public static final String ID_ATTR = "id";
  
  public static final Set<String> inputElements = new HashSet<>();
  
  static
  {
    inputElements.add("input");
    inputElements.add("button");
  }
  
  public String elementName;

  public String id;
  
  public boolean multiple = false;
  
  public Map<String, String> attributeValues = new LinkedHashMap<>();
  
  public List<Component> children = new ArrayList<>();

  @Override
  public void startElement(
        String uri,
        String localName,
        String qName, 
        Attributes attributes) 
      throws SAXException 
  {
    elementName = localName;
    for (int i = 0; i < attributes.getLength(); ++i)
    {
      String attributeUri = attributes.getURI(i);
      String attributeLocalName = attributes.getLocalName(i);
      String value = attributes.getValue(i);
      if (Constants.XML_NAMESPACE.equals(attributeUri) && ID_ATTR.equals(attributeLocalName))
      {
        id = value;
      }
      else if (Constants.XML_NAMESPACE.equals(attributeUri) 
          && ContentHandlerRegistry.MULTIPLE_ATTR.equals(attributeLocalName))
      {
        multiple = true;
      }
      else if (attributeUri == null || "".equals(attributeUri))
      {
        attributeValues.put(attributeLocalName, value);
      }
    }
    if (id == null || "".equals(id.trim()))
    {
      throw new SAXException("Attribute " + Constants.XML_NAMESPACE + ":"+ HtmlElementContentHandler.ID_ATTR 
          + " is required on element " + qName);
    }
  }
  
  
  @Override
  public void child(Component child)
  {
    children.add(child);
  }

  @Override
  public Component finished() throws SAXException
  {
    HtmlElementComponent htmlElementComponent;
    if (inputElements.contains(elementName))
    {
      htmlElementComponent = new InputComponent(elementName, id, null);
    }
    else
    {
      htmlElementComponent = new HtmlElementComponent(elementName, id, null);
    }
    htmlElementComponent.attributes.putAll(attributeValues);
    htmlElementComponent.children.addAll(children);
    if (inputElements.contains(elementName) && attributeValues.get("name") == null)
    {
      htmlElementComponent.attributes.put("name", id);
    }
    if (multiple)
    {
      Component result = new ChildListComponent<HtmlElementComponent>(
          null,
          null, 
          htmlElementComponent);
      htmlElementComponent.parent = result;
      return result;
    }
    return htmlElementComponent;
  }
}