package com.seitenbau.micgwaf.component;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Component for compositions. Only used for parsing.
 */
public class Composition extends Component
{
  /** serialVersionUID. */
  private static final long serialVersionUID = 1L;
  
  public Map<String, Component> definitions = new LinkedHashMap<>();
  
  public String templateId;
  
  public Component template;

  public Composition(Component parent)
  {
    super(null, parent);
  }

  @Override
  public void resolveComponentReferences(Map<String, ? extends Component> allComponents)
  {
    super.resolveComponentReferences(allComponents);
    this.template = allComponents.get(templateId);
    if (template == null)
    {
      throw new IllegalStateException("unknown template component with id " + templateId);
    }
  }
  
  @Override
  public List<? extends Component> getChildren()
  {
    List<Component> children = new ArrayList<>();
    children.addAll(definitions.values());
    return children;
  }

  @Override
  public void render(Writer writer) throws IOException
  {
    // TODO Auto-generated method stub
    
  }
}
