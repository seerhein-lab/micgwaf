package com.seitenbau.sicgwaf.generator;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import com.seitenbau.sicgwaf.component.ChildListComponent;
import com.seitenbau.sicgwaf.component.Component;
import com.seitenbau.sicgwaf.component.ComponentRegistry;
import com.seitenbau.sicgwaf.component.EmptyComponent;
import com.seitenbau.sicgwaf.component.HtmlElementComponent;
import com.seitenbau.sicgwaf.component.InputComponent;
import com.seitenbau.sicgwaf.component.RefComponent;
import com.seitenbau.sicgwaf.component.SnippetListComponent;
import com.seitenbau.sicgwaf.parser.HtmlParser;

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
      String componentName = entry.getKey();
      Component component = entry.getValue();
      
      ComponentGenerator componentGenerator = componentGeneratorMap.get(component.getClass());
      Map<String, String> componentFilesToWrite = new HashMap<>();
      Map<String, String> extensionFilesToWrite = new HashMap<>();
      componentGenerator.generate(componentName, component, targetPackage, componentFilesToWrite);
      componentGenerator.generateExtension(componentName, component, targetPackage, extensionFilesToWrite);
      if (!componentFilesToWrite.isEmpty() && !targetDirectory.exists())
      {
        if (!targetDirectory.mkdirs())
        {
          throw new IOException("Could not create directory " + targetDirectory.getAbsolutePath());
        }
      }
      for (Map.Entry<String, String> fileToWriteEntry : componentFilesToWrite.entrySet())
      {
        File targetFile = new File(targetDirectory, fileToWriteEntry.getKey() + ".java");
        FileUtils.writeStringToFile(
            targetFile, 
            fileToWriteEntry.getValue(), 
            "ISO-8859-1");
      }
      for (Map.Entry<String, String> fileToWriteEntry : extensionFilesToWrite.entrySet())
      {
        File targetFile = new File(extensionsTargetDirectory, fileToWriteEntry.getKey() + ".java");
        FileUtils.writeStringToFile(
            targetFile, 
            fileToWriteEntry.getValue(), 
            "ISO-8859-1");
      }
    }
    generateComponentRegistry(componentMap, targetDirectory, targetPackage);
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
      String componentName = entry.getKey();
      Component component = entry.getValue();
      ComponentGenerator componentGenerator = componentGeneratorMap.get(component.getClass());
      String componentClassName = componentGenerator.getReferencableClassName(componentName, component, targetPackage);
      content.append("    components.put(\"").append(componentName)
          .append("\", new ").append(componentClassName).append("(null));\n");
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
