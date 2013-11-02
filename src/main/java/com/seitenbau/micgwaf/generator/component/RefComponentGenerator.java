package com.seitenbau.micgwaf.generator.component;

import com.seitenbau.micgwaf.component.Component;
import com.seitenbau.micgwaf.component.RefComponent;
import com.seitenbau.micgwaf.generator.JavaClassName;

public class RefComponentGenerator extends ComponentGenerator
{
  @Override
  public JavaClassName getClassName(
      Component component,
      String targetPackage)
  {
    // remove last package part and add own
    String basePackage = targetPackage.substring(0, targetPackage.lastIndexOf('.'));
    RefComponent refComponent = (RefComponent) component;
    return toExtensionClassName(refComponent.refid, basePackage + "." + refComponent.refid);
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