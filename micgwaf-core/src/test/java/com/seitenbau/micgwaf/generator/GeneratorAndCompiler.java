package com.seitenbau.micgwaf.generator;

import java.io.File;
import java.util.Collection;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import org.apache.commons.io.FileUtils;

public class GeneratorAndCompiler
{
  public static File compileRootDir = new File("target/manually-compiled-classes");
  
  public static File generatedSourcesDir = new File("target/generated-sources");
  
  public static File generatedExtensionsDir = new File("target/generated-extension-sources");

  public static void generateAndCompile(String componentPath) throws Exception
  {
    Generator.configurationClasspathResource = "/com/seitenbau/micgwaf/config/test-micgwaf-codegen.properties";
    
    File componentDir = new File(componentPath);
    
    FileUtils.deleteDirectory(generatedSourcesDir);
    FileUtils.deleteDirectory(generatedExtensionsDir);
    FileUtils.deleteDirectory(compileRootDir);
    
    Generator generator = new Generator();
    generator.generate(
        componentDir, 
        generatedSourcesDir, 
        generatedExtensionsDir,
        "com.seitenbau.micgwaf.test.generated");

    Collection<File> files = FileUtils.listFiles(generatedSourcesDir, new String[] {"java"}, true);
    files.addAll(FileUtils.listFiles(generatedExtensionsDir, new String[] {"java"}, true));
    String[] args = new String[files.size() + 6];
    int i = 6;
    for (File file : files)
    {
      args[i] = file.getAbsolutePath();
      ++i;
    }
    args[0] = "-g";
    args[1] = "-d";
    args[2] = compileRootDir.getAbsolutePath();
    args[3] = "-verbose";
    args[4] = "-sourcepath";
    args[5] = generatedSourcesDir.getAbsolutePath() + ";" + generatedExtensionsDir.getAbsolutePath();

    compileRootDir.mkdirs();
    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    compiler.run(null, null, null, args);
  }
}
