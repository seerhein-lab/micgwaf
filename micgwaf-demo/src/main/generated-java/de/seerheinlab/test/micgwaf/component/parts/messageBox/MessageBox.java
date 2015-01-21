package de.seerheinlab.test.micgwaf.component.parts.messageBox;


import java.io.IOException;
import java.io.Writer;

import de.seerheinlab.micgwaf.component.Component;

/**
 * This class represents the HTML element with m:id messageBox.
 * Instances of this class are used whenever these elements are rendered
 * or when form date from a page containing these elements is processed.
 **/
public class MessageBox extends BaseMessageBox
{
  /** Serial Version UID. */
  private static final long serialVersionUID = 1L;


  /**
  * Constructor.
  *
  * @param id the id of this component, or null.
  * @param parent the parent component. Can be null if this is a standalone component (e.g. a page).
  */
  public MessageBox(String id, Component parent)
  {
    super(id, parent);
  }

  /**
   * Override default rendering behavior.
   * Only render messgeBox if any messages are contained.
   */
  @Override
  public void render(Writer writer) throws IOException
  {
    if (errorMessageList != null && errorMessageList.children.size() > 0)
    {
      writer.write(SNIPPET_1);
      errorMessageList.render(writer);
      writer.write(SNIPPET_2);
    }
  }

}
