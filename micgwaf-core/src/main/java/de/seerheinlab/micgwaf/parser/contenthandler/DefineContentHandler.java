package de.seerheinlab.micgwaf.parser.contenthandler;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import de.seerheinlab.micgwaf.component.ChildListComponent;
import de.seerheinlab.micgwaf.component.Component;
import de.seerheinlab.micgwaf.component.DefineComponent;
import de.seerheinlab.micgwaf.util.Constants;

public class DefineContentHandler extends ContentHandler
{
  public static final String NAME_ATTR = "name";

  public static final String DEFINE_ELEMENT_NAME = "define";
  
  public String name;
  
  public Component child;

  @Override
  public void startElement(
        String uri,
        String localName,
        String qName, 
        Attributes attributes) 
      throws SAXException 
  {
    // check for define elements
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
    if (this.child == null)
    {
      this.child = child;
      return;
    }
    if (child instanceof ChildListComponent<?>)
    {
      @SuppressWarnings("unchecked")
      ChildListComponent<Component> childList = (ChildListComponent<Component>) child;
      childList.children.add(child);
      child.setParent(childList);
    }
    else
    {
      ChildListComponent<Component> childList = new ChildListComponent<Component>(null);
      childList.children.add(this.child);
      this.child.setParent(childList);
      childList.children.add(child);
      child.setParent(childList);
      this.child = childList;
   }
  }
  
  @Override
  public DefineComponent finished() throws SAXException
  {
    DefineComponent result = new DefineComponent(name, null);
    result.referencedComponent = child;
    child.setParent(result);
    return result;
  }
}