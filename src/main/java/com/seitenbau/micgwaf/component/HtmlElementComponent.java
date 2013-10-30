package com.seitenbau.micgwaf.component;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A component for a single HTML element.
 */
public class HtmlElementComponent extends Component
{
  /** Serial Version UID. */
  private static final long serialVersionUID = 1L;

  public static final char ELEMENT_OPEN_CHAR = '<';
  
  public static final char ELEMENT_CLOSE_CHAR = '>';
  
  public static final char ELEMENT_END_CHAR = '/';

  public static final char SPACE_CHAR = ' ';

  public static final char EQUALS_CHAR = '=';

  public static final char QUOT_CHAR = '"';

  public Map<String, String> attributes = new LinkedHashMap<>();

  public String elementName;
  
  public final List<Component> children = new ArrayList<Component>();
  
  public boolean renderSelf = true;
  
  public boolean renderChildren = true;
  
  public HtmlElementComponent(Component parent)
  {
    super(null, parent);
  }

  public HtmlElementComponent(String elementName, String id, Component parent)
  {
    super(id, parent);
    this.elementName = elementName;
  }

  public List<Component> getChildren()
  {
    return children;
  }

  /**
   * Returns the attributes to be used for rendering.
   * This implementation adds the id of the component as id attribute, if not null.
   * This method may be overwritten in subclasses.
   * 
   * @return the attributes for rendering, in a map with a defined iteration order, not null.
   */
  public Map<String, String> getRenderedAttributes()
  {
    Map<String, String> renderedAttributes = new LinkedHashMap<>(attributes);
    if (renderedAttributes.get("id") == null && id != null)
    {
      renderedAttributes.put("id", id);
    }
    return renderedAttributes;
  }
  
  public void setRender(boolean render)
  {
    renderSelf = render;
    renderChildren = render;
  }
  
  @Override
  public void render(Writer writer) throws IOException
  {
    Map<String, String> renderedAttributes = getRenderedAttributes(); 
    if (renderSelf)
    {
      writer.write(ELEMENT_OPEN_CHAR);
      writer.write(elementName);
      for (Map.Entry<String, String> attributeEntry : renderedAttributes.entrySet())
      {
        writer.write(SPACE_CHAR);
        writer.write(attributeEntry.getKey());
        writer.write(EQUALS_CHAR);
        writer.write(QUOT_CHAR);
        writer.write(attributeEntry.getValue());
        writer.write(QUOT_CHAR);
      }
      writer.write(ELEMENT_CLOSE_CHAR);
    }
    if (renderChildren)
    {
      for (Component child : getChildren())
      {
        child.render(writer);
      }
    }
    if (renderSelf)
    {
      writer.write(ELEMENT_OPEN_CHAR);
      writer.write(ELEMENT_END_CHAR);
      writer.write(elementName);
      writer.write(ELEMENT_CLOSE_CHAR);
    }
  }
  
  /**
   * Replaces XML special characters by their respective entities.
   * 
   * @param toEscape the string to escape.
   * 
   * @return the escaped string, or null if null is passed in.
   */
  public String escapeHtml(String toEscape)
  {
    if (toEscape == null)
    {
      return null;
    }
    String result = toEscape.replace("<", "&lt;");
    result = result.replace(">", "&gt;");
    result = result.replace("&", "&amp;");
    result = result.replace("'", "&apo;");
    result = result.replace("\"", "&quot;");
    return result;
  }

  /**
   * Resolves entities for XML special characters.
   * 
   * @param toResolve the string to resolve entities in.
   * 
   * @return the resolved string, or null if null is passed in.
   */
  public String resolveEntities(String toResolve)
  {
    if (toResolve == null)
    {
      return null;
    }
    String result = toResolve.replace("&lt;", "<");
    result = result.replace("&gt;", ">");
    result = result.replace("&apo;", "'");
    result = result.replace("&quot;", "\"");
    result = result.replace("&amp;", "&");
    return result;
  }
}
