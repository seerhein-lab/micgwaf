package com.seitenbau.micgwaf.generator;

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

import com.seitenbau.micgwaf.Application;
import com.seitenbau.micgwaf.config.ApplicationBase;

public class GeneratedSourcesTest
{
  @Test
  public void testRenderPage() throws Exception
  {
    GeneratorAndCompiler.generateAndCompile("src/test/resources/com/seitenbau/micgwaf/page");

    URLClassLoader classLoader = getClassLoader();
    initMicgwaf(classLoader);
    String pageContent = invokeRenderForPage(
        "com.seitenbau.micgwaf.test.generated.root.ExtensionClassPrefixRootExtensionClassSuffix", 
        classLoader);
    File componentDir = new File("src/test/resources/com/seitenbau/micgwaf/page");
    String expected = FileUtils.readFileToString(new File(componentDir, "expected/expected.xhtml"));
    expected = expected.replace("\r\n", "\n");
    assertEquals(expected, pageContent);
  }

  @Test
  public void testRenderTemplate() throws Exception
  {
    GeneratorAndCompiler.generateAndCompile("src/test/resources/com/seitenbau/micgwaf/template");

    URLClassLoader classLoader = getClassLoader();
    initMicgwaf(classLoader);
    String pageContent = invokeRenderForPage(
        "com.seitenbau.micgwaf.test.generated.templatedPage.ExtensionClassPrefixTemplatedPageExtensionClassSuffix", 
        classLoader);
    File componentDir = new File("src/test/resources/com/seitenbau/micgwaf/template");
    String expected = FileUtils.readFileToString(new File(componentDir, "expected/templatedPageExpected.xhtml"));
    expected = expected.replace("\r\n", "\n");
    assertEquals(expected, pageContent);
  }

  private URLClassLoader getClassLoader() throws MalformedURLException {
    URLClassLoader classLoader = URLClassLoader.newInstance(
        new URL[] { 
            GeneratorAndCompiler.compileRootDir.toURI().toURL(), 
            new File("target/classes").toURI().toURL() ,
            new File("target/test-classes").toURI().toURL() 
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
    Object root = rootConstructor.newInstance(new Object[] {null});
    StringWriter stringWriter = new StringWriter();
    Method renderMethod = cls.getMethod("render", Writer.class);
    renderMethod.invoke(root, stringWriter);
    return stringWriter.toString();
  }
}
