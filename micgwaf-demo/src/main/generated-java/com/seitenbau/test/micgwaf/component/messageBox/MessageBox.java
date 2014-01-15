package com.seitenbau.test.micgwaf.component.messageBox;


import java.io.IOException;
import java.io.Writer;

import com.seitenbau.micgwaf.component.Component;

/**
 * This class represents the HTML element with m:id messageBox.
 * Instances of this class are used whenever these elements are rendered
 * or when form date from a page containing these elements is processed.
 **/
public class MessageBox extends BaseMessageBox
{
  /** Serial Version UID. */
  private static final long serialVersionUID = 1L;

  public boolean render = false;

  /**
  * Constructor. 
  *
  * @param parent the parent component, or null if this is a standalone component (e.g. a page)
  */
  public MessageBox(Component parent)  {
    super(parent);
    getChildren().clear();
  }
  
  @Override
  public void afterRender()
  {
    super.afterRender();
    render = false;
    errorMessageList.children.clear();
  }
  
  public void errorMessage(String errorText)
  {
    ErrorMessage errorMessage = new ErrorMessage(errorMessageList);
    errorMessage.setTextContent(errorText);
    errorMessageList.children.add(errorMessage);
    render = true;
  }
  
  @Override
  public void render(Writer writer) throws IOException
  {
    if (render)
    {
      super.render(writer);
    }
  }
}
