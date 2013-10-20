package com.seitenbau.micgwaf.filter;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import com.seitenbau.micgwaf.component.Component;
import com.seitenbau.micgwaf.config.ApplicationBase;

public class WebappFilter implements Filter
{
  public static final String APPLICATION_CLASS_NAME_INIT_PARAM = "applicationClassName";
  
  public ApplicationBase application;
  
  @Override
  public void init(FilterConfig filterConfig) throws ServletException
  {
    String applicationClassName = filterConfig.getInitParameter(APPLICATION_CLASS_NAME_INIT_PARAM);
    try
    {
      application = (ApplicationBase) Class.forName(applicationClassName).newInstance();
    } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e)
    {
      throw new ServletException(e);
    }
  }

  @Override
  public void doFilter(
        ServletRequest request,
        ServletResponse response,
        FilterChain chain) 
      throws IOException, ServletException
  {
    HttpServletRequest httpServletRequest = (HttpServletRequest) request;
    String path = httpServletRequest.getServletPath();
    String pathFromSession = (String) httpServletRequest.getSession().getAttribute("path");
    Component toProcess = (Component) httpServletRequest.getSession().getAttribute("page");
    if (toProcess == null || !path.equals(pathFromSession))
    {
      toProcess = application.getComponent(path);
    }
    if (toProcess == null)
    {
      chain.doFilter(httpServletRequest, response);
      return;
    }
    Component toRender = null;
    try
    {
      toRender = toProcess.processRequest(httpServletRequest);
      if (toRender == null)
      {
        toRender = toProcess;
      }
    }
    catch (Exception e)
    {
      toRender = application.handleException(toProcess, e, false);
    }
    PrintWriter writer = response.getWriter();
    try
    {
      toRender.render(writer);
    }
    catch (Exception e)
    {
      response.reset();
      toRender = application.handleException(toProcess, e, false);
      toRender.render(writer);
    }
    writer.close();
    httpServletRequest.getSession().setAttribute("page", toRender);
    httpServletRequest.getSession().setAttribute("path", path);
  }

  @Override
  public void destroy()
  {
  }
}