package de.seerheinlab.test.micgwaf.component.editBookPage;


import de.seerheinlab.micgwaf.component.Component;

public class BookForm extends BaseBookForm
{
  /** Serial Version UID. */
  private static final long serialVersionUID = 1L;


  /**
  * Constructor. 
  *
  * @param parent the parent component, or null if this is a standalone component (e.g. a page)
  */
  public BookForm(Component parent)
  {
    super(parent);
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

  /**
   * Hook method which is called when the button cancelButton was pressed.
   *
   * @return the page to be rendered.
   *         If no component or called hook method returns a not-null result, the current page
   *         in the current state will be rendered.
   *         If more than one component or called hook method returns a not-null result,
   *         the last not-null result will be used.
   */
  @Override
  public Component cancelButtonPressed()
  {
    return super.cancelButtonPressed();
  }
}
