package de.seerheinlab.micgwaf.generator.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Configuration of the generator. Contains settings for the naming of the generated classes.
 */
public class GeneratorConfiguration
{
  protected static final String BASE_CLASS_PREFIX = "baseClassPrefix";

  protected static final String BASE_CLASS_SUFFIX = "baseClassSuffix";

  protected static final String EXTENSION_CLASS_PREFIX = "extensionClassPrefix";

  protected static final String EXTENSION_CLASS_SUFFIX = "extensionClassSuffix";

  protected static final String BASE_CLASS_WITHOUT_EXTENSION_NAMED_LIKE_BASE_CLASS
      = "baseClassWithoutExtensionNamedLikeBaseClass";

  /**
   * The properties containing the configuration.
   */
  public Properties generationProperties = new Properties();

  /**
   * Constructor.
   *
   * @param classPathResource the class path resource to load the configuration from, not null.
   *
   * @throws IllegalArgumentException if classPathResource does not exist or cannot be read.
   */
  public GeneratorConfiguration(String classPathResource)
  {
    InputStream configStream = GeneratorConfiguration.class.getResourceAsStream(classPathResource);
    if (configStream == null)
    {
      throw new IllegalArgumentException("classPathResource " + classPathResource + " is not accessible");
    }
    try
    {
      generationProperties.load(configStream);
    }
    catch (IOException e)
    {
      throw new IllegalArgumentException("Could not load generator configuration from classPathResource "
          + classPathResource,
          e);
    }
  }

  /**
   * Returns the prefix for the class name of all generated base classes.
   *
   * @return the prefix for the base classes, not null.
   */
  public String getBaseClassPrefix()
  {
    String result = generationProperties.getProperty(BASE_CLASS_PREFIX);
    if (result == null)
    {
      return "";
    }
    return result;
  }

  /**
   * Returns the suffix for the class name of all generated base classes.
   *
   * @return the suffix for the base classes, not null.
   */
  public String getBaseClassSuffix()
  {
    String result = generationProperties.getProperty(BASE_CLASS_SUFFIX);
    if (result == null)
    {
      return "";
    }
    return result;
  }

  /**
   * Returns the prefix for the class name of all generated extension classes.
   *
   * @return the prefix for the extension classes, not null.
   */
  public String getExtensionClassPrefix()
  {
    String result = generationProperties.getProperty(EXTENSION_CLASS_PREFIX);
    if (result == null)
    {
      return "";
    }
    return result;
  }

  /**
   * Returns the suffix for the class name of all generated extension classes.
   *
   * @return the suffix for the extension classes, not null.
   */
  public String getExtensionClassSuffix()
  {
    String result = generationProperties.getProperty(EXTENSION_CLASS_SUFFIX);
    if (result == null)
    {
      return "";
    }
    return result;
  }

  /**
   * Returns whether naming of classes without an extension should follow the naming for base classes (true)
   * or the naming of extension classes (false)
   *
   * @return whether all base classes should be named after the base class naming rule.
   */
  public boolean isBaseClassWithoutExtensionNamedLikeBaseClasses()
  {
    String result = generationProperties.getProperty(BASE_CLASS_WITHOUT_EXTENSION_NAMED_LIKE_BASE_CLASS);
    if (result == null)
    {
      return false;
    }
    return Boolean.parseBoolean(result);
  }
}
