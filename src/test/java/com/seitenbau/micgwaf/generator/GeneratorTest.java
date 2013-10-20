package com.seitenbau.micgwaf.generator;

import java.io.File;

import org.junit.Test;

import com.seitenbau.micgwaf.generator.Generator;

public class GeneratorTest
{
  public Generator generator = new Generator();
  
  @Test
  public void testGenerate() throws Exception
  {
    File componentDir = new File("src/test/resources/com/seitenbau/micgwaf/page");
    generator.generateComponent(
        componentDir, 
        new File("target/generated-sources/com/sb/test"), 
        new File("target/generated-extension-sources/com/sb/test"),
        "com.sb.test");
  }
}
