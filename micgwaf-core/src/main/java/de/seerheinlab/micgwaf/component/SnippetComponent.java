package de.seerheinlab.micgwaf.component;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A component representing a text snippet which is simply rendered to the output.
 */
public class SnippetComponent extends Component
{
  /** Serial Version UID. */
  private static final long serialVersionUID = 1L;

  /** The text which is rendered to the output. */
  public String text;
  
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
   * @param text the text to be written to the output, or null to not write any text to the output.
   * @param parent the parent component. May be null if this is a standalone component (e.g. a page).
   * 
   * @throws NullPointerException if text is null.
   */
  public SnippetComponent(String id, String text, Component parent)
  {
    super(id, parent);
    this.text = text;
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
    if (text != null)
    {
      writer.write(text);
    }
  }
}