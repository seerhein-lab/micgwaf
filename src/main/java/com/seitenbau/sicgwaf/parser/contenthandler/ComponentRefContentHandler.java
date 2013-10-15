package com.seitenbau.sicgwaf.parser.contenthandler;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.seitenbau.sicgwaf.component.Component;
import com.seitenbau.sicgwaf.component.RefComponent;
import com.seitenbau.sicgwaf.util.Constants;

public class ComponentRefContentHandler extends ContentHandler
{
  public static final String COMPONENT_REF_REFID_ATTR = "refid";

  public static final String COMPONENT_REF_ELEMENT_NAME = "componentRef";
  
  public String refid;

  @Override
  public void startElement(
        String uri,
        String localName,
        String qName, 
        Attributes attributes) 
      throws SAXException 
  {
    // check for componentRef elements
    if (!Constants.XML_NAMESPACE.equals(uri) || !ComponentRefContentHandler.COMPONENT_REF_ELEMENT_NAME.equals(localName)) 
    {
      throw new SAXException("unknown Element " + uri + ":" + localName);
    }
    refid = attributes.getValue(ComponentRefContentHandler.COMPONENT_REF_REFID_ATTR);
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
    return new RefComponent(refid, null);
  }
}