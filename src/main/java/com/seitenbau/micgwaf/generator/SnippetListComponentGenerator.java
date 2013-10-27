package com.seitenbau.micgwaf.generator;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import com.seitenbau.micgwaf.component.ChildListComponent;
import com.seitenbau.micgwaf.component.Component;
import com.seitenbau.micgwaf.component.RefComponent;
import com.seitenbau.micgwaf.component.SnippetListComponent;

public class SnippetListComponentGenerator extends ComponentGenerator
{
  @Override
  public JavaClassName getClassName(
      Component component,
      String targetPackage)
  {
    if (component.id != null)
    {
      return toJavaClassName(component.id, targetPackage);
    }
    return new JavaClassName(SnippetListComponent.class);
  }
  
  @Override
  public String generate(
        Component component,
        String targetPackage)
  {
    if (component.id == null)
    {
      return null;
    }
    SnippetListComponent snippetListComponent = (SnippetListComponent) component;
    JavaClassName javaClassName = getClassName(component, targetPackage);
    String className = getClassName(component, targetPackage).getSimpleName();
    StringBuilder fileContent = new StringBuilder();
    fileContent.append("package ").append(javaClassName.getPackage()).append(";\n\n");
    fileContent.append("import ").append(Component.class.getName()).append(";\n");
    fileContent.append("import ").append(IOException.class.getName()).append(";\n");
    fileContent.append("import ").append(Writer.class.getName()).append(";\n");
    fileContent.append("import ").append(List.class.getName()).append(";\n");
    fileContent.append("import ").append(ArrayList.class.getName()).append(";\n");
    fileContent.append("import ").append(ChildListComponent.class.getName()).append(";\n");
    for (SnippetListComponent.ComponentPart part : snippetListComponent.parts)
    {
      if (part.component != null)
      {
        ComponentGenerator generator = Generator.getGenerator(part.component);
        JavaClassName componentClass = generator.getReferencableClassName(part.component, targetPackage);
        if (part.component instanceof RefComponent 
            && !javaClassName.getPackage().equals(componentClass.getPackage()))
        {
          fileContent.append("import ").append(componentClass.getName()).append(";\n");
        }
      }
    }
    fileContent.append("\n");
    fileContent.append("public class ").append(className)
        .append(" extends ").append(Component.class.getSimpleName())
        .append("\n");
    fileContent.append("{\n");
    int snippetCounter = 1;
    int componentCounter = 1;
    for (SnippetListComponent.ComponentPart part : snippetListComponent.parts)
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
        generator.generateFieldOrVariableFromComponent(part.component, targetPackage, fileContent, "public ", componentField, 2);
        componentCounter++;          
      }
    }
    
    fileContent.append("  public ").append(className).append("(Component parent)\n");
    fileContent.append("  {\n");
    fileContent.append("    super(\"").append(component.id).append("\", parent);\n");
    fileContent.append("  }\n\n");
    
    fileContent.append("  @Override\n");
    fileContent.append("  public List<Component> getChildren()\n");
    fileContent.append("  {\n");
    fileContent.append("    List<Component> result = new ArrayList<>();\n");
    componentCounter = 1;
    for (SnippetListComponent.ComponentPart part : snippetListComponent.parts)
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
    for (SnippetListComponent.ComponentPart part : snippetListComponent.parts)
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
    return fileContent.toString();
  }

  @Override
  public String generateExtension(
      Component component,
      String targetPackage)
  {
    String className = getClassName(component, targetPackage).getSimpleName();
    String extensionClassName = getExtensionClassName(component, targetPackage).getSimpleName();
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
    // add no-arg constructor for standalone pages
    if (component.id == null)
    {
      fileContent.append("  \npublic " + extensionClassName + "()\n");
      fileContent.append("  {\n");
      fileContent.append("    super(null);\n");
      fileContent.append("  }\n");
    }
    fileContent.append("}\n");

    return fileContent.toString();
  }

  @Override
  public String generateInitializer(
      String componentField,
      Component component,
      String targetPackage,
      int indent)
  {
    String indentString = getIndentString(indent);
    SnippetListComponent snippetListComponent = (SnippetListComponent) component;
    StringBuilder result = new StringBuilder();
    result.append(indentString).append("{\n");
    int counter = 1;
    for (SnippetListComponent.ComponentPart part : snippetListComponent.parts)
    {
      if (part.component != null)
      {
        String fieldName = getChildName(componentField, counter);
        generateFieldOrVariableFromComponent(part.component, targetPackage, result, "", fieldName, indent + 2);
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

  @Override
  public boolean generateExtensionClass(Component component)
  {
    return component.id != null;
  }  
}
