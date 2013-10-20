package com.seitenbau.micgwaf.generator;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.seitenbau.micgwaf.generator.Generator;

public class GeneratorTest
{
  public Generator generator = new Generator();
  
  @Test
  public void testGenerate() throws Exception
  {
    File componentDir = new File("src/test/resources/com/seitenbau/micgwaf/page");
    File generatedSourcesDir = new File("target/generated-sources/com/sb/test");
    File generatedExtensionsDir = new File("target/generated-extension-sources/com/sb/test");
    
    FileUtils.deleteDirectory(generatedSourcesDir);
    FileUtils.deleteDirectory(generatedExtensionsDir);
    
    generator.generateComponent(
        componentDir, 
        generatedSourcesDir, 
        generatedExtensionsDir,
        "com.sb.test");
  }
}
