package de.seerheinlab.micgwaf.component;

import java.io.Serializable;

/**
 * Contains parameters for a component which are used in generation time but not in runtime.
 */
public class GenerationParameters implements Serializable
{
  /** Serial Version UID. */
  private static final long serialVersionUID = 1L;
  
  /**
   * If true, a pair of classes (base and extension) should be generated for the component, if possible.
   * If false, only one class should be generated.
   * Currently only supported by HtmlElement component.
   */
  public Boolean generateExtensionClass;
  
  /**
   * True if this component is an external component from a component lib, 
   * false if it is an internal component.
   */
  public boolean fromComponentLib = false;
}
