package com.seitenbau.micgwaf.parser;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.StringWriter;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.junit.Ignore;
import org.junit.Test;

import com.seitenbau.micgwaf.component.Component;
import com.seitenbau.micgwaf.parser.HtmlParser;

public class ParseAndRenderTest
{
  @Test
  public void testRenderPage() throws Exception
  {
    File componentDir = new File("src/test/resources/com/seitenbau/micgwaf/page");
    Map<String, Component> components 
        = new HtmlParser().readComponents(componentDir);
    assertEquals(3, components.size());
    for (Component component : components.values())
    {
      component.resolveComponentReferences(components);
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
  public void testRenderForm() throws Exception
  {
    File componentDir = new File("src/test/resources/com/seitenbau/micgwaf/parser");
    Map<String, Component> components 
        = new HtmlParser().readComponents(componentDir);
    assertEquals(1, components.size());
    StringWriter stringWriter = new StringWriter();
    components.get("form").render(stringWriter);
    String actual = stringWriter.toString();
    actual = actual.replace("\r\n", "\n");
    String expected = FileUtils.readFileToString(new File(componentDir, "expected/formExpected.xhtml"));
    expected = expected.replace("\r\n", "\n");
    assertEquals(expected, stringWriter.toString());
  }

  @Test
  public void testRenderTemplatedPage() throws Exception
  {
    File componentDir = new File("src/test/resources/com/seitenbau/micgwaf/template");
    Map<String, Component> components 
        = new HtmlParser().readComponents(componentDir);
    assertEquals(3, components.size());
    for (Component component : components.values())
    {
      component.resolveComponentReferences(components);
    }
    StringWriter stringWriter = new StringWriter();
    components.get("templatedPage").render(stringWriter);
    String actual = stringWriter.toString();
    actual = actual.replace("\r\n", "\n");
    String expected = FileUtils.readFileToString(new File(componentDir, "expected/templatedPageExpected.xhtml"));
    expected = expected.replace("\r\n", "\n");
    assertEquals(expected, stringWriter.toString());
  }

  @Test
  @Ignore
  public void testChildAndParentReferences() throws Exception
  {
    File componentDir = new File("src/test/resources/com/seitenbau/micgwaf/page");
    Map<String, Component> components 
        = new HtmlParser().readComponents(componentDir);
    // TODO check child and parent references
  }
}
