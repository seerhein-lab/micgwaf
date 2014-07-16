package de.seerheinlab.micgwaf.component;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import de.seerheinlab.micgwaf.util.Assertions;

/**
 * A Component consisting solely of a list of child components of the same type.
 *
 * @param <T> the type of components this list contains.
 */
public class ChildListComponent<T extends Component> extends Component implements ChangesChildHtmlId
{
  /** serial Version UID. */
  private static final long serialVersionUID = 1L;

  /** The children of this component. */
  public final List<T> children = new ArrayList<T>();

  /**
   * Constructor without an id.
   *
   * @param parent the parent component. May be null if this is a standalone component (e.g. a page).
   */
  public ChildListComponent(Component parent)
  {
    super(null, parent);
  }

  /**
   * Constructor.
   *
   * @param id the id of the component, may be null.
   *        If set, it should be unique in the current context (e.g. page).
   * @param parent the parent component. May be null if this is a standalone component (e.g. a page).
   */
  public ChildListComponent(String id, Component parent)
  {
    super(id, parent);
  }

  /**
   * Constructor with a single child..
   *
   * @param id the id of the component, may be null.
   *        If set, it should be unique in the current context (e.g. page).
   * @param parent the parent component. May be null if this is a standalone component (e.g. a page).
   * @param child a single child to be added into the component at construction time.
   */
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
   * Adds the given child at the end of the list of children.
   * It also sets the parent of the added child to this component.
   *
   * @param child the child to add, not null.
   *
   * @throws NullPointerException if child is null.
   */
  public void add(T child)
  {
    Assertions.assertNotNull(child, "child");
    children.add(child);
    child.setParent(this);
  }

  /**
   * Clears the list of children.
   * The parents of the children are set to null.
   */
  public void clear()
  {
    for (T child : children)
    {
      child.setParent(null);
    }
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

  /**
   * Changes the id of a child by adding a colon an the index of the child in the list.
   *
   * @param child the child which HTML id should be changed, not null.
   * @param htmlId the HTML id to change, not null.
   *
   * @return the changed HTML id, not null.
   */
  @Override
  public String changeChildHtmlId(Component child, String htmlId)
  {
    int i = children.indexOf(child);
    return htmlId + ":" + i;
  }
}
