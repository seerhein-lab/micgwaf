package de.seerheinlab.micgwaf.maven;

import java.io.File;
import java.io.IOException;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

import de.seerheinlab.micgwaf.generator.Generator;

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
   * @parameter expression="${micgwaf.source.directory}" default-value="${project.basedir}/src/main/html"
   * @required
   */
  private File sourceDir;

  /**
   * The target directory of the generated base files.
   *
   * @parameter expression="${micgwaf.target.base.directory}" default-value="${project.basedir}/target/generated-sources"
   * @required
   */
  private File baseTargetDir;

  /**
   * The target directory of the generated extension files.
   *
   * @parameter expression="${micgwaf.target.extensions.directory}" default-value="${project.basedir}/src/main/generated-java"
   * @required
   */
  private File extensionsTargetDir;

  /**
   * The Maven project this plugin runs in.
   *
   * @parameter expression="${project}"
   * @required
   * @readonly
   */
  private MavenProject project;

  /**
   * The package in which all generated code should live in.
   *
   * @parameter expression="${micgwaf.target.package}"
   * @required
   */
  private String targetPackage;

  /**
   * Configures and runs the micgwaf generator.
   */
  public void execute() throws MojoExecutionException
  {
    Generator generator = new Generator();
    
    getLog().debug("Running generation");
    getLog().debug("sourceDir: " + sourceDir);
    getLog().debug("baseTargetDir: " + baseTargetDir);
    getLog().debug("extensionsTargetDir: " + extensionsTargetDir);
    getLog().debug("targetPackage: " + targetPackage);
    try 
    {
      generator.generate(sourceDir, baseTargetDir, extensionsTargetDir, targetPackage);
    } 
    catch (IOException e) 
    {
      throw new MojoExecutionException("Generation failed", e);
	}
    getLog().debug("Generation successful");

    project.addCompileSourceRoot(baseTargetDir.toString());
    getLog().debug("Added " + baseTargetDir.toString() + " as compile source root");
    project.addCompileSourceRoot(extensionsTargetDir.toString());
    getLog().debug("Added " + extensionsTargetDir.toString() + " as compile source root");
    }

    /**
     * Sets the source directory.
     *
     * @param sourceDir the source directory.
     */
    public void setSourceDir(final File sourceDir)
    {
        this.sourceDir = sourceDir;
    }

    /**
     * Sets the target directory for the base classes.
     *
     * @param baseTargetDir the target directory for the base classes.
     */
    public void setBaseTargetDir(final File baseTargetDir)
    {
        this.baseTargetDir = baseTargetDir;
    }

    /**
     * Sets the target directory for the extension classes.
     *
     * @param baseTargetDir the target directory for the extension classes.
     */
    public void setExtensionsTargetDir(final File extensionsTargetDir)
    {
        this.extensionsTargetDir = extensionsTargetDir;
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
