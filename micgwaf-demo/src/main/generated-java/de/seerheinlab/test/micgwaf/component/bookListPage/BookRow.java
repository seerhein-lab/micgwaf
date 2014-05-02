package de.seerheinlab.test.micgwaf.component.bookListPage;

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

  /** The book to edit, not null. */
  private Book book;

  /**
  * Constructor. 
  *
  * @param id the id of this component, or null.
  * @param parent the parent component, or null if this is a standalone component (e.g. a page)
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
    title.setTextContent(book.getTitle());
    publisher.setTextContent(book.getPublisher());
    isbn.setTextContent(book.getIsbn());
    this.book = book;
  }

  /**
   * Returns the displayed book.
   * 
   * @return the displayed book, not null.
   */
  public Book getBook()
  {
    return book;
  }
}
