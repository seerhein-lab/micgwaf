package com.seitenbau.micgwaf.generator.component;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.seitenbau.micgwaf.component.ChildListComponent;
import com.seitenbau.micgwaf.component.Component;
import com.seitenbau.micgwaf.component.Composition;
import com.seitenbau.micgwaf.component.DefineComponent;
import com.seitenbau.micgwaf.component.RefComponent;
import com.seitenbau.micgwaf.component.SnippetListComponent;
import com.seitenbau.micgwaf.config.ApplicationBase;
import com.seitenbau.micgwaf.generator.Generator;
import com.seitenbau.micgwaf.generator.JavaClassName;

public class CompositionGenerator extends ComponentGenerator
{
  @Override
  public JavaClassName getClassName(
      Component component,
      String targetPackage)
  {
    return toBaseClassName(component, targetPackage);
  }
  
  @Override
  public String generate(
        Component component,
        String targetPackage)
  {
    if (component.getId() == null)
    {
      return null;
    }
    Composition composition = (Composition) component;
    JavaClassName javaClassName = getClassName(component, targetPackage);
    String className = getClassName(component, targetPackage).getSimpleName();
    StringBuilder fileContent = new StringBuilder();
    fileContent.append("package ").append(javaClassName.getPackage()).append(";\n\n");
    fileContent.append("import ").append(Component.class.getName()).append(";\n");
    fileContent.append("import ").append(ApplicationBase.class.getName()).append(";\n");
    fileContent.append("import ").append(IOException.class.getName()).append(";\n");
    fileContent.append("import ").append(Writer.class.getName()).append(";\n");
    fileContent.append("import ").append(List.class.getName()).append(";\n");
    fileContent.append("import ").append(ArrayList.class.getName()).append(";\n");
    fileContent.append("import ").append(ChildListComponent.class.getName()).append(";\n");
    fileContent.append("import ").append(SnippetListComponent.class.getName()).append(";\n");
    fileContent.append("import ").append(SnippetListComponent.ComponentPart.class.getCanonicalName()).append(";\n");
    
    {
      RefComponent template = new RefComponent(composition.templateId, null);
      ComponentGenerator generator = Generator.getGenerator(template);
      JavaClassName componentClass = generator.getReferencableClassName(template, targetPackage);
      if (template instanceof RefComponent 
          && !javaClassName.getPackage().equals(componentClass.getPackage()))
      {
        fileContent.append("import ").append(componentClass.getName()).append(";\n");
      }
    }
    
    for (Map.Entry<String, Component> entry : composition.definitions.entrySet())
    {
      Component definedComponent = entry.getValue();
      ComponentGenerator generator = Generator.getGenerator(definedComponent);
      JavaClassName componentClass = generator.getReferencableClassName(definedComponent, targetPackage);
      if (definedComponent instanceof RefComponent 
          && !javaClassName.getPackage().equals(componentClass.getPackage()))
      {
        fileContent.append("import ").append(componentClass.getName()).append(";\n");
      }
    }
    
    // class definition header
    fileContent.append("\n");
    generateClassJavadoc(component, fileContent, false);
    fileContent.append("public class ").append(className)
        .append(" extends ").append(Component.class.getSimpleName())
        .append("\n");
    fileContent.append("{\n");
    generateSerialVersionUid(fileContent);
    
    String templateFieldName = "template";
    {
      while (composition.definitions.keySet().contains(templateFieldName))
      {
        templateFieldName = "_" + templateFieldName;
      }
      RefComponent template = new RefComponent(composition.templateId, null);
      ComponentGenerator generator = Generator.getGenerator(template);
      fileContent.append("\n");
      generator.generateFieldOrVariableFromComponent(
          template,
          targetPackage,
          fileContent,
          "public ",
          templateFieldName,
          2);
    }

    // Constructor
    fileContent.append("\n  /**\n");
    fileContent.append("  * Constructor. \n");
    fileContent.append("  *\n");
    fileContent .append("  * @param parent the parent component,")
        .append(" or null if this is a standalone component (e.g. a page)\n");
    fileContent.append("  */\n");
    fileContent.append("  public ").append(className).append("(Component parent)\n");
    fileContent.append("  {\n");
    fileContent.append("    super(\"").append(component.getId()).append("\", parent);\n");
    for (Map.Entry<String, Component> entry : composition.definitions.entrySet())
    {
      Component componentDefinition = entry.getValue();
      if (componentDefinition instanceof DefineComponent)
      {
        componentDefinition = ((DefineComponent) componentDefinition).referencedComponent;
      }
      ComponentGenerator generator = Generator.getGenerator(componentDefinition);
      generator.generateFieldOrVariableFromComponent(componentDefinition, targetPackage, fileContent, "", entry.getKey(), 4);
      fileContent.append("    ").append(templateFieldName).append(".").append(entry.getKey()).append(" = ").append(entry.getKey()).append(";\n");
    }
    fileContent.append("  }\n\n");
    
    // getChildren
    fileContent.append("  @Override\n");
    fileContent.append("  public List<Component> getChildren()\n");
    fileContent.append("  {\n");
    fileContent.append("    return ").append(templateFieldName).append(".getChildren();\n");
    fileContent.append("  }\n");

    fileContent.append("\n  @Override\n");
    fileContent.append("  public void render(Writer writer) throws IOException\n");
    fileContent.append("  {\n");
    
    fileContent.append("    ").append(templateFieldName).append(".render(writer);\n");
    fileContent.append("  }\n");
    fileContent.append("\n  @Override\n");
    fileContent.append("  public void afterRender()\n");
    fileContent.append("  {\n");
    
    fileContent.append("    ").append(templateFieldName).append(".afterRender();\n");
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
    generateClassJavadoc(component, fileContent, true);
    fileContent.append("public class ").append(extensionClassName)
        .append(" extends ").append(className)
        .append("\n");
    fileContent.append("{\n");
    generateSerialVersionUid(fileContent);
    generateComponentConstructorWithParent(extensionClassName, fileContent);
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
    return component.getId() != null;
  }  
}
