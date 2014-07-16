package de.seerheinlab.micgwaf.generator;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import de.seerheinlab.micgwaf.Application;
import de.seerheinlab.micgwaf.config.ApplicationBase;
import de.seerheinlab.micgwaf.parser.LoadComponentLibTest;

/**
 * Tests the parse and generate part of micgwaf by parsing and generating files
 * in a test directory, compiling the generated sources, running the compiled code and checking the
 * output of the run.
 */
public class GeneratedSourcesTest
{
  @Test
  public void testComponentlib() throws Exception
  {
    URLClassLoader compileClassLoader = LoadComponentLibTest.createClassloaderWithLibJars();
    testParseAndCompile("componentlib", "root", compileClassLoader);
  }

  @Test
  public void testForm() throws Exception
  {
    testParseAndCompile("form", "root");
  }

  @Test
  public void testLoop() throws Exception
  {
    testParseAndCompile("loop", "root");
  }

  @Test
  public void testSinglePage() throws Exception
  {
    testParseAndCompile("singlepage", "singlePage");
  }

  @Test
  public void testRemove() throws Exception
  {
    testParseAndCompile("remove", "root");
  }

  @Test
  public void testReference() throws Exception
  {
    testParseAndCompile("reference", "root");
  }

  @Test
  public void testTemplate() throws Exception
  {
    testParseAndCompile("template", "templatedPage");
  }

  @Test
  public void testTextInComponent() throws Exception
  {
    testParseAndCompile("textInComponent", "root");
  }

  @Test
  public void testVariables() throws Exception
  {
    testParseAndCompile("variable", "body");
  }

  private void testParseAndCompile(String testDir, String rootComponentName)
      throws Exception
  {
    testParseAndCompile(testDir, rootComponentName, null);
  }

  private void testParseAndCompile(String testDir, String rootComponentName, ClassLoader compileClassLoader)
      throws Exception
  {
    String prefixedTestDir = "src/test/resources/de/seerheinlab/micgwaf/" + testDir;
    GeneratorAndCompiler.generateAndCompile(prefixedTestDir, compileClassLoader);

    URLClassLoader classLoader = getClassLoader();
    initMicgwaf(classLoader);
    String className = "ExtensionClassPrefix"
          + rootComponentName.substring(0,1).toUpperCase() + rootComponentName.substring(1)
          + "ExtensionClassSuffix";
    String pageContent = invokeRenderForPage(
        "de.seerheinlab.micgwaf.test.generated." + rootComponentName + "." + className,
        classLoader);
    File componentDir = new File(prefixedTestDir);
    String expected = FileUtils.readFileToString(new File(componentDir, "expected.txt"));
    expected = expected.replace("\r\n", "\n");
    assertEquals(expected, pageContent);
  }


  private URLClassLoader getClassLoader() throws MalformedURLException
  {
    URL httpServletRequestUrl = GeneratedSourcesTest.class.getClassLoader().getResource("javax/servlet/http/HttpServletRequest.class");
    // Assuming we have a file jar URL, just get the URL to the jar file
    String servletJarPath = httpServletRequestUrl.toString();
    servletJarPath = servletJarPath.substring(servletJarPath.indexOf("file:"), servletJarPath.indexOf("!"));
    URLClassLoader classLoader = URLClassLoader.newInstance(
        new URL[] {
            GeneratorAndCompiler.compileRootDir.toURI().toURL(),
            new File("target/classes").toURI().toURL() ,
            new File("target/test-classes").toURI().toURL(),
            new URL(servletJarPath),
        },
        null );
    return classLoader;
  }

  private void initMicgwaf(URLClassLoader classLoader) throws Exception
  {
    Class<?> applicationBaseClass = Class.forName(ApplicationBase.class.getName(), true, classLoader);
    Method setApplicationMethod = applicationBaseClass.getMethod("setApplication", applicationBaseClass);
    Class<?> applicationClass = Class.forName(Application.class.getName(), true, classLoader);
    Object application = applicationClass.newInstance();
    setApplicationMethod.invoke(null, application);
  }

  private String invokeRenderForPage(String pageClass, URLClassLoader classLoader)
      throws Exception
  {
    Class<?> cls = Class.forName(pageClass, true, classLoader);
    Constructor<?> rootConstructor = cls.getConstructors()[0];
    Object root = rootConstructor.newInstance(new Object[] {null, null});
    StringWriter stringWriter = new StringWriter();
    Method renderMethod = cls.getMethod("render", Writer.class);
    renderMethod.invoke(root, stringWriter);
    return stringWriter.toString();
  }
}
