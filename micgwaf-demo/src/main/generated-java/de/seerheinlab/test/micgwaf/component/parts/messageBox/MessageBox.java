package de.seerheinlab.test.micgwaf.component.parts.messageBox;


import de.seerheinlab.micgwaf.component.Component;

/**
 * This class represents the HTML element with m:id messageBox.
 * Instances of this class are used whenever these elements are rendered
 * or when form date from a page containing these elements is processed.
 **/
public class MessageBox extends BaseMessageBox
{
  /** Serial Version UID. */
  private static final long serialVersionUID = 1L;


  /**
  * Constructor.
  *
  * @param id the id of this component, or null.
  * @param parent the parent component. Can be null if this is a standalone component (e.g. a page).
  */
  public MessageBox(String id, Component parent)
  {
    super(id, parent);
    clear();
  }

  /**
   * Clears all contained messages.
   */
  public void clear()
  {
    successMessageList.clear();
    infoMessageList.clear();
    warnMessageList.clear();
    errorMessageList.clear();
  }

  public void addSuccessMessage(String messageString)
  {
    if (messageString == null)
    {
      return;
    }
    SuccessMessage message = new SuccessMessage(null, null);
    message.setTextContent(messageString);
    successMessageList.add(message);
    setRender(true);
  }

  public void addInfoMessage(String messageString)
  {
    if (messageString == null)
    {
      return;
    }
    InfoMessage message = new InfoMessage(null, null);
    message.setTextContent(messageString);
    infoMessageList.add(message);
    setRender(true);
  }

  public void addWarnMessage(String messageString)
  {
    if (messageString == null)
    {
      return;
    }
    WarnMessage message = new WarnMessage(null, null);
    message.setTextContent(messageString);
    warnMessageList.add(message);
    setRender(true);
  }

  public void addErrorMessage(String messageString)
  {
    if (messageString == null)
    {
      return;
    }
    ErrorMessage message = new ErrorMessage(null, null);
    message.setTextContent(messageString);
    errorMessageList.add(message);
    setRender(true);
  }

//  TODO use once component is an interface
//  protected <T extends Message> void addMessage(String messageString, T messageObject, ChildListComponent<T> messageList)
//  {
//    if (messageString == null)
//    {
//      return;
//    }
//    messageObject.setTextContent(messageString);
//    messageList.add(messageObject);
//    setRender(true);
//  }

}
