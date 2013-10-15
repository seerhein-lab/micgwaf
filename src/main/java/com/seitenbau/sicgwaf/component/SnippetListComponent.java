package com.seitenbau.sicgwaf.component;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A component from a HTML file.
 */
public class SnippetListComponent extends Component
{
  /** Unmodifiable list of parts. */
  public final List<ComponentPart> parts;
  
  public SnippetListComponent()
  {
    super(null);
    this.parts = new ArrayList<>();
  }

  public SnippetListComponent(List<ComponentPart> parts)
  {
    super(null);
    this.parts = parts;
  }
  
  public void bind(Map<String, ? extends Component> allComponents)
  {
    super.bind(allComponents);
    for (ComponentPart part : parts)
    {
      if (part.component != null)
      {
        part.component.bind(allComponents);
      }
    }
  }
  
  public List<Component> getChildren()
  {
    List<Component> result = new ArrayList<>();
    for (ComponentPart part : parts)
    {
      if (part.component != null)
      {
        result.add(part.component);
      }
    }
    return result;
  }

  @Override
  public void render(Writer writer) throws IOException
  {
    for (ComponentPart part : parts)
    {
      if (part.htmlSnippet != null)
      {
        writer.write(part.htmlSnippet);
      }
      else if (part.component != null)
      {
        part.component.render(writer);
      }
      else
      {
        throw new IllegalStateException(
            "either htmlSnippet or component must be set on part " + part);
      }
    }
  }
}