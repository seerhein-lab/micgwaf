package de.seerheinlab.micgwaf.component;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import de.seerheinlab.micgwaf.util.Assertions;

/**
 * A Component consisting of a list of child components.
 */
public class ChildListComponent<T extends Component> extends Component implements ChangesChildHtmlId
{
  /** serial Version UID. */
  private static final long serialVersionUID = 1L;

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

  @Override
  public List<T> getChildren()
  {
    return children;
  }

  /**
   * Adds the given child.
   *
   * @param child the child to add, not null.
   *
   * @throws NullPointerException if child is null.
   */
  public void add(T child)
  {
    Assertions.assertNotNull(child, "child");
    children.add(child);
  }

  /**
   * Clears the list of children.
   */
  public void clear()
  {
    children.clear();
  }

  @Override
  public void render(Writer writer) throws IOException
  {
    for (Component child : getChildren())
    {
      child.render(writer);
    }
  }

  @Override
  public String changeChildHtmlId(Component child, String htmlId)
  {
    int i = children.indexOf(child);
    return htmlId + ":" + i;
  }
}
