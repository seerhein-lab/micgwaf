package de.seerheinlab.test.micgwaf.component.editBookPage;

import de.seerheinlab.micgwaf.component.Component;
import de.seerheinlab.micgwaf.util.Assertions;
import de.seerheinlab.test.micgwaf.component.bookListPage.BookListPage;
import de.seerheinlab.test.micgwaf.component.messageBox.ErrorMessage;
import de.seerheinlab.test.micgwaf.service.Book;
import de.seerheinlab.test.micgwaf.service.BookService;

public class BookForm extends BaseBookForm
{
  /** Serial Version UID. */
  private static final long serialVersionUID = 1L;

  public Book book;
  
  public int messageIdCounter;

  /**
  * Constructor. 
  *
  * @param baseEditBookPage the parent component, not null.
  */
  public BookForm(String id, BaseEditBookPage baseEditBookPage)
  {
    super(id, baseEditBookPage);
    Assertions.assertNotNull(baseEditBookPage, "baseEditBookPage");
    setBook(null);
    clearErrorMessages();
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
      this.book = new Book();
    }
    else
    {
      this.book = book;
      // TODO simplify mapping
      setAuthorInput(book.getAuthor());
      setTitleInput(book.getTitle());
      setPublisherInput(book.getPublisher());
      setIsbnInput(book.getIsbn());
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
    if (!processInput())
    {
      return null;
    }
    BookService.instance.save(book);
    return new BookListPage(null, null);
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
    return new BookListPage(null, null);
  }
  
  /**
   * Validates the input and sets the input values to the corresponding fields in the stored book, 
   * if validation was successful.
   * If validation was not successful, the erroneous fields are marked and an error message is printed.
   * 
   * @return true if the input is valid and the form data was copied into the book, 
   *         or false if an validation error occurred.
   */
  public boolean processInput()
  {
    hideErrorBoxes();
    clearErrorMessages();
    boolean valid = true;
    if (getAuthorInput() == null || getAuthorInput().trim().isEmpty())
    {
      valid = false;
      author.setDivClass(author.getDivClass() + " has-error");
      addErrorMessage("Author must be filled");
    }
    if (getTitleInput() == null || getTitleInput().trim().isEmpty())
    {
      valid = false;
      title.setDivClass(author.getDivClass() + " has-error");
      addErrorMessage("Title must be filled");
    }
    if (valid)
    {
      book.setAuthor(getAuthorInput());
      book.setTitle(getTitleInput());
      book.setPublisher(getPublisherInput());
      book.setIsbn(getIsbnInput());

    }
    return valid;
  }

  /**
   * Remove the has-error class from all input field error boxes.
   */
  public void hideErrorBoxes()
  {
    author.setDivClass("form-group");
    title.setDivClass("form-group");
    publisher.setDivClass("form-group");
    isbn.setDivClass("form-group");
  }

  /**
   * Adds an error message to the surrounding page.
   * 
   * @param message the message to add, not null.
   */
  public void addErrorMessage(String message)
  {
    ((EditBookPage) parent).messageBox.errorMessageList.add(new ErrorMessage("message" + messageIdCounter, parent, message));
    messageIdCounter++;
  }
  
  /**
   * Clears the error messages in the surrounding page.
   */
  public void clearErrorMessages()
  {
    ((EditBookPage) parent).messageBox.errorMessageList.clear();
  }
}
