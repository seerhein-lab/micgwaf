package de.seerheinlab.micgwaf.export;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import de.seerheinlab.micgwaf.component.Component;
import de.seerheinlab.micgwaf.parser.HtmlParser;

/**
 * Exports the components as static file web site.
 */
public class HtmlExporter
{
  public File componentDir;

  public HtmlParser htmlParser;

  /**
   * Key is element name, value is attribute name of element attributes
   * which denote urls.
   */
  private final Map<String, List<String>> urlAttributesForNodeName = new HashMap<>();


  public HtmlExporter(File componentDir)
  {
    urlAttributesForNodeName.put("img", Arrays.asList(new String[] {"src"}));
    urlAttributesForNodeName.put("script", Arrays.asList(new String[] {"src"}));
    urlAttributesForNodeName.put("a", Arrays.asList(new String[] {"href"}));
    urlAttributesForNodeName.put("link", Arrays.asList(new String[] {"href"}));
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

  public void export(File targetDir) throws Exception
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
      String renderedComponent = null;
      Document parsedHtml;
      try (Writer writer = new StringWriter())
      {
        component.render(writer);
        renderedComponent = writer.toString();
        parsedHtml = parseXhtml(renderedComponent);
      }
      catch (Exception e)
      {
        throw new RuntimeException("Konnte Ergebnis nicht rendern oder parsen. Id: " + id + ", renderedComponent: " + renderedComponent, e);
      }

      String componentPathPrefix = computePathPrefix(id);
      replaceAbsolutePaths(componentPathPrefix, parsedHtml);

      try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), "UTF-8"))
      {
        StreamResult result = new StreamResult(writer);
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.transform(new DOMSource(parsedHtml), result);
      }
    }
  }

  private Document parseXhtml(String toParse) throws SAXException, IOException, ParserConfigurationException
  {
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

    dbf.setNamespaceAware(true);
    dbf.setValidating(false);

    DocumentBuilder db = dbf.newDocumentBuilder();
    Document document = db.parse(new InputSource(new StringReader(toParse)));
    return document;
  }

  private void replaceAbsolutePaths(String componentPathPrefix, Node node)
  {
    if (node.getNodeType() == Node.DOCUMENT_NODE)
    {
      NodeList children = node.getChildNodes();
      for (int i = 0; i < children.getLength(); ++i)
      {
        replaceAbsolutePaths(componentPathPrefix, children.item(i));
      }
      return;
    }
    if (node.getNodeType() != Node.ELEMENT_NODE)
    {
      return;
    }
    replaceUrlAttributes(node, componentPathPrefix);
    NodeList children = node.getChildNodes();
    for (int i = 0; i < children.getLength(); ++i)
    {
      replaceAbsolutePaths(componentPathPrefix, children.item(i));
    }
  }

  private void replaceUrlAttributes(Node node, String componentPathPrefix)
  {
    List<String> urlAttributes = urlAttributesForNodeName.get(node.getNodeName());
    if (urlAttributes != null)
    {
      for (String urlAttributeName : urlAttributes)
      {
        replaceUrlAttribute(node, urlAttributeName, componentPathPrefix);
      }
    }
  }

  private void replaceUrlAttribute(
      Node node,
      String urlAttributeName,
      String componentPathPrefix)
  {
    Node attributeNode = node.getAttributes().getNamedItem(urlAttributeName);
    if (attributeNode == null)
    {
      return;
    }
    String attributeValue = attributeNode.getNodeValue();
    if (attributeValue != null)
    {
      if (attributeValue.startsWith("/"))
      {
        attributeValue = componentPathPrefix + attributeValue.substring(1);
        if (attributeValue.endsWith(".xhtml"))
        {
          attributeValue = attributeValue.substring(0, attributeValue.length() - ".xhtml".length()) + ".htm";
        }
        attributeNode.setNodeValue(attributeValue);
      }
    }
  }

  private String computePathPrefix(String path)
  {
    StringBuilder result = new StringBuilder();
    int index = 0;
    while (path.indexOf('/', index) != - 1)
    {
      result.append("../");
      index = path.indexOf('/', index) + 1;
    }
    return result.toString();
  }

  public static void main(String[] args) throws Exception
  {
    HtmlExporter exporter = new HtmlExporter(new File("src/main/html"));
    exporter.export(new File("target/export"));
  }



}