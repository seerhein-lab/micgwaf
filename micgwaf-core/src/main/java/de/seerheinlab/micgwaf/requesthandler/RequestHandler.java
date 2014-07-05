package de.seerheinlab.micgwaf.requesthandler;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Can handle a HTTP request.
 */
public interface RequestHandler
{
  /**
   * Either processes a HttpServletRequest and generates the appropriate response,
   * or skips processing the request.
   *
   * @param request the request, not null.
   * @param response the response, not null.
   * @return true if the request has been handled and the response has been created,
   *         false if this handler cannot handle the request.
   *
   * @throws IOException in case of an IO Error.
   */
  public boolean handle(HttpServletRequest request, HttpServletResponse response)
      throws IOException;
}
