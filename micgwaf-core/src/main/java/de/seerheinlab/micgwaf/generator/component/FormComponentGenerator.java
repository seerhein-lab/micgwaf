package de.seerheinlab.micgwaf.generator.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import de.seerheinlab.micgwaf.component.ChildListComponent;
import de.seerheinlab.micgwaf.component.Component;
import de.seerheinlab.micgwaf.component.FormComponent;
import de.seerheinlab.micgwaf.component.InputComponent;
import de.seerheinlab.micgwaf.component.RefComponent;
import de.seerheinlab.micgwaf.generator.GeneratedClass;
import de.seerheinlab.micgwaf.generator.Generator;
import de.seerheinlab.micgwaf.generator.JavaClassName;

public class FormComponentGenerator extends HtmlElementComponentGenerator
{
  @Override
  public JavaClassName getClassName(GenerationContext generationContext)
  {
    return toBaseClassName(generationContext);
  }

  @Override
  public void generate(GenerationContext generationContext)
  {
    super.generate(generationContext);
    GeneratedClass result = generationContext.generatedClass;

    // replace inheritance
    result.classDefinition = new StringBuilder();
    generateClassDefinition(generationContext, FormComponent.class);

    // add import
    result.imports.add(HttpServletRequest.class.getName());
    result.imports.add(FormComponent.class.getName());

    // add methods
    List<InputComponent> buttons = new ArrayList<>();
    getButtons(generationContext.component, buttons);
    Map<InputComponent, Component> buttonsInLoops = new HashMap<>();
    getButtonsInLoops(generationContext.component, buttonsInLoops);
    List<ComponentWithPath> inputs = new ArrayList<>();
    List<Component> componentPath = new ArrayList<>();
    getInputs(generationContext.component, inputs, componentPath);

    for (InputComponent button : buttons)
    {
      generateButtonHookMethod(result, button, false);
    }

    for (Map.Entry<InputComponent, Component> buttonEntry : buttonsInLoops.entrySet())
    {
      InputComponent button = buttonEntry.getKey();
      Component loopComponent = buttonEntry.getValue();
      generateButtonInLoopHookMethod(generationContext, button, loopComponent, false);
    }

    for (ComponentWithPath input : inputs)
    {
      generateSubmittedValueGettersAndSetters(generationContext.component, result, input);
    }

    result.classBody.append("\n  /**\n")
        .append("   * Hook method which is called when the form was submitted.\n")
        .append("   *\n")
        .append("   * @return the page to be rendered.\n")
        .append("   *         If no component or called hook method returns a not-null result,")
            .append(" the current page\n")
        .append("   *         in the current state will be rendered.\n")
        .append("   *         If more than one component returns a not-null result,")
            .append(" the last not-null result will be used.\n")
        .append("   *         If another hook method of this component returns a not-null result,\n")
        .append("   *         the result of this method will be discarded in favor")
            .append(" of the other result.\n")
        .append("   */\n")
        .append("  public Component onSubmit()\n")
        .append("  {\n")
        .append("    return null;\n")
        .append("  }\n\n")
        .append("  @Override\n")
        .append("  public Component processRequest(HttpServletRequest request)\n")
        .append("  {\n")
        .append("    Component result = super.processRequest(request);\n")
        .append("    if (submitted)\n")
        .append("    {\n")
        .append("      Component potentialResult = onSubmit();\n");
    for (InputComponent button : buttons)
    {
      result.classBody.append("      if (").append(removeLoopPart(button.getId())).append(".submitted)\n")
          .append("      {\n")
          .append("        potentialResult = ").append(removeLoopPart(button.getId()))
              .append("Pressed();\n")
          .append("      }\n");
    }
    for (Map.Entry<InputComponent, Component> buttonEntry : buttonsInLoops.entrySet())
    {
      InputComponent button = buttonEntry.getKey();
      Component loopComponent = buttonEntry.getValue();
      ComponentGenerator loopComponentGenerator = Generator.getGenerator(loopComponent);
      JavaClassName loopComponentReferencableClassName
          = loopComponentGenerator.getReferencableClassName(
              new GenerationContext(generationContext, loopComponent));
      result.classBody.append("      for (").append(loopComponentReferencableClassName.getSimpleName())
              .append(" loopComponent : ").append(removeLoopPart(loopComponent.getParent().getId()))
              .append(".children)\n")
          .append("      {\n")
          .append("        if (loopComponent.").append(removeLoopPart(button.getId()))
              .append(".submitted)\n")
          .append("        {\n")
          .append("          potentialResult = ").append(removeLoopPart(button.getId()))
              .append("Pressed(loopComponent);\n")
          .append("        }\n")
          .append("      }\n");
    }
    result.classBody.append("      if (potentialResult != null)\n")
        .append("      {\n")
        .append("        result = potentialResult;\n")
        .append("      }\n")
        .append("    }\n")
        .append("    return result;\n")
        .append("  }\n");
  }

  private void generateSubmittedValueGettersAndSetters(
      Component component,
      GeneratedClass result,
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
    result.classBody.append("\n  /**\n")
        .append("   * Convenience method to retrieve the submitted value of the ")
        .append(normalizedInputId).append(" component");
    if (refComponentPath.length() > 0)
    {
      result.classBody.append(" in the ").append(refComponentPath).append(" component");
    }
    result.classBody.append(".\n")
        .append("   *\n")
        .append("   * @return the submitted value of the ").append(normalizedInputId)
            .append(" component.\n")
        .append("   */\n")
        .append("  public String get").append(getterSetterSuffix).append("()\n")
        .append("  {\n")
        .append("    return ").append(pathToComponent).append(".submittedValue;\n")
        .append("  }\n")

        .append("\n  /**\n")
        .append("   * Convenience method to set the value of the ")
            .append(normalizedInputId).append(" component");
    if (refComponentPath.length() > 0)
    {
      result.classBody.append(" in the ").append(refComponentPath).append(" component");
    }
    result.classBody.append(".\n")
        .append("   *\n")
        .append("   * @param value the value of the ").append(normalizedInputId)
            .append(" component.\n")
        .append("   */\n")
        .append("  public void set").append(getterSetterSuffix).append("(String value)\n")
        .append("  {\n")
        .append("    ").append(pathToComponent).append(".setValue(value);\n")
        .append("  }\n");
  }

  private void generateButtonInLoopHookMethod(
      GenerationContext generationContext,
      InputComponent button,
      Component loopComponent,
      boolean overrideMethod)
  {
    String bareButtonId = removeLoopPart(button.getId());
    String bareLoopComponentId = removeLoopPart(loopComponent.getId());
    ComponentGenerator loopComponentGenerator = Generator.getGenerator(loopComponent);
    JavaClassName loopComponentReferencableClassName
        = loopComponentGenerator.getReferencableClassName(
            new GenerationContext(generationContext, loopComponent));
    generationContext.generatedClass.classBody.append("\n  /**\n")
        .append("   * Hook method which is called when the button ")
            .append(bareButtonId).append(" was pressed.\n")
        .append("   *\n")
        .append("   * @param ").append(bareLoopComponentId).append(" The component in the list of ")
            .append(loopComponentReferencableClassName.getSimpleName())
            .append(" Components\n")
        .append("   *        to which this button belongs.\n")
        .append("   *\n")
        .append("   * @return the page to be rendered.\n")
        .append("   *         If no component returns a not-null result, the current page")
            .append(" in the current state\n")
        .append("   *         will be rendered.\n")
        .append("   *         If more than one component returns a not-null result, the last")
            .append(" not-null result will be used.\n")
        .append("   */\n");
    if (overrideMethod)
    {
      generationContext.generatedClass.classBody.append("  @Override\n");
    }
    generationContext.generatedClass.classBody.append("  public Component ")
            .append(bareButtonId).append("Pressed(")
            .append(loopComponentReferencableClassName.getSimpleName()).append(" ")
            .append(bareLoopComponentId).append(")\n")
        .append("  {\n");
    if (overrideMethod)
    {
      generationContext.generatedClass.classBody.append("    return super.").append(bareButtonId)
          .append("Pressed(").append(bareLoopComponentId).append(");\n");
    }
    else
    {
      generationContext.generatedClass.classBody.append("    return null;\n");
    }
    generationContext.generatedClass.classBody.append("  }\n");
  }

  private void generateButtonHookMethod(
      GeneratedClass result,
      InputComponent button,
      boolean overrideMethod)
  {
    String bareComponentId = removeLoopPart(button.getId());
    result.classBody.append("\n  /**\n")
         .append("   * Hook method which is called when the button ").append(bareComponentId)
            .append(" was pressed.\n")
        .append("   *\n")
        .append("   * @return the page to be rendered.\n")
        .append("   *         If no component or called hook method returns a not-null result,")
            .append(" the current page\n")
        .append("   *         in the current state will be rendered.\n")
        .append("   *         If more than one component or called hook method")
            .append(" returns a not-null result,\n")
        .append("   *         the last not-null result will be used.\n")
        .append("   */\n");
    if (overrideMethod)
    {
      result.classBody.append("  @Override\n");
    }
    result.classBody.append("  public Component ").append(bareComponentId).append("Pressed()\n")
        .append("  {\n");
    if (overrideMethod)
    {
      result.classBody.append("    return super.").append(bareComponentId).append("Pressed();\n");
    }
    else
    {
      result.classBody.append("    return null;\n");
    }
    result.classBody.append("  }\n");
  }

  @Override
  public void generateExtension(GenerationContext generationContext)
  {
    GeneratedClass result = generationContext.generatedClass;

    String extensionClassName = getExtensionClassName(generationContext).getSimpleName();

    result.classPackage = generationContext.getPackage();
    result.imports.add(Component.class.getName());
    generateExtensionDefinition(generationContext);
    generateSerialVersionUid(result);
    generateConstructorWithIdAndParent(extensionClassName, null, result);

    List<InputComponent> buttons = new ArrayList<>();
    getButtons(generationContext.component, buttons);
    Map<InputComponent, Component> buttonsInLoops = new HashMap<>();
    getButtonsInLoops(generationContext.component, buttonsInLoops);

    for (InputComponent button : buttons)
    {
      generateButtonHookMethod(result, button, true);
    }

    for (Map.Entry<InputComponent, Component> buttonEntry : buttonsInLoops.entrySet())
    {
      InputComponent button = buttonEntry.getKey();
      Component loopComponent = buttonEntry.getValue();
      generateButtonInLoopHookMethod(generationContext, button, loopComponent, true);
    }
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
