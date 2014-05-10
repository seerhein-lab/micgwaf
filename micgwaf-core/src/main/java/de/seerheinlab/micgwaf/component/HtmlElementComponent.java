package de.seerheinlab.micgwaf.component;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

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
  
  public static final String CLASS_ATTR = "class";

  public Map<String, String> attributes = new LinkedHashMap<>();

  public String elementName;
  
  public final List<Component> children = new ArrayList<Component>();
  
  public boolean renderSelf = true;
  
  public boolean renderChildren = true;
  
  public HtmlElementComponent(Component parent)
  {
    super(null, parent);
  }

  public HtmlElementComponent(String id, Component parent)
  {
    super(id, parent);
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
    renderedAttributes.put("id", getHtmlId(renderedAttributes.get("id")));
    return renderedAttributes;
  }
  
  /**
   * Sets whether to render this component and its children.
   *
   * @param render false for not rendering the component and its visible children, 
   *       true for rendering the component and its visible children.
   */
  public void setRender(boolean render)
  {
    renderSelf = render;
    renderChildren = render;
  }
  
  /**
   * Sets the class attribute to the passed value.
   * 
   * @param styleClass the value of the class attribute, or null to remove the class attribute.
   */
  public void setClass(String styleClass)
  {
    if (styleClass == null)
    {
      attributes.remove(CLASS_ATTR);
    }
    else
    {
      attributes.put(CLASS_ATTR, styleClass);
    }
  }
  
  /**
   * Adds a CSS class to an attribute if it is not already present.
   * The class is appended to the existing class attribute value, separated by a space character.
   * 
   * @param toAdd the class to add, not null, must not contain spaces.
   * 
   * @throws NullPointerException if toAdd is null.
   * @throws IllegalArgumentException if toAdd contains spaces.
   */
  public void addClass(String toAdd)
  {
    if (toAdd == null)
    {
      throw new NullPointerException("toAdd must not be null");
    }
    if (toAdd.indexOf(" ") != -1)
    {
      throw new IllegalArgumentException("toAdd must not contain spaces, but is \"" + toAdd + "\"");
    }
    
    String oldClass = attributes.get(CLASS_ATTR);
    if (oldClass == null)
    {
      attributes.put(CLASS_ATTR, toAdd);
      return;
    }
    StringTokenizer classTokenizer = new StringTokenizer(oldClass, " ");
    while (classTokenizer.hasMoreTokens())
    {
      String token = classTokenizer.nextToken();
      if (token.equals(toAdd))
      {
        // class already present
        return;
      }
    }
    attributes.put(CLASS_ATTR, oldClass + " " + toAdd);
  }
  
  /**
   * Removes a CSS class to an attribute if it is present.
   * The class attribute value is split into tokens separated by spaces, 
   * and if any token equals <code>toRemove</code>, this token is removed.
   * 
   * @param toRemove the class to remove, not null, should not contain spaces.
   * 
   * @throws NullPointerException if toRemove is null.
   * @throws IllegalArgumentException if toRemove contains spaces.
   */
  public void removeClass(String toRemove)
  {
    if (toRemove == null)
    {
      throw new NullPointerException("toRemove must not be null");
    }
    if (toRemove.indexOf(" ") != -1)
    {
      throw new IllegalArgumentException("toRemove must not contain spaces, but is \"" + toRemove + "\"");
    }

    String oldClass = attributes.get(CLASS_ATTR);
    if (oldClass == null)
    {
      return;
    }
    StringBuilder newClass = new StringBuilder();
    StringTokenizer classTokenizer = new StringTokenizer(oldClass, " ");
    while (classTokenizer.hasMoreTokens())
    {
      String token = classTokenizer.nextToken();
      if (!token.equals(toRemove))
      {
        if (newClass.length() != 0)
        {
          newClass.append(" "); 
        }
        newClass.append(token);
      }
    }
    attributes.put(CLASS_ATTR, newClass.toString());
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
}
