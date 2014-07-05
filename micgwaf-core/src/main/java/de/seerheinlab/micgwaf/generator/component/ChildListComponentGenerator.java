package de.seerheinlab.micgwaf.generator.component;

import de.seerheinlab.micgwaf.component.ChildListComponent;
import de.seerheinlab.micgwaf.component.Component;
import de.seerheinlab.micgwaf.generator.Generator;
import de.seerheinlab.micgwaf.generator.GeneratorHelper;
import de.seerheinlab.micgwaf.generator.JavaClassName;

public class ChildListComponentGenerator extends ComponentGenerator
{
  @Override
  public JavaClassName getClassName(GenerationContext generationContext)
  {
    Component child = generationContext.component.getChildren().get(0);
    ComponentGenerator delegate = Generator.getGenerator(child);
    return new JavaClassName(
        "ChildListComponent<" 
            + delegate.getClassName(new GenerationContext(generationContext, child)).getSimpleName() + ">",
        ChildListComponent.class.getPackage().getName());
  }
  
  @Override
  public JavaClassName getExtensionClassName(GenerationContext generationContext)
  {
    Component child = generationContext.component.getChildren().get(0);
    ComponentGenerator delegate = Generator.getGenerator(child);
    String delegateClassName = delegate.getReferencableClassName(
        new GenerationContext(generationContext, child)).getSimpleName();
    if (delegateClassName == null)
    {
      return null;
    }
    return  new JavaClassName(
        "ChildListComponent<" + delegateClassName + ">",
        ChildListComponent.class.getPackage().getName()); 
  }
  
  @Override
  public JavaClassName getReferencableClassName(GenerationContext generationContext)
  {
    return getExtensionClassName(generationContext);
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
    ChildListComponent<?> childListComponent = (ChildListComponent<?>) generationContext.component;
    String indentString = GeneratorHelper.getIndentString(generationContext.indent);
    generationContext.generatedClass.classBody.append(indentString).append("{\n");
    for (int i = 0; i < childListComponent.children.size(); ++i)
    {
      Component child = childListComponent.children.get(i);
      ComponentGenerator delegate = Generator.getGenerator(child);
      generateFieldOrVariableFromComponent(
          new GenerationContext(generationContext, child, 4),
          "",
          componentField + i,
          componentField);
      delegate.generateInitializer(
          new GenerationContext(generationContext, child, generationContext.indent + 2),
          componentField + i);
      generationContext.generatedClass.classBody.append(indentString).append("  ")
          .append(componentField).append(".children.add(")
          .append(componentField).append(i).append(");\n");
    }
    generationContext.generatedClass.classBody.append(indentString).append("}\n");
  }

  @Override
  public boolean generateExtensionClass(Component component)
  {
    return false;
  }
}
