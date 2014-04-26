package de.seerheinlab.micgwaf.requesthandler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import de.seerheinlab.micgwaf.component.Component;
import de.seerheinlab.micgwaf.config.ApplicationBase;

/**
 * A state handler which stores the state in the session and adds a request parameter to the
 * request as a state key.
 */
public class UrlParamSessionStateHandler implements StateHandler
{
  // private static Logger logger = Logger.getLogger(UrlParamStateHandler.class);
  
  public static final String COMPONENT_MAP_SESSION_ATTRIBUTE_NAME 
      = UrlParamSessionStateHandler.class.getName() + "." + "COMPONENT_MAP";

  public static final String COMPONENT_KEY_LIST_SESSION_ATTRIBUTE_NAME 
      = UrlParamSessionStateHandler.class.getName() + "." + "COMPONENT_KEY_LIST";

  public static final String NEXT_CONVERSATION_SESSION_ATTRIBUTE_NAME 
      = UrlParamSessionStateHandler.class.getName() + "." + "NEXT_CONVERSATION";

  public String stateKeyParam = "state";
  
  /** Starts the part of the state key which marks the current conversion. */
  public String conversationPrefix = "c";
  
  /** Starts the part of the state key which marks the current step. */
  public String stepPrefix = "s";
  
  /** How many states are stored maximally in the session. */
  public int maxStates = 100;

  @Override
  public Component getState(HttpServletRequest request) throws IOException
  {
    Map<ComponentMapSessionKey, byte[]> componentMap = getComponentMapFromSession(request);

    String path = ApplicationBase.getApplication().getMountPath(request);
    String stateKey = getCurrentStateKey(request);
    if (stateKey == null)
    {
      return null;
    }
    ComponentMapSessionKey sessionStateKey = new ComponentMapSessionKey(path, stateKey);
    Component state = deserialize(componentMap.get(sessionStateKey));
    return state;
  }

  @Override
  public void saveNextStateAndCompleteRequest(
        Component component,
        HttpServletRequest request,
        HttpServletResponse response)
      throws IOException
  {
    Map<ComponentMapSessionKey, byte[]> componentMap = getComponentMapFromSession(request);
    String nextStateKey = getNextStateKey(request);
    String path = ApplicationBase.getApplication().getMountPath(request);
    ComponentMapSessionKey sessionStateKey = new ComponentMapSessionKey(path, nextStateKey);
    componentMap.put(sessionStateKey, serialize(component));
    while (maxStates > 0 && componentMap.size() > maxStates)
    {
      Iterator<Map.Entry<ComponentMapSessionKey, byte[]>> entryIt = componentMap.entrySet().iterator();
      entryIt.next();
      entryIt.remove();
    }
    response.sendRedirect(".?" + stateKeyParam + "=" + nextStateKey);
  }


  /**
   * Gets the component map from the session.
   * If the session does not exist, an ew session is created.
   * 
   * @param request the servlet request, not null.
   * 
   * @return the component map, not null.
   */
  protected Map<ComponentMapSessionKey, byte[]> getComponentMapFromSession(HttpServletRequest request)
  {
    @SuppressWarnings("unchecked")
    Map<ComponentMapSessionKey, byte[]> componentMap = (Map<ComponentMapSessionKey, byte[]>) request.getSession()
        .getAttribute(COMPONENT_MAP_SESSION_ATTRIBUTE_NAME);
    if (componentMap == null)
    {
      componentMap = new LinkedHashMap<>();
      request.getSession().setAttribute(COMPONENT_MAP_SESSION_ATTRIBUTE_NAME, componentMap);
    }
    return componentMap;
  }
  
  /**
   * Returns the current state key from the servlet request.
   * It is assumed that the state key is sent as HTTP parameter with the name given in the variable
   * <code>stateKeyParam</code>
   * 
   * @param request the request to extract the state key from, not null.
   * 
   * @return the state key, or null if not supplied.
   */
  protected String getCurrentStateKey(HttpServletRequest request)
  {
    String stateKey = request.getParameter(stateKeyParam);
    return stateKey;
  }
  
  /**
   * Calculates the next state key from the servlet request.
   * If the servlet request contains a parseable state key, the step number is increased.
   * If not, a new conversation index is created and the step number is set to 1.
   * 
   * @param request the current request, not null.
   * 
   * @return the next state key, not null.
   */
  protected String getNextStateKey(HttpServletRequest request)
  {
    String stateKey = getCurrentStateKey(request);
    int conversation = 1;
    int step = 1;
    if (stateKey != null)
    {
      int conversationStart = stateKey.indexOf(conversationPrefix);
      int stepStart = stateKey.indexOf(stepPrefix);
      boolean nextConversationCreated = false;
      if (stepStart != -1)
      {
        try
        {
          String stepAsString = stateKey.substring(stepStart + 1);
          step = Integer.parseInt(stepAsString);
          step++;
          // logger.debug("getNextStep(): Read step from state key " + stateKey + ", next step is " + step);
        }
        catch (NumberFormatException e)
        {
          // logger.info("getNextStep(): Could not parse step part of state key " + stateKey);
          // create next conversation to make sure we do not re-use a key
          conversation = createNextConversationIndex(request);
          nextConversationCreated = true;
        }
      }
      else
      {
        // logger.info("getNextStep(): state key " + stateKey + " does not contain a step, creating a new one");
        // logger.debug("getNextStep(): created step " + step);
      }
      if (conversationStart != -1 && stepStart != -1 && conversationStart < stepStart && !nextConversationCreated)
      {
        try
        {
          String conversationAsString = stateKey.substring(conversationStart + 1, stepStart);
          conversation = Integer.parseInt(conversationAsString);
          // logger.debug("getNextStep(): Read conversation from state key " + stateKey + ", conversation is " + conversation);
        }
        catch (NumberFormatException e)
        {
          // logger.info("getNextStep(): Could not parse conversation part of state key " + stateKey);
        }
      }
      else
      {
        conversation = createNextConversationIndex(request);
      }
    }
    else
    {
      // logger.debug("getNextStep(): no state key found in request);
      conversation = createNextConversationIndex(request);
    }
    return conversationPrefix + conversation + stepPrefix + step;
  }

  /**
   * Creates the next conversation key.
   * The key is unique for the session.
   * 
   * @param request the current request, not null.
   * 
   * @return the next conversation index.
   */
  protected int createNextConversationIndex(HttpServletRequest request)
  {
    HttpSession session = request.getSession();
    synchronized (session)
    {
      // logger.info("getNextStep(): state key " + stateKey + " does not contain a conversation, creating a new one");
      Integer nextConversation = (Integer) session.getAttribute(NEXT_CONVERSATION_SESSION_ATTRIBUTE_NAME);
      if (nextConversation == null)
      {
        nextConversation = 1;
      }
      session.setAttribute(NEXT_CONVERSATION_SESSION_ATTRIBUTE_NAME, nextConversation + 1);
      // logger.debug("getNextStep(): created conversation " + conversation);
      return nextConversation;
    }
  }
  
  /**
   * Serializes a component to a byte array.
   * 
   * @param component the component to serialize, not null.
   * 
   * @return the serialized component.
   * 
   * @throws IOException if serialization fails.
   */
  protected byte[] serialize(Component component) throws IOException
  {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
    try 
    {
      objectOutputStream.writeObject(component);
      objectOutputStream.flush();
    } 
    finally 
    {
      objectOutputStream.close();
    }
    return outputStream.toByteArray();
  }
  
  /**
   * Deserializes a byte array to a component.
   * 
   * @param data the byte array to deserialize, or null.
   * 
   * @return the deserialized component, may be null.
   * 
   * @throws IOException if deserialization fails.
   */
  protected Component deserialize(byte[] data) throws IOException 
  {
    if (data == null)
    {
      return null;
    }
    ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
    try 
    {
      return (Component) ois.readObject();
    }
    catch (ClassNotFoundException e)
    {
      throw new IOException(e);
    }
    finally
    {
      ois.close();
    }
  }
}
