package de.seerheinlab.test.micgwaf.component.bookListPage;

import de.seerheinlab.micgwaf.component.Component;
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

  public Integer bookId;

  /**
  * Constructor. 
  *
  * @param parent the parent component, or null if this is a standalone component (e.g. a page)
  */
  public BookRow(Component parent)
  {
    super(parent);
  }
  
  public BookRow(Component parent, Book book)
  {
    super(parent);
    display(book);
  }

  public void display(Book book)
  {
    // TODO simplify mapping ?
    author.setTextContent(book.getAuthor());
    title.setTextContent(book.getTitle());
    publisher.setTextContent(book.getPublisher());
    isbn.setTextContent(book.getIsbn());
    bookId = book.getId();
  }
  
  @Deprecated
  // TODO remove method
  // Implementation note: if the displayed book is of interest after displaying, it should be stored
  // and not (as here) be retrieved again
  public Book getDisplayedBook()
  {
    // TODO simplify mapping ?
    Book book = new Book(bookId);
    book.setAuthor(author.getTextContent());
    book.setTitle(title.getTextContent());
    book.setPublisher(publisher.getTextContent());
    book.setIsbn(isbn.getTextContent());
    return book;
  }

}
