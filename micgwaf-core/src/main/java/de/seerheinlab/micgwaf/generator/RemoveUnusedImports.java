package de.seerheinlab.micgwaf.generator;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * Utility for removing unused imports from generated classes.
 *
 * The tool simply checks if imported class names appear more than once in the file;
 * if no, the import is removed.
 * This may be overly defensive, there are certainly situations where the import can be removed
 * although the class name appears more than once, however, such cases are rare and as ther is no
 * big harm in having unused imports, these cases are not consoidered.
 */
public class RemoveUnusedImports
{
  private static final String IMPORT = "import ";

  private static final String SEMICOLON = ";";

  /**
   * Removes unused imports from a java source file.
   *
   * @param toProcess the content of the java source file, not null.
   *
   * @return the content with removed imports, not null.
   */
  public String removeUnusedImports(String toProcess)
  {
    // key is the line number of the import, value is the unqualified name of the imported class.
    Map<Integer, String> unqualifiedImportedClasses = new LinkedHashMap<>();
    Set<String> qualifiedImportedClasses = new HashSet<>();
    StringTokenizer tokenizer = new StringTokenizer(toProcess, "\n", true);
    Set<Integer> linesToRemove = new HashSet<>();
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
        if (qualifiedImportedClasses.contains(token))
        {
          // duplicate import, remove
          linesToRemove.add(lineNumber);
        }
        else
        {
          qualifiedImportedClasses.add(token.trim());
          int classNameStart = token.lastIndexOf(".") + 1;
          if (classNameStart > 0)
          {
            String className = token.substring(classNameStart);
            unqualifiedImportedClasses.put(lineNumber, className);
          }
        }
      }
      lineNumber++;
    }
    for (Map.Entry<Integer, String> importedClassEntry : unqualifiedImportedClasses.entrySet())
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
