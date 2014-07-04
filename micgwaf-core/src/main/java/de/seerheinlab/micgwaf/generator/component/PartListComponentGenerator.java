package de.seerheinlab.micgwaf.generator.component;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import de.seerheinlab.micgwaf.component.ChangesChildHtmlId;
import de.seerheinlab.micgwaf.component.ChildListComponent;
import de.seerheinlab.micgwaf.component.Component;
import de.seerheinlab.micgwaf.component.PartListComponent;
import de.seerheinlab.micgwaf.component.RefComponent;
import de.seerheinlab.micgwaf.component.SnippetComponent;
import de.seerheinlab.micgwaf.config.ApplicationBase;
import de.seerheinlab.micgwaf.generator.GeneratedClass;
import de.seerheinlab.micgwaf.generator.Generator;
import de.seerheinlab.micgwaf.generator.GeneratorHelper;
import de.seerheinlab.micgwaf.generator.JavaClassName;

public class PartListComponentGenerator extends ComponentGenerator
{
  @Override
  public JavaClassName getClassName(GenerationContext generationContext)
  {
    if (generationContext.component.getId() != null)
    {
      return toBaseClassName(generationContext);
    }
    return new JavaClassName(PartListComponent.class);
  }
  
  @Override
  public GeneratedClass generate(GenerationContext generationContext)
  {
    if (generationContext.component.getId() == null)
    {
      return null;
    }
    GeneratedClass result = new GeneratedClass();
    generationContext.generatedClass = result;
    
    PartListComponent partListComponent = (PartListComponent) generationContext.component;
    JavaClassName javaClassName = getClassName(generationContext);
    String className = javaClassName.getSimpleName();

    result.classPackage = javaClassName.getPackage();
    result.imports.add(ChangesChildHtmlId.class.getName());
    result.imports.add(Component.class.getName());
    result.imports.add(SnippetComponent.class.getName());
    result.imports.add(ApplicationBase.class.getName());
    result.imports.add(IOException.class.getName());
    result.imports.add(Writer.class.getName());
    result.imports.add(List.class.getName());
    result.imports.add(ArrayList.class.getName());
    result.imports.add(ChildListComponent.class.getName());
    for (PartListComponent.ComponentPart part : partListComponent.parts)
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
    
    generateClassJavadoc(generationContext.component, result, false);
    generateClassDefinition(generationContext, Component.class);
    generateSerialVersionUid(result);
    
    int snippetCounter = 1;
    int componentCounter = 1;
    for (PartListComponent.ComponentPart part : partListComponent.parts)
    {
      if (part.htmlSnippet != null)
      {
        result.classBody.append("\n  public static final String SNIPPET_").append(snippetCounter)
            .append(" = ").append(asConstant(part.htmlSnippet)).append(";\n");
        ++snippetCounter;
      }
      else if (part.variableName != null)
      {
        String componentField = getComponentFieldName(part, componentCounter);
        generateVariableComponentField(part, componentField, result);
        componentCounter++;
      }
      else if (part.component != null)
      {
        result.classBody.append("\n");
        String componentField = getComponentFieldName(part, componentCounter);
        ComponentGenerator generator = Generator.getGenerator(part.component);
        generator.generateFieldOrVariableFromComponent(
            new GenerationContext(generationContext, part.component, 2), 
            "public ",
            componentField, 
            "this");
        componentCounter++;
      }
    }
    
    // Constructor
    generateConstructorWithIdAndParent(className, generationContext.component.getId(), result);
    
    // getChildren
    result.classBody.append("  @Override\n")
        .append("  public List<Component> getChildren()\n")
        .append("  {\n")
        .append("    List<Component> result = new ArrayList<>();\n");
    componentCounter = 1;
    for (PartListComponent.ComponentPart part : partListComponent.parts)
    {
      if (part.component != null || part.variableName != null)
      {
        String componentField = getComponentFieldName(part, componentCounter);
        result.classBody.append("    result.add(").append(componentField).append(");\n");
        componentCounter++;
      }
    }
    result.classBody.append("    return result;\n")
        .append("  }\n")

        .append("\n  @Override\n")
        .append("  public void render(Writer writer) throws IOException\n")
        .append("  {\n");
    
    snippetCounter = 1;
    componentCounter = 1;
    for (PartListComponent.ComponentPart part : partListComponent.parts)
    {
      if (part.htmlSnippet != null)
      {
        result.classBody.append("    writer.write(SNIPPET_").append(snippetCounter)
            .append(");\n");
        ++snippetCounter;
      }
      else if (part.component != null || part.variableName != null)
      {
        String componentField = getComponentFieldName(part, componentCounter);
        result.classBody.append("    if (").append(componentField).append(" != null)\n")
            .append("    {\n")
            .append("      ").append(componentField).append(".render(writer);\n")
            .append("    }\n");
        componentCounter++;
      }
    }
    result.classBody.append("  }\n");
    
    componentCounter = 1;
    for (PartListComponent.ComponentPart part : partListComponent.parts)
    {
      if (part.variableName != null)
      {
        String componentField = getComponentFieldName(part, componentCounter);
        generateVariableGetterSetter(part, componentField, result);
        componentCounter++;
      }
      else if (part.component != null)
      {
        componentCounter++;
      }
    }
    
    if (generationContext.component.getParent() == null)
    {
      generateChangeChildHtmlId(result);
    }

    generationContext.generatedClass = null;
    return result;
  }

  @Override
  public GeneratedClass generateExtension(GenerationContext generationContext)
  {
    GeneratedClass result = new GeneratedClass();
    generationContext.generatedClass = result;

    String extensionClassName = getExtensionClassName(generationContext).getSimpleName();

    result.classPackage = generationContext.getPackage();

    result.imports.add(Component.class.getName());

    generateClassJavadoc(generationContext.component, result, true);
    generateExtensionDefinition(generationContext);

    generateSerialVersionUid(result);
    generateConstructorWithIdAndParent(extensionClassName, null, result);

    generationContext.generatedClass = null;
    return result;
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
