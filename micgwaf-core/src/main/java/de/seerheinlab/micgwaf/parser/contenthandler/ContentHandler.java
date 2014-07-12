package de.seerheinlab.micgwaf.parser.contenthandler;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import de.seerheinlab.micgwaf.component.Component;

public abstract class ContentHandler extends DefaultHandler
{
  /**
   * Called when another handler has parsed a child component,
   * which is inserted into the content handler's main component by this method.
   * The Handler's responsibility is to add the component to its children, if it accepts any, and to
   * set the child's parent to the main component.
   */
  public abstract void child(Component component) throws SAXException;

  /**
   * Called when the html part to be parsed by this handler has ended.
   * Returns the component parsed by this Handler.
   *
   * @return the parsed component, not null.
   *
   * @throws SAXException if an error occurs.
   */
  public abstract Component finished() throws SAXException;
}
