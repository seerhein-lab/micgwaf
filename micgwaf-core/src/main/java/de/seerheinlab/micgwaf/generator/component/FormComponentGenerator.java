package de.seerheinlab.micgwaf.generator.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import de.seerheinlab.micgwaf.component.ChildListComponent;
import de.seerheinlab.micgwaf.component.Component;
import de.seerheinlab.micgwaf.component.FormComponent;
import de.seerheinlab.micgwaf.component.HtmlElementComponent;
import de.seerheinlab.micgwaf.component.InputComponent;
import de.seerheinlab.micgwaf.component.RefComponent;
import de.seerheinlab.micgwaf.generator.Generator;
import de.seerheinlab.micgwaf.generator.JavaClassName;

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
    rootContent = rootContent.replace(
        " extends " + HtmlElementComponent.class.getSimpleName(), 
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
    List<ComponentWithPath> inputs = new ArrayList<>();
    List<Component> componentPath = new ArrayList<>();
    getInputs(component, inputs, componentPath);
    
    for (InputComponent button : buttons)
    {
      generateButtonHookMethod(fileContent, button, false);
    }
    
    for (Map.Entry<InputComponent, Component> buttonEntry : buttonsInLoops.entrySet())
    {
      InputComponent button = buttonEntry.getKey();
      Component loopComponent = buttonEntry.getValue();
      generateButtonInLoopHookMethod(targetPackage, fileContent, button, loopComponent, false);
    }
    
    for (ComponentWithPath input : inputs)
    {
      generateSubmittedValueGettersAndSetters(component, fileContent, input);
    }
    
    fileContent.append("\n  /**\n");
    fileContent.append("   * Hook method which is called when the form was submitted.\n");
    fileContent.append("   *\n");
    fileContent.append("   * @return the page to be rendered.\n");
    fileContent.append("   *         If no component or called hook method returns a not-null result,")
        .append(" the current page\n");
    fileContent.append("   *         in the current state will be rendered.\n");
    fileContent.append("   *         If more than one component returns a not-null result,")
        .append(" the last not-null result will be used.\n");
    fileContent.append("   *         If another hook method of this component returns a not-null result,\n");
    fileContent.append("   *         the result of this method will be discarded in favor")
        .append(" of the other result.\n");
    fileContent.append("   */\n");
    fileContent.append("  public Component onSubmit()\n");
    fileContent.append("  {\n");
    fileContent.append("    return null;\n");
    fileContent.append("  }\n\n");
    fileContent.append("  @Override\n");
    fileContent.append("  public Component processRequest(HttpServletRequest request)\n");
    fileContent.append("  {\n");
    fileContent.append("    Component result = super.processRequest(request);\n");
    fileContent.append("    if (submitted)\n");
    fileContent.append("    {\n");
    fileContent.append("      Component potentialResult = onSubmit();\n");
    for (InputComponent button : buttons)
    {
      fileContent.append("      if (").append(removeLoopPart(button.getId())).append(".submitted)\n");
      fileContent.append("      {\n");
      fileContent.append("        potentialResult = ").append(removeLoopPart(button.getId())).append("Pressed();\n");
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
          .append(".children)\n");
      fileContent.append("      {\n");
      fileContent.append("        if (loopComponent.").append(removeLoopPart(button.getId())).append(".submitted)\n");
      fileContent.append("        {\n");
      fileContent.append("          potentialResult = ").append(removeLoopPart(button.getId())).append("Pressed(loopComponent);\n");
      fileContent.append("        }\n");
      fileContent.append("      }\n");
    }
    fileContent.append("      if (potentialResult != null)\n");
    fileContent.append("      {\n");
    fileContent.append("        result = potentialResult;\n");
    fileContent.append("      }\n");
    fileContent.append("    }\n");
    fileContent.append("    return result;\n");
    fileContent.append("  }\n");
    fileContent.append("}\n");
    
    return fileContent.toString();
  }

  private void generateSubmittedValueGettersAndSetters(
      Component component,
      StringBuilder fileContent,
      ComponentWithPath input)
  {
    String normalizedInputId = removeLoopPart(input.component.getId());
    StringBuilder pathToComponent = new StringBuilder();
    StringBuilder getterSetterSuffix = new StringBuilder();
    StringBuilder refComponentPath = new StringBuilder(); 
    for (Component pathElement : input.path)
    {
      if (pathElement.getId() != null 
          && pathElement.getParent() != null) // do not output ids of component root referenced by RefComponents
      {
        pathToComponent.append(removeLoopPart(pathElement.getId())).append(".");
      }
      if (pathElement instanceof RefComponent && pathElement.getId() != null)
      {
        getterSetterSuffix.append(pathElement.getId().substring(0, 1).toUpperCase())
            .append(pathElement.getId().substring(1));
        if (refComponentPath.length() > 0)
        {
          refComponentPath.append(" -> ");
        }
        refComponentPath.append(pathElement.getId());
      }
    }
    pathToComponent.append(normalizedInputId);
    getterSetterSuffix.append(normalizedInputId.substring(0, 1).toUpperCase())
        .append(normalizedInputId.substring(1));
    fileContent.append("\n  /**\n");
    fileContent.append("   * Convenience method to retrieve the submitted value of the ")
        .append(normalizedInputId).append(" component");
    if (refComponentPath.length() > 0)
    {
      fileContent.append(" in the ").append(refComponentPath).append(" component");
    }
    fileContent.append(".\n");
    fileContent.append("   *\n");
    fileContent.append("   * @return the submitted value of the ").append(normalizedInputId)
        .append(" component.\n");;
    fileContent.append("   */\n");
    fileContent.append("  public String get").append(getterSetterSuffix).append("()\n");
    fileContent.append("  {\n");
    fileContent.append("    return ").append(pathToComponent).append(".submittedValue;\n");
    fileContent.append("  }\n");

    fileContent.append("\n  /**\n");
    fileContent.append("   * Convenience method to set the value of the ")
        .append(normalizedInputId).append(" component");
    if (refComponentPath.length() > 0)
    {
      fileContent.append(" in the ").append(refComponentPath).append(" component");
    }
    fileContent.append(".\n");
    fileContent.append("   *\n");
    fileContent.append("   * @param value the value of the ").append(normalizedInputId)
        .append(" component.\n");;
    fileContent.append("   */\n");
    fileContent.append("  public void set").append(getterSetterSuffix).append("(String value)\n");
    fileContent.append("  {\n");
    fileContent.append("    ").append(pathToComponent).append(".setValue(value);\n");
    fileContent.append("  }\n");
  }

  private void generateButtonInLoopHookMethod(
      String targetPackage,
      StringBuilder fileContent,
      InputComponent button,
      Component loopComponent,
      boolean overrideMethod)
  {
    String bareButtonId = removeLoopPart(button.getId());
    String bareLoopComponentId = removeLoopPart(loopComponent.getId());
    ComponentGenerator loopComponentGenerator = Generator.getGenerator(loopComponent);
    JavaClassName loopComponentReferencableClassName 
        = loopComponentGenerator.getReferencableClassName(loopComponent, targetPackage);
    fileContent.append("\n  /**\n");
    fileContent.append("   * Hook method which is called when the button ")
        .append(bareButtonId).append(" was pressed.\n");
    fileContent.append("   *\n");
    fileContent.append("   * @param ").append(bareLoopComponentId).append(" The component in the list of ")
        .append(loopComponentReferencableClassName.getSimpleName())
        .append(" Components\n");
    fileContent.append("   *        to which this button belongs.\n");
    fileContent.append("   *\n");
    fileContent.append("   * @return the page to be rendered.\n");
    fileContent.append("   *         If no component returns a not-null result, the current page")
        .append(" in the current state\n");
    fileContent.append("   *         will be rendered.\n");
    fileContent.append("   *         If more than one component returns a not-null result, the last")
        .append(" not-null result will be used.\n");
    fileContent.append("   */\n");
    if (overrideMethod)
    {
      fileContent.append("  @Override\n");
    }
    fileContent.append("  public Component ").append(bareButtonId).append("Pressed(")
        .append(loopComponentReferencableClassName.getSimpleName()).append(" ")
        .append(bareLoopComponentId).append(")\n");
    fileContent.append("  {\n");
    if (overrideMethod)
    {
      fileContent.append("    return super.").append(bareButtonId).append("Pressed(")
          .append(bareLoopComponentId).append(");\n");
    }
    else
    {
      fileContent.append("    return null;\n");
    }
    fileContent.append("  }\n");
  }

  private void generateButtonHookMethod(
      StringBuilder fileContent,
      InputComponent button,
      boolean overrideMethod)
  {
    String bareComponentId = removeLoopPart(button.getId());
    fileContent.append("\n  /**\n");
    fileContent.append("   * Hook method which is called when the button ")
        .append(bareComponentId).append(" was pressed.\n");
    fileContent.append("   *\n");
    fileContent.append("   * @return the page to be rendered.\n");
    fileContent.append("   *         If no component or called hook method returns a not-null result,")
        .append(" the current page\n");
    fileContent.append("   *         in the current state will be rendered.\n");
    fileContent.append("   *         If more than one component or called hook method")
        .append(" returns a not-null result,\n");
    fileContent.append("   *         the last not-null result will be used.\n");
    fileContent.append("   */\n");
    if (overrideMethod)
    {
      fileContent.append("  @Override\n");
    }
    fileContent.append("  public Component ").append(bareComponentId).append("Pressed()\n");
    fileContent.append("  {\n");
    if (overrideMethod)
    {
      fileContent.append("    return super.").append(bareComponentId).append("Pressed();\n");
    }
    else
    {
      fileContent.append("    return null;\n");
    }
    fileContent.append("  }\n");
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
    generateConstructorWithIdAndParent(extensionClassName, null, fileContent);
    
    List<InputComponent> buttons = new ArrayList<>();
    getButtons(component, buttons);
    Map<InputComponent, Component> buttonsInLoops = new HashMap<>();
    getButtonsInLoops(component, buttonsInLoops);

    for (InputComponent button : buttons)
    {
      generateButtonHookMethod(fileContent, button, true);
    }
    
    for (Map.Entry<InputComponent, Component> buttonEntry : buttonsInLoops.entrySet())
    {
      InputComponent button = buttonEntry.getKey();
      Component loopComponent = buttonEntry.getValue();
      generateButtonInLoopHookMethod(targetPackage, fileContent, button, loopComponent, true);
    }
    
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
  
  public void getInputs(Component component, List<ComponentWithPath> inputs, List<Component> componentPath)
  {
    if (component instanceof InputComponent)
    {
      InputComponent inputComponent = (InputComponent) component;
      if (!inputComponent.isButton())
      {
        // use subList because path contains base component, which we do not want to store as path member 
        inputs.add(new ComponentWithPath(
            inputComponent, 
            new ArrayList<>(componentPath.subList(1, componentPath.size() - 1))));
      }
    }
    if (component instanceof ChildListComponent)
    {
      // do not consider inputs in loops
      return;
    }
    componentPath.add(component);
    if (component instanceof RefComponent)
    {
      RefComponent refComponent = (RefComponent) component;
      if (refComponent.referencedComponent != null)
      {
        getInputs(refComponent.referencedComponent, inputs, componentPath);
      }
      else
      {
        throw new IllegalStateException("Component reference " + refComponent + " is not resolved");
      }
    }
    for (Component child : component.getChildren())
    {
      getInputs(child, inputs, componentPath);
    }
    componentPath.remove(componentPath.size() - 1);
  }
  
  private static class ComponentWithPath
  {
    public Component component;
    
    public List<Component> path;
    
    public ComponentWithPath(Component component, List<Component> path)
    {
      this.component = component;
      this.path = path;
    }
  }
}
