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

public class GeneratedSourcesTest
{
  @Test
  public void testRenderSinglePage() throws Exception
  {
    GeneratorAndCompiler.generateAndCompile("src/test/resources/de/seerheinlab/micgwaf/singlepage", null);

    URLClassLoader classLoader = getClassLoader();
    initMicgwaf(classLoader);
    String pageContent = invokeRenderForPage(
        "de.seerheinlab.micgwaf.test.generated.singlePage.ExtensionClassPrefixSinglePageExtensionClassSuffix",
        classLoader);
    File componentDir = new File("src/test/resources/de/seerheinlab/micgwaf/singlepage");
    String expected = FileUtils.readFileToString(new File(componentDir, "expected.txt"));
    expected = expected.replace("\r\n", "\n");
    assertEquals(expected, pageContent);
  }

  @Test
  public void testRenderComponentRefs() throws Exception
  {
    GeneratorAndCompiler.generateAndCompile("src/test/resources/de/seerheinlab/micgwaf/componentref", null);

    URLClassLoader classLoader = getClassLoader();
    initMicgwaf(classLoader);
    String pageContent = invokeRenderForPage(
        "de.seerheinlab.micgwaf.test.generated.root.ExtensionClassPrefixRootExtensionClassSuffix",
        classLoader);
    File componentDir = new File("src/test/resources/de/seerheinlab/micgwaf/componentref");
    String expected = FileUtils.readFileToString(new File(componentDir, "expected.txt"));
    expected = expected.replace("\r\n", "\n");
    assertEquals(expected, pageContent);
  }

  @Test
  public void testRenderForm() throws Exception
  {
    GeneratorAndCompiler.generateAndCompile("src/test/resources/de/seerheinlab/micgwaf/form", null);

    URLClassLoader classLoader = getClassLoader();
    initMicgwaf(classLoader);
    String pageContent = invokeRenderForPage(
        "de.seerheinlab.micgwaf.test.generated.form.ExtensionClassPrefixFormExtensionClassSuffix",
        classLoader);
    File componentDir = new File("src/test/resources/de/seerheinlab/micgwaf/form");
    String expected = FileUtils.readFileToString(new File(componentDir, "expected.txt"));
    expected = expected.replace("\r\n", "\n");
    assertEquals(expected, pageContent);
  }

  @Test
  public void testRenderTemplate() throws Exception
  {
    GeneratorAndCompiler.generateAndCompile("src/test/resources/de/seerheinlab/micgwaf/template", null);

    URLClassLoader classLoader = getClassLoader();
    initMicgwaf(classLoader);
    String pageContent = invokeRenderForPage(
        "de.seerheinlab.micgwaf.test.generated.templatedPage.ExtensionClassPrefixTemplatedPageExtensionClassSuffix",
        classLoader);
    File componentDir = new File("src/test/resources/de/seerheinlab/micgwaf/template");
    String expected = FileUtils.readFileToString(new File(componentDir, "expected.txt"));
    expected = expected.replace("\r\n", "\n");
    assertEquals(expected, pageContent);
  }

  @Test
  public void testRenderVariables() throws Exception
  {
    GeneratorAndCompiler.generateAndCompile("src/test/resources/de/seerheinlab/micgwaf/variable", null);

    URLClassLoader classLoader = getClassLoader();
    initMicgwaf(classLoader);
    String pageContent = invokeRenderForPage(
        "de.seerheinlab.micgwaf.test.generated.body.ExtensionClassPrefixBodyExtensionClassSuffix",
        classLoader);
    File componentDir = new File("src/test/resources/de/seerheinlab/micgwaf/variable");
    String expected = FileUtils.readFileToString(new File(componentDir, "expected.txt"));
    expected = expected.replace("\r\n", "\n");
    assertEquals(expected, pageContent);
  }

  @Test
  public void testRenderComponentRefsInLib() throws Exception
  {
    URLClassLoader classLoader = LoadComponentLibTest.createClassloaderWithLibJars();
    GeneratorAndCompiler.generateAndCompile(
        "src/test/resources/de/seerheinlab/micgwaf/componentlib",
        classLoader);

    classLoader = getClassLoader();
    initMicgwaf(classLoader);
    String pageContent = invokeRenderForPage(
        "de.seerheinlab.micgwaf.test.generated.root.ExtensionClassPrefixRootExtensionClassSuffix",
        classLoader);
    File componentDir = new File("src/test/resources/de/seerheinlab/micgwaf/componentlib");
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
