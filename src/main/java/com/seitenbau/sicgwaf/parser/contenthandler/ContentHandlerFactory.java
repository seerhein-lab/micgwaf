package com.seitenbau.sicgwaf.parser.contenthandler;

import org.xml.sax.Attributes;


public interface ContentHandlerFactory
{
  public ContentHandler create(
      String uri,
      String localName,
      String qName, 
      Attributes attributes);
}
