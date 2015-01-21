package de.seerheinlab.test.micgwaf.component.parts.messageBox;

import de.seerheinlab.micgwaf.component.Component;


/**
 * This class represents the HTML element with m:id successMessage.
 * Instances of this class are used whenever these elements are rendered
 * or when form date from a page containing these elements is processed.
 **/
public class SuccessMessage extends BaseSuccessMessage implements Message
{
  /** Serial Version UID. */
  private static final long serialVersionUID = 1L;


  /**
  * Constructor.
  *
  * @param id the id of this component, or null.
  * @param parent the parent component. Can be null if this is a standalone component (e.g. a page).
  */
  public SuccessMessage(String id, Component parent)
  {
    super(id, parent);
  }


}
