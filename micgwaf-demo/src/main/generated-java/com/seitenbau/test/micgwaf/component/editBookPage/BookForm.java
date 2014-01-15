package com.seitenbau.test.micgwaf.component.editBookPage;


import com.seitenbau.micgwaf.component.Component;
import com.seitenbau.test.micgwaf.component.bookListPage.BookListPage;
import com.seitenbau.test.micgwaf.service.Book;
import com.seitenbau.test.micgwaf.service.BookService;

public class BookForm extends BaseBookForm
{
  /** Serial Version UID. */
  private static final long serialVersionUID = 1L;

  public Integer bookId;

  /**
  * Constructor. 
  *
  * @param parent the parent component, or null if this is a standalone component (e.g. a page)
  */
  public BookForm(Component parent)  {
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
    if (bookId == null)
    {
      bookId = BookService.instance.getNewListNumber();
    }
    // TODO validation
    Book book = getEditedBook();
    BookService.instance.save(book);
    return new BookListPage(null);
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
    return new BookListPage(null);
  }
  
  public Book getEditedBook()
  {
    // TODO simplify mapping ?
    Book book = new Book(bookId);
    book.setAuthor(authorGroup.authorInput.submittedValue);
    book.setTitle(titleGroup.titleInput.submittedValue);
    book.setPublisher(publisherGroup.publisherInput.submittedValue);
    book.setIsbn(isbnGroup.isbnInput.submittedValue);
    return book;
  }
  
  public void display(Book book)
  {
    // TODO simplify mapping ?
    authorGroup.authorInput.setValue(book.getAuthor());
    titleGroup.titleInput.setValue(book.getTitle());
    publisherGroup.publisherInput.setValue(book.getPublisher());
    isbnGroup.isbnInput.setValue(book.getIsbn());
    bookId = book.getId();
  }
}
