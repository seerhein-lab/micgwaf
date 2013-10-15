package com.seitenbau.sicgwaf.parser.contenthandler;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.seitenbau.sicgwaf.component.Component;
import com.seitenbau.sicgwaf.component.ComponentPart;
import com.seitenbau.sicgwaf.component.SnippetListComponent;
import com.seitenbau.sicgwaf.util.Constants;

public class SnippetListContentHandler extends ContentHandler
{
  public static final String COMPONENT_REF_REFID_ATTR = "refid";

  public static final String COMPONENT_REF_ELEMENT_NAME = "componentRef";
  
  public StringBuilder currentStringPart = new StringBuilder();
  
  private SnippetListComponent component = new SnippetListComponent(null);

  @Override
  public void startElement(
        String uri,
        String localName,
        String qName, 
        Attributes attributes) 
      throws SAXException 
  {
    currentStringPart.append("<").append(localName);
    if (attributes != null) {
      for (int i = 0; i < attributes.getLength(); i++) {
        String attributeName = attributes.getLocalName(i); // Attr name
        if ("".equals(attributeName)) 
        {
          attributeName = attributes.getQName(i);
        }
        String attributeValue = attributes.getValue(i);
        if (attributeName.startsWith("xmlns:") && Constants.XML_NAMESPACE.equals(attributeValue))
        {
          // do not output our own namespace
          continue;
        }
        currentStringPart.append(" ")
            .append(attributeName)
            .append("=\"")
            .append(attributeValue)
            .append("\"");
      }
    }
    currentStringPart.append(">");
  }
  
  public void endElement (String uri, String localName, String qName)
      throws SAXException
  {
    currentStringPart.append("</").append(localName).append(">");
  }
  
  public void characters (char[] ch, int start, int length)
      throws SAXException
  {
    for (int i = start; i < start + length; ++i)
    {
      currentStringPart.append(ch[i]);
    }
  }


  public void ignorableWhitespace (char[] ch, int start, int length)
      throws SAXException
  {
    for (int i = start; i < start + length; ++i)
    {
      currentStringPart.append(ch[i]);
    }
  }
  
  @Override
  public void child(Component child)
  {
    component.parts.add(ComponentPart.fromHtmlSnippet(currentStringPart.toString()));
    currentStringPart = new StringBuilder();
    component.parts.add(ComponentPart.fromComponent(child));
  }

  @Override
  public SnippetListComponent finished() throws SAXException
  {
    component.parts.add(ComponentPart.fromHtmlSnippet(currentStringPart.toString()));
    return component;
  }
}