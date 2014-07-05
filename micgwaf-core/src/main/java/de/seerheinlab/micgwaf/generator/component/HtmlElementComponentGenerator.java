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
import de.seerheinlab.micgwaf.component.PartListComponent;
import de.seerheinlab.micgwaf.component.RefComponent;
import de.seerheinlab.micgwaf.component.SnippetComponent;
import de.seerheinlab.micgwaf.config.ApplicationBase;
import de.seerheinlab.micgwaf.generator.GeneratedClass;
import de.seerheinlab.micgwaf.generator.Generator;
import de.seerheinlab.micgwaf.generator.JavaClassName;

public class HtmlElementComponentGenerator extends ComponentGenerator
{
  @Override
  public JavaClassName getClassName(GenerationContext generationContext)
  {
    return getBaseClassName(generationContext);
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
  public void generate(GenerationContext generationContext)
  {
    GeneratedClass result = generationContext.generatedClass;
    HtmlElementComponent htmlElementCompont = (HtmlElementComponent) generationContext.component;
    JavaClassName javaClassName = getClassName(generationContext);
    String className = javaClassName.getSimpleName();

    result.classPackage = generationContext.getPackage();
    result.imports.add(ChangesChildHtmlId.class.getName());
    result.imports.add(Component.class.getName());
    result.imports.add(HtmlElementComponent.class.getName());
    result.imports.add(SnippetComponent.class.getName());
    result.imports.add(ChildListComponent.class.getName());
    result.imports.add(ApplicationBase.class.getName());

    result.imports.add(IOException.class.getName());
    result.imports.add(Writer.class.getName());
    result.imports.add(List.class.getName());
    result.imports.add(ArrayList.class.getName());
    for (Component child : htmlElementCompont.getChildren())
    {
      if (child instanceof RefComponent)
      {
        ComponentGenerator generator = Generator.getGenerator(child);
        JavaClassName componentClass = generator.getReferencableClassName(
            new GenerationContext(generationContext, child));
        if (!javaClassName.getPackage().equals(componentClass.getPackage()))
        {
          result.imports.add(componentClass.getName());
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
            JavaClassName componentClass = generator.getReferencableClassName(
                new GenerationContext(generationContext, part.component));
            if (part.component instanceof RefComponent
                && !javaClassName.getPackage().equals(componentClass.getPackage()))
            {
              result.imports.add(componentClass.getName());
            }
          }
        }
      }
    }
    generateClassJavadoc(generationContext.component, result, false);
    generateClassDefinition(generationContext, HtmlElementComponent.class);

    generateSerialVersionUid(result);

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
            generateFieldOrVariableFromComponent(
                new GenerationContext(generationContext, part.component, 2),
                "public ",
                componentField,
                "this");
          }
          else if (part.htmlSnippet != null)
          {
            result.classBody.append("  public ").append(SnippetComponent.class.getSimpleName())
                    .append(" ").append(componentField)
                    .append(" = (").append(SnippetComponent.class.getSimpleName())
                    .append(") ApplicationBase.getApplication().postConstruct(\n")
                    .append("      ")
                    .append("new ").append(SnippetComponent.class.getSimpleName())
                    .append("(null, ").append(asConstant(part.htmlSnippet)).append(", this));\n\n");
          }
          else // variable
          {
            generateVariableComponentField(part, componentField, result);
          }
          componentCounter++;
        }
        continue;
      }
      String componentField = getComponentFieldName(child, componentCounter);
      generateFieldOrVariableFromComponent(
          new GenerationContext(generationContext, child, 2),
          "public ",
          componentField,
          "this");
      componentCounter++;
    }

    // Constructor
    result.classBody.append("  /**\n");
    result.classBody.append("  * Constructor. \n");
    result.classBody.append("  *\n");
    result.classBody.append("  * @param id the id of this component, or null to use the default id \"")
        .append(htmlElementCompont.getId()).append("\".\n");
    result.classBody.append("  * @param parent the parent component.")
        .append(" Can be null if this is a standalone component (e.g. a page).\n");
    result.classBody.append("  */\n");
    result.classBody.append("  public " + className + "(String id, Component parent)\n");
    result.classBody.append("  {\n");
    result.classBody.append("    super(id == null ? \"").append(htmlElementCompont.getId()).append("\" : id, parent);\n");
    if (htmlElementCompont.renderChildren == false)
    {
      result.classBody.append("    renderChildren = false;\n");
    }
    if (htmlElementCompont.renderSelf == false)
    {
      result.classBody.append("    renderSelf = false;\n");
    }
    result.classBody.append("    ").append("elementName = \"").append(htmlElementCompont.elementName)
        .append("\";\n");
    for (Map.Entry<String, String> attributeEntry : htmlElementCompont.attributes.entrySet())
    {
      result.classBody.append("    ").append("attributes.put(\"").append(attributeEntry.getKey())
          .append("\", \"").append(attributeEntry.getValue()).append("\");\n");
    }
    result.classBody.append("  }\n\n");

    // getChildren()
    result.classBody.append("  /**\n");
    result.classBody.append("   * Returns the list of children of this component.\n");
    result.classBody.append("   * The returned list is modifiable, but changes in the list\n");
    result.classBody.append("   * (i.e. adding and removing components) are not written back\n");
    result.classBody.append("   * to this component. Changes in the components DO affect this component.\n");
    result.classBody.append("   *\n");
    result.classBody.append("   * @return the list of children, not null.\n");
    result.classBody.append("   */\n");
    result.classBody.append("  @Override\n");
    result.classBody.append("  public List<Component> getChildren()\n");
    result.classBody.append("  {\n");
    result.classBody.append("    List<Component> result = new ArrayList<>();\n");
    componentCounter = 1;
    for (Component child : htmlElementCompont.children)
    {
      if (child instanceof PartListComponent)
      {
        PartListComponent snippetListChild = (PartListComponent) child;
        for (PartListComponent.ComponentPart part : snippetListChild.parts)
        {
          String componentField = getComponentFieldName(part, componentCounter);
          result.classBody.append("    result.add(").append(componentField).append(");\n");
          componentCounter++;
        }
        continue;
      }
      String componentField = getComponentFieldName(child, componentCounter);
      result.classBody.append("    result.add(").append(componentField).append(");\n");
      componentCounter++;
    }
    result.classBody.append("    return result;\n");
    result.classBody.append("  }\n");

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
            generateVariableGetterSetter(part, getComponentFieldName(part, componentCounter), result);
          }
          componentCounter++;
        }
        continue;
      }
      componentCounter++;
    }

    if (htmlElementCompont.getParent() == null)
    {
      generateChangeChildHtmlId(result);
    }
    generateConvenienceMethods(htmlElementCompont, result);
  }

  protected void generateConvenienceMethods(
      HtmlElementComponent htmlElementComponent,
      GeneratedClass generatedClass)
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
              generatedClass.classBody.append("  /**\n")
                  .append("   * Returns the HTML snippet which is the inner content of this HTML element.\n")
                  .append("   *\n")
                  .append("   * @return the inner HTML, not null.\n")
                  .append("   */\n")
                  .append("\n").append("  public String getInnerContent()\n")
                  .append("  {\n")
                  .append("    return ").append(componentField).append(".text;\n")
                  .append("  }\n")
                  .append("  /**\n")
                  .append("   * Sets the HTML snippet which is the inner content of this HTML element.\n")
                  .append("   *\n")
                  .append("   * @param innerContent the new inner HTML, not null.\n")
                  .append("   */\n")
                  .append("\n").append("  public void setInnerContent(String innerContent)\n")
                  .append("  {\n")
                  .append("    ").append(componentField).append(".text = innerContent;\n")
                  .append("  }\n");
            }
            else
            {
              generatedClass.classBody.append("\n  /**\n")
                  .append("   * Returns the text content of this HTML element.\n")
                  .append("   * HTML entities are resolved in the returned text.\n")
                  .append("   *\n")
                  .append("   * @return the text content, not null.\n")
                  .append("   */\n")
                  .append("  public String getTextContent()\n")
                  .append("  {\n")
                  .append("    return resolveXmlEntities(").append(componentField).append(".text);\n")
                  .append("  }\n")
                  .append("\n  /**\n")
                  .append("   * Sets the text content of this HTML element.\n")
                  .append("   * HTML special characters are escaped in the rendered text.\n")
                  .append("   *\n")
                  .append("   * @param text the text content, not null.\n")
                  .append("   *\n")
                  .append("   * @return this component, not null")
                  .append("   */\n")
                  .append("  public Component setTextContent(String text)\n")
                  .append("  {\n")
                  .append("    ").append(componentField).append(".text = escapeXmlText(text);\n")
                  .append("    return this;")
                  .append("  }\n");
            }
          }
        }
      }
    }
  }

  @Override
  public void generateExtension(GenerationContext generationContext)
  {
    String className = getClassName(generationContext).getSimpleName();
    String extensionClassName = getExtensionClassName(generationContext).getSimpleName();
    GeneratedClass result =generationContext.generatedClass;
    result.classPackage = generationContext.getPackage();
    result.imports.add(Component.class.getName());
    generateClassJavadoc(generationContext.component, result, true);
    result.classDefinition.append("public class ").append(extensionClassName)
        .append(" extends ").append(className);
    generateSerialVersionUid(result);
    generateExtensionConstructor(generationContext);
  }

  @Override
  public void generateInitializer(GenerationContext generationContext, String componentField)
  {
  }
}
