package de.seerheinlab.micgwaf.export;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map;

import de.seerheinlab.micgwaf.component.Component;
import de.seerheinlab.micgwaf.parser.HtmlParser;

/**
 * Exports the components as static file web site.
 */
public class HtmlExporter
{
  public File componentDir;

  public HtmlParser htmlParser;

  public HtmlExporter(File componentDir)
  {
    if (!componentDir.exists())
    {
      throw new RuntimeException("Cannot access component directory " + componentDir.getAbsolutePath());
    }
    if (!componentDir.isDirectory())
    {
      throw new RuntimeException(
          "The component directory " + componentDir.getAbsolutePath() + " is not a directory");
    }
    this.componentDir = componentDir;
    htmlParser = new HtmlParser();
  }

  public void export(File targetDir) throws IOException
  {
    targetDir.mkdirs();
    Map<String, Component> allComponents = htmlParser.readComponents(componentDir);
    for (Map.Entry<String, Component> componentEntry : allComponents.entrySet())
    {
      Component component = componentEntry.getValue();
      String id = componentEntry.getKey();
      component.resolveComponentReferences(allComponents);
      File file = new File(targetDir, id + ".htm");
      file.getParentFile().mkdirs();
      try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), "UTF-8"))
      {
        component.render(writer);
      }
    }
  }

  public static void main(String[] args) throws IOException
  {
    HtmlExporter exporter = new HtmlExporter(new File("src/main/html"));
    exporter.export(new File("target/export"));
  }

}