package com.seitenbau.micgwaf.config;

import java.util.HashMap;
import java.util.Map;

import com.seitenbau.micgwaf.component.Component;

public abstract class ApplicationBase
{
  public Map<String, Component> components = new HashMap<String, Component>();

  public abstract Component getHomePage();
}
