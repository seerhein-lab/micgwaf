package de.seerheinlab.micgwaf.generator.component;

import java.util.Map;

import de.seerheinlab.micgwaf.component.Component;
import de.seerheinlab.micgwaf.component.RefComponent;
import de.seerheinlab.micgwaf.generator.JavaClassName;

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
    RefComponent refComponent = (RefComponent) rawComponent;
    StringBuilder result = new StringBuilder();
    for (Map.Entry<String, String> variableEntry : refComponent.variableValues.entrySet())
    {
      result.append("    ").append(componentField).append(".set")
        .append(variableEntry.getKey().substring(0, 1).toUpperCase())
        .append(variableEntry.getKey().substring(1))
        .append("(").append(asConstant(variableEntry.getValue())).append(");\n");
    }
    if (result.length() > 0)
    {
      result.insert(0, "  {\n");
      result.append("  }\n\n");
    }
    return result.toString();
  }
}
