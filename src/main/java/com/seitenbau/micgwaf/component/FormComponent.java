package com.seitenbau.micgwaf.component;

import javax.servlet.http.HttpServletRequest;

public class FormComponent extends HtmlElementComponent
{
  public boolean submitted;
  
  public FormComponent(Component parent)
  {
    super(parent);
    // TODO: check whether this should be set by the PRG handler and not fixed in the component
    attributes.put("method", "POST");
  }

  public FormComponent(String elementName, String id, Component parent)
  {
    super(elementName, id, parent);
  }

  @Override
  public Component processRequest(HttpServletRequest request)
  {
    Component result = super.processRequest(request);
    checkSubmitted(this);
    return result;
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
