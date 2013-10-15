package com.seitenbau.sicgwaf.parser.contenthandler;

import java.util.LinkedHashMap;
import java.util.Map;

public class ContentHandlerRegistry
{

  public static final String MULTIPLE_ATTR = "multiple";
  
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
    attributeHandlerMap.put(
        HtmlElementContentHandler.ID_ATTR,
        new HtmlElementContentHandlerFactory());
  };
}
