package de.seerheinlab.micgwaf.component;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * An empty component which does nothing.
 */
public class EmptyComponent extends Component
{
  /** Serial Version UID. */
  private static final long serialVersionUID = 1L;

  /**
   * Constructor for an empty Component without id.
   *
   * @param parent the parent component.
   */
  public EmptyComponent(Component parent)
  {
    super(null, parent);
  }

  /**
   * Constructor for an empty Component.
   *
   * @param id the id of the component.
   * @param parent the parent component.
   */
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
