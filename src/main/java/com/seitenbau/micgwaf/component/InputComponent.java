package com.seitenbau.micgwaf.component;

import javax.servlet.http.HttpServletRequest;

public class InputComponent extends HtmlElementComponent
{
  /** The name of the HTML input element. */
  public static final String INPUT_ELEM = "input";
  
  /** The name of the HTML button element. */
  public static final String BUTTON_ELEM = "button";
  
  /** The value of the type attribute which signifies a submit button. */
  public static final String SUBMIT_TYPE = "submit";
  
  /** The name of the type attribute. */
  public static final String TYPE_ATTR = "type";
  
  /** The name of the name attribute. */
  public static final String NAME_ATTR = "name";
  
  /** The name of the value attribute. */
  public static final String VALUE_ATTR = "value";
  
  /** Serial Version UID */
  private static final long serialVersionUID = 1L;

  /** Whether this input element received a submitted value in the current HTML request. */
  public boolean submitted;
  
  /** 
   * The value which was submitted in the current HTML request.
   * For non-button components, this value is rendered as content of the value attribute 
   * when this component is re-rendered.
   */
  public String submittedValue;
  
  /**
   * Constructor without id. 
   * 
   * @param parent the parent component, or null if this is a standalone component (e.g. a page)
   */
  public InputComponent(Component parent)
  {
    super(parent);
  }

  /**
   * Constructor. 
   * 
   * @param id the id of the component, may be null. 
   *        If set, it should be unique in the current context (e.g. page).
   * @param parent the parent component, or null if this is a standalone component (e.g. a page)
   */
  public InputComponent(String elementName, String id, Component parent)
  {
    super(elementName, id, parent);
  }

  @Override
  public Component processRequest(HttpServletRequest request)
  {
    String nameAttr = attributes.get(NAME_ATTR);
    if (nameAttr != null)
    {
      submittedValue = request.getParameter(nameAttr);
      if (submittedValue != null)
      {
        submitted = true;
        if (!isButton())
        {
          // in case this component is re-rendered
          attributes.put(VALUE_ATTR, submittedValue);
        }
      }
    }
    return super.processRequest(request);
  }
  
  @Override
  public void inLoop(int loopIndex)
  {
    super.inLoop(loopIndex);
    attributes.put(NAME_ATTR, attributes.get(NAME_ATTR) + ":" + loopIndex);
  }
  
  @Override
  public void afterRender()
  {
    super.afterRender();
    this.submitted = false;
    this.submittedValue = null;
  }

  /**
   * Checks whether this input component is a button or not.
   * 
   * @return true if this component is a button, false otherwise.
   */
  public boolean isButton()
  {
    if (BUTTON_ELEM.equals(elementName) 
        || (INPUT_ELEM.equals(elementName) 
            && SUBMIT_TYPE.equals(attributes.get(TYPE_ATTR))))
    {
      return true;
    }
    return false;
  }
}
