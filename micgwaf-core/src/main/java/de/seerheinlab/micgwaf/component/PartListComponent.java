package de.seerheinlab.micgwaf.component;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.seerheinlab.micgwaf.util.Assertions;

/**
 * A component representing a list of HTML snippets or other components (called parts).
 * This component is mainly used during parsing HTML documents.
 */
public class PartListComponent extends Component
{
  /** Serial Version UID. */
  private static final long serialVersionUID = 1L;

  /** The list of parts. */
  public final List<ComponentPart> parts;
  
  /**
   * Constructs a SnippetListComponent containing no parts.
   * 
   * @param parent the parent component. May be null if this is a standalone component (e.g. a page).
   */
  public PartListComponent(Component parent)
  {
    super(null, parent);
    this.parts = new ArrayList<>();
  }

  /**
   * Constructs a SnippetListComponent with an id, containing no parts.
   * 
   * @param id the id of the component, may be null. The id is not used in this component.
   * @param parent the parent component. May be null if this is a standalone component (e.g. a page).
   */
  public PartListComponent(String id, Component parent)
  {
    super(id, parent);
    this.parts = new ArrayList<>();
  }

  /**
   * Constructs a SnippetListComponent with the given initial parts.
   * 
   * @param id the id of the component, may be null. The id is not used in this component.
   * @param parts the initial parts of this component, not null. This list is copied, but it contents
   *        are not cloned in any way.
   * @param parent the parent component. May be null if this is a standalone component (e.g. a page).
   */
  public PartListComponent(String id, List<ComponentPart> parts, Component parent)
  {
    super(id, parent);
    Assertions.assertNotNull(parts, "parts");
    this.parts = new ArrayList<>(parts);
  }
  
  public void resolveComponentReferences(Map<String, ? extends Component> allComponents)
  {
    super.resolveComponentReferences(allComponents);
    for (ComponentPart part : parts)
    {
      if (part.component != null)
      {
        part.component.resolveComponentReferences(allComponents);
      }
    }
  }
  
  public List<Component> getChildren()
  {
    List<Component> result = new ArrayList<>();
    for (ComponentPart part : parts)
    {
      if (part.component != null)
      {
        result.add(part.component);
      }
    }
    return result;
  }

  @Override
  public void render(Writer writer) throws IOException
  {
    for (ComponentPart part : parts)
    {
      if (part.htmlSnippet != null)
      {
        writer.write(part.htmlSnippet);
      }
      else if (part.component != null)
      {
        part.component.render(writer);
      }
      else if (part.variableName != null)
      {
        writer.write(part.variableDefaultValue);
      }
      else
      {
        throw new IllegalStateException(
            "either htmlSnippet or component must be set on part " + part);
      }
    }
  }
  
  /**
   * Returns a shallow copy of this object.
   * The part list is copied and thus may be changed in the copy without affecting the copy origin.
   * However, the referenced components (the parts and the parent reference) are not copied.
   * 
   * @return a shallow copy of this object, not null.
   */
  public PartListComponent copy()
  {
    PartListComponent result = new PartListComponent(parent);
    for (ComponentPart part : parts)
    {
      result.parts.add(part.clone());
    }
    return result;
  }
  
  /**
   * A part of the SnippetList component.
   * Contains either a HTML Snippet or a child component.
   */
  public static class ComponentPart implements Cloneable
  {
    public String htmlSnippet;
    
    public String variableName;
    
    public String variableDefaultValue;
    
    public Component component;
    
    public static ComponentPart fromHtmlSnippet(String htmlSnippet)
    {
      ComponentPart result = new ComponentPart();
      result.htmlSnippet = htmlSnippet;
      return result;
    }
    
    /**
     * Creates a component part from a variable.
     * 
     * @param variableName the name of the variable to create the part from, not null.
     * @param variableDefaultValue the default value of the variable, not null.
     * 
     * @return the created ComponentPart, not null.
     */
    public static ComponentPart fromVariable(String variableName, String variableDefaultValue)
    {
      ComponentPart result = new ComponentPart();
      result.variableName = variableName;
      result.variableDefaultValue = variableDefaultValue;
      return result;
    }

    /**
     * Creates a component part from a component.
     * 
     * @param component the component to create the part from, not null.
     * 
     * @return the created ComponentPart, not null.
     */
    public static ComponentPart fromComponent(Component component)
    {
      ComponentPart result = new ComponentPart();
      result.component = component;
      return result;
    }

    public ComponentPart clone()
    {
      try
      {
        return (ComponentPart) super.clone();
      }
      catch (CloneNotSupportedException e)
      {
        // should not happen as we implement Cloneable
        throw new RuntimeException(e);
      }
    }

    @Override
    public String toString()
    {
      return "ComponentPart [htmlSnippet=" + htmlSnippet + ", variable="
          + variableName + ", component=" + component + "]";
    }
  }
}