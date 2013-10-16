package com.seitenbau.sicgwaf.component;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * A Component consisting of a list of child componets.
 */
public class ChildListComponent<T extends Component> extends Component
{
  public final List<T> children = new ArrayList<T>();
  
  public ChildListComponent(Component parent)
  {
    super(null, parent);
  }

  public ChildListComponent(String id, Component parent)
  {
    super(id, parent);
  }

  public ChildListComponent(String id, Component parent, T child)
  {
    super(id, parent);
    children.add(child);
  }

  public List<T> getChildren()
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
