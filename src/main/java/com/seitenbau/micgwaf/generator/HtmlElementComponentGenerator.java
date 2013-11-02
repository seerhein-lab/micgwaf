package com.seitenbau.micgwaf.generator;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.seitenbau.micgwaf.component.ChildListComponent;
import com.seitenbau.micgwaf.component.Component;
import com.seitenbau.micgwaf.component.GenerationParameters;
import com.seitenbau.micgwaf.component.HtmlElementComponent;
import com.seitenbau.micgwaf.component.RefComponent;
import com.seitenbau.micgwaf.component.SnippetComponent;
import com.seitenbau.micgwaf.component.SnippetListComponent;

public class HtmlElementComponentGenerator extends ComponentGenerator
{
  @Override
  public JavaClassName getClassName(
      Component component,
      String targetPackage)
  {
    return toBaseClassName(component, targetPackage);
  }
  
  @Override
  public boolean generateExtensionClass(Component component)
  {
    final GenerationParameters generationParameters = component.getGenerationParameters();
    if (generationParameters != null)
    {
      if (generationParameters.generateExtensionClass != null)
      {
        return generationParameters.generateExtensionClass;
      }
      return false;
    }
    return true;
  }
  
  @Override
  public String generate(
        Component component,
        String targetPackage)
  {
    HtmlElementComponent htmlElementCompont = (HtmlElementComponent) component;
    JavaClassName javaClassName = getClassName(component, targetPackage);
    String className = javaClassName.getSimpleName();
    StringBuilder fileContent = new StringBuilder();
    fileContent.append("package ").append(targetPackage).append(";\n\n");
    fileContent.append("import ").append(Component.class.getName()).append(";\n");
    fileContent.append("import ").append(HtmlElementComponent.class.getName()).append(";\n");
    fileContent.append("import ").append(SnippetComponent.class.getName()).append(";\n");
    fileContent.append("import ").append(ChildListComponent.class.getName()).append(";\n");
    fileContent.append("import ").append(IOException.class.getName()).append(";\n");
    fileContent.append("import ").append(Writer.class.getName()).append(";\n");
    fileContent.append("import ").append(List.class.getName()).append(";\n");
    fileContent.append("import ").append(ArrayList.class.getName()).append(";\n");
    for (Component child : htmlElementCompont.getChildren())
    {
      if (child instanceof RefComponent)
      {
        ComponentGenerator generator = Generator.getGenerator(child);
        JavaClassName componentClass = generator.getReferencableClassName(child, targetPackage);
        if (!javaClassName.getPackage().equals(componentClass.getPackage()))
        {
          fileContent.append("import ").append(componentClass.getName()).append(";\n");
        }
      }
      else if (child instanceof SnippetListComponent)
      {
        SnippetListComponent snippetListChild = (SnippetListComponent) child;
        for (SnippetListComponent.ComponentPart part : snippetListChild.parts)
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
      }
    }
    fileContent.append("\n");
    generateClassJavadoc(component, fileContent, false);
    fileContent.append("public class ").append(className)
        .append(" extends ").append(HtmlElementComponent.class.getSimpleName())
        .append("\n");
    fileContent.append("{\n");
    
    generateSerialVersionUid(fileContent);
    
    // fields
    int componentCounter = 1;
    for (Component child : htmlElementCompont.children)
    {
      if (child instanceof SnippetListComponent)
      {
        SnippetListComponent snippetListChild = (SnippetListComponent) child;
        for (SnippetListComponent.ComponentPart part : snippetListChild.parts)
        {
          String componentField = getComponentFieldName(part, componentCounter);
          if (part.component != null)
          {
            generateFieldOrVariableFromComponent(part.component, targetPackage, fileContent, "public ", componentField, 2);
          }
          else
          {
            fileContent.append("  public ").append(SnippetComponent.class.getSimpleName())
                    .append(" snippet").append(componentCounter)
                    .append(" = new ").append(SnippetComponent.class.getSimpleName())
                    .append("(null, ").append(asConstant(part.htmlSnippet)).append(", this);\n\n");
          }
          componentCounter++;                      
        }
        continue;
      }
      String componentField = getComponentFieldName(child, componentCounter);
      generateFieldOrVariableFromComponent(child, targetPackage, fileContent, "public ", componentField, 2);
      componentCounter++;          
    }

    // Constructor
    fileContent.append("  /**\n");
    fileContent.append("  * Constructor. \n");
    fileContent.append("  *\n");
    fileContent .append("  * @param parent the parent component,")
        .append(" or null if this is a standalone component (e.g. a page)\n");
    fileContent.append("  */\n");
    fileContent.append("  public " + className + "(Component parent)");
    fileContent.append("  {\n");
    fileContent.append("    super(parent);\n");
    if (htmlElementCompont.renderChildren == false)
    {
      fileContent.append("    renderChildren = false;\n");
    }
    if (htmlElementCompont.renderSelf == false)
    {
      fileContent.append("    renderSelf = false;\n");
    }
    fileContent.append("    ").append("elementName = \"").append(htmlElementCompont.elementName)
        .append("\";\n");
    fileContent.append("    ").append("id = \"")
        .append(removeLoopPart(htmlElementCompont.getId())).append("\";\n");
    for (Map.Entry<String, String> attributeEnty : htmlElementCompont.attributes.entrySet())
    {
      fileContent.append("    ").append("attributes.put(\"").append(attributeEnty.getKey())
          .append("\", \"").append(attributeEnty.getValue()).append("\");\n");
    }
    fileContent.append("  }\n\n");

    // getChildren()
    fileContent.append("  /**\n");
    fileContent.append("   * Returns the list of children of this component.\n");
    fileContent.append("   * The returned list is modifiable, but changes in the list\n");
    fileContent.append("   * (i.e. adding and removing components) are not written back\n");
    fileContent.append("   * to this component. Changes in the components DO affect this component.\n");
    fileContent.append("   *\n");
    fileContent.append("   * @return the list of children, not null.\n");
    fileContent.append("   */\n");
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
        for (SnippetListComponent.ComponentPart part : snippetListChild.parts)
        {
          String componentField = getComponentFieldName(part, componentCounter);
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
    generateConvenienceMethods(htmlElementCompont, fileContent);
    fileContent.append("}\n");
    return fileContent.toString();
  }

  protected void generateConvenienceMethods(
      HtmlElementComponent htmlElementComponent, 
      StringBuilder stringBuilder)
  {
    if (htmlElementComponent.children.size() == 1)
    {
      Component child = htmlElementComponent.children.get(0);
      String componentField = getComponentFieldName(child, 1);
      if (child instanceof SnippetListComponent)
      {
        SnippetListComponent snippetListComponent = (SnippetListComponent) child;
        if (snippetListComponent.parts.size() == 1)
        {
          SnippetListComponent.ComponentPart part = snippetListComponent.parts.get(0);
          componentField = getComponentFieldName(part, 1);
          if (part.htmlSnippet != null)
          {
            if (part.htmlSnippet.contains("<"))
            {
              stringBuilder.append("  /**\n");
              stringBuilder.append("   * Returns the HTML snippet which is the inner content of this HTML element.\n");
              stringBuilder.append("   *\n");
              stringBuilder.append("   * @return the inner HTML, not null.\n");
              stringBuilder.append("   */\n");
              stringBuilder.append("\n").append("  public String getInnerContent()\n")
                  .append("  {\n")
                  .append("    return ").append(componentField).append(".snippet;\n")
                  .append("  }\n");
              stringBuilder.append("  /**\n");
              stringBuilder.append("   * Sets the HTML snippet which is the inner content of this HTML element.\n");
              stringBuilder.append("   *\n");
              stringBuilder.append("   * @param innerContent the new inner HTML, not null.\n");
              stringBuilder.append("   */\n");
              stringBuilder.append("\n").append("  public void setInnerContent(String innerContent)\n")
                  .append("  {\n")
                  .append("    ").append(componentField).append(".snippet = innerContent;\n")
                  .append("  }\n");
            }
            else
            {
              stringBuilder.append("\n  /**\n");
              stringBuilder.append("   * Returns the text content of this HTML element.\n");
              stringBuilder.append("   * HTML entities are resoved in the returned text.\n");
              stringBuilder.append("   *\n");
              stringBuilder.append("   * @return the text content, not null.\n");
              stringBuilder.append("   */\n");
              stringBuilder.append("  public String getTextContent()\n")
                  .append("  {\n")
                  .append("    return resolveEntities(").append(componentField).append(".snippet);\n")
                  .append("  }\n");
              stringBuilder.append("\n  /**\n");
              stringBuilder.append("   * Sets the text content of this HTML element.\n");
              stringBuilder.append("   * HTML special characters are escaped in the rendered text.\n");
              stringBuilder.append("   *\n");
              stringBuilder.append("   * @param text the text content, not null.\n");
              stringBuilder.append("   */\n");
              stringBuilder.append("  public void setTextContent(String text)\n")
                  .append("  {\n")
                  .append("    ").append(componentField).append(".snippet = escapeHtml(text);\n")
                  .append("  }\n");
            }
          }
        }
      }
    }
  }
  
  @Override
  public String generateExtension(
      Component component,
      String targetPackage)
  {
    String className = getClassName(component, targetPackage).getSimpleName();
    String extensionClassName = getExtensionClassName(component, targetPackage).getSimpleName();
    StringBuilder fileContent = new StringBuilder();
    fileContent.append("package ").append(targetPackage).append(";\n");
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
    return "";
  }
}
