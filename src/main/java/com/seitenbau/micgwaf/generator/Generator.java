package com.seitenbau.micgwaf.generator;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import com.seitenbau.micgwaf.component.ChildListComponent;
import com.seitenbau.micgwaf.component.Component;
import com.seitenbau.micgwaf.component.ComponentRegistry;
import com.seitenbau.micgwaf.component.EmptyComponent;
import com.seitenbau.micgwaf.component.HtmlElementComponent;
import com.seitenbau.micgwaf.component.InputComponent;
import com.seitenbau.micgwaf.component.RefComponent;
import com.seitenbau.micgwaf.component.SnippetListComponent;
import com.seitenbau.micgwaf.parser.HtmlParser;

public class Generator
{
  public static final Map<Class<? extends Component>, ComponentGenerator> componentGeneratorMap = new HashMap<>();
  
  static
  {
    componentGeneratorMap.put(SnippetListComponent.class, new SnippetListComponentGenerator());
    componentGeneratorMap.put(HtmlElementComponent.class, new HtmlElementComponentGenerator());
    componentGeneratorMap.put(InputComponent.class, new InputComponentGenerator());
    componentGeneratorMap.put(RefComponent.class, new RefComponentGenerator());
    componentGeneratorMap.put(ChildListComponent.class, new ChildListComponentGenerator());
    componentGeneratorMap.put(EmptyComponent.class, new EmptyComponentGenerator());
  }
  
  public void generateComponent(
        File sourceDirectory,
        File targetDirectory,
        File extensionsTargetDirectory,
        String targetPackage)
      throws IOException
  {
    HtmlParser parser = new HtmlParser();
    Map<String, Component> componentMap 
        = parser.readComponents(sourceDirectory);
    for (Map.Entry<String, Component> entry : componentMap.entrySet())
    {
      Component component = entry.getValue();
      
      Map<JavaClassName, String> componentFilesToWrite = new HashMap<>();
      Map<JavaClassName, String> extensionFilesToWrite = new HashMap<>();
      generate(component, targetPackage, componentFilesToWrite);
      generateExtension(component, targetPackage, extensionFilesToWrite);
      if (!componentFilesToWrite.isEmpty() && !targetDirectory.exists())
      {
        if (!targetDirectory.mkdirs())
        {
          throw new IOException("Could not create directory " + targetDirectory.getAbsolutePath());
        }
      }
      for (Map.Entry<JavaClassName, String> fileToWriteEntry : componentFilesToWrite.entrySet())
      {
        // TODO use package information
        File targetFile = new File(
            targetDirectory,
            fileToWriteEntry.getKey().getSimpleName() + ".java");
        FileUtils.writeStringToFile(
            targetFile, 
            fileToWriteEntry.getValue(), 
            "ISO-8859-1");
      }
      for (Map.Entry<JavaClassName, String> fileToWriteEntry : extensionFilesToWrite.entrySet())
      {
        // TODO use package information
        File targetFile = new File(
            extensionsTargetDirectory,
            fileToWriteEntry.getKey().getSimpleName() + ".java");
        if (!targetFile.exists())
        {
          FileUtils.writeStringToFile(
              targetFile, 
              fileToWriteEntry.getValue(), 
              "ISO-8859-1");
        }
      }
    }
    generateComponentRegistry(componentMap, targetDirectory, targetPackage);
  }
  
  public void generate(Component component, String targetPackage, Map<JavaClassName, String> filesToWrite)
  {
    ComponentGenerator componentGenerator = componentGeneratorMap.get(component.getClass());
    String result = componentGenerator.generate(component, targetPackage);
    if (result != null)
    {
      filesToWrite.put(componentGenerator.getClassName(component, targetPackage), result);
    }
    for (Component child : component.getChildren())
    {
      generate(child, targetPackage, filesToWrite);
    }
  }
  
  public void generateExtension(Component component, String targetPackage, Map<JavaClassName, String> filesToWrite)
  {
    ComponentGenerator componentGenerator = componentGeneratorMap.get(component.getClass());
    if (componentGenerator.generateExtensionClass(component))
    {
      String result = componentGenerator.generateExtension(component, targetPackage);
      filesToWrite.put(componentGenerator.getExtensionClassName(component, targetPackage), result);
    }
    for (Component child : component.getChildren())
    {
      generateExtension(child, targetPackage, filesToWrite);
    }
  }
  
  public void generateComponentRegistry(
      Map<String, Component> componentMap,
      File targetDirectory,
      String targetPackage) throws IOException
  {
    String className = "ComponentRegistryImpl";
    StringBuilder content = new StringBuilder();
    content.append("package ").append(targetPackage).append(";\n\n");
    content.append("import ").append(ComponentRegistry.class.getName()).append(";\n");
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
      ComponentGenerator componentGenerator = componentGeneratorMap.get(component.getClass());
      JavaClassName componentClassName = componentGenerator.getReferencableClassName(component, targetPackage);
      content.append("    components.put(\"").append(componentClassName.getSimpleName())
          .append("\", new ").append(componentClassName.getSimpleName()).append("(null));\n");
    }
    content.append("  }\n");
    content.append("}\n");
    File targetFile = new File(targetDirectory, className + ".java");
    FileUtils.writeStringToFile(
        targetFile, 
        content.toString(), 
        "ISO-8859-1");
  }
  
  public static ComponentGenerator getGenerator(Component component)
  {
    return getGenerator(component.getClass());
  }

  public static ComponentGenerator getGenerator(Class<? extends Component> componentClass)
  {
    ComponentGenerator result = componentGeneratorMap.get(componentClass);
    if (result == null)
    {
      throw new IllegalArgumentException("Unknown component class " + componentClass);
    }
    return result;
  }
}
