package com.seitenbau.micgwaf.component;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * Base class of all components.
 * A component is a part of a HTML page that knows how to render itself and how to process HTTP requests.
 */
public abstract class Component implements Serializable
{
  /** Serial Version UID. */
  private static final long serialVersionUID = 1L;

  /** The component id. Can be null, but if set, it should be unique in a the current context (e.g. page). */
  public String id;
  
  /** The parent of the component, or null if this is a standalone component (e.g. a page). */ 
  public Component parent;
  
  /** Parameters used for generation. Not used during runtime. */
  public GenerationParameters generationParameters;
  
  /**
   * Constructor. 
   * @param id the id of the component, may be null. 
   *        If set, it should be unique in the current context (e.g. page).
   * @param parent the parent component, or null if this is a standalone component (e.g. a page)
   */
  public Component(String id, Component parent)
  {
    this.id = id;
    this.parent = parent;
  }
  
  /**
   * Returns the lsit of children of this component.
   * 
   * @return the list of children, not null.
   */
  public abstract List<? extends Component> getChildren();
  
  /**
   * Binds other components to this component, i.e. resolves component references.
   * 
   * @param allComponents all known standalone components, keyed by their id, not null.
   */
  public void resolveComponentReferences(Map<String, ? extends Component> allComponents)
  {
    for (Component child : getChildren())
    {
      child.resolveComponentReferences(allComponents);
    }
  }
  
  /**
   * Marks this component as being the loopIndex'th part in a loop.
   * May be called repetitively if several loops surround the component.
   * 
   * @param loopIndex the number of the component in a loop, 0 based.
   */
  public void inLoop(int loopIndex)
  {
    if (id != null)
    {
      id = id + ":" + loopIndex;
    }
    for (Component child : getChildren())
    {
      child.inLoop(loopIndex);
    }
  }
  
  /** 
   * Renders the component. 
   * 
   * @param writer the writer to render to, not null.
   * 
   * @throws IOException if the writer does not accept the rendered output.
   */
  public abstract void render(Writer writer) throws IOException;
  
  /**
   * Hook method which is called after a component tree has been rendered.
   * Typically used to reset state which should only be retained for one rendering.
   */
  public void afterRender()
  {
    for (Component child : getChildren())
    {
      child.afterRender();
    }
  }

  /**
   * Processes the HTTP request and executes any actions triggered by the request.
   * 
   * @param request the request to process, not null.
   * 
   * @return the page to render after processing, or null if this component does not wish another
   *         page to be rendered.
   *         note: if more than one child component wishes to redirect to another page, 
   *         the last component wins.
   */
  public Component processRequest(HttpServletRequest request)
  {
    Component toRedirectTo = null;
    for (Component child : getChildren())
    {
      toRedirectTo = child.processRequest(request);
    }
    return toRedirectTo;
  }
  
  /**
   * Returns the nearest ancestor of the component with the given class.
   * 
   * @param classOfAncestor the class of the ancestor, not null.
   * 
   * @return the nearest ancestor, or null if no such ancestor exists.
   */
  @SuppressWarnings("unchecked")
  public <T extends Component> T getAncestor(Class<T> classOfAncestor)
  {
   if (parent == null)
   {
     return null;
   }
   if (parent.getClass().equals(classOfAncestor))
   {
     return (T) parent;
   }
   return parent.getAncestor(classOfAncestor);
  }
}
