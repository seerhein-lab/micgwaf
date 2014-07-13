package de.seerheinlab.micgwaf.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.io.File;
import java.io.StringWriter;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import de.seerheinlab.micgwaf.component.Component;

public class ParseAndRenderTest
{
  @Test
  public void testRenderComponentRefs() throws Exception
  {
    File componentDir = new File("src/test/resources/de/seerheinlab/micgwaf/componentref");
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
    String expected = FileUtils.readFileToString(new File(componentDir, "expected.txt"));
    expected = expected.replace("\r\n", "\n");
    assertEquals(expected, stringWriter.toString());
  }

  @Test
  public void testRemove() throws Exception
  {
    File componentDir = new File("src/test/resources/de/seerheinlab/micgwaf/remove");
    Map<String, Component> components
        = new HtmlParser().readComponents(componentDir);
    assertEquals(1, components.size());
    StringWriter stringWriter = new StringWriter();
    components.get("root").render(stringWriter);
    String actual = stringWriter.toString();
    actual = actual.replace("\r\n", "\n");
    String expected = FileUtils.readFileToString(new File(componentDir, "expected.txt"));
    expected = expected.replace("\r\n", "\n");
    assertEquals(expected, stringWriter.toString());
  }

  @Test
  public void testRenderForm() throws Exception
  {
    File componentDir = new File("src/test/resources/de/seerheinlab/micgwaf/form");
    Map<String, Component> components
        = new HtmlParser().readComponents(componentDir);
    assertEquals(1, components.size());
    StringWriter stringWriter = new StringWriter();
    components.get("form").render(stringWriter);
    String actual = stringWriter.toString();
    actual = actual.replace("\r\n", "\n");
    String expected = FileUtils.readFileToString(new File(componentDir, "expected.txt"));
    expected = expected.replace("\r\n", "\n");
    assertEquals(expected, stringWriter.toString());
  }

  @Test
  public void testRenderTemplatedPage() throws Exception
  {
    File componentDir = new File("src/test/resources/de/seerheinlab/micgwaf/template");
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
    String expected = FileUtils.readFileToString(new File(componentDir, "expected.txt"));
    expected = expected.replace("\r\n", "\n");
    assertEquals(expected, stringWriter.toString());
  }

  @Test
  public void testRenderVariables() throws Exception
  {
    File componentDir = new File("src/test/resources/de/seerheinlab/micgwaf/variable");
    Map<String, Component> components
        = new HtmlParser().readComponents(componentDir);
    assertEquals(3, components.size());
    for (Component component : components.values())
    {
      component.resolveComponentReferences(components);
    }
    StringWriter stringWriter = new StringWriter();
    components.get("body").render(stringWriter);
    String actual = stringWriter.toString();
    actual = actual.replace("\r\n", "\n");
    String expected = FileUtils.readFileToString(new File(componentDir, "expected.txt"));
    expected = expected.replace("\r\n", "\n");
    assertEquals(expected, stringWriter.toString());
  }

  @Test
  public void testChildAndParentReferences() throws Exception
  {
    File componentDir = new File("src/test/resources/de/seerheinlab/micgwaf/componentref");
    Map<String, Component> components
        = new HtmlParser().readComponents(componentDir);
    assertEquals(3, components.size());
    Component root = components.get("root");
    Component form = components.get("form");
    Component content = components.get("content");
    checkParentAndChildrenReferences(root);
    checkParentAndChildrenReferences(content);
    checkParentAndChildrenReferences(form);
  }

  private void checkParentAndChildrenReferences(Component root) {
    assertNull(root.getParent());
    for (Component child : root.getChildren())
    {
      assertSame(root, child.getParent());
      for (Component grandchild : child.getChildren())
      {
        assertSame(child, grandchild.getParent());
      }
    }
  }
}
