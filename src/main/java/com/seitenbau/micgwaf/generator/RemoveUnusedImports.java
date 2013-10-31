package com.seitenbau.micgwaf.generator;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

public class RemoveUnusedImports
{
  private static final String IMPORT = "import ";
  
  private static final String SEMICOLON = ";";
 
  public String removeUnusedImports(String toProcess)
  {
    Map<Integer, String> importedClasses = new LinkedHashMap<>();
    StringTokenizer tokenizer = new StringTokenizer(toProcess, "\n", true);
    int lineNumber = 0;
    while (tokenizer.hasMoreTokens())
    {
      String token = tokenizer.nextToken();
      if ("\n".equals(token))
      {
        continue;
      }
      if (token.startsWith(IMPORT) && token.endsWith(SEMICOLON))
      {
        token = token.replace("\r ", "");
        token = token.substring(IMPORT.length(), token.length() - SEMICOLON.length());
        token = token.replace("static ", "");
        int classNameStart = token.lastIndexOf(".") + 1;
        if (classNameStart > 0)
        {
          String className = token.substring(classNameStart);
          importedClasses.put(lineNumber, className);
        }
      }
      lineNumber++;
    }
    Set<Integer> linesToRemove = new HashSet<>();
    for (Map.Entry<Integer, String> importedClassEntry : importedClasses.entrySet())
    {
      String className = importedClassEntry.getValue();
      if (toProcess.indexOf(className) == toProcess.lastIndexOf(className))
      {
        linesToRemove.add(importedClassEntry.getKey());
      }
    }
    StringBuilder result = new StringBuilder(toProcess.length());
    tokenizer = new StringTokenizer(toProcess, "\n", true);
    lineNumber = 0;
    boolean skip = false;
    while (tokenizer.hasMoreTokens())
    {
      String token = tokenizer.nextToken();
      if ("\n".equals(token))
      {
        if (!skip)
        {
          result.append(token);
        }
        skip = false;
        continue;
      }
      if (!linesToRemove.contains(lineNumber))
      {
        result.append(token);
        skip = false;
      }
      else
      {
        skip = true;
      }
      lineNumber++;
    }
    return result.toString();
  }
}
