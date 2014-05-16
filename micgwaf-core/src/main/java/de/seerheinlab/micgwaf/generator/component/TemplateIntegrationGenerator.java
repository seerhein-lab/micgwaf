package de.seerheinlab.micgwaf.generator.component;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.seerheinlab.micgwaf.component.ChildListComponent;
import de.seerheinlab.micgwaf.component.Component;
import de.seerheinlab.micgwaf.component.TemplateIntegration;
import de.seerheinlab.micgwaf.component.DefineComponent;
import de.seerheinlab.micgwaf.component.RefComponent;
import de.seerheinlab.micgwaf.component.PartListComponent;
import de.seerheinlab.micgwaf.config.ApplicationBase;
import de.seerheinlab.micgwaf.generator.Generator;
import de.seerheinlab.micgwaf.generator.JavaClassName;

public class TemplateIntegrationGenerator extends ComponentGenerator
{
  @Override
  public JavaClassName getClassName(GenerationContext generationContext)
  {
    return toBaseClassName(generationContext);
  }
  
  @Override
  public String generate(GenerationContext generationContext)
  {
    if (generationContext.component.getId() == null)
    {
      return null;
    }
    TemplateIntegration templateIntegration = (TemplateIntegration) generationContext.component;
    JavaClassName javaClassName = getClassName(generationContext);
    String className = javaClassName.getSimpleName();
    StringBuilder fileContent = new StringBuilder();
    generationContext.stringBuilder = fileContent;
    fileContent.append("package ").append(javaClassName.getPackage()).append(";\n\n");
    fileContent.append("import ").append(Component.class.getName()).append(";\n");
    fileContent.append("import ").append(ApplicationBase.class.getName()).append(";\n");
    fileContent.append("import ").append(IOException.class.getName()).append(";\n");
    fileContent.append("import ").append(Writer.class.getName()).append(";\n");
    fileContent.append("import ").append(List.class.getName()).append(";\n");
    fileContent.append("import ").append(ArrayList.class.getName()).append(";\n");
    fileContent.append("import ").append(ChildListComponent.class.getName()).append(";\n");
    fileContent.append("import ").append(PartListComponent.class.getName()).append(";\n");
    fileContent.append("import ").append(PartListComponent.ComponentPart.class.getCanonicalName()).append(";\n");
    
    {
      RefComponent template = new RefComponent(templateIntegration.templateId, null, null);
      ComponentGenerator generator = Generator.getGenerator(template);
      JavaClassName componentClass = generator.getReferencableClassName(
          new GenerationContext(generationContext, template));
      if (!javaClassName.getPackage().equals(componentClass.getPackage()))
      {
        fileContent.append("import ").append(componentClass.getName()).append(";\n");
      }
    }
    
    for (Map.Entry<String, Component> entry : templateIntegration.definitions.entrySet())
    {
      Component definedComponent = entry.getValue();
      ComponentGenerator generator = Generator.getGenerator(definedComponent);
      JavaClassName componentClass = generator.getReferencableClassName(
          new GenerationContext(generationContext, definedComponent));
      if (definedComponent instanceof RefComponent 
          && !javaClassName.getPackage().equals(componentClass.getPackage()))
      {
        fileContent.append("import ").append(componentClass.getName()).append(";\n");
      }
    }
    
    // class definition header
    fileContent.append("\n");
    generateClassJavadoc(generationContext.component, fileContent, false);
    fileContent.append("public class ").append(className)
        .append(" extends ").append(Component.class.getSimpleName())
        .append("\n");
    fileContent.append("{\n");
    generateSerialVersionUid(fileContent);
    
    String templateFieldName = "template";
    {
      while (templateIntegration.definitions.keySet().contains(templateFieldName))
      {
        templateFieldName = "_" + templateFieldName;
      }
      RefComponent template = new RefComponent(templateIntegration.templateId, null, null);
      ComponentGenerator generator = Generator.getGenerator(template);
      fileContent.append("\n");
      generator.generateFieldOrVariableFromComponent(
          new GenerationContext(generationContext, template, 2),
          "public ",
          templateFieldName,
          "this");
    }

    // Constructor
    fileContent.append("\n  /**\n");
    fileContent.append("  * Constructor. \n");
    fileContent.append("  *\n");
    fileContent.append("  * @param id the id of this component, or null.\n");
    fileContent.append("  * @param parent the parent component,")
        .append(" or null if this is a standalone component (e.g. a page)\n");
    fileContent.append("  */\n");
    fileContent.append("  public ").append(className).append("(String id, Component parent)\n");
    fileContent.append("  {\n");
    fileContent.append("    super(id == null ? \"").append(generationContext.component.getId())
        .append("\" : id, parent);\n");
    for (Map.Entry<String, Component> entry : templateIntegration.definitions.entrySet())
    {
      Component componentDefinition = entry.getValue();
      if (componentDefinition instanceof DefineComponent)
      {
        componentDefinition = ((DefineComponent) componentDefinition).referencedComponent;
      }
      ComponentGenerator generator = Generator.getGenerator(componentDefinition);
      generator.generateFieldOrVariableFromComponent(
          new GenerationContext(generationContext, componentDefinition, 4),
          "",
          entry.getKey(),
          "this");
      fileContent.append("    ").append(templateFieldName).append(".").append(entry.getKey())
          .append(" = ").append(entry.getKey()).append(";\n");
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
    generationContext.stringBuilder = null;
    return fileContent.toString();
  }

  @Override
  public String generateExtension(GenerationContext generationContext)
  {
    String className = getClassName(generationContext).getSimpleName();
    String extensionClassName = getExtensionClassName(generationContext).getSimpleName();
    StringBuilder fileContent = new StringBuilder();
    generationContext.stringBuilder = fileContent;
    fileContent.append("package ").append(generationContext.getPackage()).append(";\n\n");
    fileContent.append("\n");
    fileContent.append("import ").append(Component.class.getName()).append(";\n");
    fileContent.append("\n");
    generateClassJavadoc(generationContext.component, fileContent, true);
    fileContent.append("public class ").append(extensionClassName)
        .append(" extends ").append(className)
        .append("\n");
    fileContent.append("{\n");
    generateSerialVersionUid(fileContent);
    generateConstructorWithIdAndParent(extensionClassName, null, fileContent);
    fileContent.append("}\n");

    generationContext.stringBuilder = null;
    return fileContent.toString();
  }

  @Override
  public void generateInitializer(
      GenerationContext generationContext,
      String componentField)
  {
    String indentString = getIndentString(generationContext.indent);
    PartListComponent snippetListComponent = (PartListComponent) generationContext.component;
    generationContext.stringBuilder.append(indentString).append("{\n");
    int counter = 1;
    for (PartListComponent.ComponentPart part : snippetListComponent.parts)
    {
      if (part.component != null)
      {
        String fieldName = getChildName(componentField, counter);
        generateFieldOrVariableFromComponent(
            new GenerationContext(generationContext, part.component, generationContext.indent + 2),
            "",
            fieldName,
            "this");
        generationContext.stringBuilder.append(indentString).append("  ").append(componentField)
            .append(".parts.add(ComponentPart.fromComponent(")
            .append(fieldName).append("));\n");

        counter++;
      }
      else if (part.htmlSnippet != null)
      {
        generationContext.stringBuilder.append(indentString).append("  ").append(componentField)
            .append(".parts.add(ComponentPart.fromHtmlSnippet(")
            .append(asConstant(part.htmlSnippet)).append("));\n");
      }
      else if (part.variableName != null)
      {
        generationContext.stringBuilder.append(indentString).append("  ").append(componentField)
            .append(".parts.add(ComponentPart.fromHtmlSnippet(")
            .append(asConstant(part.variableName)).append("));\n");
      }
    }
    generationContext.stringBuilder.append(indentString).append("}\n");
  }

  @Override
  public boolean generateExtensionClass(Component component)
  {
    return component.getId() != null;
  }  
}
