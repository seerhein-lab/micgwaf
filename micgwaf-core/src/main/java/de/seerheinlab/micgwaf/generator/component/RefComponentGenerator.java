package de.seerheinlab.micgwaf.generator.component;

import java.util.Map;

import de.seerheinlab.micgwaf.component.Component;
import de.seerheinlab.micgwaf.component.RefComponent;
import de.seerheinlab.micgwaf.generator.Generator;
import de.seerheinlab.micgwaf.generator.JavaClassName;

public class RefComponentGenerator extends ComponentGenerator
{
  @Override
  public JavaClassName getClassName(GenerationContext generationContext)
  {
    RefComponent refComponent = (RefComponent) generationContext.component;
    if (refComponent.referencedComponent != null
        && refComponent.referencedComponent.getGenerationParameters() != null 
        && refComponent.referencedComponent.getGenerationParameters().fromComponentLib)
    {
      return new JavaClassName(
          refComponent.referencedComponent.getClass().getSimpleName(),
          refComponent.referencedComponent.getClass().getPackage().getName());
    }
    String subpackage = Generator.getComponentSubpackage(refComponent.refid);
    return toExtensionClassName(
        refComponent.refid,
        generationContext.rootPackage + '.'+ subpackage);
  }
  
  @Override
  public boolean generateExtensionClass(Component component)
  {
    return false;
  }

  @Override
  public String generate(GenerationContext generationContext)
  {
    return null;
  }
  
  @Override
  public String generateExtension(GenerationContext generationContext)
  {
    return null;
  }

  @Override
  public void generateInitializer(GenerationContext generationContext, String componentField)
  {
    RefComponent refComponent = (RefComponent) generationContext.component;
    if (refComponent.variableValues.isEmpty())
    {
      return;
    }
    generationContext.stringBuilder.append("  {\n");
    for (Map.Entry<String, String> variableEntry : refComponent.variableValues.entrySet())
    {
      generationContext.stringBuilder.append("    ").append(componentField).append(".set")
        .append(variableEntry.getKey().substring(0, 1).toUpperCase())
        .append(variableEntry.getKey().substring(1))
        .append("(").append(asConstant(variableEntry.getValue())).append(");\n");
    }
    generationContext.stringBuilder.append("  }\n\n");
  }
}
