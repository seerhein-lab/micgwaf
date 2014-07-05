package de.seerheinlab.micgwaf.parser.contenthandler;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import de.seerheinlab.micgwaf.component.Component;
import de.seerheinlab.micgwaf.component.EmptyComponent;
import de.seerheinlab.micgwaf.util.Constants;

public class RemoveContentHandler extends ContentHandler
{
  public static final String REMOVE_ELEMENT_NAME = "remove";

  @Override
  public void startElement(
        String uri,
        String localName,
        String qName,
        Attributes attributes)
      throws SAXException
  {
    if (!Constants.XML_NAMESPACE.equals(uri) || !REMOVE_ELEMENT_NAME.equals(localName))
    {
      throw new SAXException("unknown Element " + uri + ":" + localName);
    }
  }


  @Override
  public void child(Component child) throws SAXException
  {
    // ignore all content in remove element;
  }

  @Override
  public EmptyComponent finished() throws SAXException
  {
    return new EmptyComponent(null);
  }
}