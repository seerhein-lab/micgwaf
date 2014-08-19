package de.seerheinlab.micgwaf.generator.component;

import de.seerheinlab.micgwaf.component.Component;
import de.seerheinlab.micgwaf.generator.JavaClassName;

public class InsertComponentGenerator extends ComponentGenerator
{
  @Override
  public JavaClassName getClassName(GenerationContext generationContext)
  {
    return new JavaClassName(Component.class);
  }

  @Override
  public boolean generateExtensionClass(Component component)
  {
    return false;
  }

  @Override
  public void generate(GenerationContext generationContext)
  {
    generationContext.generatedClass = null;
  }

  @Override
  public void generateExtension(GenerationContext generationContext)
  {
  }

  @Override
  public void generateInitializer(GenerationContext generationContext, String componentField)
  {
  }

  @Override
  public void addImportsForField(Component component,
      GenerationContext generationContext)
  {
    if (!generationContext.generatedClass.imports.contains(Component.class.getName()))
    {
      generationContext.generatedClass.imports.add(Component.class.getName());
    }
  }
}
