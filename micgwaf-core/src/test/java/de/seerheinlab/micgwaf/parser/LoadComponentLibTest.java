package de.seerheinlab.micgwaf.parser;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import de.seerheinlab.micgwaf.component.Component;

public class LoadComponentLibTest
{
  @Test
  public void testRenderComponentRefsInLib() throws Exception
  {
    URLClassLoader classLoader = createClassloaderWithLibJars();

    File componentDir = new File("src/test/resources/de/seerheinlab/micgwaf/componentlib");
    Map<String, Component> components 
        = new HtmlParser().readComponents(componentDir, classLoader);
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

  public static URLClassLoader createClassloaderWithLibJars()
      throws FileNotFoundException, IOException, MalformedURLException
  {
    File libdir = new File("target/testlib");
    libdir.mkdirs();
    File jar1 = new File(libdir, "componentLib1.jar");
    createComponentJar(
        jar1,
        "/de/seerheinlab/micgwaf/componentlib/list/components1.properties", 
        "/de/seerheinlab/micgwaf/test/TestComponent1.class");
    File jar2 = new File(libdir, "componentLib2.jar");
    createComponentJar(
        jar2,
        "/de/seerheinlab/micgwaf/componentlib/list/components2.properties", 
        "/de/seerheinlab/micgwaf/test/TestComponent2.class");
    URLClassLoader classLoader = URLClassLoader.newInstance(
        new URL[] { 
            new File("target/classes").toURI().toURL() ,
            new File("target/test-classes").toURI().toURL(),
            jar1.toURI().toURL(),
            jar2.toURI().toURL()
        }, 
        null );
    return classLoader;
  }

  private static void createComponentJar(
      File targetJar,
      String classpathToProperies, 
      String classpathToComponent)
      throws FileNotFoundException, IOException
  {
    ZipOutputStream outputStream = new ZipOutputStream(new FileOutputStream(targetJar));
    ZipEntry zipEntry = new ZipEntry("META-INF/micgwaf-components.properties");
    outputStream.putNextEntry(zipEntry);
    IOUtils.copy(LoadComponentLibTest.class.getResourceAsStream(classpathToProperies), outputStream);
    zipEntry = new ZipEntry(classpathToComponent.substring(1));
    outputStream.putNextEntry(zipEntry);    
    IOUtils.copy(LoadComponentLibTest.class.getResourceAsStream(classpathToComponent), outputStream);
    outputStream.close();
  }
}
