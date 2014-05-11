package de.seerheinlab.micgwaf.config;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import de.seerheinlab.micgwaf.component.Component;
import de.seerheinlab.micgwaf.component.SnippetComponent;
import de.seerheinlab.micgwaf.requesthandler.AjaxHandler;
import de.seerheinlab.micgwaf.requesthandler.PRGHandler;
import de.seerheinlab.micgwaf.requesthandler.RequestHandler;
import de.seerheinlab.micgwaf.util.Assertions;

public abstract class ApplicationBase
{
  /** The singleton instance which can be retrieved by getApplication(). */
  private static ApplicationBase instance;
  
  /** Mapping from path patterns to components handling the requests with the path patterns. */
  public Map<String, Class<? extends Component>> components = new HashMap<>();
  
  /** The request handler chain. The first handler gets invoked first. */
  public List<RequestHandler> requestHandlers = new ArrayList<>();
  
  /**
   * Constructor.
   */
  public ApplicationBase()
  {
    requestHandlers.add(new AjaxHandler());
    requestHandlers.add(new PRGHandler());
  }

  /**
   * Sets the application instance retrieved by <code>getApplication()</code>.
   * This method must be called at least once with the appropriate application instance
   * because the micgwaf framework depends on getting the application instance by <code>getApplication()</code>.
   *  
   * @param instance the instance which is used by the micgwaf framework.
   */
  public static void setApplication(ApplicationBase instance)
  {
    ApplicationBase.instance = instance;
  }
  
  /**
   * Returns the application instance to be used by the micgwaf framework.
   * 
   * @return the instance which should be used, not null.
   * 
   * @throws IllegalStateException if no instance was set.
   */
  public static ApplicationBase getApplication()
  {
    if (instance == null)
    {
      throw new IllegalStateException("Micgwaf was not (yet) initialized correctly: no instance of Application is known.");
    }
    return instance;
  }


  /**
   * Returns the path considered for mounting components.
   * We assume we are running in a servlet filter, 
   * so this implementation returns the path info from the request,
   * or "/" if the path info is null.
   * 
   * @param request the request, not null.
   *
   * @return the path considered for mounting, not null.
   */
  public String getMountPath(HttpServletRequest request)
  {
    String path = request.getServletPath();
    if (path == null || "".equals(path))
    {
      path = "/";
    }
    return path;
  }

  /**
   * Mounts a component to a certain path.
   * @param path the path to mount the component to, not null.
   * @param component the component to mount, or null to remove an already mounted component.
   * 
   * @throws NullPointerException if path is null.
   */
  protected void mount(String path, Class<? extends Component> component)
  {
    Assertions.assertNotNull(path, "path");
    if (component == null)
    {
      components.remove(path);
    }
    else
    {
      components.put(path, component);
    }
  }
  
  /**
   * Returns the component which is responsible for rendering a request with a given request path.
   *  
   * @param path the request path, not null.
   * 
   * @return the component, or null if no component is associated with the path
   * 
   * @throws RuntimeException if the component cannot be constructed.
   */
  public Component getComponent(String path)
  {
    Class<? extends Component> componentClass = components.get(path);
    if (componentClass == null)
    {
      return null;
    }
    Component result = Component.getInstance(componentClass);
    postConstruct(result);
    return result;
  }
  
  /**
   * Returns the request handler chain.
   * 
   * @return the request handler chain, not null. The first handler in the list gets invoked first.
   */
  public List<RequestHandler> getRequestHandlerChain()
  {
    return requestHandlers;
  }
  
  /**
   * This handler is called whenever an Exception is not caught by the components.
   * This implementation returns a component which writes "An error occurred".
   * 
   * @param component the page in which the error occurred 
   * @param exception the exception which was caught 
   * @param onRender true if the exception was caught on rendering, false if not.
   * 
   * @return the component to display as error page.
   */
  public Component handleException(Component component, Exception exception, boolean onRender)
  {
    SnippetComponent result = new SnippetComponent(null);
    StringWriter writer = new StringWriter();
    writer.write("An error occurred\n");
    exception.printStackTrace(new PrintWriter(writer));
    result.text = writer.toString();
    return result;
  }
  
  /**
   * Hook method to process beans after construction.
   * It is called for every component constructed by micgwaf after construction,
   * so if any custom initialization is needed (e.g. injection of services),
   * this can be done by overriding this method.
   * The component can be even replaced by a more appropriate instance, if needed.
   * 
   * TODO should this hook method be called automatically by Component's constructor ?
   *      PRO: hand-instantiated components also have this method called automatically
   *      CONTRA: Components are not properly constructed (are made known before constructed)
   * 
   * This implementation of the method does nothing.
   * 
   * @param component the component to process.
   * 
   * @return The component to be used by micgwaf.
   *         This implementation returns the passed in component.
   */
  public <T extends Component> T postConstruct(T component)
  {
    // do nothing.
    return component;
  }
}
