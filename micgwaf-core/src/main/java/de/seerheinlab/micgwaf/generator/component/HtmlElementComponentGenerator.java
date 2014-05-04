package de.seerheinlab.micgwaf.generator.component;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.seerheinlab.micgwaf.component.ChangesChildHtmlId;
import de.seerheinlab.micgwaf.component.ChildListComponent;
import de.seerheinlab.micgwaf.component.Component;
import de.seerheinlab.micgwaf.component.GenerationParameters;
import de.seerheinlab.micgwaf.component.HtmlElementComponent;
import de.seerheinlab.micgwaf.component.RefComponent;
import de.seerheinlab.micgwaf.component.SnippetComponent;
import de.seerheinlab.micgwaf.component.PartListComponent;
import de.seerheinlab.micgwaf.config.ApplicationBase;
import de.seerheinlab.micgwaf.generator.Generator;
import de.seerheinlab.micgwaf.generator.JavaClassName;

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
    }
    return component.getParent() == null;
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
    fileContent.append("import ").append(ChangesChildHtmlId.class.getName()).append(";\n");
    fileContent.append("import ").append(Component.class.getName()).append(";\n");
    fileContent.append("import ").append(HtmlElementComponent.class.getName()).append(";\n");
    fileContent.append("import ").append(SnippetComponent.class.getName()).append(";\n");
    fileContent.append("import ").append(ChildListComponent.class.getName()).append(";\n");
    fileContent.append("import ").append(ApplicationBase.class.getName()).append(";\n");

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
      else if (child instanceof PartListComponent)
      {
        PartListComponent snippetListChild = (PartListComponent) child;
        for (PartListComponent.ComponentPart part : snippetListChild.parts)
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
        .append(" extends ").append(HtmlElementComponent.class.getSimpleName());
    if (htmlElementCompont.getParent() == null)
    {
      fileContent.append(" implements ChangesChildHtmlId");
    }
    fileContent.append("\n");
    fileContent.append("{\n");
    
    generateSerialVersionUid(fileContent);
    
    // fields
    int componentCounter = 1;
    for (Component child : htmlElementCompont.children)
    {
      if (child instanceof PartListComponent)
      {
        PartListComponent snippetListChild = (PartListComponent) child;
        for (PartListComponent.ComponentPart part : snippetListChild.parts)
        {
          String componentField = getComponentFieldName(part, componentCounter);
          if (part.component != null)
          {
            generateFieldOrVariableFromComponent(part.component, targetPackage, fileContent, "public ", componentField, "this", 2);
          }
          else if (part.htmlSnippet != null)
          {
            fileContent.append("  public ").append(SnippetComponent.class.getSimpleName())
                    .append(" ").append(componentField)
                    .append(" = (").append(SnippetComponent.class.getSimpleName())
                    .append(") ApplicationBase.getApplication().postConstruct(\n")
                    .append("      ")
                    .append("new ").append(SnippetComponent.class.getSimpleName())
                    .append("(null, ").append(asConstant(part.htmlSnippet)).append(", this));\n\n");
          }
          else // variable
          {
            generateVariableComponentField(part, componentField, fileContent);
          }
          componentCounter++;
        }
        continue;
      }
      String componentField = getComponentFieldName(child, componentCounter);
      generateFieldOrVariableFromComponent(child, targetPackage, fileContent, "public ", componentField, "this", 2);
      componentCounter++;
    }

    // Constructor
    fileContent.append("  /**\n");
    fileContent.append("  * Constructor. \n");
    fileContent.append("  *\n");
    fileContent.append("  * @param id the id of this component, or null to use the default id \"")
        .append(htmlElementCompont.getId()).append("\".\n");
    fileContent.append("  * @param parent the parent component.")
        .append(" Can be null if this is a standalone component (e.g. a page).\n");
    fileContent.append("  */\n");
    fileContent.append("  public " + className + "(String id, Component parent)\n");
    fileContent.append("  {\n");
    fileContent.append("    super(id == null ? \"").append(htmlElementCompont.getId()).append("\" : id, parent);\n");
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
    for (Map.Entry<String, String> attributeEntry : htmlElementCompont.attributes.entrySet())
    {
      fileContent.append("    ").append("attributes.put(\"").append(attributeEntry.getKey())
          .append("\", \"").append(attributeEntry.getValue()).append("\");\n");
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
      if (child instanceof PartListComponent)
      {
        PartListComponent snippetListChild = (PartListComponent) child;
        for (PartListComponent.ComponentPart part : snippetListChild.parts)
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
    
    componentCounter = 1;
    for (Component child : htmlElementCompont.children)
    {
      if (child instanceof PartListComponent)
      {
        PartListComponent snippetListChild = (PartListComponent) child;
        for (PartListComponent.ComponentPart part : snippetListChild.parts)
        {
          if (part.variableName != null)
          {
            generateVariableGetterSetter(part, getComponentFieldName(part, componentCounter), fileContent);
          }
          componentCounter++;
        }
        continue;
      }
      componentCounter++;
    }

    if (htmlElementCompont.getParent() == null)
    {
      generateChangeChildHtmlId(fileContent);
    }
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
      if (child instanceof PartListComponent)
      {
        PartListComponent snippetListComponent = (PartListComponent) child;
        if (snippetListComponent.parts.size() == 1)
        {
          PartListComponent.ComponentPart part = snippetListComponent.parts.get(0);
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
                  .append("    return ").append(componentField).append(".text;\n")
                  .append("  }\n");
              stringBuilder.append("  /**\n");
              stringBuilder.append("   * Sets the HTML snippet which is the inner content of this HTML element.\n");
              stringBuilder.append("   *\n");
              stringBuilder.append("   * @param innerContent the new inner HTML, not null.\n");
              stringBuilder.append("   */\n");
              stringBuilder.append("\n").append("  public void setInnerContent(String innerContent)\n")
                  .append("  {\n")
                  .append("    ").append(componentField).append(".text = innerContent;\n")
                  .append("  }\n");
            }
            else
            {
              stringBuilder.append("\n  /**\n");
              stringBuilder.append("   * Returns the text content of this HTML element.\n");
              stringBuilder.append("   * HTML entities are resolved in the returned text.\n");
              stringBuilder.append("   *\n");
              stringBuilder.append("   * @return the text content, not null.\n");
              stringBuilder.append("   */\n");
              stringBuilder.append("  public String getTextContent()\n")
                  .append("  {\n")
                  .append("    return resolveEntities(").append(componentField).append(".text);\n")
                  .append("  }\n");
              stringBuilder.append("\n  /**\n");
              stringBuilder.append("   * Sets the text content of this HTML element.\n");
              stringBuilder.append("   * HTML special characters are escaped in the rendered text.\n");
              stringBuilder.append("   *\n");
              stringBuilder.append("   * @param text the text content, not null.\n");
              stringBuilder.append("   */\n");
              stringBuilder.append("  public void setTextContent(String text)\n")
                  .append("  {\n")
                  .append("    ").append(componentField).append(".text = escapeHtml(text);\n")
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
    generateConstructorWithIdAndParent(extensionClassName, null, fileContent);
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
