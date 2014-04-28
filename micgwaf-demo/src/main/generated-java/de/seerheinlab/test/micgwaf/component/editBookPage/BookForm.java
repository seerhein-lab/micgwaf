package de.seerheinlab.test.micgwaf.component.editBookPage;


import de.seerheinlab.micgwaf.component.Component;
import de.seerheinlab.test.micgwaf.component.bookListPage.BookListPage;
import de.seerheinlab.test.micgwaf.service.Book;
import de.seerheinlab.test.micgwaf.service.BookService;

public class BookForm extends BaseBookForm
{
  /** Serial Version UID. */
  private static final long serialVersionUID = 1L;

  public Book book;

  /**
  * Constructor. 
  *
  * @param parent the parent component, or null if this is a standalone component (e.g. a page)
  */
  public BookForm(Component parent)
  {
    super(parent);
    setBook(null);
  }

  /**
  * Constructor. 
  *
  * @param parent the parent component, or null if this is a standalone component (e.g. a page)
  * @param book the book to edit, or null for creating a new book from scratch.
  */
  public BookForm(Component parent, Book book)
  {
    super(parent);
    setBook(book);
  }

  /**
   * Sets a book to be edited in this page.
   * The book's content is copied to the appropriate input fields.
   * 
   * @param book the book to edit, or null to create a new Book from scratch.
   */
  public void setBook(Book book)
  {
    if (book == null)
    {
      // TODO should not be necessary.
      // When using a database, id is set when object is saved, not earlier.
      this.book = new Book(BookService.instance.getNewListNumber());
    }
    else
    {
      this.book = book;
      // TODO why go via groups
      // TODO simplify mapping
      authorGroup.authorInput.setValue(book.getAuthor());
      titleGroup.titleInput.setValue(book.getTitle());
      publisherGroup.publisherInput.setValue(book.getPublisher());
      isbnGroup.isbnInput.setValue(book.getIsbn());
    }
  }

  /**
   * Hook method which is called when the button okButton was pressed.
   * The input is processed and the corresponding book is saved in the business service.
   *
   * @return the page to be rendered, in this case the BookListPage.
   */
  @Override
  public Component okButtonPressed()
  {
    // TODO validate
    processInput();
    BookService.instance.save(book);
    return new BookListPage(null);
  }

  /**
   * Hook method which is called when the button cancelButton was pressed.
   * This method just redirects to the BookListPage.
   *
   * @return the page to be rendered, in this case the BookListPage.
   */
  @Override
  public Component cancelButtonPressed()
  {
    return new BookListPage(null);
  }
  
  /**
   * Sets the input values to the corresponding fields in the stored book.
   */
  public void processInput()
  {
    book.setAuthor(getAuthorInput());
    book.setTitle(getTitleInput());
    book.setPublisher(getPublisherInput());
    book.setIsbn(getIsbnInput());
  }
}
