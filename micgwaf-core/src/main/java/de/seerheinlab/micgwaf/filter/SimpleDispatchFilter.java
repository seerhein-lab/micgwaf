package de.seerheinlab.micgwaf.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * A dispatch Filter which uses the full path path to the requested file
 * to find the requested file and dispatches to the file.
 * A path prefix can be defined to define the path where the resoures
 * actually are.
 * This filter can be used to serve files (images, css, ...) as-is.
 */
public class SimpleDispatchFilter implements Filter
{
  public static final String RESOURCE_PATH_INIT_PARAM = "resourcePath";

  private String resourcePath;

  @Override
  public void init(FilterConfig filterConfig) throws ServletException
  {
    resourcePath = filterConfig.getInitParameter(RESOURCE_PATH_INIT_PARAM);
    if (resourcePath == null)
    {
      resourcePath = "/";
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

    StringBuilder dispatchPath = new StringBuilder(httpServletRequest.getServletPath());
    if (httpServletRequest.getPathInfo() != null)
    {
      dispatchPath.append(httpServletRequest.getPathInfo());
    }

    dispatchPath = dispatchPath.insert(0, resourcePath);
    RequestDispatcher requestDispatcher = request.getRequestDispatcher(dispatchPath.toString());
    requestDispatcher.forward(request, response);
   }

  @Override
  public void destroy()
  {
  }
}