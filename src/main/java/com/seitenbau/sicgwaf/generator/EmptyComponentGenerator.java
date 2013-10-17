package com.seitenbau.sicgwaf.generator;

import java.util.Map;

import com.seitenbau.sicgwaf.component.Component;

public class EmptyComponentGenerator extends ComponentGenerator
{
  public String getClassName(
      String componentName,
      Component component,
      String targetPackage)
  {
    return null;
  }
  
  public String getExtensionClassName(
      String componentName,
      Component component,
      String targetPackage)
  {
    return null;
  }
  
  public void generate(
        String componentName,
        Component rawComponent,
        String targetPackage,
        Map<String, String> filesToWrite)
  {
    throw new UnsupportedOperationException();
  }
  
  public void generateExtension(
        String componentName,
        Component rawComponent,
        String targetPackage,
        Map<String, String> filesToWrite)
  {
    throw new UnsupportedOperationException();
  }

  public String generateNewComponent(
      String componentName,
      Component component,
      String targetPackage)
  {
    return null;
  }

  public String generateNewExtensionComponent(
      String componentName,
      Component component,
      String targetPackage)
  {
    return null;
  }

  public String generateInitializer(
      String componentField,
      Component rawComponent,
      String targetPackage,
      int indent,
      Map<String, String> filesToWrite)
  {
    return "";
  }

}
