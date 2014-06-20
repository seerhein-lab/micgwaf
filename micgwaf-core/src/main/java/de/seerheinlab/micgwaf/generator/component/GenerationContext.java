package de.seerheinlab.micgwaf.generator.component;

import de.seerheinlab.micgwaf.component.Component;

/**
 * A Container holding the necessary information for generation code from a component.
 */
public class GenerationContext
{
  /** The component to generate code for. */
  public Component component;
  
  /** The root package for all generated components. */
  public String rootPackage;
  
  /** The subpackage for the specific component, may be null. */
  public String componentSubpackage;
  
  /** The current indent for generation, if applicable. */
  public int indent;
  
  /** The String builder to append the current input to, or null if a complete file is generated. */
  public StringBuilder stringBuilder;

  /**
   * Constructor without indent and stringBuilder.
   * 
   * @param component The component to generate code for.
   * @param rootPackage The root package for all generated components.
   * @param componentSubpackage The subpackage for the specific component.
   */
  public GenerationContext(Component component, String rootPackage, String componentSubpackage)
  {
    this(component, rootPackage, componentSubpackage, 0, null);
  }
  
  /**
   * Constructor.
   * 
   * @param component The component to generate code for.
   * @param rootPackage The root package for all generated components.
   * @param componentSubpackage The subpackage for the specific component.
   * @param indent The current indent for generation, if applicable, or 0 otherwise.
   * @param stringBuilder The String builder to append the current input to, 
   *        or null if a complete file is generated. 
   */
  public GenerationContext(
      Component component,
      String rootPackage,
      String componentSubpackage,
      int indent,
      StringBuilder stringBuilder)
  {
    this.component = component;
    this.rootPackage = rootPackage;
    this.componentSubpackage = componentSubpackage;
    this.indent = 0;
    this.stringBuilder = stringBuilder;
  }
  
  /**
   * Constructor.
   * Copies the parent context, but exchanges component and indent.
   * 
   * @param parentContext the parent context, not null.
   * @param component the new component, not null.
   * @param indent the new indent.
   */
  public GenerationContext(GenerationContext parentContext, Component component, int indent)
  {
    this.component = component;
    this.indent = indent;
    this.rootPackage = parentContext.rootPackage;
    this.componentSubpackage = parentContext.componentSubpackage;
    this.stringBuilder = parentContext.stringBuilder;
  }
  
  /**
   * Constructor.
   * Copies the parent context, but exchanges the contained component.
   * 
   * @param parentContext the parent context, not null.
   * @param component the new component, not null.
   */
  public GenerationContext(GenerationContext parentContext, Component component)
  {
    this.component = component;
    this.indent = parentContext.indent;
    this.rootPackage = parentContext.rootPackage;
    this.componentSubpackage = parentContext.componentSubpackage;
    this.stringBuilder = parentContext.stringBuilder;
  }
  /**
   * Returns the complete package for the component.
   * 
   * @return  the complete package for the component, not null.
   */
  public String getPackage()
  {
    if (componentSubpackage == null || componentSubpackage.isEmpty())
    {
      return rootPackage;
    }
    return rootPackage + '.' + componentSubpackage;
  }
}
