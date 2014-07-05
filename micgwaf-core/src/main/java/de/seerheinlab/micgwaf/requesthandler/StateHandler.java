package de.seerheinlab.micgwaf.requesthandler;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.seerheinlab.micgwaf.component.Component;

/**
 * Stores the component state between requests and is able to restore the state
 * given a particular request.
 */
public interface StateHandler
{
  /**
   * Returns the component in the state for answering the passed request.
   *
   * @param request the request to answer, not null.
   *
   * @return the component, or null if no such component can be found.
   *
   * @throws IOException if an IOError occurs.
   */
  public Component getState(HttpServletRequest request) throws IOException;

  /**
   * Creates a new state containing the passed component, saves it so it can be retrieved next time,
   * and completes the current request.
   * The stored state must be decoupled from the component, i.e. if the component changes
   * after state saving, the saved state must stay the same.
   *
   * @param component the component to save in the state, not null.
   * @param request the current servlet request, not null.
   *
   * @throws IOException if an IOError occurs.
   */
  public void saveNextStateAndCompleteRequest(
      Component component,
      HttpServletRequest request,
      HttpServletResponse response)
    throws IOException;
}
