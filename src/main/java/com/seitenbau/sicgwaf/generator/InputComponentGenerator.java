package com.seitenbau.sicgwaf.generator;

import javax.servlet.http.HttpServletRequest;

import com.seitenbau.sicgwaf.component.Component;
import com.seitenbau.sicgwaf.component.HtmlElementComponent;

public class InputComponentGenerator extends HtmlElementComponentGenerator
{
  @Override
  public JavaClassName getClassName(
      Component component,
      String targetPackage)
  {
    return toJavaClassName(component.id, targetPackage);
  }
  
  @Override
  public String generate(
      Component component,
      String targetPackage)
  {
    String rootContent = super.generate(component, targetPackage);

    HtmlElementComponent htmlElementCompont = (HtmlElementComponent) component;
    StringBuilder fileContent = new StringBuilder();
    
    // remove last "}"
    rootContent = rootContent.substring(0,  rootContent.lastIndexOf("}") -1);
    
    // add import
    int indexOfImport = rootContent.indexOf("\nimport");
    fileContent.append(rootContent.substring(0, indexOfImport)).append("import ")
        .append(HttpServletRequest.class.getName()).append(";\n").append(rootContent.substring(indexOfImport));

    fileContent.append("\n  @Override\n");
    fileContent.append("  public void processRequest(HttpServletRequest request)\n");
    fileContent.append("  {\n");
    fileContent.append("    if (request.getParameter(\"" + htmlElementCompont.attributes.get("name") + "\") != null)\n");
    fileContent.append("    {\n");
    fileContent.append("      onSubmit();\n");
    fileContent.append("    }\n");
    fileContent.append("  }\n\n");
    fileContent.append("  public void onSubmit()\n");
    fileContent.append("  {\n");
    fileContent.append("  }\n");
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
    fileContent.append("public class ").append(extensionClassName)
        .append(" extends ").append(className)
        .append("\n");
    fileContent.append("{\n");
    fileContent.append("  public " + extensionClassName + "(Component parent)\n");
    fileContent.append("  {\n");
    fileContent.append("    super(parent);\n");
    fileContent.append("  }\n");
    fileContent.append("}\n");
    return fileContent.toString();
  }
}
