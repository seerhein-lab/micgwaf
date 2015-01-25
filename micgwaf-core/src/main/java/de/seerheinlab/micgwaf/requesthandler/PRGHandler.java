package de.seerheinlab.micgwaf.requesthandler;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.seerheinlab.micgwaf.component.Component;
import de.seerheinlab.micgwaf.config.ApplicationBase;

/**
 * Handles the PostRedirectGet-Pattern
 */
// TODO Currently the session increases to the infinite. define maxConversations, maxSteps, maxStates and evict Components
public class PRGHandler implements RequestHandler
{
  public String defaultEncoding = "UTF-8";

  public StateHandler stateHandler = new UrlParamSessionStateHandler();

  @Override
  public boolean handle(HttpServletRequest request, HttpServletResponse response)
      throws IOException
  {
    response.setCharacterEncoding(defaultEncoding);
    boolean processed = false;
    if ("POST".equals(request.getMethod()))
    {
      processed = process(request, response);
    }
    if (!processed)
    {
      processed = render(request, response);
    }
    return processed;
  }

  public boolean process(HttpServletRequest request,
      HttpServletResponse response) throws IOException
  {
    Component toProcess = stateHandler.getState(request);
    if (toProcess == null)
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
      toRender = ApplicationBase.getApplication().handleException(toProcess, e, false);
    }
    stateHandler.saveNextStateAndCompleteRequest(toRender, request, response);

    return true;
  }

  public boolean render(HttpServletRequest request, HttpServletResponse response)
      throws IOException
  {
    Component toRender = stateHandler.getState(request);
    if (toRender != null)
    {
      // TODO this is dirty.
      // Although storing/retrieving from the session may loose information,
      // which may need rebuilding,
      // we may not be sure whether this method does initializations which
      // should happen only once during instance lifetime.
      ApplicationBase.getApplication().postConstruct(toRender);
    }
    else
    {
      String path = ApplicationBase.getApplication().getMountPath(request);
      toRender = ApplicationBase.getApplication().getComponent(path);
      if (toRender == null)
      {
        return false;
      }
      stateHandler.saveNextStateAndCompleteRequest(toRender, request, response);
      return true;
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
      toRender = ApplicationBase.getApplication().handleException(toRender, e, false);
      toRender.render(writer);
      toRender.afterRender();
    }
    writer.close();
    // TODO should state changes in the toRender component be tracked, i.e. stored in the session?
    // This also affects if toRender is completely new because of Exception handling...
    return true;
  }
}
