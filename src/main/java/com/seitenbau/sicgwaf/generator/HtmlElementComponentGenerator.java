package com.seitenbau.sicgwaf.generator;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.seitenbau.sicgwaf.component.Component;
import com.seitenbau.sicgwaf.component.ComponentPart;
import com.seitenbau.sicgwaf.component.HtmlElementComponent;
import com.seitenbau.sicgwaf.component.SnippetComponent;
import com.seitenbau.sicgwaf.component.SnippetListComponent;

public class HtmlElementComponentGenerator extends ComponentGenerator
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
    return HtmlElementComponent.class.getName();
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
  
  public void generate(
        String componentName,
        Component component,
        String targetPackage,
        Map<String, String> filesToWrite)
  {
    HtmlElementComponent htmlElementCompont = (HtmlElementComponent) component;
    String className = getClassName(componentName, component, targetPackage);
    StringBuilder fileContent = new StringBuilder();
    fileContent.append("package ").append(targetPackage).append(";\n\n");
    fileContent.append("import ").append(Component.class.getName()).append(";\n");
    fileContent.append("import ").append(HtmlElementComponent.class.getName()).append(";\n");
    fileContent.append("import ").append(SnippetComponent.class.getName()).append(";\n");
    fileContent.append("import ").append(ComponentPart.class.getName()).append(";\n");
    fileContent.append("import ").append(IOException.class.getName()).append(";\n");
    fileContent.append("import ").append(Writer.class.getName()).append(";\n");
    fileContent.append("import ").append(List.class.getName()).append(";\n");
    fileContent.append("import ").append(ArrayList.class.getName()).append(";\n");
    fileContent.append("\n");
    fileContent.append("public class ").append(className)
        .append(" extends ").append(HtmlElementComponent.class.getSimpleName())
        .append("\n");
    fileContent.append("{\n");
    int componentCounter = 1;
    for (Component child : htmlElementCompont.children)
    {
      if (child instanceof SnippetListComponent)
      {
        SnippetListComponent snippetListChild = (SnippetListComponent) child;
        for (ComponentPart part : snippetListChild.parts)
        {
          String componentField = getComponentFieldName(part.component, componentCounter);
          if (part.component != null)
          {
            ComponentGenerator generator = Generator.getGenerator(part.component);
            String newComponentName = generator.generateNewComponent(componentField, part.component, targetPackage);
            if (newComponentName == null)
            {
              String componentClassName = generator.getReferencableClassName(null, part.component, targetPackage);
              fileContent.append("  public ").append(componentClassName).append(" ").append(componentField)
                  .append(" = new ").append(componentClassName).append("();\n\n");
              fileContent.append(generator.generateInitializer(componentField, part.component, targetPackage, 2));
            }
            else
            {
              generator.generate(part.component.id, part.component, targetPackage, filesToWrite);
              String componentClassName = generator.getReferencableClassName(part.component.id, part.component, targetPackage);
              fileContent.append("  public ").append(componentClassName).append(" ").append(componentField)
                  .append(" = new ").append(componentClassName).append("();\n\n");
            }
          }
          else
          {
            fileContent.append("  public ").append(SnippetComponent.class.getSimpleName()).append(" ")
                    .append(componentField).append(" = new ").append(SnippetComponent.class.getSimpleName())
                    .append("(null, ").append(asConstant(part.htmlSnippet)).append(");\n\n");
          }
          componentCounter++;                      
        }
        continue;
      }
      String componentField = getComponentFieldName(child, componentCounter);
      ComponentGenerator generator = Generator.getGenerator(child);
      String newComponentName = generator.generateNewComponent(componentField, child, targetPackage);
      if (newComponentName == null)
      {
        String componentClassName = generator.getReferencableClassName(null, child, targetPackage);
        fileContent.append("  public ").append(componentClassName).append(" ").append(componentField)
            .append(" = new ").append(componentClassName).append("();\n\n");
        fileContent.append(generator.generateInitializer(componentField, child, targetPackage, 2));
      }
      else
      {
        generator.generate(child.id, child, targetPackage, filesToWrite);
        String componentClassName = generator.getReferencableClassName(child.id, child, targetPackage);
        fileContent.append("  public ").append(componentClassName).append(" ").append(componentField)
            .append(" = new ").append(componentClassName).append("();\n\n");
      }
      componentCounter++;          
    }
    fileContent.append("  ").append("{\n");
    fileContent.append("    ").append("elementName = \"").append(htmlElementCompont.elementName)
        .append("\";\n");
    fileContent.append("    ").append("id = \"").append(htmlElementCompont.id).append("\";\n");
    for (Map.Entry<String, String> attributeEnty : htmlElementCompont.attributes.entrySet())
    {
      fileContent.append("    ").append("attributes.put(\"").append(attributeEnty.getKey())
          .append("\", \"").append(attributeEnty.getValue()).append("\");\n");
    }
    fileContent.append("  ").append("}\n");

    fileContent.append("  @Override\n");
    fileContent.append("  public List<Component> getChildren()\n");
    fileContent.append("  {\n");
    fileContent.append("    List<Component> result = new ArrayList<>();\n");
    componentCounter = 1;
    for (Component child : htmlElementCompont.children)
    {
      if (child instanceof SnippetListComponent)
      {
        SnippetListComponent snippetListChild = (SnippetListComponent) child;
        for (ComponentPart part : snippetListChild.parts)
        {
          String componentField = getComponentFieldName(part.component, componentCounter);
          fileContent.append("    result.add(").append(componentField).append(");\n");
          componentCounter++;
        }
        continue;
      }
      String componentField = getComponentFieldName(child, componentCounter);
      fileContent.append("    result.add(").append(componentField).append(");\n");
      componentCounter++;
    }
    fileContent.append("    return result;\n");
    fileContent.append("  }\n");

    fileContent.append("}\n");
    filesToWrite.put(className, fileContent.toString());
  }
  
  public void generateExtension(
      String componentName,
      Component component,
      String targetPackage,
      Map<String, String> filesToWrite)
  {
    HtmlElementComponent htmlElementCompont = (HtmlElementComponent) component;
    String className = getClassName(componentName, component, targetPackage);
    String extensionClassName = getExtensionClassName(componentName, component, targetPackage);
    StringBuilder fileContent = new StringBuilder();
    fileContent.append("package ").append(targetPackage).append(";\n\n");
    fileContent.append("\n");
    fileContent.append("public class ").append(extensionClassName)
        .append(" extends ").append(className)
        .append("\n");
    fileContent.append("{\n");
    fileContent.append("}\n");
    int componentCounter = 1;
    for (Component child : htmlElementCompont.children)
    {
      if (child instanceof SnippetListComponent)
      {
        SnippetListComponent snippetListChild = (SnippetListComponent) child;
        for (ComponentPart part : snippetListChild.parts)
        {
          String componentField = getComponentFieldName(part.component, componentCounter);
          if (part.component != null)
          {
            ComponentGenerator generator = Generator.getGenerator(part.component);
            String newComponentName = generator.generateNewComponent(componentField, part.component, targetPackage);
            if (newComponentName != null)
            {
              generator.generateExtension(part.component.id, part.component, targetPackage, filesToWrite);
            }
          }
          componentCounter++;                      
        }
        continue;
      }
      String componentField = getComponentFieldName(child, componentCounter);
      ComponentGenerator generator = Generator.getGenerator(child);
      String newComponentName = generator.generateNewComponent(componentField, child, targetPackage);
      if (newComponentName != null)
      {
        generator.generateExtension(child.id, child, targetPackage, filesToWrite);
      }
      componentCounter++;          
    }
    filesToWrite.put(extensionClassName, fileContent.toString());
  }

  public String generateNewComponent(
      String componentName,
      Component component,
      String targetPackage)
  {
    HtmlElementComponent htmlElementCompont = (HtmlElementComponent) component;
    return htmlElementCompont.id;
  }

  public String generateInitializer(
      String componentField,
      Component component,
      String targetPackage,
      int indent)
  {
    String indentString = getIndentString(indent);
    HtmlElementComponent htmlElementCompont = (HtmlElementComponent) component;
    StringBuilder result = new StringBuilder();
    result.append(indentString).append("{\n");
    result.append(indentString).append("  ").append(componentField)
        .append(".elementName = \"").append(htmlElementCompont.elementName).append("\";\n");
    result.append(indentString).append("  ").append(componentField)
        .append(".id = \"").append(htmlElementCompont.id).append("\";\n");
    for (Map.Entry<String, String> attributeEnty : htmlElementCompont.attributes.entrySet())
    {
      result.append(indentString).append("  ").append(componentField)
          .append(".attributes.put(\"").append(attributeEnty.getKey())
          .append("\", \"").append(attributeEnty.getValue()).append("\");\n");
    }
    generateInitChildren(component, targetPackage, result, componentField, indent + 2);
    result.append(indentString).append("}\n");
    return result.toString();
  }
}
