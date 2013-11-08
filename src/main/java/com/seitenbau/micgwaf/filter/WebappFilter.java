package com.seitenbau.micgwaf.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
    HttpServletResponse httpServletResponse = (HttpServletResponse) response;
    boolean processed = new AjaxHandler().handle(httpServletRequest, httpServletResponse, application);
    if (!processed)
    {
      processed = new PRGHandler().handle(httpServletRequest, httpServletResponse, application);
    }
    if (!processed)
    {
      chain.doFilter(httpServletRequest, response);
    }
  }

  @Override
  public void destroy()
  {
  }
}