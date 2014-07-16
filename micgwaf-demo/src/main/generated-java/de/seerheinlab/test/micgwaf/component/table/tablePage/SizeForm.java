package de.seerheinlab.test.micgwaf.component.table.tablePage;

import de.seerheinlab.micgwaf.component.Component;



public class SizeForm extends BaseSizeForm
{
  /** Serial Version UID. */
  private static final long serialVersionUID = 1L;


  /**
  * Constructor.
  *
  * @param id the id of this component, or null.
  * @param parent the parent component. Can be null if this is a standalone component (e.g. a page).
  */
  public SizeForm(String id, Component parent)
  {
    super(id, parent);
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
    try
    {
      Integer rows = Integer.parseInt(getRowsInput());
      Integer columns = Integer.parseInt(getColumnsInput());
      ((TablePage) parent).displayTable(rows, columns);
    }
    catch (NumberFormatException e)
    {
      // TODO
    }
    return this;
  }
}
