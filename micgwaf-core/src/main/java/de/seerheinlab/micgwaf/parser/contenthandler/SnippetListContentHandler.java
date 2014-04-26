package de.seerheinlab.micgwaf.parser.contenthandler;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import de.seerheinlab.micgwaf.component.Component;
import de.seerheinlab.micgwaf.component.PartListComponent;
import de.seerheinlab.micgwaf.util.Constants;

public class SnippetListContentHandler extends ContentHandler
{
  public StringBuilder currentStringPart = new StringBuilder();
  
  private PartListComponent component = new PartListComponent(null);

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
          // do not output definition of our own namespace
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
    component.parts.add(PartListComponent.ComponentPart.fromHtmlSnippet(currentStringPart.toString()));
    currentStringPart = new StringBuilder();
    component.parts.add(PartListComponent.ComponentPart.fromComponent(child));
  }

  @Override
  public PartListComponent finished() throws SAXException
  {
    component.parts.add(PartListComponent.ComponentPart.fromHtmlSnippet(currentStringPart.toString()));
    return component;
  }
}