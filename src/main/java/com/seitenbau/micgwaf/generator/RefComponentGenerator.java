package com.seitenbau.micgwaf.generator;

import com.seitenbau.micgwaf.component.Component;
import com.seitenbau.micgwaf.component.RefComponent;

public class RefComponentGenerator extends ComponentGenerator
{
  @Override
  public JavaClassName getClassName(
      Component component,
      String targetPackage)
  {
    RefComponent refComponent = (RefComponent) component;
    return toJavaClassName(refComponent.refid, targetPackage);
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
