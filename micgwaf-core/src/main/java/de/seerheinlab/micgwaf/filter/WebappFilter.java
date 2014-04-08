package de.seerheinlab.micgwaf.filter;

import java.io.IOException;
import java.util.Iterator;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.seerheinlab.micgwaf.config.ApplicationBase;
import de.seerheinlab.micgwaf.requesthandler.RequestHandler;

public class WebappFilter implements Filter
{
  public static final String APPLICATION_CLASS_NAME_INIT_PARAM = "applicationClassName";
  
  @Override
  public void init(FilterConfig filterConfig) throws ServletException
  {
    String applicationClassName = filterConfig.getInitParameter(APPLICATION_CLASS_NAME_INIT_PARAM);
    try
    {
      ApplicationBase application = (ApplicationBase) Class.forName(applicationClassName).newInstance();
      ApplicationBase.setApplication(application);
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
    long startTime = System.currentTimeMillis();

    boolean processed = false;
    Iterator<RequestHandler> requestHandlerIt 
        = ApplicationBase.getApplication().getRequestHandlerChain().iterator();
    while (!processed && requestHandlerIt.hasNext())
    {
      RequestHandler requestHandler = requestHandlerIt.next();
      processed = requestHandler.handle(httpServletRequest, httpServletResponse);
    }

    long endTime = System.currentTimeMillis();
    System.out.println("Request took " + (endTime - startTime) + " ms");
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