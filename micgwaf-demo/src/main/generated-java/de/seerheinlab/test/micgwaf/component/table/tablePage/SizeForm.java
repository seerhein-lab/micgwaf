package de.seerheinlab.test.micgwaf.component.table.tablePage;

import de.seerheinlab.micgwaf.component.Component;
import de.seerheinlab.test.micgwaf.component.parts.messageBox.ErrorMessage;



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
    int rowNumber = 0;
    TablePage page = (TablePage) parent;
    page.messageBox.errorMessageList.clear();
    try
    {
      rowNumber = Integer.parseInt(getRowsInput());
      rows.removeErrorMarker();
    }
    catch (NumberFormatException e)
    {
      rows.markError();
      page.messageBox.errorMessageList.add(new ErrorMessage("rowsNoInt", null, "rows is no integer"));
    }
    int columnNumber = 0;
    try
    {
      columnNumber = Integer.parseInt(getColumnsInput());
      columns.removeErrorMarker();
    }
    catch (NumberFormatException e)
    {
      columns.markError();
      page.messageBox.errorMessageList.add(new ErrorMessage("columnsNoInt", null, "columns is no integer"));
    }
    ((TablePage) parent).displayTable(rowNumber, columnNumber);
    return this;
  }
}
