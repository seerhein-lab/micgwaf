package de.seerheinlab.micgwaf.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.io.File;
import java.io.StringWriter;
import java.net.URLClassLoader;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import de.seerheinlab.micgwaf.component.Component;

public class ParseAndRenderTest
{
  @Test
  public void testComponentlib() throws Exception
  {
    URLClassLoader parseClassLoader = LoadComponentLibTest.createClassloaderWithLibJars();
    testParseAndRender("componentlib", "root", 3, parseClassLoader);
  }

  @Test
  public void testRenderForm() throws Exception
  {
    testParseAndRender("form", "root", 1);
  }

  @Test
  public void testLoop() throws Exception
  {
    testParseAndRender("loop", "root", 1);
  }

  @Test
  public void testReference() throws Exception
  {
    testParseAndRender("reference", "root", 3);
  }

  @Test
  public void testRemove() throws Exception
  {
    testParseAndRender("remove", "root", 1);
  }

  @Test
  public void testRenderTemplatedPage() throws Exception
  {
    testParseAndRender("template", "templatedPage", 3);
  }

  @Test
  public void testTextInComponent() throws Exception
  {
    testParseAndRender("textInComponent", "root", 1);
  }

  @Test
  public void testRenderVariables() throws Exception
  {
    testParseAndRender("variable", "body", 3);
  }

  private void testParseAndRender(
      String testSubdir,
      String rootComponentKey,
      int rootComponentsExcpectedSize)
    throws Exception
  {
    testParseAndRender(testSubdir, rootComponentKey, rootComponentsExcpectedSize, null);
  }

  private void testParseAndRender(
        String testSubdir,
        String rootComponentKey,
        int rootComponentsExcpectedSize,
        ClassLoader parseClassLoader)
      throws Exception
  {
    String testDir = "src/test/resources/de/seerheinlab/micgwaf/" + testSubdir;
    File componentDir = new File(testDir);
    Map<String, Component> components
        = new HtmlParser().readComponents(componentDir, parseClassLoader);
    assertEquals(rootComponentsExcpectedSize, components.size());
    for (Component component : components.values())
    {
      component.resolveComponentReferences(components);
    }
    StringWriter stringWriter = new StringWriter();
    components.get(rootComponentKey).render(stringWriter);
    String actual = stringWriter.toString();
    actual = actual.replace("\r\n", "\n");
    String expected = FileUtils.readFileToString(new File(componentDir, "expected.txt"));
    expected = expected.replace("\r\n", "\n");
    assertEquals(expected, stringWriter.toString());
  }

  @Test
  public void testChildAndParentReferences() throws Exception
  {
    File componentDir = new File("src/test/resources/de/seerheinlab/micgwaf/reference");
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

  private void checkParentAndChildrenReferences(Component root)
  {
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
