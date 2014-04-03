package de.seerheinlab.test.micgwaf.component.bookListPage;


import de.seerheinlab.micgwaf.component.Component;

public class BookListForm extends BaseBookListForm
{
  /** Serial Version UID. */
  private static final long serialVersionUID = 1L;


  /**
  * Constructor. 
  *
  * @param parent the parent component, or null if this is a standalone component (e.g. a page)
  */
  public BookListForm(Component parent)
  {
    super(parent);
  }

  /**
   * Hook method which is called when the button hinzufuegenButton was pressed.
   *
   * @return the page to be rendered.
   *         If no component or called hook method returns a not-null result, the current page
   *         in the current state will be rendered.
   *         If more than one component or called hook method returns a not-null result,
   *         the last not-null result will be used.
   */
  @Override
  public Component hinzufuegenButtonPressed()
  {
    return super.hinzufuegenButtonPressed();
  }

  /**
   * Hook method which is called when the button errorButton was pressed.
   *
   * @return the page to be rendered.
   *         If no component or called hook method returns a not-null result, the current page
   *         in the current state will be rendered.
   *         If more than one component or called hook method returns a not-null result,
   *         the last not-null result will be used.
   */
  @Override
  public Component errorButtonPressed()
  {
    return super.errorButtonPressed();
  }

  /**
   * Hook method which is called when the button resetButton was pressed.
   *
   * @return the page to be rendered.
   *         If no component or called hook method returns a not-null result, the current page
   *         in the current state will be rendered.
   *         If more than one component or called hook method returns a not-null result,
   *         the last not-null result will be used.
   */
  @Override
  public Component resetButtonPressed()
  {
    return super.resetButtonPressed();
  }

  /**
   * Hook method which is called when the button editExtraPageButton was pressed.
   *
   * @param bookRow The component in the list of BookRow Components
   *        to which this button belongs.
   *
   * @return the page to be rendered.
   *         If no component returns a not-null result, the current page in the current state
   *         will be rendered.
   *         If more than one component returns a not-null result, the last not-null result will be used.
   */
  @Override
  public Component editExtraPageButtonPressed(BookRow bookRow)
  {
    return super.editExtraPageButtonPressed(bookRow);
  }

  /**
   * Hook method which is called when the button saveButton was pressed.
   *
   * @param bookRow The component in the list of BookRow Components
   *        to which this button belongs.
   *
   * @return the page to be rendered.
   *         If no component returns a not-null result, the current page in the current state
   *         will be rendered.
   *         If more than one component returns a not-null result, the last not-null result will be used.
   */
  @Override
  public Component saveButtonPressed(BookRow bookRow)
  {
    return super.saveButtonPressed(bookRow);
  }

  /**
   * Hook method which is called when the button cancelEditButton was pressed.
   *
   * @param bookRow The component in the list of BookRow Components
   *        to which this button belongs.
   *
   * @return the page to be rendered.
   *         If no component returns a not-null result, the current page in the current state
   *         will be rendered.
   *         If more than one component returns a not-null result, the last not-null result will be used.
   */
  @Override
  public Component cancelEditButtonPressed(BookRow bookRow)
  {
    return super.cancelEditButtonPressed(bookRow);
  }

  /**
   * Hook method which is called when the button editInlineAjaxButton was pressed.
   *
   * @param bookRow The component in the list of BookRow Components
   *        to which this button belongs.
   *
   * @return the page to be rendered.
   *         If no component returns a not-null result, the current page in the current state
   *         will be rendered.
   *         If more than one component returns a not-null result, the last not-null result will be used.
   */
  @Override
  public Component editInlineAjaxButtonPressed(BookRow bookRow)
  {
    return super.editInlineAjaxButtonPressed(bookRow);
  }

  /**
   * Hook method which is called when the button editInlineButton was pressed.
   *
   * @param bookRow The component in the list of BookRow Components
   *        to which this button belongs.
   *
   * @return the page to be rendered.
   *         If no component returns a not-null result, the current page in the current state
   *         will be rendered.
   *         If more than one component returns a not-null result, the last not-null result will be used.
   */
  @Override
  public Component editInlineButtonPressed(BookRow bookRow)
  {
    return super.editInlineButtonPressed(bookRow);
  }
}
