package de.seerheinlab.micgwaf.util;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.StringTokenizer;

public final class StringTools
{
  private StringTools()
  {
  }

  /**
   * Adds all unique tokens in toAdd to toAddTo, if they are not already present there.
   * Duplicate tokens in toAddTo are ignored.
   *
   * @param toAdd the tokens to add, or null to return toAddTo
   * @param toAddTo the base string to add the tokens to, or null to return the unique tokens in toAdd
   * @param delimiter the delimiter, not null.
   *
   * @return the String toAddTo with the unique tokens in toAdd added.
   */
  public static String addTokens(final String toAdd, final String toAddTo, final String delimiter)
  {
    if (toAdd == null)
    {
      return toAddTo;
    }
    Assertions.assertNotNull(delimiter, "delimiter");

    // create token list from toAdd
    Set<String> toAddTokenSet = new LinkedHashSet<>();
    StringTokenizer tokenizer = new StringTokenizer(toAdd, delimiter);
    while (tokenizer.hasMoreTokens())
    {
      toAddTokenSet.add(tokenizer.nextToken());
    }

    // remove tokens from toAddTokenList which are already present in toAddTo
    if (toAddTo != null)
    {
      tokenizer = new StringTokenizer(toAddTo, delimiter);
      while (tokenizer.hasMoreTokens())
      {
        String token = tokenizer.nextToken();
        toAddTokenSet.remove(token);
      }
    }

    // first make sure using string builder is worth while
    if (toAddTokenSet.isEmpty())
    {
      return toAddTo;
    }
    // add tokens from tokenList
    StringBuilder result;
    if (toAddTo == null)
    {
      result = new StringBuilder();
    }
    else
    {
      result = new StringBuilder(toAddTo);
    }
    for (String toAddToken : toAddTokenSet)
    {
      if (result.length() > 0)
      {
        result.append(delimiter);
      }
      result.append(toAddToken);
    }
    return result.toString();
  }

  /**
   * Adds all tokens in toAdd from toAddTo, if they are present there.
   *
   * @param toRemove the tokens to remove, or null to return toRemoveFrom
   * @param toRemoveFrom the base string to remove, or null to return null
   * @param delimiter the delimiter, not null.
   *
   * @return toRemoveFrom with the tokens in toRemove removed. Duplicate delimiters are also removed.
   */
  public static String removeTokens(final String toRemove, final String toRemoveFrom, final String delimiter)
  {
    if (toRemove == null)
    {
      return toRemoveFrom;
    }
    if (toRemoveFrom == null)
    {
      return null;
    }
    Assertions.assertNotNull(delimiter, "delimiter");

    // create token list from toRemove
    Set<String> toRemoveTokenSet = new HashSet<>();
    StringTokenizer tokenizer = new StringTokenizer(toRemove, delimiter);
    while (tokenizer.hasMoreTokens())
    {
      toRemoveTokenSet.add(tokenizer.nextToken());
    }

    // remove tokens from toAddTokenList which are already present in toAddTo
    StringBuilder result = new StringBuilder();
    tokenizer = new StringTokenizer(toRemoveFrom, delimiter);
    while (tokenizer.hasMoreTokens())
    {
      String token = tokenizer.nextToken();
      if (!toRemoveTokenSet.contains(token))
      {
        if (result.length() > 0)
        {
          result.append(delimiter);
        }
        result.append(token);
      }
    }
    return result.toString();
  }
}
