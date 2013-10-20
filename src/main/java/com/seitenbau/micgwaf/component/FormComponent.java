package com.seitenbau.micgwaf.component;

import javax.servlet.http.HttpServletRequest;

public class FormComponent extends HtmlElementComponent
{
  public boolean submitted;
  
  public FormComponent(Component parent)
  {
    super(parent);
  }

  public FormComponent(String elementName, String id, Component parent)
  {
    super(elementName, id, parent);
  }

  public void processRequest(HttpServletRequest request)
  {
    super.processRequest(request);
    checkSubmitted(this);
  }
  
  public void checkSubmitted(Component component)
  {
    if (submitted)
    {
      return;
    }
    if (component instanceof InputComponent)
    {
      submitted = ((InputComponent) component).submitted;
      if (submitted)
      {
        return;
      }
    }
    for (Component child : component.getChildren())
    {
      checkSubmitted(child);
    }
  }
}
