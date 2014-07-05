package de.seerheinlab.micgwaf.parser.contenthandler;

import java.util.LinkedHashMap;
import java.util.Map;

public class ContentHandlerRegistry
{
  public static final String MULTIPLE_ATTR = "multiple";

  public static final String DEFAULT_RENDER_ATTR = "defaultRender";

  public static final String DEFAULT_RENDER_SELF_ATTR = "defaultRenderSelf";

  public static final String DEFAULT_RENDER_CHILDREN_ATTR = "defaultRenderChildren";

  public static final String GENRATE_EXTENSION_CLASS_ATTR = "generateExtensionClass";

  /**
   * Key is name of attribute in our namespace, value is handler.
   */
  public static Map<String, ContentHandlerFactory> attributeHandlerMap
      = new LinkedHashMap<String, ContentHandlerFactory>();

  /**
   * Key is name of element in our namespace, value is handler.
   */
  public static Map<String, ContentHandlerFactory> elementHandlerMap
      = new LinkedHashMap<String, ContentHandlerFactory>();

  static
  {
    elementHandlerMap.put(
        ComponentRefContentHandler.COMPONENT_REF_ELEMENT_NAME,
        new ComponentRefContentHandlerFactory());
    elementHandlerMap.put(
        RemoveContentHandler.REMOVE_ELEMENT_NAME,
        new RemoveContentHandlerFactory());
    elementHandlerMap.put(
        InsertContentHandler.INSERT_ELEMENT_NAME,
        new InsertContentHandlerFactory());
    elementHandlerMap.put(
        TemplateIntegrationContentHandler.USE_TEMPLATE_ELEMENT_NAME,
        new TemplateIntegrationContentHandlerFactory());
    elementHandlerMap.put(
        DefineContentHandler.DEFINE_ELEMENT_NAME,
        new DefineContentHandlerFactory());
    attributeHandlerMap.put(
        HtmlElementContentHandler.ID_ATTR,
        new HtmlElementContentHandlerFactory());
  };
}
