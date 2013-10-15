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
  public EmptyComponent()
  {
    super(null);
  }

  public EmptyComponent(String id)
  {
    super(id);
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
