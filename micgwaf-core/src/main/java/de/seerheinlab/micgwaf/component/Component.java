package de.seerheinlab.micgwaf.component;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * Base class of all components.
 * A component is a part of a HTML page that knows how to render itself and how to process HTTP requests.
 * 
 * All components must be serializable (because the component state is stored in serialized form
 * between requests).
 */
public abstract class Component implements Serializable
{
  /** Serial Version UID. */
  private static final long serialVersionUID = 1L;

  /** The component id. Can be null, but if set, it should be unique in a the current context (e.g. page). */
  protected String id;
  
  /** The parent of the component, or null if this is a standalone component (e.g. a page). */ 
  protected Component parent;
  
  /** Parameters used for generation. Not used during runtime. */
  protected GenerationParameters generationParameters;
  
  /**
   * Constructor. 
   * 
   * @param id the id of the component, may be null. 
   *        If set, it should be unique in the current context (e.g. page).
   * @param parent the parent component. May be null if this is a standalone component (e.g. a page).
   */
  public Component(String id, Component parent)
  {
    this.id = id;
    this.parent = parent;
  }
  
  /**
   * Returns the list of children of this component.
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
   * Calculates the HTML id of a component, taking into account potential parents implementing the
   * ChangesChildHtmlId interface.
   * 
   * @param initialHtmlId the initial HTML id of this component, or null.
   * 
   * @return the HTML id with the modifications of all ancestors implementing the ChangesChildHtmlId
   *         interfaces, or null if null wass passed in.
   */
  public String getHtmlId(String initialHtmlId)
  {
    if (initialHtmlId == null)
    {
      return null;
    }
    Component parent = getParent();
    if (parent == null)
    {
      return initialHtmlId;
    }
    String newId = initialHtmlId;
    if (parent instanceof ChangesChildHtmlId)
    {
      newId = ((ChangesChildHtmlId) parent).changeChildHtmlId(this, initialHtmlId);
    }
    return parent.getHtmlId(newId);
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
   * Processes the HTTP request and executes any actions triggered by the request.
   * 
   * @param request the request to process, not null.
   * 
   * @return The component which produces the snippet to render as a response to this ajax request,
   *         or null to signal that this component or its children are not interested in
   *         this ajax request.
   *         note: if more than one child component wishes to hande the request,
   *         the first component wins.
   */
  public Component processAjaxRequest(HttpServletRequest request)
  {
    for (Component child : getChildren())
    {
      Component toRedirectTo = child.processAjaxRequest(request);
      if (toRedirectTo != null) 
      {
        return toRedirectTo;
      }
    }
    return null;
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
  
  /**
   * Returns the component's parent.
   * 
   * @return the parent component, or null if the component is standalone (e.g. a page)
   */
  public Component getParent()
  {
    return parent;
  }
  
  /**
   * Sets the component's parent.
   * 
   * @param parent the parent component, or null if the component is standalone (e.g. a page)
   */
  public void setParent(Component parent)
  {
    this.parent = parent;
  }
  
  /**
   * Returns the component id. 
   * If set, it should be unique in a the current context (e.g. page), but this is not enforced.
   * 
   * @return the component id, or null.
   */
  public String getId()
  {
    return id;
  }

  /**
   * Sets the component id. If set, the id should be unique in a the current context (e.g. page).
   * 
   * @param id the component id, or null.
   */
  public void setId(String id)
  {
    this.id = id;
  }
  
  /**
   * Returns the parameters used for generation.
   * 
   * @return the parameters for generation, null if not set or in the runtime.
   */
  public GenerationParameters getGenerationParameters()
  {
    return generationParameters;
  }

  /**
   * Sets the parameters used for generation.
   * 
   * @param generationParameters the parameters for generation, or null.
   */
  public void setGenerationParameters(GenerationParameters generationParameters)
  {
    this.generationParameters = generationParameters;
  }
}
