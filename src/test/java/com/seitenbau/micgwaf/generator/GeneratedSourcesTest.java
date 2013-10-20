package com.seitenbau.micgwaf.generator;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.StringWriter;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.sb.test.Root;

public class GeneratedSourcesTest
{
  @Test
  public void testRender() throws Exception
  {
    Root root = new Root(null);
    StringWriter stringWriter = new StringWriter();
    root.render(stringWriter);
    System.out.println(stringWriter);
    File componentDir = new File("src/test/resources/com/seitenbau/micgwaf/page");
    String expected = FileUtils.readFileToString(new File(componentDir, "expected/expected.xhtml"));
    expected = expected.replace("\r\n", "\n");
    assertEquals(expected, stringWriter.toString());
  }
}
