package com.seitenbau.micgwaf.parser.contenthandler; 

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.seitenbau.micgwaf.component.ChildListComponent;
import com.seitenbau.micgwaf.component.Component;
import com.seitenbau.micgwaf.component.FormComponent;
import com.seitenbau.micgwaf.component.GenerationParameters;
import com.seitenbau.micgwaf.component.HtmlElementComponent;
import com.seitenbau.micgwaf.component.InputComponent;
import com.seitenbau.micgwaf.util.Constants;

public class HtmlElementContentHandler extends ContentHandler
{
  public static final String ID_ATTR = "id";
  
  public String elementName;

  public String id;
  
  public boolean multiple = false;
  
  public Map<String, String> attributeValues = new LinkedHashMap<>();
  
  public List<Component> children = new ArrayList<>();
  
  public Boolean render;
  
  public Boolean renderSelf;
  
  public Boolean renderChildren;
  
  public GenerationParameters generationParameters = new GenerationParameters();

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
      String attributeName = attributes.getLocalName(i);
      if ("".equals(attributeName)) 
      {
        attributeName = attributes.getQName(i);
      }
      String value = attributes.getValue(i);
      if (attributeName.startsWith("xmlns:") && Constants.XML_NAMESPACE.equals(value))
      {
        // do not output definition of our own namespace
        continue;
      }
      if (Constants.XML_NAMESPACE.equals(attributeUri) && ID_ATTR.equals(attributeName))
      {
        id = value;
      }
      else if (Constants.XML_NAMESPACE.equals(attributeUri) 
          && ContentHandlerRegistry.MULTIPLE_ATTR.equals(attributeName))
      {
        multiple = true;
      }
      else if (Constants.XML_NAMESPACE.equals(attributeUri) 
          && ContentHandlerRegistry.GENRATE_EXTENSION_CLASS_ATTR.equals(attributeName))
     {
        generationParameters.generateExtensionClass = Boolean.parseBoolean(value);
      }
      else if (Constants.XML_NAMESPACE.equals(attributeUri) 
          && ContentHandlerRegistry.DEFAULT_RENDER_ATTR.equals(attributeName))
      {
        render = Boolean.parseBoolean(value);
      }
      else if (Constants.XML_NAMESPACE.equals(attributeUri) 
          && ContentHandlerRegistry.DEFAULT_RENDER_SELF_ATTR.equals(attributeName))
      {
        renderSelf = Boolean.parseBoolean(value);
      }
      else if (Constants.XML_NAMESPACE.equals(attributeUri) 
          && ContentHandlerRegistry.DEFAULT_RENDER_CHILDREN_ATTR.equals(attributeName))
      {
        renderChildren = Boolean.parseBoolean(value);
      }
      else if (attributeUri == null || "".equals(attributeUri))
      {
        attributeValues.put(attributeName, value);
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
    if (InputComponent.INPUT_ELEM.equals(elementName)
        || InputComponent.BUTTON_ELEM.equals(elementName))
    {
      htmlElementComponent = new InputComponent(elementName, id, null);
      // use id as name attribute if not set.
      if (attributeValues.get(InputComponent.NAME_ATTR) == null)
      {
        htmlElementComponent.attributes.put(InputComponent.NAME_ATTR, id);
      }
    }
    else if (FormComponent.FORM_ELEM.equals(elementName))
    {
      htmlElementComponent = new FormComponent(id, null);
      if (generationParameters.generateExtensionClass == null)
      {
        // per default generate extension class for forms
        generationParameters.generateExtensionClass = true;
      }
    }
    else
    {
      htmlElementComponent = new HtmlElementComponent(elementName, id, null);
    }
    htmlElementComponent.setGenerationParameters(generationParameters);
    htmlElementComponent.attributes.putAll(attributeValues);
    htmlElementComponent.children.addAll(children);
    for (Component child : htmlElementComponent.children)
    {
      child.setParent(htmlElementComponent);
    }
    if (render != null)
    {
      htmlElementComponent.setRender(render);
    }
    if (renderSelf != null)
    {
      htmlElementComponent.renderSelf = renderSelf;
    }
    if (renderChildren != null)
    {
      htmlElementComponent.renderChildren = renderChildren;
    }
    if (multiple)
    {
      Component result = new ChildListComponent<HtmlElementComponent>(
          id + "List",
          null, 
          htmlElementComponent);
      htmlElementComponent.setParent(result);
      return result;
    }
    return htmlElementComponent;
  }
}