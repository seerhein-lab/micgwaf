package de.seerheinlab.test.micgwaf.component.bookListPage;


import java.util.List;

import de.seerheinlab.micgwaf.component.Component;
import de.seerheinlab.test.micgwaf.component.editBookPage.EditBookPage;
import de.seerheinlab.test.micgwaf.service.Book;
import de.seerheinlab.test.micgwaf.service.BookService;

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
    return new EditBookPage(null);
  }

  /**
   * Hook method which is called when the button errorButton was pressed.
   * This method always throws a RuntimeException.
   *
   * @return the page to be rendered. In this implementation, no page is returned, as an exception is thrown.
   */
  @Override
  public Component errorButtonPressed()
  {
    throw new RuntimeException("error button was pressed");
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
    BookService.instance.resetBooks();
    return new BookListPage(null);
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
   * @param bookRow The component in the list of BookRow components
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
   * @param bookRow The component in the list of BookRow components
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
  
  /**
   * Displays the given list of books.
   * The bookRowList is re-filled with BookRow instances representing the passed list of books.
   * 
   * @param toDisplay the list of books to display, not null.
   */
  public void display(List<Book> toDisplay)
  {
    bookRowList.children.clear();
    for (Book book : toDisplay)
    {
      bookRowList.children.add(new BookRow(bookRowList, book));
    }
  }
}
