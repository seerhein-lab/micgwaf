package de.seerheinlab.micgwaf.requesthandler;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.seerheinlab.micgwaf.component.Component;
import de.seerheinlab.micgwaf.config.ApplicationBase;

/**
 * Handles the PostReditectGet-Pattern
 */
public class AjaxHandler implements RequestHandler
{
  public boolean handle(HttpServletRequest request, HttpServletResponse response)
      throws IOException
  {
    response.setCharacterEncoding("UTF-8");
    boolean processed = false;
    String path = request.getServletPath();
    if (path.startsWith("/ajax"))
    {
      processed = process(request, response);
    }
    return processed;
  }
  
  public boolean process(HttpServletRequest request, HttpServletResponse response)
      throws IOException
  {
    Component toProcess = (Component) request.getSession().getAttribute("toProcess");
    if (toProcess == null)
    {
      return false;
    }
    Integer lastStep = (Integer) request.getSession().getAttribute("step");
    if (lastStep == null)
    {
      lastStep = 0;
    }
    Component toRender;
    try
    {
      toRender = toProcess.processAjaxRequest(request);
    }
    catch (Exception e)
    {
      // TODO extra method handleAjaxException ?
      toRender = ApplicationBase.getApplication().handleException(toProcess, e, false);
    }
    
    PrintWriter writer = response.getWriter();
    if (toRender != null)
    {
      try
      {
        toRender.render(writer);
        toRender.afterRender();
      }
      catch (Exception e)
      {
        response.reset();
        toRender = ApplicationBase.getApplication().handleException(toRender, e, false);
        toRender.render(writer);
        toRender.afterRender();
      }
    }
    writer.close();
    
    return true;
  }
}
