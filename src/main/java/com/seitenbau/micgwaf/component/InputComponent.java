package com.seitenbau.micgwaf.component;

import javax.servlet.http.HttpServletRequest;

public class InputComponent extends HtmlElementComponent
{
  /** Serial Version UID */
  private static final long serialVersionUID = 1L;

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

  @Override
  public Component processRequest(HttpServletRequest request)
  {
    String nameAttr = attributes.get("name");
    if (nameAttr != null)
    {
      value = request.getParameter(nameAttr);
      if (value != null)
      {
        submitted = true;
        if (!isButton())
        {
          // in case this component is re-rendered
          attributes.put("value", value);
        }
      }
    }
    return super.processRequest(request);
  }
  
  @Override
  public void inLoop(int loopIndex)
  {
    super.inLoop(loopIndex);
    attributes.put("name", attributes.get("name") + ":" + loopIndex);
  }
  
  public boolean isButton()
  {
    if ("button".equals(elementName) 
        || ("input".equals(elementName) 
            && "submit".equals(attributes.get("type"))))
    {
      return true;
    }
    return false;
  }
}
