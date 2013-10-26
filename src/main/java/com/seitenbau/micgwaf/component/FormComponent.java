package com.seitenbau.micgwaf.component;

import java.util.LinkedHashMap;
import java.util.Map;

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

  @Override
  public Component processRequest(HttpServletRequest request)
  {
    Component result = super.processRequest(request);
    checkSubmitted(this);
    return result;
  }
  
  @Override
  public Map<String, String> getRenderedAttributes()
  {
    Map<String, String> renderedAttributes = super.getRenderedAttributes();
    // TODO: check whether this should be set by the PRG handler and not fixed in the component
    renderedAttributes.put("method", "POST");
    return renderedAttributes;
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
  
  @Override
  public void afterRender()
  {
    super.afterRender();
    submitted = false;
  }

}
