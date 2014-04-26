package de.seerheinlab.micgwaf.component;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A component representing a fixed html snippet.
 */
public class SnippetComponent extends Component
{
  /** Serial Version UID. */
  private static final long serialVersionUID = 1L;

  /** The referenced snippet. */
  public String snippet;
  
  /**
   * Constructor.
   * 
   * @param parent the parent component. May be null if this is a standalone component (e.g. a page).
   */
  public SnippetComponent(Component parent)
  {
    super(null, parent);
  }

  /**
   * Constructor. 
   * 
   * @param id the id of the component, may be null. The id is not used in this component.
   * @param parent the parent component. May be null if this is a standalone component (e.g. a page).
   */
  public SnippetComponent(String id, String snippet, Component parent)
  {
    super(id, parent);
    this.snippet = snippet;
  }
  
  /**
   * Returns the list of children of this component.
   * This component has no children and therefore returns an empty unmodifiable list.
   * 
   * @return the list of children, not null.
   */
  @Override
  public List<Component> getChildren()
  {
    List<Component> result = Collections.unmodifiableList(new ArrayList<Component>());
    return result;
  }

  @Override
  public void render(Writer writer) throws IOException
  {
    if (snippet != null)
    {
      writer.write(snippet);
    }
  }
}