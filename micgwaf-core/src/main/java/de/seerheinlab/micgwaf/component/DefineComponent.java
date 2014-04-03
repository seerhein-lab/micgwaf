package de.seerheinlab.micgwaf.component;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class DefineComponent extends Component
{
  /** Serial Version UID. */
  private static final long serialVersionUID = 1L;

  public String name;
  
  public Component referencedComponent;
  
  public DefineComponent(String name, Component parent)
  {
    super(null, parent);
    this.name = name;
  }
  
  public List<Component> getChildren()
  {
    List<Component> result = new ArrayList<>();
    result.add(referencedComponent);
    return result;
  }

  @Override
  public void render(Writer writer) throws IOException
  {
    if (referencedComponent == null)
    {
      throw new IllegalStateException("No component bound to component definition, name=" + name);
    }
    referencedComponent.render(writer);
  }

  @Override
  public String toString()
  {
    return "DefineComponent [name=" + name + ", referencedComponent="
        + referencedComponent + "]";
  }
}
