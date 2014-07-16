package de.seerheinlab.test.micgwaf;

import java.io.PrintWriter;
import java.io.StringWriter;

import de.seerheinlab.micgwaf.component.Component;
import de.seerheinlab.micgwaf.config.ApplicationBase;
import de.seerheinlab.test.micgwaf.component.book.bookListPage.BookListPage;
import de.seerheinlab.test.micgwaf.component.errorPage.ErrorPage;
import de.seerheinlab.test.micgwaf.component.table.tablePage.TablePage;

public class Application extends ApplicationBase
{

  public Application()
  {
    mount("/", BookListPage.class);
    mount("/table", TablePage.class);
  }

  @Override
  public Component handleException(Component component, Exception exception, boolean onRender)
  {
    StringWriter writer = new StringWriter();
    // poor man's logging
    exception.printStackTrace(new PrintWriter(writer));
    System.out.println(writer.toString());
    // forward to error page
    return new ErrorPage(null, null);
  }

}
