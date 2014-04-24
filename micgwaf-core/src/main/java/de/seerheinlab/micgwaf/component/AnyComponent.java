package de.seerheinlab.micgwaf.component;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * Acts as a placeholder for any component which can be inserted ad the
 * specified location. Only used for parsing, e.g. for insert elements in
 * templates.
 */
public class AnyComponent extends Component
{
  /** Serial Version UID. */
  private static final long serialVersionUID = 1L;

  public String name;

  public AnyComponent(String name, Component parent)
  {
    super(name, parent);
    this.name = name;
  }

  public List<Component> getChildren()
  {
    return new ArrayList<>();
  }

  @Override
  public void render(Writer writer) throws IOException
  {
  }
}
