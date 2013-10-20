package com.seitenbau.micgwaf.generator;

import javax.servlet.http.HttpServletRequest;

import com.seitenbau.micgwaf.component.Component;
import com.seitenbau.micgwaf.component.HtmlElementComponent;
import com.seitenbau.micgwaf.component.InputComponent;

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
    
    // replace inheritance
    rootContent = rootContent.replace(" extends " + HtmlElementComponent.class.getSimpleName(), 
        " extends " + InputComponent.class.getSimpleName());

    // remove last "}"
    rootContent = rootContent.substring(0,  rootContent.lastIndexOf("}"));
    
    // add import
    int indexOfImport = rootContent.indexOf("\nimport");
    fileContent.append(rootContent.substring(0, indexOfImport))
        .append("import ").append(HttpServletRequest.class.getName()).append(";\n")
        .append("import ").append(InputComponent.class.getName()).append(";\n")
        .append(rootContent.substring(indexOfImport));

    if (isButton(htmlElementCompont))
    {
      fileContent.append("\n\n  @Override\n");
      fileContent.append("  public void processRequest(HttpServletRequest request)\n");
      fileContent.append("  {\n");
      fileContent.append("    super.processRequest(request);\n");
      fileContent.append("    if (submitted)\n");
      fileContent.append("    {\n");
      fileContent.append("      onSubmit();\n");
      fileContent.append("    }\n");
      fileContent.append("  }\n\n");
      fileContent.append("  public void onSubmit()\n");
      fileContent.append("  {\n");
      fileContent.append("  }\n");
    }
    fileContent.append("}\n");
    
    return fileContent.toString();
  }

  private boolean isButton(HtmlElementComponent htmlElementCompont)
  {
    if ("button".equals(htmlElementCompont.elementName) 
        || ("input".equals(htmlElementCompont.elementName) 
            && "submit".equals(htmlElementCompont.attributes.get("type"))))
    {
      return true;
    }
    return false;
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
