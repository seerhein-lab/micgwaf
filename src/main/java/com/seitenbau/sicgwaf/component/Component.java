package com.seitenbau.sicgwaf.component;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public abstract class Component
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
  
  public void processRequest(HttpServletRequest request)
  {
    for (Component child : getChildren())
    {
      child.processRequest(request);
    }
    
  }
}
