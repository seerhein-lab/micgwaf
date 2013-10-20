package com.seitenbau.micgwaf.config;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import com.seitenbau.micgwaf.component.Component;

public abstract class ApplicationBase
{
  public Map<String, Class<? extends Component>> components = new HashMap<>();

  protected void mount(String path, Class<? extends Component> component)
  {
    components.put(path, component);
  }
  
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
        + " has noc constructor with a single Component parameter");
  }
}
