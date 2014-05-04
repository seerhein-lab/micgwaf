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
import de.seerheinlab.micgwaf.config.ApplicationBase;
import de.seerheinlab.micgwaf.generator.Generator;
import de.seerheinlab.micgwaf.generator.JavaClassName;

public class PartListComponentGenerator extends ComponentGenerator
{
  @Override
  public JavaClassName getClassName(
      Component component,
      String targetPackage)
  {
    if (component.getId() != null)
    {
      return toBaseClassName(component, targetPackage);
    }
    return new JavaClassName(PartListComponent.class);
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
    PartListComponent snippetListComponent = (PartListComponent) component;
    JavaClassName javaClassName = getClassName(component, targetPackage);
    String className = getClassName(component, targetPackage).getSimpleName();
    StringBuilder fileContent = new StringBuilder();
    fileContent.append("package ").append(javaClassName.getPackage()).append(";\n\n");
    fileContent.append("import ").append(ChangesChildHtmlId.class.getName()).append(";\n");
    fileContent.append("import ").append(Component.class.getName()).append(";\n");
    fileContent.append("import ").append(ApplicationBase.class.getName()).append(";\n");
    fileContent.append("import ").append(IOException.class.getName()).append(";\n");
    fileContent.append("import ").append(Writer.class.getName()).append(";\n");
    fileContent.append("import ").append(List.class.getName()).append(";\n");
    fileContent.append("import ").append(ArrayList.class.getName()).append(";\n");
    fileContent.append("import ").append(ChildListComponent.class.getName()).append(";\n");
    for (PartListComponent.ComponentPart part : snippetListComponent.parts)
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
    
    // class definition header
    fileContent.append("\n");
    generateClassJavadoc(component, fileContent, false);
    fileContent.append("public class ").append(className)
        .append(" extends ").append(Component.class.getSimpleName());
    if (component.getParent() == null)
    {
      fileContent.append(" implements ChangesChildHtmlId");
    }
    fileContent.append("\n");
    fileContent.append("{\n");
    generateSerialVersionUid(fileContent);
    
    int snippetCounter = 1;
    int componentCounter = 1;
    for (PartListComponent.ComponentPart part : snippetListComponent.parts)
    {
      if (part.htmlSnippet != null)
      {
        fileContent.append("\n  public static final String SNIPPET_").append(snippetCounter)
            .append(" = ").append(asConstant(part.htmlSnippet)).append(";\n");
        ++snippetCounter;
      }
      else if (part.variable != null)
      {
        fileContent.append("\n  public static final String SNIPPET_").append(snippetCounter)
            .append(" = ").append(asConstant(part.variable)).append(";\n");
        ++snippetCounter;
      }
      else if (part.component != null)
      {
        fileContent.append("\n");
        String componentField = getComponentFieldName(part.component, componentCounter);
        ComponentGenerator generator = Generator.getGenerator(part.component);
        generator.generateFieldOrVariableFromComponent(part.component, targetPackage, fileContent, "public ", componentField, "this", 2);
        componentCounter++;
      }
    }
    
    // Constructor
    generateConstructorWithIdAndParent(className, component.getId(), fileContent);
    
    // getChildren
    fileContent.append("  @Override\n");
    fileContent.append("  public List<Component> getChildren()\n");
    fileContent.append("  {\n");
    fileContent.append("    List<Component> result = new ArrayList<>();\n");
    componentCounter = 1;
    for (PartListComponent.ComponentPart part : snippetListComponent.parts)
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

    fileContent.append("\n  @Override\n");
    fileContent.append("  public void render(Writer writer) throws IOException\n");
    fileContent.append("  {\n");
    
    snippetCounter = 1;
    componentCounter = 1;
    for (PartListComponent.ComponentPart part : snippetListComponent.parts)
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
    
    if (component.getParent() == null)
    {
      generateChangeChildHtmlId(fileContent);
    }
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
    String indentString = getIndentString(indent);
    PartListComponent snippetListComponent = (PartListComponent) component;
    StringBuilder result = new StringBuilder();
    result.append(indentString).append("{\n");
    int counter = 1;
    for (PartListComponent.ComponentPart part : snippetListComponent.parts)
    {
      if (part.component != null)
      {
        String fieldName = getChildName(componentField, counter);
        generateFieldOrVariableFromComponent(part.component, targetPackage, result, "", fieldName, "this", indent + 2);
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
      else if (part.variable != null)
      {
        result.append(indentString).append("  ").append(componentField)
            .append(".parts.add(ComponentPart.fromHtmlSnippet(")
            .append(asConstant(part.variable)).append("));\n");
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
