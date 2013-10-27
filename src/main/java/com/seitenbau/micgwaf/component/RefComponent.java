package com.seitenbau.micgwaf.component;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RefComponent extends Component
{
  /** Serial Version UID. */
  private static final long serialVersionUID = 1L;

  public String refid;
  
  public Component referencedComponent;
  
  public RefComponent(String refid, Component parent)
  {
    super(null, parent);
    this.refid = refid;
  }
  
  public List<Component> getChildren()
  {
    return new ArrayList<>();
  }

  
  @Override
  public void resolveComponentReferences(Map<String, ? extends Component> allComponents)
  {
    super.resolveComponentReferences(allComponents);
    referencedComponent = allComponents.get(refid);
    if (referencedComponent == null)
    {
      throw new IllegalStateException("unknown refid " + refid);
    }
  }

  @Override
  public void render(Writer writer) throws IOException
  {
    if (referencedComponent == null)
    {
      throw new IllegalStateException("No component bound to component reference, refid=" + refid);
    }
    referencedComponent.render(writer);
  }
}
