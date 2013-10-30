package com.seitenbau.micgwaf.generator;

import java.util.List;

import com.seitenbau.micgwaf.component.Component;
import com.seitenbau.micgwaf.component.RefComponent;
import com.seitenbau.micgwaf.component.SnippetListComponent;

public abstract class ComponentGenerator
{
  /**
   * Returns the class name of the component which will represent the parsed component
   * in the generated code. This can either be a generated component, or a component
   * supplied in some Library.
   * Also there may be an extension class containing user modifications to the generating class,
   * in which case the extension class should be used in references instead of this class.
   * 
   * @param component the component to be represented, not null.
   * @param targetPackage the base target package for the generated classes, not null.
   * 
   * @return the class name, not null.
   */
  public abstract JavaClassName getClassName(
      Component component,
      String targetPackage);

  /**
   * Returns the class name of the  extension class containing user modifications to the generating class.
   * 
   * @param component the component to be represented, not null.
   * @param targetPackage the base target package for the generated classes, not null.
   * 
   * @return the class name of the extension class. 
   *         May return null if generateExtensionClass() returns null for the component.
   */
  public JavaClassName getExtensionClassName(
      Component component,
      String targetPackage)
  {
    String className = getClassName(component, targetPackage).getSimpleName();
    return new JavaClassName(className + "Extension", targetPackage);
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
   * Returns the class name of the class by which this component can be referenced in generated code.
   * 
   * @param component the component to be represented, not null.
   * @param targetPackage the base target package for the generated classes, not null.
   * 
   * @return the class name of the extension class. 
   *         May return null if generateExtensionClass() returns null for the component.
   */
  public JavaClassName getReferencableClassName(
      Component component,
      String targetPackage)
  {
    if (generateExtensionClass(component))
    {
      return getExtensionClassName(component, targetPackage);
    }
    return getClassName(component, targetPackage);
  }

  /**
   * Generates the component class representing the component.
   * 
   * @param component the component for which code should be generated, not null.
   * @param targetPackage the base target package for the generated classes, not null.
   * 
   * @return the code for the generated class, or null if no class should be generated.
   */
  public abstract String generate(
      Component component,
      String targetPackage);
  
  /**
   * Generates the component extension class representing the component.
   * The extension class contains user-specific extensions to the generated class.
   * 
   * @param component the component for which code should be generated, not null.
   * @param targetPackage the base target package for the generated classes, not null.
   * 
   * @return the code for the generated extension class, or null if no extension class should be generated.
   */
  public abstract String generateExtension(
      Component component,
      String targetPackage);

  /**
   * Generates an initializer which initializes this component if this component is constructed
   * and assigned to a field.
   * If this component does not need to be initialized, the empty string can be returned.
   * 
   * @param componentField the name of the field the component is assigned to, not null.
   * @param component the component to generate the initializer for, not null.
   * @param targetPackage the target package of the component, not null.
   * @param indent how many spaces to indent, not null.
   * 
   * @return the initializer java code, of the empty string if no initilaizer code is needed; not null.
   */
  public abstract String generateInitializer(
      String componentField,
      Component component,
      String targetPackage,
      int indent);
  
  /**
   * Converts a component id and a package into a java class name.
   * Any loop part suffies (starting with a colon :) are removed, 
   * and the first character is converted to upper case.
   * 
   * @param componentId the component id, not null.
   * @param packageName TODO
   * 
   * @return the java class name for the componentid/package pair.
   */
  public JavaClassName toJavaClassName(String componentId, String packageName)
  {
    String normalizedId = removeLoopPart(componentId);
    String simpleName = normalizedId.substring(0, 1).toUpperCase()
        + normalizedId.substring(1);
    return new JavaClassName(simpleName, packageName);
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
   * Generates a field or a local variable from a component. 
   * The component is constructed using the constructor with the parent argument, assigned to
   * the local variable and field, and afterwards is initialized using the initializer.
   * 
   * @param component The component to create the package name for, not null.
   * @param targetPackage TODO
   * @param toAppendTo the String builder to which the generated code should be appended, not null.
   * @param modifier any modifier to the variable or field, e.g "public " or "final ".
   * @param fieldName the name of the field or variable
   * @param indent how many spaces the generated code should be indented.
   */
  public void generateFieldOrVariableFromComponent(
      Component component,
      String targetPackage, 
      StringBuilder toAppendTo,
      String modifier,
      String fieldName,
      int indent)
  {
    String indentString = getIndentString(indent);
    ComponentGenerator generator = Generator.getGenerator(component);
    JavaClassName componentClassName = generator.getReferencableClassName(component, targetPackage);
    toAppendTo.append(indentString).append(modifier).append(componentClassName.getSimpleName())
        .append(" ").append(fieldName)
        .append(" = new ").append(componentClassName.getSimpleName()).append("(this);\n\n");
    toAppendTo.append(generator.generateInitializer(fieldName, component, targetPackage, indent));
  }


  public void generateInitChildren(
      Component component, 
      String targetPackage,
      StringBuilder result,
      String componentField,
      int indent)
  {
    List<? extends Component> children = component.getChildren();
    if (children.isEmpty())
    {
      return;
    }
    String indentString = getIndentString(indent);
    result.append(indentString).append("{\n");
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
      generateFieldOrVariableFromComponent(child, targetPackage, result, "", fieldName, indent + 2);
      result.append(indentString).append("  ").append(componentField).append(".children.add(")
          .append(fieldName).append(");\n");

      counter++;
    }
    result.append(indentString).append("}\n");
  }
  
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
      return removeLoopPart(component.getId());
    }
    else if (component instanceof RefComponent)
    {
      RefComponent refComponent = (RefComponent) component;
      return refComponent.refid;
    }
    return  "component" + componentCounter;
  }

  public String getComponentFieldName(SnippetListComponent.ComponentPart part, int componentCounter)
  {
    if (part.component != null)
    {
      return getComponentFieldName(part.component, componentCounter);
    }
    return  "snippet" + componentCounter;
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
}
