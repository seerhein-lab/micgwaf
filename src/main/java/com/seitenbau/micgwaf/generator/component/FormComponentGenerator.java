package com.seitenbau.micgwaf.generator.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.seitenbau.micgwaf.component.ChildListComponent;
import com.seitenbau.micgwaf.component.Component;
import com.seitenbau.micgwaf.component.FormComponent;
import com.seitenbau.micgwaf.component.HtmlElementComponent;
import com.seitenbau.micgwaf.component.InputComponent;
import com.seitenbau.micgwaf.generator.Generator;
import com.seitenbau.micgwaf.generator.JavaClassName;

public class FormComponentGenerator extends HtmlElementComponentGenerator
{
  @Override
  public JavaClassName getClassName(
      Component component,
      String targetPackage)
  {
    return toBaseClassName(component, targetPackage);
  }
  
  @Override
  public String generate(
      Component component,
      String targetPackage)
  {
    String rootContent = super.generate(component, targetPackage);

    StringBuilder fileContent = new StringBuilder();
    
    // replace inheritance
    rootContent = rootContent.replace(" extends " + HtmlElementComponent.class.getSimpleName(), 
        " extends " + FormComponent.class.getSimpleName());

    // remove last "}"
    rootContent = rootContent.substring(0,  rootContent.lastIndexOf("}") -1);
    
    // add import
    int indexOfImport = rootContent.indexOf("\nimport");
    fileContent.append(rootContent.substring(0, indexOfImport))
        .append("\nimport ").append(HttpServletRequest.class.getName()).append(";\n")
        .append("import ").append(FormComponent.class.getName()).append(";")
        .append(rootContent.substring(indexOfImport));

    List<InputComponent> buttons = new ArrayList<>();
    getButtons(component, buttons);
    Map<InputComponent, Component> buttonsInLoops = new HashMap<>();
    getButtonsInLoops(component, buttonsInLoops);
    List<InputComponent> inputs = new ArrayList<>();
    getInputs(component, inputs);
    
    for (InputComponent button : buttons)
    {
      fileContent.append("\n\n");
      fileContent.append("  public Component ").append(removeLoopPart(button.getId())).append("Pressed()\n");
      fileContent.append("  {\n");
      fileContent.append("    return null;\n");
      fileContent.append("  }\n");
    }
    
    for (Map.Entry<InputComponent, Component> buttonEntry : buttonsInLoops.entrySet())
    {
      InputComponent button = buttonEntry.getKey();
      Component loopComponent = buttonEntry.getValue();
      fileContent.append("\n\n");
      ComponentGenerator loopComponentGenerator = Generator.getGenerator(loopComponent);
      JavaClassName loopComponentReferencableClassName 
          = loopComponentGenerator.getReferencableClassName(loopComponent, targetPackage);
      fileContent.append("  public Component ").append(removeLoopPart(button.getId())).append("Pressed(")
          .append(loopComponentReferencableClassName.getSimpleName()).append(" ")
          .append(removeLoopPart(loopComponent.getId())).append(")\n");
      fileContent.append("  {\n");
      fileContent.append("    return null;\n");
      fileContent.append("  }\n");
    }
    
    for (InputComponent input : inputs)
    {
      fileContent.append("\n\n");
      String normalizedInputId = removeLoopPart(input.getId());
      fileContent.append("  public String get").append(normalizedInputId.substring(0, 1).toUpperCase())
          .append(normalizedInputId.substring(1)).append("()\n");
      fileContent.append("  {\n");
      String pathToComponent = normalizedInputId;
      Component parent = input.getParent();
      while (parent != component)
      {
        if (parent.getId() != null)
        {
          pathToComponent = removeLoopPart(parent.getId()) + "." + pathToComponent;
        }
        parent = parent.getParent();
      }
      fileContent.append("    return ").append(pathToComponent).append(".submittedValue;\n");
      fileContent.append("  }\n");
    }
    
    fileContent.append("\n");
    fileContent.append("  public void onSubmit()\n");
    fileContent.append("  {\n");
    fileContent.append("  }\n\n");
    fileContent.append("  @Override\n");
    fileContent.append("  public Component processRequest(HttpServletRequest request)\n");
    fileContent.append("  {\n");
    fileContent.append("    Component result = super.processRequest(request);\n");
    fileContent.append("    if (submitted)\n");
    fileContent.append("    {\n");
    fileContent.append("      onSubmit();\n");
    for (InputComponent button : buttons)
    {
      fileContent.append("      if (").append(removeLoopPart(button.getId())).append(".submitted)\n");
      fileContent.append("      {\n");
      fileContent.append("        result = ").append(removeLoopPart(button.getId())).append("Pressed();\n");
      fileContent.append("      }\n");
    }
    for (Map.Entry<InputComponent, Component> buttonEntry : buttonsInLoops.entrySet())
    {
      InputComponent button = buttonEntry.getKey();
      Component loopComponent = buttonEntry.getValue();
      ComponentGenerator loopComponentGenerator = Generator.getGenerator(loopComponent);
      JavaClassName loopComponentReferencableClassName 
          = loopComponentGenerator.getReferencableClassName(loopComponent, targetPackage);
      fileContent.append("      for (").append(loopComponentReferencableClassName.getSimpleName())
          .append(" loopComponent : ").append(removeLoopPart(loopComponent.getParent().getId()))
          .append(".children.copy())\n");
      fileContent.append("      {\n");
      fileContent.append("        if (loopComponent.").append(removeLoopPart(button.getId())).append(".submitted)\n");
      fileContent.append("        {\n");
      fileContent.append("          result = ").append(removeLoopPart(button.getId())).append("Pressed(loopComponent);\n");
      fileContent.append("        }\n");
      fileContent.append("      }\n");
    }
    fileContent.append("    }\n");
    fileContent.append("    return result;\n");
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
    generateSerialVersionUid(fileContent);
    generateComponentConstructorWithParent(extensionClassName, fileContent);
    fileContent.append("}\n");
    return fileContent.toString();
  }
  
  public void getButtons(Component component, List<InputComponent> buttons)
  {
    if (component instanceof InputComponent)
    {
      InputComponent inputComponent = (InputComponent) component;
      if (inputComponent.isButton())
      {
        buttons.add(inputComponent);
      }
    }
    if (component instanceof ChildListComponent)
    {
      // do not consider buttons in loops
      return;
    }
    for (Component child : component.getChildren())
    {
      getButtons(child, buttons);
    }
  }

  public void getButtonsInLoops(Component component, Map<InputComponent, Component> buttons)
  {
    if (component instanceof InputComponent)
    {
      InputComponent inputComponent = (InputComponent) component;
      ChildListComponent<?> childListParent = component.getAncestor(ChildListComponent.class);
      if (childListParent == null)
      {
        return;
      }
      if (inputComponent.isButton())
      {
        buttons.put(inputComponent, childListParent.getChildren().get(0));
      }
    }
    for (Component child : component.getChildren())
    {
      getButtonsInLoops(child, buttons);
    }
  }
  
  public void getInputs(Component component, List<InputComponent> inputs)
  {
    if (component instanceof InputComponent)
    {
      InputComponent inputComponent = (InputComponent) component;
      if (!inputComponent.isButton())
      {
        inputs.add(inputComponent);
      }
    }
    if (component instanceof ChildListComponent)
    {
      // do not consider buttons in loops
      return;
    }
    for (Component child : component.getChildren())
    {
      getInputs(child, inputs);
    }
  }
}
