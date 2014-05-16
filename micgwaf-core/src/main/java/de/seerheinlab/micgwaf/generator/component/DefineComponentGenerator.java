package de.seerheinlab.micgwaf.generator.component;

import de.seerheinlab.micgwaf.component.Component;
import de.seerheinlab.micgwaf.generator.JavaClassName;

public class DefineComponentGenerator extends ComponentGenerator
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
  }
}
