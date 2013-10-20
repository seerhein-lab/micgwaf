package com.seitenbau.micgwaf.component;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public abstract class Component implements Serializable
{
  public String id;
  
  public Component parent;
  
  public Component(String id, Component parent)
  {
    this.id = id;
    this.parent = parent;
  }
  
  public abstract List<? extends Component> getChildren();
  
  /**
   * Binds other components to this component
   */
  public void bind(Map<String, ? extends Component> allComponents)
  {
    for (Component child : getChildren())
    {
      child.bind(allComponents);
    }
  }
  
  public abstract void render(Writer writer) throws IOException;
  
  /**
   * Processes the HTTP request and executes any actions triggered by the request.
   * 
   * @param request the request to process, not null.
   * 
   * @return the page to render after processing, or null if this component does not wish another
   *         page to be rendered.
   *         note: if more than one child component wishes to redirect to another page, 
   *         the last component wins.
   */
  public Component processRequest(HttpServletRequest request)
  {
    Component toRedirectTo = null;
    for (Component child : getChildren())
    {
      toRedirectTo = child.processRequest(request);
    }
    return toRedirectTo;
  }
}
