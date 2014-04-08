package de.seerheinlab.micgwaf.component;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A component representing a list of HTML snippets.
 */
public class SnippetListComponent extends Component
{
  /** Serial Version UID. */
  private static final long serialVersionUID = 1L;

  /** Unmodifiable list of parts. */
  public final List<ComponentPart> parts;
  
  public SnippetListComponent(Component parent)
  {
    super(null, parent);
    this.parts = new ArrayList<>();
  }

  public SnippetListComponent(List<ComponentPart> parts, Component parent)
  {
    super(null, parent);
    this.parts = parts;
  }
  
  public SnippetListComponent(String id, Component parent)
  {
    super(id, parent);
    this.parts = new ArrayList<>();
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
   * However, referenced component (in the parts or the parent reference) are not copied.
   * 
   * @return a copy of this object, not null.
   */
  public SnippetListComponent copy()
  {
    SnippetListComponent result = new SnippetListComponent(parent);
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
    
    public Component component;
    
    public static ComponentPart fromHtmlSnippet(String htmlSnippet)
    {
      ComponentPart result = new ComponentPart();
      result.htmlSnippet = htmlSnippet;
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
        // should not happen as we implementn Cloneaable
        throw new RuntimeException(e);
      }
    }

    public static ComponentPart fromComponent(Component component)
    {
      ComponentPart result = new ComponentPart();
      result.component = component;
      return result;
    }
    
    @Override
    public String toString()
    {
      return "ComponentPart [htmlSnippet="
          + htmlSnippet + ", component=" + component + "]";
    }
  }
}