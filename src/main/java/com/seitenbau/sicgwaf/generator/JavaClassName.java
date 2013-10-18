package com.seitenbau.sicgwaf.generator;

import static com.seitenbau.sicgwaf.util.Assertions.*;

/**
 * The fully qualified name of a java class.
 */
public class JavaClassName
{
  private String packageName;

  private String simpleName;
  
  public JavaClassName(String packageName, String simpleName)
  {
    assertNotNull(packageName, "packageName");
    assertNotNull(simpleName, "simpleName");
    this.packageName = packageName;
    this.simpleName = simpleName;
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
}
