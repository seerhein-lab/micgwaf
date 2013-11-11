package com.seitenbau.micgwaf.parser.contenthandler;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.seitenbau.micgwaf.component.Component;
import com.seitenbau.micgwaf.component.SnippetListComponent;
import com.seitenbau.micgwaf.util.Constants;

public class DefineContentHandler extends ContentHandler
{
  public static final String NAME_ATTR = "name";

  public static final String DEFINE_ELEMENT_NAME = "define";
  
  public String name;

  @Override
  public void startElement(
        String uri,
        String localName,
        String qName, 
        Attributes attributes) 
      throws SAXException 
  {
    // check for componentRef elements
    if (!Constants.XML_NAMESPACE.equals(uri) || !DefineContentHandler.DEFINE_ELEMENT_NAME.equals(localName)) 
    {
      throw new SAXException("unknown Element " + uri + ":" + localName);
    }
    name = attributes.getValue(DefineContentHandler.NAME_ATTR);
    if (name == null || "".equals(name.trim()))
    {
      throw new SAXException("Attribute " + DefineContentHandler.NAME_ATTR 
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
  public SnippetListComponent finished() throws SAXException
  {
    return new SnippetListComponent(name, null);
  }
}