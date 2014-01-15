package com.seitenbau.test.micgwaf.component.bookListPage;


import com.seitenbau.micgwaf.component.Component;
import com.seitenbau.test.micgwaf.service.BookService;

/**
 * This class represents the HTML element with m:id bookListPage.
 * Instances of this class are used whenever these elements are rendered
 * or when form date from a page containing these elements is processed.
 **/
public class BookListPage extends BaseBookListPage
{
  /** Serial Version UID. */
  private static final long serialVersionUID = 1L;

  /**
  * Constructor. 
  *
  * @param parent the parent component, or null if this is a standalone component (e.g. a page)
  */
  public BookListPage(Component parent)  {
    super(parent);
    bookListForm.display(BookService.instance.getBookList());
  }
}
