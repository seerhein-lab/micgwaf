package com.seitenbau.sicgwaf.parser;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.StringWriter;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.junit.Ignore;
import org.junit.Test;

import com.seitenbau.sicgwaf.component.Component;
import com.seitenbau.sicgwaf.parser.HtmlParser;

public class ParseAndRenderTest
{
  @Test
  public void testRenderSnippets() throws Exception
  {
    File componentDir = new File("src/test/resources/com/seitenbau/sicgwaf/page");
    Map<String, Component> components 
        = new HtmlParser().readComponents(componentDir);
    assertEquals(3, components.size());
    for (Component component : components.values())
    {
      component.bind(components);
    }
    StringWriter stringWriter = new StringWriter();
    components.get("root").render(stringWriter);
    String actual = stringWriter.toString();
    actual = actual.replace("\r\n", "\n");
    String expected = FileUtils.readFileToString(new File(componentDir, "expected/expected.xhtml"));
    expected = expected.replace("\r\n", "\n");
    assertEquals(expected, stringWriter.toString());
  }
  
  @Test
  @Ignore
  public void testChildAndParentReferences() throws Exception
  {
    File componentDir = new File("src/test/resources/com/seitenbau/sicgwaf/page");
    Map<String, Component> components 
        = new HtmlParser().readComponents(componentDir);
    // TODO check child and parent references
  }
}
