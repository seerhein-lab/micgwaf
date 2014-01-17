package com.seitenbau.micgwaf.util;

public class Assertions
{
  /**
   * Checks that an object is not null.
   * 
   * @param toCheck the object to check.
   * @param name the name of the object, which is referenced in a NullPointerException
   *        if the check fails.
   *
   * @throws NullPointerException if toCheck is null.
   */
  public static void assertNotNull(Object toCheck, String name)
  {
    if (toCheck == null)
    {
      throw new NullPointerException(name + " must not be null");
    }
  }
}
