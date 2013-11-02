package com.seitenbau.micgwaf.generator.component;

import com.seitenbau.micgwaf.component.Component;
import com.seitenbau.micgwaf.component.EmptyComponent;
import com.seitenbau.micgwaf.generator.JavaClassName;

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
