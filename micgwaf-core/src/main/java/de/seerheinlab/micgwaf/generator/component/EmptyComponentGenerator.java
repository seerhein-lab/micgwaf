package de.seerheinlab.micgwaf.generator.component;

import de.seerheinlab.micgwaf.component.Component;
import de.seerheinlab.micgwaf.component.EmptyComponent;
import de.seerheinlab.micgwaf.generator.JavaClassName;

public class EmptyComponentGenerator extends ComponentGenerator
{
  @Override
  public JavaClassName getClassName(GenerationContext generationContext)
  {
    return new JavaClassName(EmptyComponent.class);
  }
  
  @Override
  public JavaClassName getExtensionClassName(GenerationContext generationContext)
  {
    return null;
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
}
