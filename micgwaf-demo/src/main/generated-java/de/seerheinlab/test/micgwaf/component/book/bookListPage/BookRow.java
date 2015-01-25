package de.seerheinlab.test.micgwaf.component.book.bookListPage;

import de.seerheinlab.micgwaf.component.Component;
import de.seerheinlab.micgwaf.util.Assertions;
import de.seerheinlab.test.micgwaf.service.Book;

/**
 * This class represents the HTML element with m:id bookRow.
 * Instances of this class are used whenever these elements are rendered
 * or when form date from a page containing these elements is processed.
 **/
public class BookRow extends BaseBookRow
{
  /** Serial Version UID. */
  private static final long serialVersionUID = 1L;

  /** The id of the displayed book. */
  private Integer bookId;

  /** Whether the row is in display mode (false) or edit mode (true). */
  private boolean editMode = false;

  /**
  * Constructor.
  *
  * @param id the id of this component, or null.
  * @param parent the parent component. Can be null if this is a standalone component (e.g. a page).
  */
  public BookRow(String id, Component parent)
  {
    super(id, parent);
  }


  public BookRow(String id, Component parent, Book book)
  {
    super(id, parent);
    setBook(book);
  }

  /**
   * Sets the book to display.
   *
   * @param book the book to display, not null.
   */
  public void setBook(Book book)
  {
    Assertions.assertNotNull(book, "book");
    // TODO simplify mapping ?
    author.setTextContent(book.getAuthor());
    authorErrorBox.authorInput.setValue(book.getAuthor());
    title.setTextContent(book.getTitle());
    titleErrorBox.titleInput.setValue(book.getTitle());
    publisher.setTextContent(book.getPublisher());
    publisherErrorBox.publisherInput.setValue(book.getPublisher());
    isbn.setTextContent(book.getIsbn());
    isbnErrorBox.isbnInput.setValue(book.getIsbn());
    bookId = book.getId();
  }

  /**
   * Returns the displayed book.
   *
   * @return the displayed book, not null.
   */
  public Book getBook()
  {
    Book book = new Book();
    if (editMode)
    {
      book.setAuthor(authorErrorBox.authorInput.getValue());
      book.setTitle(titleErrorBox.titleInput.getValue());
      book.setPublisher(publisherErrorBox.publisherInput.getValue());
      book.setIsbn(isbnErrorBox.isbnInput.getValue());
    }
    else
    {
      book.setAuthor(author.getTextContent());
      book.setTitle(title.getTextContent());
      book.setPublisher(publisher.getTextContent());
      book.setIsbn(isbn.getTextContent());
    }
    book.setId(bookId);
    return book;
  }

  /**
   * Turns editMode on (<code>editMode = true</code>) or off (<code>editMode = false</code>).
   *
   * @param editMode true to turn editMode on, false to turn editMode off.
   */
  public void editMode(boolean editMode)
  {
    // book columns
    author.setRender(!editMode);
    authorErrorBox.setRender(editMode);
    title.setRender(!editMode);
    titleErrorBox.setRender(editMode);
    publisher.setRender(!editMode);
    publisherErrorBox.setRender(editMode);
    isbn.setRender(!editMode);
    isbnErrorBox.setRender(editMode);

    // buttons
    editExtraPageButton.setRender(!editMode);
    editInlineButton.setRender(!editMode);
    editInlineAjaxButton.setRender(!editMode);
    saveButton.setRender(editMode);
    cancelEditButton.setRender(editMode);
    this.editMode = editMode;
  }
}
