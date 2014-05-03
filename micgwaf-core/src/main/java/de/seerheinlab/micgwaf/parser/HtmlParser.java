package de.seerheinlab.micgwaf.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import de.seerheinlab.micgwaf.component.Component;

/**
 * Parses HTML files and generates components from them.
 * The parsed components can render the HTML source file again.
 */
public class HtmlParser
{
  private static final String SAX_NAMESPACE_FEATURE_NAME = "http://xml.org/sax/features/namespaces";
  
  /**
   * Parses the HTML files in a directory and generates components from them.
   * Only files directly in the given directory with the suffix .xhtml are parsed,
   * all other files are ignored.
   * The created components are stored in the returned map.
   * The map key is the component id, and the map value is the root component for a HTML source file.
   * 
   * @param sourceDirectory the directory where the parsed files reside, not null.
   * 
   * @return the map with the parsed components, one entry for each parsed file.
   */
  public Map<String, Component> readComponents(File sourceDirectory)
  {
    if (sourceDirectory == null)
    {
      throw new NullPointerException("sourceDirectory must not be null");
    }
    if (!sourceDirectory.isDirectory())
    {
      throw new IllegalArgumentException(
          sourceDirectory.getAbsolutePath() + " is no Directory");
    }
    
    Map<String, Component> result = new HashMap<>();
    File[] files = sourceDirectory.listFiles();
    for (File file : files)
    {
      String fileName = file.getName();
      if (fileName.endsWith(".xhtml"))
      {
        try
        {
          FileInputStream inputStream = new FileInputStream(file);
          Component component = parse(inputStream);
          if (component.getId() == null)
          {
            component.setId(fileName.substring(0, fileName.length() - 6));
          }
          result.put(component.getId(), component);
        }
        catch (SAXException | IOException | ParserConfigurationException e)
        {
          throw new RuntimeException(e);
        } 
      }
    }
    return result;
  }  
  
  /**
   * Parses a HTML file and returns the root component.
   * 
   * @param inputStream the input stream with the content of the file, not null.
   * 
   * @return the root component, not null.
   * 
   * @throws SAXException if XML parsing fails.
   * @throws IOException if reading from the stream fails.
   * @throws ParserConfigurationException if the SAX parser cannot be configured.
   */
  public Component parse(InputStream inputStream) 
      throws SAXException, IOException, ParserConfigurationException
  {
    SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
    SAXParser saxParser = saxParserFactory.newSAXParser(); 
    XMLReader xmlReader = saxParser.getXMLReader(); 
    DelegatingContentHandler handler = new DelegatingContentHandler();
    xmlReader.setContentHandler(handler);
    xmlReader.setEntityResolver(handler);
    xmlReader.setDTDHandler(handler);
    xmlReader.setErrorHandler(handler);
    xmlReader.setFeature(SAX_NAMESPACE_FEATURE_NAME, true);
    xmlReader.parse(new InputSource(inputStream)); 
    return handler.currentResult;
  }
}
