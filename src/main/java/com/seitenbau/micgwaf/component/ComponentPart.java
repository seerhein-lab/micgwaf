package com.seitenbau.micgwaf.component;

public class ComponentPart
{
  public String htmlSnippet;
  
  public Component component;
  
  public static ComponentPart fromHtmlSnippet(String htmlSnippet)
  {
    ComponentPart result = new ComponentPart();
    result.htmlSnippet = htmlSnippet;
    return result;
  }

  public static ComponentPart fromComponent(Component component)
  {
    ComponentPart result = new ComponentPart();
    result.component = component;
    return result;
  }

  @Override
  public String toString()
  {
    return "ComponentPart [htmlSnippet="
        + htmlSnippet + ", component=" + component + "]";
  }
}
