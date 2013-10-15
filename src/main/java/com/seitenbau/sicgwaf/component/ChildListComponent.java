package com.seitenbau.sicgwaf.component;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * A Component consisting of a list of child componets.
 */
public class ChildListComponent extends Component
{
  public final List<Component> children = new ArrayList<Component>();
  
  public ChildListComponent()
  {
    super(null);
  }

  public ChildListComponent(String id)
  {
    super(id);
  }

  public ChildListComponent(String id, Component child)
  {
    super(id);
    children.add(child);
  }

  public List<Component> getChildren()
  {
    return children;
  }

  @Override
  public void render(Writer writer) throws IOException
  {
    for (Component child : getChildren())
    {
      child.render(writer);
    }
  }
}
