package com.seitenbau.test.micgwaf;

import java.io.PrintWriter;
import java.io.StringWriter;

import com.seitenbau.micgwaf.component.Component;
import com.seitenbau.micgwaf.config.ApplicationBase;
import com.seitenbau.test.micgwaf.component.bookListPage.BookListPage;
import com.seitenbau.test.micgwaf.component.errorPage.ErrorPage;

public class Application extends ApplicationBase
{

  public Application()
  {
    mount("/", BookListPage.class);
  }
  
  @Override
  public Component handleException(Component component, Exception exception, boolean onRender)
  {
    StringWriter writer = new StringWriter();
    // poor man's logging
    exception.printStackTrace(new PrintWriter(writer));
    System.out.println(writer.toString());
    // forward to error page
    return new ErrorPage(null);
  }

}
