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
 * A dispatcher which ignores the full path path to the requested file.
 * It only considers the part of the path after the last slash, prefixes it with the
 * resourcePath set as init parameter, and forwards the resource to it.
 *
 * It can be used e.g. to use simple paths to css files anywhere in the directory structure.
 */
public class IgnorePathDispatchFilter implements Filter
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

    if (dispatchPath != null && dispatchPath.toString().lastIndexOf('/') > 0)
    {
      dispatchPath.delete(0, dispatchPath.toString().lastIndexOf('/'));
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