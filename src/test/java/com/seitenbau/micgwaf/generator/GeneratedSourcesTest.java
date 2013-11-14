package com.seitenbau.micgwaf.generator;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class GeneratedSourcesTest
{
  @Test
  public void testRenderPage() throws Exception
  {
    GeneratorAndCompiler.generateAndCompile("src/test/resources/com/seitenbau/micgwaf/page");

    URLClassLoader classLoader = URLClassLoader.newInstance(
        new URL[] { 
            GeneratorAndCompiler.compileRootDir.toURI().toURL(), 
            new File("target/classes").toURI().toURL() 
        }, 
        null );
    Class<?> cls = Class.forName(
        "com.seitenbau.micgwaf.test.generated.root.ExtensionClassPrefixRootExtensionClassSuffix",
        true, 
        classLoader);
    Constructor<?> rootConstructor = cls.getConstructors()[0];
    Object root = rootConstructor.newInstance(new Object[] {null});
    StringWriter stringWriter = new StringWriter();
    Method renderMethod = cls.getMethod("render", Writer.class);
    renderMethod.invoke(root, stringWriter);
    System.out.println(stringWriter);
    File componentDir = new File("src/test/resources/com/seitenbau/micgwaf/page");
    String expected = FileUtils.readFileToString(new File(componentDir, "expected/expected.xhtml"));
    expected = expected.replace("\r\n", "\n");
    assertEquals(expected, stringWriter.toString());
  }
  
  @Test
  public void testRenderTemplate() throws Exception
  {
    GeneratorAndCompiler.generateAndCompile("src/test/resources/com/seitenbau/micgwaf/template");

    URLClassLoader classLoader = URLClassLoader.newInstance(
        new URL[] { 
            GeneratorAndCompiler.compileRootDir.toURI().toURL(), 
            new File("target/classes").toURI().toURL() 
        }, 
        null );
    Class<?> cls = Class.forName(
        "com.seitenbau.micgwaf.test.generated.templatedPage.ExtensionClassPrefixTemplatedPageExtensionClassSuffix",
        true, 
        classLoader);
    Constructor<?> rootConstructor = cls.getConstructors()[0];
    Object root = rootConstructor.newInstance(new Object[] {null});
    StringWriter stringWriter = new StringWriter();
    Method renderMethod = cls.getMethod("render", Writer.class);
    renderMethod.invoke(root, stringWriter);
    System.out.println(stringWriter);
    File componentDir = new File("src/test/resources/com/seitenbau/micgwaf/template");
    String expected = FileUtils.readFileToString(new File(componentDir, "expected/templatedPageExpected.xhtml"));
    expected = expected.replace("\r\n", "\n");
    assertEquals(expected, stringWriter.toString());
  }
}
