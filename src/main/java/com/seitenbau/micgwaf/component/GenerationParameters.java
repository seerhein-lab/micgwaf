package com.seitenbau.micgwaf.component;

import java.io.Serializable;

public class GenerationParameters implements Serializable
{
  /**
   * If true, a pair of classes (base and extension) should be generated for the component, if possible.
   * If false, only one class should be generated.
   * Currently only supported by HtmlElement component.
   */
  public Boolean generateExtensionClass;
}
