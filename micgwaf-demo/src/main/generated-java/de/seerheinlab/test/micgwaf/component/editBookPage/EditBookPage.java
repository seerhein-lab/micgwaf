package de.seerheinlab.test.micgwaf.component.editBookPage;


import de.seerheinlab.micgwaf.component.Component;
import de.seerheinlab.test.micgwaf.service.Book;

/**
 * This class represents the HTML element with m:id editBookPage.
 * Instances of this class are used whenever these elements are rendered
 * or when form date from a page containing these elements is processed.
 **/
public class EditBookPage extends BaseEditBookPage
{
  /** Serial Version UID. */
  private static final long serialVersionUID = 1L;


  /**
  * Constructor. 
  *
  * @param parent the parent component, or null if this is a standalone component (e.g. a page)
  */
  public EditBookPage(String id, Component parent)
  {
    this(id, parent, null);
  }

  /**
  * Creates a EditBookPage which edits the specified book.
  *
  * @param id the id of this component, or null.
  * @param parent the parent component, or null if this is a standalone component (e.g. a page)
  * @param book the book to edit, or null for creating a new Book.
  */
  public EditBookPage(String id, Component parent, Book book)
  {
    super(id, parent);
    bookForm.setBook(book);
  }
}
