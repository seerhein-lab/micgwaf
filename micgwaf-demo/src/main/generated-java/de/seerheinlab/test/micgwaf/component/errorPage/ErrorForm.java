package de.seerheinlab.test.micgwaf.component.errorPage;


import de.seerheinlab.micgwaf.component.Component;

public class ErrorForm extends BaseErrorForm
{
  /** Serial Version UID. */
  private static final long serialVersionUID = 1L;


  /**
  * Constructor. 
  *
  * @param id the id of this component, or null.
  * @param parent the parent component. Can be null if this is a standalone component (e.g. a page).
  */
  public ErrorForm(String id, Component parent)
  {
    super(id, parent);
  }


  /**
   * Hook method which is called when the button okButton was pressed.
   *
   * @return the page to be rendered.
   *         If no component or called hook method returns a not-null result, the current page
   *         in the current state will be rendered.
   *         If more than one component or called hook method returns a not-null result,
   *         the last not-null result will be used.
   */
  @Override
  public Component okButtonPressed()
  {
    return super.okButtonPressed();
  }
}
