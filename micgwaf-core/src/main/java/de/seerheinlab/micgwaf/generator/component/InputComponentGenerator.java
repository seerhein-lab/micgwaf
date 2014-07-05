package de.seerheinlab.micgwaf.generator.component;

import javax.servlet.http.HttpServletRequest;

import de.seerheinlab.micgwaf.component.Component;
import de.seerheinlab.micgwaf.component.InputComponent;
import de.seerheinlab.micgwaf.generator.GeneratedClass;
import de.seerheinlab.micgwaf.generator.JavaClassName;

public class InputComponentGenerator extends HtmlElementComponentGenerator
{
  @Override
  public JavaClassName getClassName(GenerationContext generationContext)
  {
    return getBaseClassName(generationContext);
  }

  @Override
  public void generate(GenerationContext generationContext)
  {
    super.generate(generationContext);
    GeneratedClass result = generationContext.generatedClass;

    InputComponent inputComponent = (InputComponent) generationContext.component;
    // In the generated class, the value of the attribute "name" should not contain any loop parts.
    // They will be added again when generating the name.
    inputComponent.attributes.put(
        InputComponent.NAME_ATTR,
        removeLoopPart(inputComponent.attributes.get(InputComponent.NAME_ATTR)));

    // replace inheritance class
    result.classDefinition = new StringBuilder();
    generateClassDefinition(generationContext, InputComponent.class);

    result.imports.add(HttpServletRequest.class.getName());
    result.imports.add(InputComponent.class.getName());
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
  }
}
