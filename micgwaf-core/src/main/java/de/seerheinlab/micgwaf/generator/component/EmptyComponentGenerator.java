package de.seerheinlab.micgwaf.generator.component;

import de.seerheinlab.micgwaf.component.Component;
import de.seerheinlab.micgwaf.component.EmptyComponent;
import de.seerheinlab.micgwaf.generator.JavaClassName;

public class EmptyComponentGenerator extends ComponentGenerator
{
  @Override
  public JavaClassName getClassName(
      Component component,
      String targetPackage)
  {
    return new JavaClassName(EmptyComponent.class);
  }
  
  @Override
  public JavaClassName getExtensionClassName(
      Component component,
      String targetPackage)
  {
    return null;
  }
  
  @Override
  public boolean generateExtensionClass(Component component)
  {
    return false;
  }
  
  @Override
  public String generate(
        Component component,
        String targetPackage)
  {
    return null;
  }
  
  @Override
  public String generateExtension(
        Component component,
        String targetPackage)
  {
    return null;
  }

  @Override
  public String generateInitializer(
      String componentField,
      Component rawComponent,
      String targetPackage,
      int indent)
  {
    return "";
  }

}
