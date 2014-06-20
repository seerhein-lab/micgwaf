package de.seerheinlab.micgwaf.generator.component;

import java.util.List;

import de.seerheinlab.micgwaf.component.Component;
import de.seerheinlab.micgwaf.component.RefComponent;
import de.seerheinlab.micgwaf.component.PartListComponent;
import de.seerheinlab.micgwaf.component.SnippetComponent;
import de.seerheinlab.micgwaf.generator.Generator;
import de.seerheinlab.micgwaf.generator.JavaClassName;

public abstract class ComponentGenerator
{
  /**
   * Returns the class name of the component which will represent a parsed component
   * in the generated code. The component can either be a generated component, or a component
   * supplied in a component library.
   * 
   * @param generationContext the generation context for the class, not null.
   * 
   * @return the class name, not null.
   */
  public abstract JavaClassName getClassName(GenerationContext generationContext);

  /**
   * Returns the class name of the extension class containing user modifications to the generated class.
   * The component can either be a generated component, or a component supplied in a component library.
   * 
   * @param generationContext the generation context for the class, not null.
   * 
   * @return the class name of the extension class. 
   *         May return null if generateExtensionClass() returns null for the component.
   */
  public JavaClassName getExtensionClassName(GenerationContext generationContext)
  {
    return toExtensionClassName(generationContext.component.getId(), generationContext.getPackage());
  }
  
  /**
   * Returns the class name of the class by which this component can be referenced in generated code.
   * The component can either be a generated component, or a component supplied in a component library.
   * If an extension class containing user modifications to the generating class exists,
   * the extension class is returned, otherwise the base class is returned.
   * 
   * @param generationContext the generation context for the class, not null.
   * 
   * @return the class name of the extension class. 
   *         May return null if generateExtensionClass() returns null for the component.
   */
  public JavaClassName getReferencableClassName(GenerationContext generationContext)
  {
    if (generateExtensionClass(generationContext.component))
    {
      return getExtensionClassName(generationContext);
    }
    return getClassName(generationContext);
  }

  /**
   * Converts a component and package info into a java class name following the base class naming pattern.
   * The method first checks if an extension class is generated, and if yes and the configuration
   * says that base classes without extension should follow the extension naming pattern, it forwards
   * to the toExtensionClassName method.
   * If this is not the case, any loop part suffixes (starting with a colon :) and directory prefixes 
   * (separated by a slash) are removed from the component id, 
   * and the first character of the component id is converted to upper case.
   * This modified id is then prefixed by the baseClassPrefix and baseClassSuffix 
   * from the generator configuration.
   * 
   * @param generationContext the component and package info, not null.
   * 
   * @return the java class name for the component and package info, not null.
   */
  public JavaClassName toBaseClassName(GenerationContext generationContext)
  {
    if (!generateExtensionClass(generationContext.component) 
        && !Generator.getGeneratorConfiguration().isBaseClassWithoutExtensionNamedLikeBaseClasses())
    {
      return toExtensionClassName(generationContext.component.getId(), generationContext.getPackage());
    }
    String normalizedId = removeDirectoryPrefix(removeLoopPart(generationContext.component.getId()));
    String simpleName = Generator.getGeneratorConfiguration().getBaseClassPrefix() 
        + normalizedId.substring(0, 1).toUpperCase()
        + normalizedId.substring(1)
        + Generator.getGeneratorConfiguration().getBaseClassSuffix();
    return new JavaClassName(simpleName, generationContext.getPackage());
  }
  
  /**
   * Converts a component id and package info into a java class name following the extension class 
   * naming pattern.
   * Any loop part suffixes (starting with a colon :) and any direcory prefixes (separated by a slash /)
   * are removed from the component id, 
   * and the first character of the component id is converted to upper case.
   * This modified id is then prefixed by the extensionClassPrefix and extensionClassSuffix 
   * from the generator configuration.
   * 
   * @param componentKey the key of the component to generate the class name for, not null.
   * @param componentPackage the package for the component, not null
   * 
   * @return the java class name for the componentid and package info.
   */
  public JavaClassName toExtensionClassName(String componentId, String componentPackage)
  {
    String normalizedId = removeDirectoryPrefix(removeLoopPart(componentId));
    String simpleName = Generator.getGeneratorConfiguration().getExtensionClassPrefix() 
        + normalizedId.substring(0, 1).toUpperCase()
        + normalizedId.substring(1)
        + Generator.getGeneratorConfiguration().getExtensionClassSuffix();
    return new JavaClassName(simpleName, componentPackage);
  }
  
  /**
   * Returns whether an extension class should be generated for the component.
   * 
   * @param component the component to check, not null.
   * 
   * @return true if an extension class should be generated, false otherwise.
   */
  public abstract boolean generateExtensionClass(Component component);
  
  /**
   * Generates the component class representing the component.
   * 
   * @param generationContext the generation context for the class, not null.
   *        The StringBuilder and indent fields of the generationContext are not used.
   * 
   * @return the code for the generated class, or null if no class should be generated.
   */
  public abstract String generate(GenerationContext generationContext);
  
  /**
   * Generates the component extension class representing the component.
   * The extension class contains user-specific extensions to the generated class.
   * 
   * @param generationContext the generation context for the class, not null.
   *        The StringBuilder and indent fields of the generationContext are not used.
   * 
   * @return the code for the generated extension class, or null if no extension class should be generated.
   */
  public abstract String generateExtension(GenerationContext generationContext);

  /**
   * Generates an initializer which initializes this component if this component is constructed
   * and assigned to a field.
   * If this component does not need to be initialized, the empty string can be returned.
   * 
   * @param generationContext the generation context for the class, not null.
   * @param componentField the name of the field the component is assigned to, not null.
   * 
   * @return the initializer java code, of the empty string if no initializer code is needed; not null.
   */
  public abstract void generateInitializer(GenerationContext generationContext, String componentField);
  
  /**
   * Generates a field or a local variable from a component. 
   * If the component is not of the general Type Component, 
   * the component is constructed using the constructor with the parent argument, assigned to
   * the local variable and field.
   * Afterwards, the component is initialized using the initializer.
   * 
   * @param generationContext the generation context, not null.
   * @param modifier any modifier to the variable or field, e.g "public " or "final ".
   * @param fieldName the name of the field or variable
   * @param parentName the code how to access the parent of the component.
   */
  public void generateFieldOrVariableFromComponent(
      GenerationContext generationContext,
      String modifier,
      String fieldName,
      String parentName)
  {
    String indentString = getIndentString(generationContext.indent);
    StringBuilder stringBuilder = generationContext.stringBuilder;
    ComponentGenerator generator = Generator.getGenerator(generationContext.component);
    JavaClassName componentClassName = generator.getReferencableClassName(generationContext);
    if (componentClassName.isNameFor(Component.class))
    {
      stringBuilder.append(indentString).append(modifier)
        .append(componentClassName.getSimpleName())
        .append(" ").append(fieldName).append(";\n");
    }
    else
    {
      String id = generationContext.component.getId();
      if (id == null)
      {
        id = "null";
      }
      else
      {
        id ="\"" + id + "\"";
      }
      stringBuilder.append(indentString).append(modifier).append(componentClassName.getSimpleName())
          .append(" ").append(fieldName)
          .append(" = ApplicationBase.getApplication().postConstruct(\n")
          .append(indentString).append("    ").append("new ").append(componentClassName.getSimpleName())
          .append("(").append(id).append(", ").append(parentName).append("));\n");
    }
    generator.generateInitializer(generationContext, fieldName);
  }

  public void generateInitChildren(
      GenerationContext generationContext,
      String componentField)
  {
    List<? extends Component> children = generationContext.component.getChildren();
    if (children.isEmpty())
    {
      return;
    }
    String indentString = getIndentString(generationContext.indent);
    generationContext.stringBuilder.append(indentString).append("{\n");
    int counter = 1;
    for (Component child : children)
    {
      String fieldName;
      if (child.getId() != null)
      {
        fieldName = removeLoopPart(child.getId());
      }
      else
      {
        fieldName = getChildName(componentField, counter);
      }
      GenerationContext childContext = new GenerationContext(
          generationContext,
          child,
          generationContext.indent + 2);
      generateFieldOrVariableFromComponent(
          childContext, 
          "", 
          fieldName, 
          "this");
      generationContext.stringBuilder.append(indentString).append("  ")
          .append(componentField).append(".children.add(")
          .append(fieldName).append(");\n");

      counter++;
    }
    generationContext.stringBuilder.append(indentString).append("}\n");
  }
  
  /**
   * Generates an empty (except calling super) constructor for a component with an id and a parent argument.
   * 
   * @param className the unqualified class name of the class for which the constructor is generated.
   * @param defaultId the id to use in the generated component if the id parameter is null in the constructor.
   * @param toAppendTo the content to which the constructor code should be appended.
   */
  public void generateConstructorWithIdAndParent(String className, String defaultId, StringBuilder toAppendTo)
  {
    toAppendTo.append("\n  /**\n");
    toAppendTo.append("  * Constructor. \n");
    toAppendTo.append("  *\n");
    if (defaultId == null)
    {
      toAppendTo.append("  * @param id the id of this component, or null.\n");
    }
    else
    {
      toAppendTo.append("  * @param id the id of this component, or null to use the default id \"").append(defaultId).append("\".\n");
    }
    toAppendTo.append("  * @param parent the parent component.")
        .append(" Can be null if this is a standalone component (e.g. a page).\n");
    toAppendTo.append("  */\n");
    toAppendTo.append("  public ").append(className).append("(String id, Component parent)\n");
    toAppendTo.append("  {\n");
    if (defaultId == null)
    {
      toAppendTo.append("    super(id, parent);\n");
    }
    else
    {
      toAppendTo.append("    super(id == null ? \"").append(defaultId).append("\" : id, parent);\n");
    }
    toAppendTo.append("  }\n\n");
  }
  
  /**
   * Generates the class javadoc for component classes.
   * 
   * @param component the component for which the javadoc should be generated
   * @param toAppendTo the content to which the constructor code should be appended.
   * @param forExtension if the javadoc is generated for an extension class.
   */
  protected void generateClassJavadoc(Component component, StringBuilder toAppendTo, boolean forExtension)
  {
    toAppendTo.append("/**\n");
    toAppendTo.append(" * This class represents the HTML element with m:id ")
            .append(removeLoopPart(component.getId())).append(".\n");
    toAppendTo.append(" * Instances of this class are used whenever these elements are rendered\n");
    toAppendTo.append(" * or when form date from a page containing these elements is processed.\n");
    if (!forExtension)
    {
      ComponentGenerator generator = Generator.getGenerator(component);
      if (generator.generateExtensionClass(component))
      {
        toAppendTo.append(" *\n");
        toAppendTo.append(" * NOTE: This class should not be referenced; instead, the class\n");
        toAppendTo.append(" * ")
            .append(generator.getExtensionClassName(
                new GenerationContext(component, "dummy", null)).getSimpleName())
            .append(" should be used.").append("\n");
      }
    }
    toAppendTo.append(" **/\n");
  }

  /**
   * Returns a string in a form which can be used as string constant in a java source file.
   * The String is surrounded with double quotes, and backslashes, carriage returns and newlines
   * are replaced with the appropriate escape sequences.
   *  
   * @param string the string to be converted to a string constant, not null.
   * 
   * @return the string constant, not null.
   */
  public String asConstant(String string)
  {
    String result = string.replace("\\", "\\\\");
    result = result.replace("\r", "\\r");
    result = result.replace("\n", "\\n");
    result = result.replace("\"", "\\\"");
    return "\"" + result + "\"";
  }

  /**
   * Removes the loop part of an id, extracting the original id of the component.
   * It is assumed that all loop parts are appended as suffixes starting with a colon.
   * 
   * @param id the id to remove the loop part from, or null.
   * 
   * @return the id without the loop part, not null if <code>id</code> is not null.
   */
  public String removeLoopPart(String id)
  {
    if (id == null)
    {
      return null;
    }
    int indexOfColon = id.indexOf(':');
    if (indexOfColon == -1)
    {
      return id;
    }
    return id.substring(0, indexOfColon);
  }
  
  /**
   * Removes the directory prefix of an id, extracting the original id of the component.
   * It is assumed that all directory prefixes are prepended separated by a slash.
   * 
   * @param id the id to remove the directory prefix from, or null.
   * 
   * @return the id without the directory prefix, not null if <code>id</code> is not null.
   */
  public String removeDirectoryPrefix(String id)
  {
    if (id == null)
    {
      return null;
    }
    int lastIndexOfSlash = id.lastIndexOf('/');
    if (lastIndexOfSlash == -1)
    {
      return id;
    }
    return id.substring(lastIndexOfSlash + 1);
  }
  
  /**
   * Replaces all dots by underscores in an id.
   * 
   * @param id the id to replace dots in, or null.
   * 
   * @return the id with dots replaced by underscores, not null if <code>id</code> is not null.
   */
  public String replaceDots(String id)
  {
    if (id == null)
    {
      return null;
    }
    return id.replace('.', '_');
  }
  
  /**
   * Returns the indent String for an indent number.
   * 
   * @param indent the indent index.
   * 
   * @return the corresponding indent String, not null.
   */
  public String getIndentString(int indent)
  {
    StringBuilder result = new StringBuilder(indent);
    for (int i = 0; i < indent; ++i)
    {
      result.append(" ");
    }
    return result.toString();
  }
  
  public String getChildName(String baseName, int counter)
  {
    if (baseName == null || "".equals(baseName))
    {
      return "child" + counter;
    }
    return baseName + "Child" + counter;
  }
  

  public String getComponentFieldName(Component component, int componentCounter)
  {
    if (component != null && component.getId() != null)
    {
      return removeDirectoryPrefix(removeLoopPart(component.getId()));
    }
    else if (component instanceof RefComponent)
    {
      RefComponent refComponent = (RefComponent) component;
      return removeDirectoryPrefix(replaceDots(refComponent.refid));
    }
    return "component" + componentCounter;
  }

  public String getComponentFieldName(PartListComponent.ComponentPart part, int componentCounter)
  {
    if (part.component != null)
    {
      return getComponentFieldName(part.component, componentCounter);
    }
    return "snippet" + componentCounter;
  }

  /**
   * Generates code for the serialVersionUID constant for serializable classes.
   * 
   * @param fileContent the StringBuilder to which the code should be appended.
   */
  public void generateSerialVersionUid(StringBuilder toAppendTo)
  {
    // SerialversionUID
    toAppendTo.append("  /** Serial Version UID. */\n");
    toAppendTo.append("  private static final long serialVersionUID = 1L;\n\n");
  }
  
  /**
   * Generates the changeChildHtmlId method for a component. 
   * The method adds the id of the component as prefix if the id is not null.
   * 
   * @param fileContent the StringBuilder to which the code should be appended.
   */
  public void generateChangeChildHtmlId(StringBuilder fileContent)
  {
    fileContent.append("\n  /**\n");
    fileContent.append("   * If the id of this component and its parent is non null,\n");
    fileContent.append("   * the id of this component is added as a prefix to the passed id and returned;\n");
    fileContent.append("   * otherwise, the passed id is returned unchanged.\n");
    fileContent.append("   *\n");
    fileContent.append("   * @param child the child component from which this method is called, not used here.\n");
    fileContent.append("   * @param htmlId the id to prepend the id to, not null.\n");
    fileContent.append("   *\n");
    fileContent.append("   * @returned the prefixed id, not null.\n");
    fileContent.append("   */\n");
    fileContent.append("  @Override\n");
    fileContent.append("  public String changeChildHtmlId(Component child, String htmlId)\n");
    fileContent.append("  {\n");
    fileContent.append("    if (id != null && parent != null) // do not prefix page id (page component has no parent) \n");
    fileContent.append("    {\n");
    fileContent.append("      return id + \":\" + htmlId;\n");
    fileContent.append("    }\n");
    fileContent.append("    return htmlId;\n");
    fileContent.append("  }\n");
  }
  
  protected void generateVariableComponentField(
      PartListComponent.ComponentPart part,
      String componentField, 
      StringBuilder fileContent)
  {
    fileContent.append("  public ").append(SnippetComponent.class.getSimpleName())
        .append(" ").append(componentField)
        .append(" = (").append(SnippetComponent.class.getSimpleName())
        .append(") ApplicationBase.getApplication().postConstruct(\n")
        .append("      ")
        .append("new ").append(SnippetComponent.class.getSimpleName())
        .append("(null, ").append(asConstant(part.variableDefaultValue)).append(", this));\n\n");
  }

  /**
   * Generates getters and setters for variable parts.
   * 
   * @param part the ComponentPart containing variable name, not null.
   * @param snippetVariableName the name of the snippet constant or field, not null.
   * @param fileContent the StringBuilder to which the code should be appended.
   */
  public void generateVariableGetterSetter(
      PartListComponent.ComponentPart part,
      String snippetVariableName, 
      StringBuilder fileContent)
  {
    String getterSetterSuffix 
        = part.variableName.substring(0,1).toUpperCase() + part.variableName.substring(1);
    fileContent.append("\n  /**\n")
        .append("   * Returns the text content of the html text content variable ${")
        .append(part.variableName).append("}.\n")
        .append("   * XML entities in the stored text content are resolved.\n")
        .append("   *\n")
        .append("   * @return the text content of the variable ").append(part.variableName).append(".\n")
        .append("   **/\n")
        .append("  public String get").append(getterSetterSuffix).append("()\n")
        .append("  {\n")
        .append("    return resolveXmlEntities(").append(snippetVariableName).append(".text);\n")
        .append("  }\n\n")
        .append("  /**\n")
        .append("   * Sets the text content of the html text content variable ${")
        .append(part.variableName).append("}.\n")
        .append("   * XML special characters in the passed text content are escaped.\n")
        .append("   *\n")
        .append("   * @param text the new text content of the variable ")
        .append(part.variableName).append(", or null to not output anything.\n")
        .append("   *\n")
        .append("   * @return this component, never null.\n")
        .append("   **/\n")
        .append("  public Component set").append(getterSetterSuffix).append("(String text)\n")
        .append("  {\n")
        .append("    ").append(snippetVariableName).append(".text = escapeXmlText(text);\n")
        .append("    return this;\n")
        .append("  }\n");
  }

}
