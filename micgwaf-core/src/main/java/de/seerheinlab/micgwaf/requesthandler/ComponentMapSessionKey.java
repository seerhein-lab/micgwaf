package de.seerheinlab.micgwaf.requesthandler;

import java.io.Serializable;

/**
 * A session key which contains the state key and the request path.
 */
final class ComponentMapSessionKey implements Serializable
{
  /** SerialVersionUID. */
  private static final long serialVersionUID = 1L;

  /** request path, not null. */
  private final String path;

  /** state key, not null. */
  private final String stateKey;

  public ComponentMapSessionKey(String path, String stateKey)
  {
    if (path == null)
    {
      throw new NullPointerException("path must not be null");
    }
    if (stateKey == null)
    {
      throw new NullPointerException("stateKey must not be null");
    }
    this.path = path;
    this.stateKey = stateKey;
  }

  @Override
  public int hashCode()
  {
    int result = 31 * stateKey.hashCode() + path.hashCode();
    return result;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (this == obj)
    {
      return true;
    }
    if (obj == null)
    {
      return false;
    }
    if (getClass() != obj.getClass())
    {
      return false;
    }
    ComponentMapSessionKey other = (ComponentMapSessionKey) obj;
    if (!path.equals(other.path))
    {
      return false;
    }
    return stateKey.equals(other.stateKey);
  }

  @Override
  public String toString()
  {
    return "[path=" + path + ", stateKey=" + stateKey + "]";
  }
}