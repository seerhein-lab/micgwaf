package com.seitenbau.test.micgwaf.component.bookListPage;


import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import com.seitenbau.micgwaf.component.Component;
import com.seitenbau.test.micgwaf.component.editBookPage.EditBookPage;
import com.seitenbau.test.micgwaf.service.Book;
import com.seitenbau.test.micgwaf.service.BookService;

public class BookListForm extends BaseBookListForm
{
  /** Serial Version UID. */
  private static final long serialVersionUID = 1L;

  /**
  * Constructor. 
  *
  * @param parent the parent component, or null if this is a standalone component (e.g. a page)
  */
  public BookListForm(Component parent)  {
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
    bookRow.displayMode();
    bookRow.hideErrorBoxes();
    return null;
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
    bookRow.editMode();
    return null;
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
    EditBookPage targetPage = new EditBookPage(null);
    // TODO same mapping for BookRow and BookEditPage
    targetPage.bookForm.display(bookRow.getDisplayedBook());
    return targetPage;
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
    bookRow.hideErrorBoxes();
    // TODO validation
    Book book = bookRow.getEditedBook();
    BookService.instance.save(book);
    bookRow.displayMode();
    updateBookList(BookService.instance.getBookList());
    return null;
  }
  
  /**
   * Processes the HTTP request and executes any actions triggered by the request.
   * 
   * @param request the request to process, not null.
   * 
   * @return the page to render after processing, or null if this component does not wish another
   *         page to be rendered.
   *         note: if more than one child component wishes to redirect to another page, 
   *         the last component wins.
   */
  @Override
  public Component processAjaxRequest(HttpServletRequest request)
  {
    String path = request.getServletPath();
    if (path.startsWith("/ajax/editInlineAjaxButton:"))
    {
      int index = Integer.parseInt(path.substring("/ajax/editInlineAjaxButton:".length()));
      return editInlineAjaxButtonPressed(bookRowList.getChildren().get(index));
    }
    return super.processAjaxRequest(request);
  }

  /**
   * Hook method which is called when the button editInlineAjaxButton was pressed.
   *
   * @param adresse The component in the list of Adresse Components
   *        to which this button belongs.
   *
   * @return the new state of a component which should be replaced by this ajax call.
   */
  public Component editInlineAjaxButtonPressed(BookRow bookRow)
  {
    bookRow.editMode();
    return bookRow;
  }
  
  public void display(List<Book> toDisplay)
  {
    bookRowList.children.clear();
    for (Book book : toDisplay)
    {
      bookRowList.children.add(new BookRow(bookRowList, book));
    }
  }

  public void updateBookList(List<Book> toDisplay)
  {
    Iterator<BookRow> bookRowIt = bookRowList.children.iterator();
    Set<Integer> displayedBookIds = new HashSet<>();
    while (bookRowIt.hasNext())
    {
      BookRow bookRow = bookRowIt.next();
      if (bookRow.bookId == null)
      {
        // new book, keep this entry regardless of entries in list
        continue;
      }
      displayedBookIds.add(bookRow.bookId);
      Book book = findBookById(bookRow.bookId, toDisplay);
      if (book == null)
      {
        // book has been removed, remove from list
        bookRowIt.remove(); // TODO remove currently not possible
        continue;
      }
      // update with new info in list if in display mode
      if (bookRow.isInDisplayMode())
      {
        bookRow.display(book); // update with new info in list
      }
    }
    // Add addresses not already displayed
    for (Book book : toDisplay)
    {
      if (!displayedBookIds.contains(book.getId()))
      {
        bookRowList.children.add(new BookRow(bookRowList, book));
      }
    }
  }

  public Book findBookById(int id, List<Book> toFindIn)
  {
    for (Book book : toFindIn)
    {
      if (book.getId() == id)
      {
        return book;
      }
    }
    return null;
  }
}
