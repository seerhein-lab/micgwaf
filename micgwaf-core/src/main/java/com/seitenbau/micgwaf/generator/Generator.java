package com.seitenbau.micgwaf.generator;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import com.seitenbau.micgwaf.component.AnyComponent;
import com.seitenbau.micgwaf.component.ChildListComponent;
import com.seitenbau.micgwaf.component.Component;
import com.seitenbau.micgwaf.component.ComponentRegistry;
import com.seitenbau.micgwaf.component.Composition;
import com.seitenbau.micgwaf.component.DefineComponent;
import com.seitenbau.micgwaf.component.EmptyComponent;
import com.seitenbau.micgwaf.component.FormComponent;
import com.seitenbau.micgwaf.component.HtmlElementComponent;
import com.seitenbau.micgwaf.component.InputComponent;
import com.seitenbau.micgwaf.component.RefComponent;
import com.seitenbau.micgwaf.component.SnippetListComponent;
import com.seitenbau.micgwaf.generator.component.AnyComponentGenerator;
import com.seitenbau.micgwaf.generator.component.ChildListComponentGenerator;
import com.seitenbau.micgwaf.generator.component.ComponentGenerator;
import com.seitenbau.micgwaf.generator.component.CompositionGenerator;
import com.seitenbau.micgwaf.generator.component.DefineComponentGenerator;
import com.seitenbau.micgwaf.generator.component.EmptyComponentGenerator;
import com.seitenbau.micgwaf.generator.component.FormComponentGenerator;
import com.seitenbau.micgwaf.generator.component.HtmlElementComponentGenerator;
import com.seitenbau.micgwaf.generator.component.InputComponentGenerator;
import com.seitenbau.micgwaf.generator.component.RefComponentGenerator;
import com.seitenbau.micgwaf.generator.component.SnippetListComponentGenerator;
import com.seitenbau.micgwaf.generator.config.GeneratorConfiguration;
import com.seitenbau.micgwaf.parser.HtmlParser;
import com.seitenbau.micgwaf.util.Assertions;

public class Generator
{
  /** All parsed root components, keyed by their component id. */
  public static final Map<Class<? extends Component>, ComponentGenerator> componentGeneratorMap 
      = new HashMap<>();
      
  /** Post-processor to remove unused imports. */
  public static RemoveUnusedImports removeUnusedImports = new RemoveUnusedImports();
  
  /** The configuration of the generator. */
  public static GeneratorConfiguration generatorConfiguration; 
  
  /** Reference to the default generator configuration. */
  public static String configurationClasspathResource 
      = "/com/seitenbau/micgwaf/config/default-micgwaf-codegen.properties";
  
  static
  {
    componentGeneratorMap.put(SnippetListComponent.class, new SnippetListComponentGenerator());
    componentGeneratorMap.put(HtmlElementComponent.class, new HtmlElementComponentGenerator());
    componentGeneratorMap.put(InputComponent.class, new InputComponentGenerator());
    componentGeneratorMap.put(FormComponent.class, new FormComponentGenerator());
    componentGeneratorMap.put(RefComponent.class, new RefComponentGenerator());
    componentGeneratorMap.put(ChildListComponent.class, new ChildListComponentGenerator());
    componentGeneratorMap.put(EmptyComponent.class, new EmptyComponentGenerator());
    componentGeneratorMap.put(AnyComponent.class, new AnyComponentGenerator());
    componentGeneratorMap.put(Composition.class, new CompositionGenerator());
    componentGeneratorMap.put(DefineComponent.class, new DefineComponentGenerator());
  }
  
  /**
   * Generates code for all xhtml files in a directory.
   * The generated code includes base component classes, extension component classes, 
   * and a component registry class.
   * 
   * @param sourceDirectory The directory containing the XHTML files (extension .xhtml).
   * @param targetDirectory The directory where component source files are written.
   *        Existing files in this directory are overwritten each generation run without notice.
   * @param extensionsTargetDirectory The directory where component extension source files are written.
   *        These files are intended for modification by the user, thus existing files are not overwritten.
   * @param baseComponentPackage the base package for component classes.
   * 
   * @throws IOException if generated files cannot be written to the file system.
   * @throws RuntimeException if an error during generation occurs. 
   */
  public void generate(
        File sourceDirectory,
        File targetDirectory,
        File extensionsTargetDirectory,
        String baseComponentPackage)
      throws IOException
  {
    HtmlParser parser = new HtmlParser();
    Map<String, Component> componentMap 
        = parser.readComponents(sourceDirectory);
    for (Map.Entry<String, Component> entry : componentMap.entrySet())
    {
      Component component = entry.getValue();
      String componentPackage = baseComponentPackage + "." + component.getId();
      
      Map<JavaClassName, String> componentFilesToWrite = new HashMap<>();
      Map<JavaClassName, String> extensionFilesToWrite = new HashMap<>();
      generateComponentBaseClass(component, componentPackage, componentFilesToWrite);
      generateComponentExtensionClass(component, componentPackage, extensionFilesToWrite);
      for (Map.Entry<JavaClassName, String> fileToWriteEntry : componentFilesToWrite.entrySet())
      {
        File targetFile = new File(
            targetDirectory,
            fileToWriteEntry.getKey().getSourceFile());
        writeFile(targetFile, fileToWriteEntry.getValue(), true);
      }
      for (Map.Entry<JavaClassName, String> fileToWriteEntry : extensionFilesToWrite.entrySet())
      {
        File targetFile = new File(
            extensionsTargetDirectory,
            fileToWriteEntry.getKey().getSourceFile());
        writeFile(targetFile, fileToWriteEntry.getValue(), false);
      }
    }
    generateComponentRegistry(componentMap, targetDirectory, baseComponentPackage);
  }

  /**
   * Writes a file to the file system using ISO-8859-1 encoding.
   * Parent directories are created if they do not exist.
   * 
   * @param targetFile the file to write to, not null.
   * @param content the content of the file, not null.
   * @param overwrite whether existing files should be overwritten.
   * 
   * @throws IOException if writing to the file system fails.
   */
  public void writeFile(File targetFile, String content, boolean overwrite) throws IOException
  {
    File targetDir = targetFile.getParentFile();
    if (!targetDir.exists())
    {
      if (!targetDir.mkdirs())
      {
        throw new IOException("Could not create directory " + targetDir.getAbsolutePath());
      }
    }
    if (overwrite || !targetFile.exists())
    {
      FileUtils.writeStringToFile(targetFile, content, "ISO-8859-1");
    }
  }
  
  /**
   * Generates the base class for a component and its children,
   * and adds the generated content to the <code>filesToWrite</code> map.
   * 
   * @param component the component to generate the code for, not null.
   * @param targetPackage the package for the component class, not null.
   * @param filesToWrite a map where the generated files are stored: the key is the class name,
   *        and the value is the content of the file.
   */
  public void generateComponentBaseClass(
      Component component,
      String targetPackage,
      Map<JavaClassName, String> filesToWrite)
  {
    ComponentGenerator componentGenerator = getGenerator(component.getClass());
    String result = componentGenerator.generate(component, targetPackage);
    if (result != null)
    {
      result = removeUnusedImports.removeUnusedImports(result);
      filesToWrite.put(componentGenerator.getClassName(component, targetPackage), result);
    }
    for (Component child : component.getChildren())
    {
      generateComponentBaseClass(child, targetPackage, filesToWrite);
    }
  }
  
  /**
   * Generates the extension class for a component and its children,
   * and adds the generated content to the <code>filesToWrite</code> map.
   * 
   * @param component the component to generate the code for, not null.
   * @param targetPackage the package for the component extension class, not null.
   * @param filesToWrite a map where the generated files are stored: the key is the class name,
   *        and the value is the content of the file.
   */
  public void generateComponentExtensionClass(
      Component component,
      String targetPackage,
      Map<JavaClassName, String> filesToWrite)
  {
    ComponentGenerator componentGenerator = getGenerator(component.getClass());
    if (componentGenerator.generateExtensionClass(component))
    {
      String result = componentGenerator.generateExtension(component, targetPackage);
      filesToWrite.put(componentGenerator.getExtensionClassName(component, targetPackage), result);
    }
    for (Component child : component.getChildren())
    {
      generateComponentExtensionClass(child, targetPackage, filesToWrite);
    }
  }
  
  /**
   * Generates the component registry class and writes it to the file system.
   * 
   * @param componentMap a map where all referenceable components are stored: 
   *        the key is the component id, and the value is the Component itself.
   * @param targetDirectory the root directory (excluding package structure) to which the source file
   *        should be written.
   * @param targetPackage the package for the component registry class, not null.
   */
  public void generateComponentRegistry(
      Map<String, Component> componentMap,
      File targetDirectory,
      String targetPackage) throws IOException
  {
    JavaClassName javaClassName = new JavaClassName("ComponentRegistryImpl", targetPackage);
    String className = javaClassName.getSimpleName();
    StringBuilder content = new StringBuilder();
    content.append("package ").append(targetPackage).append(";\n\n");
    content.append("import ").append(ComponentRegistry.class.getName()).append(";\n");
    for (Map.Entry<String, Component> entry : componentMap.entrySet())
    {
      Component component = entry.getValue();
      ComponentGenerator componentGenerator = getGenerator(component.getClass());
      String componentPackage = targetPackage + "." + component.getId();
      JavaClassName componentClassName = componentGenerator.getReferencableClassName(component, componentPackage);
      content.append("import ").append(componentClassName.getName()).append(";\n");
    }
    content.append("\n");
    content.append("public class ").append(className)
        .append(" extends ").append(ComponentRegistry.class.getSimpleName())
        .append("\n");
    content.append("{\n");
    content.append("  public ").append(className).append("()\n");
    content.append("  {\n");
    for (Map.Entry<String, Component> entry : componentMap.entrySet())
    {
      Component component = entry.getValue();
      ComponentGenerator componentGenerator = getGenerator(component.getClass());
      String componentPackage = targetPackage + "." + component.getId();
      JavaClassName componentClassName 
          = componentGenerator.getReferencableClassName(component, componentPackage);
      content.append("    components.put(\"").append(componentClassName.getSimpleName())
          .append("\", new ").append(componentClassName.getSimpleName()).append("(null));\n");
    }
    content.append("  }\n");
    content.append("}\n");
    File targetFile = new File(targetDirectory, javaClassName.getSourceFile());
    writeFile(targetFile, content.toString(), true);
  }
  
  /**
   * Returns a ComponentGenerator for a component.
   * 
   * @param component the component, not null.
   * 
   * @return the component generator, or null if no component generator is registersed for the component.
   */
  public static ComponentGenerator getGenerator(Component component)
  {
    Assertions.assertNotNull(component, "component");
    return getGenerator(component.getClass());
  }

  /**
   * Returns the ComponentGenerator for the passed component class.
   * 
   * @param componentClass the component class to get a generator for, not null.
   * 
   * @return the ComponentGenerator for the generator class, not null.
   * 
   * @throws IllegalArgumentException if the componentClass does not have a ComponentGenerator.
   */
  public static ComponentGenerator getGenerator(Class<? extends Component> componentClass)
  {
    ComponentGenerator result = componentGeneratorMap.get(componentClass);
    if (result == null)
    {
      throw new IllegalArgumentException("Unknown component class " + componentClass);
    }
    return result;
  }

  /**
   * Returns the configuration of the generator. 
   * If the configuration is not yet loaded, it will be loaded, using the current value of
   * configurationClasspathResource.
   * 
   * @return the generator configuration, not null.
   * 
   * @throws RuntimeException if the configuration could not be loaded.
   */
  public static GeneratorConfiguration getGeneratorConfiguration()
  {
    if (generatorConfiguration == null)
    {
      try
      {
        generatorConfiguration = new GeneratorConfiguration(configurationClasspathResource);
      } 
      catch (Exception e)
      {
        throw new RuntimeException("Could not load generator configuration from classpath resource "
            + configurationClasspathResource,
          e);
      }
    }
    return generatorConfiguration;
  }
  
  public static void main(String[] argv) throws IOException
  {
    if (argv.length != 5)
    {
      System.out.println("Generation failed, Arguments cannot be parsed. "
          + "Should be configurationClasspathResource, componentDir, targetDirectory, "
          + "extensionsTargetDirectory, baseComponentPackage");
      return;
    }
    String configurationClasspathResource = argv[0];
    String componentDir = argv[1];
    String targetDirectory = argv[2];
    String extensionsTargetDirectory = argv[3];
    String baseComponentPackage = argv[4];
    if (argv.length != 5)
    {
      System.out.println("Running with:\n"
          + "configurationClasspathResource: " + configurationClasspathResource
          + "componentDir                  : " + componentDir
          + "targetDirectory               : " + targetDirectory
          + "extensionsTargetDirectory     : " + extensionsTargetDirectory
          + "baseComponentPackage          : " + baseComponentPackage);
    }
    Generator.configurationClasspathResource = configurationClasspathResource;
    new Generator().generate(
        new File(componentDir),
        new File(targetDirectory),
        new File(extensionsTargetDirectory),
        baseComponentPackage);
    System.out.println("Generation successful");
    return;
  }
}
