package com.seitenbau.micgwaf.generator.component;

import com.seitenbau.micgwaf.component.Component;
import com.seitenbau.micgwaf.generator.JavaClassName;

public class DefineComponentGenerator extends ComponentGenerator
{
  @Override
  public JavaClassName getClassName(
      Component component,
      String targetPackage)
  {
    return new JavaClassName(Component.class);
  }
  
  @Override
  public boolean generateExtensionClass(Component component)
  {
    return false;
  }

  @Override
  public String generate(
        Component rawComponent,
        String targetPackage)
  {
    return null;
  }
  
  @Override
  public String generateExtension(
        Component rawComponent,
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
