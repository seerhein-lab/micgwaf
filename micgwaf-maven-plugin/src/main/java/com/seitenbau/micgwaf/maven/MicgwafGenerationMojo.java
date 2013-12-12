package com.seitenbau.micgwaf.maven;

import java.io.File;
import java.io.IOException;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

import com.seitenbau.micgwaf.generator.Generator;

/**
 * Generates micgwaf components from html files
 *
 * @goal generate
 */
public class MicgwafGenerationMojo extends AbstractMojo implements Mojo
{
  /**
   * The source directory of the html files.
   *
   * @parameter expression="micgwaf.source.directory" default-value="false"
   * @required
   */
  private File sourceDirectory;

  /**
   * The target directory of the generated base files.
   *
   * @parameter expression="micgwaf.target.base.directory" default-value="false"
   * @required
   */
  private File baseTargetDirectory;

  /**
   * The target directory of the generated extension files.
   *
   * @parameter expression="micgwaf.target.extensions.directory" default-value="false"
   * @required
   */
  private File extensionsTargetDirectory;

  /**
   * The package in which all generated code should live in.
   *
   * @parameter
   * @required
   */
  private String targetPackage;

  /**
   * The Maven project this plugin runs in.
   *
   * @parameter expression="${project}"
   * @required
   * @readonly
   */
  private MavenProject project;

  /**
   * Configures and runs the micgwaf generator.
   */
  public void execute() throws MojoExecutionException
  {
    Generator generator = new Generator();
    
    getLog().debug("Running generation");
    try 
    {
      generator.generate(sourceDirectory, baseTargetDirectory, extensionsTargetDirectory, targetPackage);
    } 
    catch (IOException e) 
    {
      throw new MojoExecutionException("Generation failed", e);
	}
    getLog().debug("Generation successful");

    project.addCompileSourceRoot(baseTargetDirectory.toString());
    getLog().debug("Added " + baseTargetDirectory.toString() + " as compile source root");
    project.addCompileSourceRoot(extensionsTargetDirectory.toString());
    getLog().debug("Added " + extensionsTargetDirectory.toString() + " as compile source root");
    }

    /**
     * Sets the source directory.
     *
     * @param sourceDirectory the source directory.
     */
    public void setSourceDirectory(final File sourceDirectory)
    {
        this.sourceDirectory = sourceDirectory;
    }

    /**
     * Sets the target directory for the base classes.
     *
     * @param baseTargetDirectory the target directory for the base classes.
     */
    public void setBaseTargetDirectory(final File baseTargetDirectory)
    {
        this.baseTargetDirectory = baseTargetDirectory;
    }

    /**
     * Sets the target directory for the extension classes.
     *
     * @param baseTargetDirectory the target directory for the extension classes.
     */
    public void setExtensionsTargetDirectory(final File extensionsTargetDirectory)
    {
        this.extensionsTargetDirectory = extensionsTargetDirectory;
    }

    /**
     * Sets the target package.
     *
     * @param targetPackage the target package.
     */
    public void setSourceDirectory(final String targetPackage)
    {
        this.targetPackage = targetPackage;
    }

    /**
     * Sets the maven project this mojo runs in.
     *
     * @param project the maven project this mojo runs in.
     */
    public void setProject(final MavenProject project)
    {
        this.project = project;
    }
}
