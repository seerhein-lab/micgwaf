package com.seitenbau.sicgwaf.component;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * An empty component which does nothing.
 */
public class EmptyComponent extends Component
{
  public EmptyComponent(Component parent)
  {
    super(null, parent);
  }

  public EmptyComponent(String id, Component parent)
  {
    super(id, parent);
  }

  @Override
  public List<Component> getChildren()
  {
    return new ArrayList<Component>();
  }

  @Override
  public void render(Writer writer) throws IOException
  {
  }
}
