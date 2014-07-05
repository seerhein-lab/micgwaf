package de.seerheinlab.micgwaf.parser.contenthandler;

import org.xml.sax.Attributes;

public class HtmlElementContentHandlerFactory implements ContentHandlerFactory
{
  @Override
  public ContentHandler create(String uri, String localName,
      String qName, Attributes attributes)
  {
    return new HtmlElementContentHandler();
  }
}
