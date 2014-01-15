package com.seitenbau.test.micgwaf.component.errorPage;


import com.seitenbau.micgwaf.component.Component;

/**
 * This class represents the HTML element with m:id errorPage.
 * Instances of this class are used whenever these elements are rendered
 * or when form date from a page containing these elements is processed.
 **/
public class ErrorPage extends BaseErrorPage
{
  /** Serial Version UID. */
  private static final long serialVersionUID = 1L;

  /**
  * Constructor. 
  *
  * @param parent the parent component, or null if this is a standalone component (e.g. a page)
  */
  public ErrorPage(Component parent)  {
    super(parent);
  }
}
