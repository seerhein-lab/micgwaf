package de.seerheinlab.micgwaf.filter;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.seerheinlab.micgwaf.component.Component;
import de.seerheinlab.micgwaf.parser.HtmlParser;

/**
 * A filter which parses xhtml files on the fly and renders them as html.
 * Component references are resolved.
 */
public class HtmlDevelopmentFilter implements Filter
{
  public static final String HTML_DIR_INIT_PARAM = "htmlDir";
  
  public File htmlDir;
  
  public HtmlParser htmlParser;
  
  @Override
  public void init(FilterConfig filterConfig) throws ServletException
  {
    String htmlDirString = filterConfig.getInitParameter(HTML_DIR_INIT_PARAM);
    htmlDir = new File(htmlDirString);
    if (!htmlDir.exists())
    {
      throw new ServletException("Cannot find html directory " + htmlDir.getAbsolutePath());
    }
    if (!htmlDir.isDirectory())
    {
      throw new ServletException(
          "The configured html directory " + htmlDir.getAbsolutePath() + " is not a directory");
    }
    htmlParser = new HtmlParser();
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

    Component component;
    String path = httpServletRequest.getServletPath();
    String componentId = path;
    int lastSlashPos = componentId.lastIndexOf("/");
    if (lastSlashPos != -1)
    {
      componentId = componentId.substring(lastSlashPos + 1);
    }
    
    int lastDotPos = componentId.lastIndexOf(".");
    if (lastDotPos != -1)
    {
      componentId = componentId.substring(0, lastDotPos);
    }

    Map<String, Component> allComponents = htmlParser.readComponents(htmlDir);
    component = allComponents.get(componentId);
    if (component == null)
    {
      httpServletResponse.sendError(HttpServletResponse.SC_NOT_FOUND);
      return;
    }

    response.setCharacterEncoding("UTF-8");
    Writer writer = httpServletResponse.getWriter();
    component.resolveComponentReferences(allComponents);
    component.render(writer);
    writer.close();
  }

  @Override
  public void destroy()
  {
  }
}