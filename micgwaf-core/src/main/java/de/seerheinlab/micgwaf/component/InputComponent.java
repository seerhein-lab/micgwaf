package de.seerheinlab.micgwaf.component;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public class InputComponent extends HtmlElementComponent
{
  /** The name of the HTML input element. */
  public static final String INPUT_ELEM = "input";

  /** The name of the HTML button element. */
  public static final String BUTTON_ELEM = "button";

  /** The value of the type attribute which signifies a submit button. */
  public static final String SUBMIT_TYPE = "submit";

  /** The name of the type attribute. */
  public static final String TYPE_ATTR = "type";

  /** The name of the name attribute. */
  public static final String NAME_ATTR = "name";

  /** The name of the value attribute. */
  public static final String VALUE_ATTR = "value";

  /** Serial Version UID */
  private static final long serialVersionUID = 1L;

  /** Whether this input element received a submitted value in the current HTML request. */
  public boolean submitted;

  /**
   * The value which was submitted in the current HTML request.
   * For non-button components, this value is rendered as content of the value attribute
   * when this component is re-rendered.
   */
  public String submittedValue;

  /**
   * Constructor without id.
   *
   * @param parent the parent component, or null if this is a standalone component (e.g. a page)
   */
  public InputComponent(Component parent)
  {
    super(parent);
  }

  /**
   * Constructor with id.
   *
   * @param id the id of the component, may be null.
   *        If set, it should be unique in the current context (e.g. page).
   * @param parent the parent component, or null if this is a standalone component (e.g. a page)
   */
  public InputComponent(String id, Component parent)
  {
    super(id, parent);
  }

  /**
   * Constructor.
   *
   * @param elementName the name of the html element, should not be null.
   * @param id the id of the component, may be null.
   *        If set, it should be unique in the current context (e.g. page).
   * @param parent the parent component, or null if this is a standalone component (e.g. a page)
   */
  public InputComponent(String elementName, String id, Component parent)
  {
    super(elementName, id, parent);
  }

  @Override
  public Component processRequest(HttpServletRequest request)
  {
    String nameAttr = getHtmlId(attributes.get(NAME_ATTR));
    if (nameAttr != null)
    {
      submittedValue = request.getParameter(nameAttr);
      if (submittedValue != null)
      {
        submitted = true;
        if (!isButton())
        {
          // in case this component is re-rendered
          attributes.put(VALUE_ATTR, submittedValue);
        }
      }
    }
    return super.processRequest(request);
  }

  /**
   * Returns the attributes to be used for rendering.
   * This implementation adds the id of the component as id attribute, if not null,
   * and it corrects the values of id and name attributes.
   * This method may be overwritten in subclasses.
   *
   * @return the attributes for rendering, in a map with a defined iteration order, not null.
   */
  @Override
  public Map<String, String> getRenderedAttributes()
  {
    Map<String, String> renderedAttributes = super.getRenderedAttributes();
    if (renderedAttributes.containsKey(NAME_ATTR))
    {
      renderedAttributes.put(NAME_ATTR, getHtmlId(renderedAttributes.get(NAME_ATTR)));
    }
    return renderedAttributes;
  }

  @Override
  public void afterRender()
  {
    super.afterRender();
    this.submitted = false;
    this.submittedValue = null;
  }

  /**
   * Checks whether this input component is a button or not.
   *
   * @return true if this component is a button, false otherwise.
   */
  public boolean isButton()
  {
    if (BUTTON_ELEM.equals(elementName)
        || (INPUT_ELEM.equals(elementName)
            && SUBMIT_TYPE.equals(attributes.get(TYPE_ATTR))))
    {
      return true;
    }
    return false;
  }

  /**
   * Sets the value of the "value" attribute.
   * Note: this does not affect the submittedValue property of this component.
   *
   * @param value the new value of the "value" attribute, or null to remove the value attribute.
   */
  public void setValue(String value)
  {
    if (value == null)
    {
      attributes.remove(VALUE_ATTR);
      return;
    }
    attributes.put(VALUE_ATTR, value);
  }
}
