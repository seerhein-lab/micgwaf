package com.seitenbau.micgwaf.util;

public class Assertions
{
  public static void assertNotNull(Object toCheck, String name)
  {
    if (toCheck == null)
    {
      throw new NullPointerException(name + " must not be null");
    }
  }
}
