package com.seitenbau.test.micgwaf;

import java.io.File;

import com.seitenbau.micgwaf.generator.Generator;

public class GenerateSources
{
  public static Generator generator = new Generator();
  
  public static void main(String[] argv) throws Exception
  {
    Generator.configurationClasspathResource = "/micgwaf/micgwaf-codegen.properties";

    File componentDir = new File("src/main/html");
    generator.generate(
        componentDir, 
        new File("target/generated-sources"), 
        new File("src/main/generated-java"), 
        "com.seitenbau.test.micgwaf.component");
  }
}
