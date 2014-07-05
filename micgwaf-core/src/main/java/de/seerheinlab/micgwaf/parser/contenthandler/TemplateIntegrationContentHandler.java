package de.seerheinlab.micgwaf.parser.contenthandler;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import de.seerheinlab.micgwaf.component.Component;
import de.seerheinlab.micgwaf.component.DefineComponent;
import de.seerheinlab.micgwaf.component.PartListComponent;
import de.seerheinlab.micgwaf.component.TemplateIntegration;
import de.seerheinlab.micgwaf.util.Constants;

public class TemplateIntegrationContentHandler extends ContentHandler
{
  public static final String TEMPLATE_ID_ATTR = "templateId";

  public static final String USE_TEMPLATE_ELEMENT_NAME = "useTemplate";

  public TemplateIntegration result = new TemplateIntegration(null);

  public String templateId;

  @Override
  public void startElement(
        String uri,
        String localName,
        String qName,
        Attributes attributes)
      throws SAXException
  {
    // check for useTemplate elements
    if (!Constants.XML_NAMESPACE.equals(uri)
        || !TemplateIntegrationContentHandler.USE_TEMPLATE_ELEMENT_NAME.equals(localName))
    {
      throw new SAXException("unknown Element " + uri + ":" + localName);
    }
    templateId = attributes.getValue(TEMPLATE_ID_ATTR);
    if (templateId == null || "".equals(templateId.trim()))
    {
      throw new SAXException("Attribute " + TEMPLATE_ID_ATTR
          + " is required on element " + qName);
    }
  }


  @Override
  public void child(Component child) throws SAXException
  {
    if (child instanceof PartListComponent)
    {
      PartListComponent snippetListChild = (PartListComponent) child;
      for (PartListComponent.ComponentPart part : snippetListChild.parts)
      {
        if (part.htmlSnippet != null)
        {
          if (!"".equals(part.htmlSnippet.trim()))
          {
            throw new IllegalArgumentException(
                "Children of "
                + TemplateIntegrationContentHandler.USE_TEMPLATE_ELEMENT_NAME
                + " elements must not be markup, found "
                + part.htmlSnippet);
          }
          // ignore whitespace
          continue;
        }
        else if (part.variableName != null)
        {
          if (!"".equals(part.variableName.trim()))
          {
            throw new IllegalArgumentException(
                "Children of "
                + TemplateIntegrationContentHandler.USE_TEMPLATE_ELEMENT_NAME
                + " elements must not be markup, found "
                + part.variableName);
          }
          // ignore whitespace
          continue;
        }
        else
        {
          handleComponentChild(part.component);
        }
      }
      return;
    }
    handleComponentChild(child);
  }


  private void handleComponentChild(Component child)
  {
    if (!(child instanceof DefineComponent))
    {
      throw new IllegalArgumentException("Children of "
          + TemplateIntegrationContentHandler.USE_TEMPLATE_ELEMENT_NAME
          + " elements must be "
          + DefineContentHandler.DEFINE_ELEMENT_NAME
          + " elements, found "
          + child.getClass() + " component instead");
    }
    DefineComponent defineComponentChild = (DefineComponent) child;
    result.definitions.put(defineComponentChild.name, child);
  }

  @Override
  public TemplateIntegration finished() throws SAXException
  {
    result.templateId = templateId;
    return result;
  }
}