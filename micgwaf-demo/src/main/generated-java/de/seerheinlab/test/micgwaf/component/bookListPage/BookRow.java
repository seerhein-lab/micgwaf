package de.seerheinlab.test.micgwaf.component.bookListPage;

import de.seerheinlab.micgwaf.component.Component;

/**
 * This class represents the HTML element with m:id bookRow.
 * Instances of this class are used whenever these elements are rendered
 * or when form date from a page containing these elements is processed.
 **/
public class BookRow extends BaseBookRow
{
  /** Serial Version UID. */
  private static final long serialVersionUID = 1L;


  /**
  * Constructor. 
  *
  * @param parent the parent component, or null if this is a standalone component (e.g. a page)
  */
  public BookRow(Component parent)
  {
    super(parent);
  }
}
