package de.seerheinlab.micgwaf.generator;

import static de.seerheinlab.micgwaf.util.Assertions.assertNotNull;

/**
 * The fully qualified name of a java class.
 */
public class JavaClassName
{
  private final String packageName;

  private final String simpleName;

  public JavaClassName(String simpleName, String packageName)
  {
    assertNotNull(packageName, "packageName");
    assertNotNull(simpleName, "simpleName");
    this.packageName = packageName;
    this.simpleName = simpleName;
  }

  public JavaClassName(Class<?> clazz)
  {
    assertNotNull(clazz, "clazz");
    this.packageName = clazz.getPackage().getName();
    this.simpleName = clazz.getSimpleName();
  }

  /**
   * Returns the package of the java class.
   *
   * @return the package name, not null.
   */
  public String getPackage()
  {
    return packageName;
  }

  /**
   * Returns the simple (unqualified) name of the java class.
   *
   * @return the simple name, not null.
   */
  public String getSimpleName()
  {
    return simpleName;
  }

  /**
   * Returns the fully qualified name of the java class.
   *
   * @return the fully qualified name, not null.
   */
  public String getName()
  {
    if (!"".equals(packageName))
    {
      return packageName + "." + simpleName;
    }
    return simpleName;
  }

  /**
   * Converts the class name to the name of the java source file.
   *
   * @return the name of the java source file, relative to the root package directory, not null.
   */
  public String getSourceFile()
  {
    String qualifiedName = getName();
    String result = qualifiedName.replace(".", "/") + ".java";
    return result;
  }

  /**
   * Returns whether this class name references the passed class <code>clazz</code>.
   *
   * @param clazz the class to check against, not null.
   *
   * @return true it this class name describes the name of <code>clazz</code>, false otherwise.
   *
   * @throws NullPointerException if <code>clazz</code> is null.
   */
  public boolean isNameFor(Class<?> clazz)
  {
    assertNotNull(clazz, "clazz");
    return (packageName.equals(clazz.getPackage().getName()) && simpleName.equals(clazz.getSimpleName()));
  }
  @Override
  public int hashCode()
  {
    return packageName.hashCode() * 31 + simpleName.hashCode();
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
    JavaClassName other = (JavaClassName) obj;
    if (!packageName.equals(other.packageName))
    {
      return false;
    }
    return simpleName.equals(other.simpleName);
  }

  @Override
  public String toString()
  {
    return packageName + "." + simpleName;
  }
}
