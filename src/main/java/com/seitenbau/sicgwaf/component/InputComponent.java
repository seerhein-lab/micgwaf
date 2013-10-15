package com.seitenbau.sicgwaf.component;

import javax.servlet.http.HttpServletRequest;

public class InputComponent extends HtmlElementComponent
{
  public boolean submitted;
  
  public String value;
  
  public InputComponent(Component parent)
  {
    super(parent);
  }

  public InputComponent(String elementName, String id, Component parent)
  {
    super(elementName, id, parent);
  }

  public void processRequest(HttpServletRequest request)
  {
    System.out.println(request.getParameterMap());
    String nameAttr = attributes.get("name");
    if (value != null)
    {
      value = request.getParameter(nameAttr);
      if (value != null)
      {
        submitted = true;
      }
    }
    super.processRequest(request);
  }
}
