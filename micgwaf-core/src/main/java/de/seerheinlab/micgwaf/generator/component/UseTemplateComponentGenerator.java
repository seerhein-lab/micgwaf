package de.seerheinlab.micgwaf.generator.component;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.seerheinlab.micgwaf.component.ChildListComponent;
import de.seerheinlab.micgwaf.component.Component;
import de.seerheinlab.micgwaf.component.parse.DefineComponent;
import de.seerheinlab.micgwaf.component.parse.PartListComponent;
import de.seerheinlab.micgwaf.component.parse.ReferenceComponent;
import de.seerheinlab.micgwaf.component.parse.UseTemplateComponent;
import de.seerheinlab.micgwaf.config.ApplicationBase;
import de.seerheinlab.micgwaf.generator.GeneratedClass;
import de.seerheinlab.micgwaf.generator.Generator;
import de.seerheinlab.micgwaf.generator.GeneratorHelper;
import de.seerheinlab.micgwaf.generator.JavaClassName;

public class UseTemplateComponentGenerator extends ComponentGenerator
{
  @Override
  public JavaClassName getClassName(GenerationContext generationContext)
  {
    return getBaseClassName(generationContext);
  }

  @Override
  public void generate(GenerationContext generationContext)
  {
    if (generationContext.component.getId() == null)
    {
      generationContext.generatedClass = null;
      return;
    }
    GeneratedClass result = generationContext.generatedClass;

    UseTemplateComponent useTemplateComponent = (UseTemplateComponent) generationContext.component;
    JavaClassName javaClassName = getClassName(generationContext);
    String className = javaClassName.getSimpleName();

    result.classPackage = javaClassName.getPackage();
    result.imports.add(Component.class.getName());
    result.imports.add(ApplicationBase.class.getName());
    result.imports.add(IOException.class.getName());
    result.imports.add(Writer.class.getName());
    result.imports.add(List.class.getName());
    result.imports.add(ArrayList.class.getName());
    result.imports.add(ChildListComponent.class.getName());
    result.imports.add(PartListComponent.class.getName());
    result.imports.add(PartListComponent.ComponentPart.class.getCanonicalName());

    {
      ReferenceComponent template = new ReferenceComponent(useTemplateComponent.templateId, null, null);
      ComponentGenerator generator = Generator.getGenerator(template);
      JavaClassName componentClass = generator.getReferencableClassName(
          new GenerationContext(generationContext, template));
      if (!javaClassName.getPackage().equals(componentClass.getPackage()))
      {
        result.imports.add(componentClass.getName());
      }
    }

    for (Map.Entry<String, Component> entry : useTemplateComponent.definitions.entrySet())
    {
      Component definedComponent = entry.getValue();
      ComponentGenerator generator = Generator.getGenerator(definedComponent);
      JavaClassName componentClass = generator.getReferencableClassName(
          new GenerationContext(generationContext, definedComponent));
      if (definedComponent instanceof ReferenceComponent
          && !javaClassName.getPackage().equals(componentClass.getPackage()))
      {
        result.imports.add(componentClass.getName());
      }
    }

    // class definition header
    generateClassJavadoc(generationContext, false);
    result.classDefinition.append("public class ").append(className)
        .append(" extends ").append(Component.class.getSimpleName())
        .append("\n");
    generateSerialVersionUid(result);

    String templateFieldName = "template";
    {
      while (useTemplateComponent.definitions.keySet().contains(templateFieldName))
      {
        templateFieldName = "_" + templateFieldName;
      }
      ReferenceComponent template = new ReferenceComponent(useTemplateComponent.templateId, null, null);
      ComponentGenerator generator = Generator.getGenerator(template);
      result.classBody.append("\n");
      generator.generateFieldOrVariableFromComponent(
          new GenerationContext(generationContext, template, 2),
          "public ",
          templateFieldName,
          "this");
    }

    // Constructor // TODO use constructor generation from ComponentGenerator
    result.classBody.append("\n  /**\n")
      .append("  * Constructor. \n")
      .append("  *\n")
      .append("  * @param id the id of this component, or null.\n")
      .append("  * @param parent the parent component,")
          .append(" or null if this is a standalone component (e.g. a page)\n")
      .append("  */\n")
      .append("  public ").append(className).append("(String id, Component parent)\n")
      .append("  {\n")
      .append("    super(id == null ? \"").append(generationContext.component.getId())
          .append("\" : id, parent);\n");
    for (Map.Entry<String, Component> entry : useTemplateComponent.definitions.entrySet())
    {
      Component child = entry.getValue();
      if (!(child instanceof DefineComponent))
      {
        throw new IllegalStateException(
            "Only " + DefineComponent.class.getName() + " components may be children of a "
              + UseTemplateComponent.class.getName() + " component"
              + " (Only m:define elements are allowed as children of m:useTemplate elements)");
      }
      Component referencedComponent = ((DefineComponent) child).referencedComponent;
      ComponentGenerator generator = Generator.getGenerator(referencedComponent);
      generator.generateFieldOrVariableFromComponent(
          new GenerationContext(generationContext, referencedComponent, 4),
          "",
          entry.getKey(),
          "this");
      result.classBody.append("    ").append(templateFieldName).append(".").append(entry.getKey())
          .append(" = ").append(entry.getKey()).append(";\n");
    }
    result.classBody.append("  }\n\n");

    // getChildren
    result.classBody
        .append("  @Override\n")
        .append("  public List<Component> getChildren()\n")
        .append("  {\n")
        .append("    return ").append(templateFieldName).append(".getChildren();\n")
        .append("  }\n\n")

        .append("  @Override\n")
        .append("  public void render(Writer writer) throws IOException\n")
        .append("  {\n")
        .append("    ").append(templateFieldName).append(".render(writer);\n")
        .append("  }\n\n")

        .append("  @Override\n")
        .append("  public void afterRender()\n")
        .append("  {\n")

        .append("    ").append(templateFieldName).append(".afterRender();\n")
        .append("  }\n");
  }

  @Override
  public void generateExtension(GenerationContext generationContext)
  {
    GeneratedClass result = generationContext.generatedClass;

    String className = getClassName(generationContext).getSimpleName();
    String extensionClassName = getExtensionClassName(generationContext).getSimpleName();
    result.classPackage = generationContext.getPackage();

    result.imports.add(Component.class.getName());

    generateClassJavadoc(generationContext, true);
    result.classDefinition.append("public class ").append(extensionClassName)
        .append(" extends ").append(className);

    generateSerialVersionUid(result);
    generateConstructorWithIdAndParent(generationContext, extensionClassName, null, null);
  }

  @Override
  public void generateInitializer(
      GenerationContext generationContext,
      String componentField)
  {
    String indentString = GeneratorHelper.getIndentString(generationContext.indent);
    PartListComponent snippetListComponent = (PartListComponent) generationContext.component;
    generationContext.generatedClass.classBody.append(indentString).append("{\n");
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
        generationContext.generatedClass.classBody.append(indentString).append("  ").append(componentField)
            .append(".parts.add(ComponentPart.fromComponent(")
            .append(fieldName).append("));\n");

        counter++;
      }
      else if (part.htmlSnippet != null)
      {
        generationContext.generatedClass.classBody.append(indentString).append("  ").append(componentField)
            .append(".parts.add(ComponentPart.fromHtmlSnippet(")
            .append(asConstant(part.htmlSnippet)).append("));\n");
      }
      else if (part.variableName != null)
      {
        generationContext.generatedClass.classBody.append(indentString).append("  ").append(componentField)
            .append(".parts.add(ComponentPart.fromHtmlSnippet(")
            .append(asConstant(part.variableName)).append("));\n");
      }
    }
    generationContext.generatedClass.classBody.append(indentString).append("}\n");
  }

  @Override
  public boolean generateExtensionClass(Component component)
  {
    return component.getId() != null;
  }
}
