package com.seitenbau.micgwaf.filter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.seitenbau.micgwaf.component.Component;
import com.seitenbau.micgwaf.config.ApplicationBase;

/**
 * Handles the PostReditectGet-Pattern
 */
public class PRGHandler
{
private static final String REQUEST_PARAM = "step";
  
  public boolean handle(HttpServletRequest request, HttpServletResponse response, ApplicationBase application)
      throws IOException
  {
    response.setCharacterEncoding("UTF-8");
    boolean processed = false;
    if ("POST".equals(request.getMethod()))
    {
      processed =  process(request, response, application);
    }
    if (!processed)
    {
      processed = render(request, response, application);
    }
    return processed;
  }
  
  public boolean process(HttpServletRequest request, HttpServletResponse response, ApplicationBase application)
      throws IOException
  {
    String path = request.getServletPath();
    String pathFromSession = (String) request.getSession().getAttribute("path");
    Component toProcess = (Component) request.getSession().getAttribute("toProcess");
    if (toProcess == null || !path.equals(pathFromSession))
    {
      return false;
    }
    Integer lastStep = (Integer) request.getSession().getAttribute("step");
    if (lastStep == null)
    {
      lastStep = 0;
    }
    if (toProcess == null || !path.equals(pathFromSession))
    {
      return false;
    }
    Component toRender;
    try
    {
      toRender = toProcess.processRequest(request);
      if (toRender == null)
      {
        toRender = toProcess;
      }
    }
    catch (Exception e)
    {
      toRender = application.handleException(toProcess, e, false);
    }
    
    List<Component> componentList = (List<Component>) request.getSession().getAttribute("components");
    if (componentList == null)
    {
      componentList = new ArrayList<>();
      request.getSession().setAttribute("components", componentList);
    }
    componentList.add(toRender);
    
    response.sendRedirect(".?" + REQUEST_PARAM + "=" + lastStep);
    request.getSession().setAttribute("step", lastStep + 1);
    
    return true;
  }

  public boolean render(HttpServletRequest request, HttpServletResponse response, ApplicationBase application)
      throws IOException
  {
    String path = request.getServletPath();
    List<Component> componentList = (List<Component>) request.getSession().getAttribute("components");
    if (componentList == null)
    {
      componentList = new ArrayList<>();
      request.getSession().setAttribute("components", componentList);
    }

    Component toRender = null;
    if (request.getParameter(REQUEST_PARAM) != null)
    {
      Integer param = Integer.parseInt(request.getParameter(REQUEST_PARAM));
      if (param < componentList.size()) // if not assume stale index and treat as new
      {
        toRender = componentList.get(param);
      }
      
    }
    if (toRender == null)
    {
      toRender = application.getComponent(path);
    }
    if (toRender == null)
    {
      return false;
    }
    PrintWriter writer = response.getWriter();
    try
    {
      toRender.render(writer);
      toRender.afterRender();
    }
    catch (Exception e)
    {
      response.reset();
      toRender = application.handleException(toRender, e, false);
      toRender.render(writer);
      toRender.afterRender();
    }
    writer.close();
    request.getSession().setAttribute("toProcess", toRender);
    request.getSession().setAttribute("path", path);
    return true;
  }
}
