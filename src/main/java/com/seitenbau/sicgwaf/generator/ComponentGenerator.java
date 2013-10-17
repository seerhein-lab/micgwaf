package com.seitenbau.sicgwaf.generator;

import java.util.List;
import java.util.Map;

import com.seitenbau.sicgwaf.component.Component;

public abstract class ComponentGenerator
{
  public abstract String getClassName(
      String componentName,
      Component component,
      String targetPackage);

  public abstract String getExtensionClassName(
      String componentName,
      Component component,
      String targetPackage);
  
  public String getReferencableClassName(
      String componentName,
      Component component,
      String targetPackage)
  {
    String result = getExtensionClassName(componentName, component, targetPackage);
    if (result != null)
    {
      return result;
    }
    return getClassName(componentName, component, targetPackage);
  }

  public abstract void generate(
      String componentName,
      Component component,
      String targetPackage,
      Map<String, String> filesToWrite);
  
  public abstract String generateInitializer(
      String componentField,
      Component component,
      String targetPackage,
      int indent,
      Map<String, String> filesToWrite);
  
  public abstract void generateExtension(
      String componentName,
      Component component,
      String targetPackage,
      Map<String, String> filesToWrite);
  
  /**
   * 
   * @param componentName
   * @param component
   * @param targetPackage
   * @return null if inline code should be created for the component,
   *         the name of the component if a new component class should be created. 
   */
  public abstract String generateNewComponent(
      String componentName,
      Component component,
      String targetPackage);
  
  /**
   * Retuns the name of a new extension component to generate, or null not to generate an extension class
   * (beware: this also disables generating extension classes for children), or "" to generate extension
   * classes not for this component but for the children.
   * 
   * @param componentName
   * @param component
   * @param targetPackage
   * @return null if inline code should be created for the component,
   *         the name of the component if a new component class should be created. 
   */
  public abstract String generateNewExtensionComponent(
      String componentName,
      Component component,
      String targetPackage);
  
  public String toJavaName(String componentName)
  {
    return componentName.substring(0, 1).toUpperCase()
        + componentName.substring(1);
  }
  
  public String asConstant(String string)
  {
    String result = string.replace("\\", "\\\\");
    result = result.replace("\r", "\\r");
    result = result.replace("\n", "\\n");
    result = result.replace("\"", "\\\"");
    return "\"" + result + "\"";
  }

  public void generateFieldFromComponent(
      Component component,
      String targetPackage, 
      StringBuilder result,
      String modifier,
      String fieldName,
      int indent,
      Map<String, String> filesToWrite)
  {
    String indentString = getIndentString(indent);
    ComponentGenerator generator = Generator.getGenerator(component);
    String newComponentName = generator.generateNewComponent(fieldName, component, targetPackage);
    if (newComponentName == null)
    {
      String className = generator.getReferencableClassName(null, component, targetPackage);
      result.append(indentString).append(className).append(" ").append(fieldName)
          .append(" = new ").append(className).append("(this);\n");
      result.append(generator.generateInitializer(fieldName, component, targetPackage, indent, filesToWrite));
    }
    else
    {
      generator.generate(component.id, component, targetPackage, filesToWrite);
      String componentClassName = generator.getReferencableClassName(component.id, component, targetPackage);
      result.append(indentString).append(modifier).append(componentClassName).append(" ").append(fieldName)
          .append(" = new ").append(componentClassName).append("(this);\n\n");
    }
  }


  public void generateInitChildren(
      Component component, 
      String targetPackage,
      StringBuilder result,
      String componentField,
      int indent,
      Map<String, String> filesToWrite)
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
      if (child.id != null)
      {
        fieldName = child.id;
      }
      else
      {
        fieldName = getChildName(componentField, counter);
      }
      generateFieldFromComponent(child, targetPackage, result, "public", fieldName, indent + 2, filesToWrite);
      result.append(indentString).append("  ").append(componentField).append(".children.add(")
          .append(fieldName).append(");\n");

      counter++;
    }
    result.append(indentString).append("}\n");
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
    String componentField;
    if (component != null && component.id != null)
    {
      componentField = component.id;
    }
    else
    {
      componentField= "component" + componentCounter;
    }
    return componentField;
  }

}
