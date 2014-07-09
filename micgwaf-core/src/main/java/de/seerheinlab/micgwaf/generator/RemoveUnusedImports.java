package de.seerheinlab.micgwaf.generator;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Utility for removing unused imports from generated classes.
 *
 * The tool simply checks if imported class names appears in class definition, class body
 * or class annotations, if no, the import is removed.
 * This may be overly defensive, there are certainly situations where the import can be removed
 * although the class name appears more than once, however, such cases are rare and as there is no
 * big harm in having unused imports, these cases are not considered.
 */
public class RemoveUnusedImports
{
  /**
   * Removes unused imports from a java source file.
   *
   * @param toProcess the content of the java source file, not null.
   */
  public void removeUnusedImports(GeneratedClass toProcess)
  {
    List<String> imports = toProcess.calculateImports();
    toProcess.clearImportsRecursively();
    toProcess.imports.addAll(imports);

    // key is the number of the import, value is the unqualified name of the imported class.
    Map<Integer, String> unqualifiedImportedClasses = new LinkedHashMap<>();
    Set<String> qualifiedImportedClasses = new HashSet<>();
    Set<Integer> importsToRemove = new HashSet<>();
    int importNumber = 0;
    for (String importedClass : toProcess.imports)
    {
      if (qualifiedImportedClasses.contains(importedClass))
      {
        // duplicate Import, remove
        importsToRemove.add(importNumber);
        importNumber++;
        continue;
      }
      qualifiedImportedClasses.add(importedClass);
      int classNameStart = importedClass.lastIndexOf(".") + 1;
      if (classNameStart > 0)
      {
        String className = importedClass.substring(classNameStart);
        unqualifiedImportedClasses.put(importNumber, className);
      }
      importNumber++;
    }
    for (Map.Entry<Integer, String> importedClassEntry : unqualifiedImportedClasses.entrySet())
    {
      String className = importedClassEntry.getValue();
      if (!classNameUsed(className, toProcess))
      {
        importsToRemove.add(importedClassEntry.getKey());
      }
    }

    importNumber = 0;
    Iterator<String> importIt = toProcess.imports.iterator();
    while (importIt.hasNext())
    {
      importIt.next();
      if (importsToRemove.contains(importNumber))
      {
        importIt.remove();
      }
      ++importNumber;
    }
  }

  protected boolean classNameUsed(String className, GeneratedClass generatedClass)
  {
    for (String classAnnotation : generatedClass.classAnnotations)
    {
      if (classAnnotation.contains(className))
      {
        return true;
      }
    }
    if (generatedClass.classDefinition.indexOf(className) != -1)
    {
      return true;
    }
    if (generatedClass.classBody.indexOf(className) != -1)
    {
      return true;
    }
    for (GeneratedClass innerClass : generatedClass.innerClasses)
    {
      if (classNameUsed(className, innerClass))
      {
        return true;
      }
    }
    return false;
  }
}
