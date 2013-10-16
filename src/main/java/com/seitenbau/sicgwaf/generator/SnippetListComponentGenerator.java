package com.seitenbau.sicgwaf.generator;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.seitenbau.sicgwaf.component.ChildListComponent;
import com.seitenbau.sicgwaf.component.Component;
import com.seitenbau.sicgwaf.component.ComponentPart;
import com.seitenbau.sicgwaf.component.SnippetListComponent;

public class SnippetListComponentGenerator extends ComponentGenerator
{
  public String getClassName(
      String componentName,
      Component rawComponent,
      String targetPackage)
  {
    if (componentName != null)
    {
      return toJavaName(componentName);
    }
    return SnippetListComponent.class.getName();
  }
  
  public String getExtensionClassName(
      String componentName,
      Component rawComponent,
      String targetPackage)
  {
    if (componentName != null)
    {
      return toJavaName(componentName) + "Extension";
    }
    return null;
  }
  
  public String generateNewComponent(
      String componentName,
      Component component,
      String targetPackage)
  {
    return null;
  }

  public void generate(
        String componentName,
        Component component,
        String targetPackage,
        Map<String, String> filesToWrite)
  {
    SnippetListComponent snippetListComponent = (SnippetListComponent) component;
    String className = getClassName(componentName, component, targetPackage);
    StringBuilder fileContent = new StringBuilder();
    fileContent.append("package ").append(targetPackage).append(";\n\n");
    fileContent.append("import ").append(Component.class.getName()).append(";\n");
    fileContent.append("import ").append(IOException.class.getName()).append(";\n");
    fileContent.append("import ").append(Writer.class.getName()).append(";\n");
    fileContent.append("import ").append(List.class.getName()).append(";\n");
    fileContent.append("import ").append(ArrayList.class.getName()).append(";\n");
    fileContent.append("import ").append(ComponentPart.class.getName()).append(";\n");
    fileContent.append("import ").append(ChildListComponent.class.getName()).append(";\n");
    fileContent.append("\n");
    fileContent.append("public class ").append(className)
        .append(" extends ").append(Component.class.getSimpleName())
        .append("\n");
    fileContent.append("{\n");
    int snippetCounter = 1;
    int componentCounter = 1;
    for (ComponentPart part : snippetListComponent.parts)
    {
      if (part.htmlSnippet != null)
      {
        fileContent.append("  public static final String SNIPPET_").append(snippetCounter)
            .append(" = ").append(asConstant(part.htmlSnippet)).append(";\n\n");
        ++snippetCounter;
      }
      else if (part.component != null)
      {
        String componentField = getComponentFieldName(part.component, componentCounter);
        ComponentGenerator generator = Generator.getGenerator(part.component);
        String newComponentName = generator.generateNewComponent(componentField, part.component, targetPackage);
        if (newComponentName == null)
        {
          String componentClassName = generator.getReferencableClassName(null, part.component, targetPackage);
          fileContent.append("  public ").append(componentClassName).append(" ").append(componentField)
              .append(" = new ").append(componentClassName).append("(this);\n\n");
          fileContent.append(generator.generateInitializer(componentField, part.component, targetPackage, 2, filesToWrite));
        }
        else
        {
          generator.generate(part.component.id, part.component, targetPackage, filesToWrite);
          String componentClassName = generator.getReferencableClassName(part.component.id, part.component, targetPackage);
          fileContent.append("  public ").append(componentClassName).append(" ").append(componentField)
              .append(" = new ").append(componentClassName).append("(this);\n\n");
        }
        componentCounter++;          
      }
    }
    
    fileContent.append("  public ").append(className).append("(Component parent)\n");
    fileContent.append("  {\n");
    fileContent.append("    super(\"").append(componentName).append("\", parent);\n");
    fileContent.append("  }\n\n");
    
    fileContent.append("  @Override\n");
    fileContent.append("  public List<Component> getChildren()\n");
    fileContent.append("  {\n");
    fileContent.append("    List<Component> result = new ArrayList<>();\n");
    componentCounter = 1;
    for (ComponentPart part : snippetListComponent.parts)
    {
      if (part.component != null)
      {
        String componentField = getComponentFieldName(part.component, componentCounter);
        fileContent.append("    result.add(").append(componentField).append(");\n");
        componentCounter++;
      }
    }
    fileContent.append("    return result;\n");
    fileContent.append("  }\n");

    fileContent.append("  @Override\n");
    fileContent.append("  public void render(Writer writer) throws IOException\n");
    fileContent.append("  {\n");
    
    snippetCounter = 1;
    componentCounter = 1;
    for (ComponentPart part : snippetListComponent.parts)
    {
      if (part.htmlSnippet != null)
      {
        fileContent.append("    writer.write(SNIPPET_").append(snippetCounter)
            .append(");\n");
        ++snippetCounter;
      }
      else if (part.component != null)
      {
        String componentField = getComponentFieldName(part.component, componentCounter);
        fileContent.append("    if (").append(componentField).append(" != null)\n");
        fileContent.append("    {\n");
        fileContent.append("      ").append(componentField)
            .append(".render(writer);\n");
        fileContent.append("    }\n");
        componentCounter++;
      }
    }
    fileContent.append("  }\n");
    fileContent.append("}\n");
    filesToWrite.put(className,  fileContent.toString());
  }

  public void generateExtension(
      String componentName,
      Component component,
      String targetPackage,
      Map<String, String> filesToWrite)
  {
    SnippetListComponent snippetListComponent = (SnippetListComponent) component;
    String className = getClassName(componentName, component, targetPackage);
    String extensionClassName = getExtensionClassName(componentName, component, targetPackage);
    StringBuilder fileContent = new StringBuilder();
    fileContent.append("package ").append(targetPackage).append(";\n\n");
    fileContent.append("\n");
    fileContent.append("import ").append(Component.class.getName()).append(";\n");
    fileContent.append("\n");
    fileContent.append("public class ").append(extensionClassName)
        .append(" extends ").append(className)
        .append("\n");
    fileContent.append("{\n");
    fileContent.append("  public " + extensionClassName + "(Component parent)\n");
    fileContent.append("  {\n");
    fileContent.append("    super(parent);\n");
    fileContent.append("  }\n");
    fileContent.append("}\n");

    int componentCounter = 1;
    for (ComponentPart part : snippetListComponent.parts)
    {
      if (part.component != null)
      {
        String componentField = getComponentFieldName(part.component, componentCounter);
        ComponentGenerator generator = Generator.getGenerator(part.component);
        String newComponentName = generator.generateNewComponent(componentField, part.component, targetPackage);
        if (newComponentName != null)
        {
          generator.generateExtension(part.component.id, part.component, targetPackage, filesToWrite);
        }
        componentCounter++;          
      }
    }
    filesToWrite.put(extensionClassName, fileContent.toString());
  }

  public String generateInitializer(
      String componentField,
      Component component,
      String targetPackage,
      int indent,
      Map<String, String> filesToWrite)
  {
    String indentString = getIndentString(indent);
    SnippetListComponent snippetListComponent = (SnippetListComponent) component;
    StringBuilder result = new StringBuilder();
    result.append(indentString).append("{\n");
    int counter = 1;
    for (ComponentPart part : snippetListComponent.parts)
    {
      if (part.component != null)
      {
        String fieldName = getChildName(componentField, counter);
        generateFieldFromComponent(part.component, targetPackage, result, "public", fieldName, indent + 2, filesToWrite);
        result.append(indentString).append("  ").append(componentField)
            .append(".parts.add(ComponentPart.fromComponent(")
            .append(fieldName).append("));\n");

        counter++;
      }
      else if (part.htmlSnippet != null)
      {
        result.append(indentString).append("  ").append(componentField)
            .append(".parts.add(ComponentPart.fromHtmlSnippet(")
            .append(asConstant(part.htmlSnippet)).append("));\n");
      }
    }
    result.append(indentString).append("}\n");
    return result.toString();
  }  
}
