package de.seerheinlab.micgwaf.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

public class StringToolsTest
{
  @Test
  public void testAddTokens_toAddNull()
  {
    String result = StringTools.addTokens(null, "a b cc", " ");
    assertEquals("a b cc", result);
  }

  @Test
  public void testAddTokens_toAddToNull()
  {
    String result = StringTools.addTokens("a b a cc", null, " ");
    assertEquals("a b cc", result);
  }

  @Test
  public void testAddTokens_toAddToNull_toAddNull()
  {
    String result = StringTools.addTokens(null, null, " ");
    assertEquals(null, result);
  }

  @Test
  public void testAddTokens_delimiterNull()
  {
    try
    {
      StringTools.addTokens("a b cc", "a b", null);
    }
    catch (NullPointerException e)
    {
      assertEquals("delimiter must not be null", e.getMessage());
      return;
    }
    fail("Exception expected");
  }

  @Test
  public void testAddTokens_simple()
  {
    String result = StringTools.addTokens("a b cc", "d e", " ");
    assertEquals("d e a b cc", result);
  }

  @Test
  public void testAddTokens_duplicateToken()
  {
    String result = StringTools.addTokens("a b a cc", "d e d", " ");
    assertEquals("d e d a b cc", result);
  }

  @Test
  public void testAddTokens_alreadyPresent()
  {
    String result = StringTools.addTokens("a b cc dd", "cc b d e", " ");
    assertEquals("cc b d e a dd", result);
  }

  @Test
  public void testRemoveTokens_toRemoveNull()
  {
    String result = StringTools.removeTokens(null, "a b cc", " ");
    assertEquals("a b cc", result);
  }

  @Test
  public void testRemoveTokens_toRemoveFromNull()
  {
    String result = StringTools.removeTokens("a b a cc", null, " ");
    assertEquals(null, result);
  }

  @Test
  public void testAddTokens_toRemoveFromNull_toRemoveNull()
  {
    String result = StringTools.removeTokens(null, null, " ");
    assertEquals(null, result);
  }

  @Test
  public void testRemovesTokens_delimiterNull()
  {
    try
    {
      StringTools.removeTokens("a b cc", "a b", null);
    }
    catch (NullPointerException e)
    {
      assertEquals("delimiter must not be null", e.getMessage());
      return;
    }
    fail("Exception expected");
  }

  @Test
  public void testRemoveTokens_simple()
  {
    String result = StringTools.removeTokens("dd b", "a b cc dd e", " ");
    assertEquals("a cc e", result);
  }

  @Test
  public void testRemoveTokens_duplicateToken()
  {
    String result = StringTools.removeTokens("dd b dd", "a b cc dd e b", " ");
    assertEquals("a cc e", result);
  }
}
