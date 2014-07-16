package de.seerheinlab.test.micgwaf.component.table.tablePage;

import de.seerheinlab.micgwaf.component.Component;


/**
 * This class represents the HTML element with m:id tablePage.
 * Instances of this class are used whenever these elements are rendered
 * or when form date from a page containing these elements is processed.
 **/
public class TablePage extends BaseTablePage
{
  /** Serial Version UID. */
  private static final long serialVersionUID = 1L;


  /**
  * Constructor.
  *
  * @param id the id of this component, or null.
  * @param parent the parent component. Can be null if this is a standalone component (e.g. a page).
  */
  public TablePage(String id, Component parent)
  {
    super(id, parent);
    displayTable(0, 0);
    messageBox.errorMessageList.clear();
  }

  public void displayTable(int rows, int columns)
  {
    this.tableRowList.clear();
    for (int i = 0; i < rows; ++i)
    {
      TableRow row = new TableRow(null, null);
      row.tableColumnList.clear();
      for (int j = 0; j < columns; ++j)
      {
        TableColumn column = new TableColumn(null, null);
        column.span.setTextContent(i + ":" + j + " ");
        row.tableColumnList.add(column);
      }
      tableRowList.add(row);
    }
  }

}
