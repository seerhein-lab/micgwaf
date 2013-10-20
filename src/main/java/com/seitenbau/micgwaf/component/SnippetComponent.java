package com.seitenbau.micgwaf.component;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * A component representing a fixed html snippet.
 */
public class SnippetComponent extends Component
{
  /** The referenced snippet. */
  public String snippet;
  
  public SnippetComponent(Component parent)
  {
    super(null, parent);
  }

  public SnippetComponent(String id, String snippet, Component parent)
  {
    super(id, parent);
    this.snippet = snippet;
  }
  
  public void bind(Map<String, ? extends Component> allComponents)
  {
    super.bind(allComponents);
  }
  
  public List<Component> getChildren()
  {
    List<Component> result = Collections.unmodifiableList(new ArrayList<Component>());
    return result;
  }

  @Override
  public void render(Writer writer) throws IOException
  {
    if (snippet != null)
    {
      writer.write(snippet);
    }
  }
}