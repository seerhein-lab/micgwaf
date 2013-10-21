package com.seitenbau.micgwaf.config;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import com.seitenbau.micgwaf.component.Component;
import com.seitenbau.micgwaf.component.SnippetComponent;

public abstract class ApplicationBase
{
  public Map<String, Class<? extends Component>> components = new HashMap<>();

  protected void mount(String path, Class<? extends Component> component)
  {
    components.put(path, component);
  }
  
  /**
   * Returns the component which is responsible for rendering a request with a given reuqest path
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
    Constructor<?>[] constructors = componentClass.getConstructors();
    for (Constructor<?> constructor : constructors)
    {
      if ((constructor.getParameterTypes().length == 1) 
          && (constructor.getParameterTypes()[0] == Component.class))
      {
        Component instance;
        try
        {
          instance = (Component) constructor.newInstance(new Object[] {null});
        } catch (InstantiationException | IllegalAccessException
            | IllegalArgumentException | InvocationTargetException e)
        {
          throw new RuntimeException(e);
        }
        return instance;
      }
    }
    throw new IllegalStateException("Component of class " + componentClass.getName() 
        + " has no constructor with a single Component parameter");
  }
  
  /**
   * This handler is called whenever an Exception is not caught by the components.
   * This implementation returns the co
   * 
   * @param component the page in which the error occured 
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
    result.snippet = writer.toString();
    return result;
  }
}
