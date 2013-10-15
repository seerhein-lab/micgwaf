package com.seitenbau.sicgwaf.component;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HtmlElementComponent extends Component
{
  public static final char ELEMENT_OPEN_CHAR = '<';
  
  public static final char ELEMENT_CLOSE_CHAR = '>';
  
  public static final char ELEMENT_END_CHAR = '/';

  public static final char SPACE_CHAR = ' ';

  public static final char EQUALS_CHAR = '=';

  public static final char QUOT_CHAR = '"';

  public Map<String, String> attributes = new LinkedHashMap<>();

  public String elementName;
  
  public final List<Component> children = new ArrayList<Component>();
  
  public HtmlElementComponent()
  {
    super(null);
  }

  public HtmlElementComponent(String elementName, String id)
  {
    super(id);
    this.elementName = elementName;
  }

  public List<Component> getChildren()
  {
    return children;
  }

  @Override
  public void render(Writer writer) throws IOException
  {
    writer.write(ELEMENT_OPEN_CHAR);
    writer.write(elementName);
    for (Map.Entry<String, String> attributeEntry : attributes.entrySet())
    {
      writer.write(SPACE_CHAR);
      writer.write(attributeEntry.getKey());
      writer.write(EQUALS_CHAR);
      writer.write(QUOT_CHAR);
      writer.write(attributeEntry.getValue());
      writer.write(QUOT_CHAR);
    }
    writer.write(ELEMENT_CLOSE_CHAR);
    for (Component child : getChildren())
    {
      child.render(writer);
    }
    writer.write(ELEMENT_OPEN_CHAR);
    writer.write(ELEMENT_END_CHAR);
    writer.write(elementName);
    writer.write(ELEMENT_CLOSE_CHAR);
  }
}
