package de.seerheinlab.micgwaf.component;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import de.seerheinlab.micgwaf.util.Assertions;

/**
 * Acts as a placeholder for any component which can be inserted ad the
 * specified location. Only used for parsing, e.g. for insert elements in
 * templates.
 */
public class InsertComponent extends Component
{
  /** Serial Version UID. */
  private static final long serialVersionUID = 1L;

  /** The key of the component which should be inserted instead of this component, not null. */
  public String name;

  /**
   * Constructor.
   *
   * @param The key of the component which should be inserted instead of this component, not null.
   *        If set, it should be unique in the current context (e.g. page).
   * @param parent the parent component. May be null if this is a standalone component (e.g. a page).
   */
  public InsertComponent(String name, Component parent)
  {
    super(name, parent);
    Assertions.assertNotNull(name, "name");
    this.name = name;
  }

  @Override
  public List<Component> getChildren()
  {
    return new ArrayList<>();
  }

  @Override
  public void render(Writer writer) throws IOException
  {
  }
}
