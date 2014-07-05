package de.seerheinlab.micgwaf.component;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * A component representing a HTML Form.
 */
public class FormComponent extends HtmlElementComponent
{
  /** Serial Version UID. */
  private static final long serialVersionUID = 1L;

  /** The name of the form HTML element. */
  public static final String FORM_ELEM = "form";

  /**
   * Whether this form was submitted.
   * This field is set during the processRequest method of the component
   * and cleared in the afterRender method.
   */
  public boolean submitted;

  /**
   * Constructor without id.
   *
   * @param parent the parent component, or null for a standalone component.
   */
  public FormComponent(Component parent)
  {
    super(parent);
  }

  /**
   * Constructor.
   *
   * @param id the id of the component, or null if the component has no id.
   * @param parent the parent component, or null for a standalone component.
   */
  public FormComponent(String id, Component parent)
  {
    super(FORM_ELEM, id, parent);
  }

  @Override
  public Component processRequest(HttpServletRequest request)
  {
    Component result = super.processRequest(request);
    checkSubmitted(this);
    return result;
  }

  @Override
  public Map<String, String> getRenderedAttributes()
  {
    Map<String, String> renderedAttributes = super.getRenderedAttributes();
    // TODO: check whether this should be set by the PRG handler and not fixed in the component
    renderedAttributes.put("method", "POST");
    return renderedAttributes;
  }

  /**
   * Checks whether the passed component was submitted and sets the submitted flag of this class accordingly.
   * The check works by asking all (child and self) input components whether they were submitted,
   * if one of them was submitted, the form was submitted.
   *
   * @param component the component to check, not null.
   */
  public void checkSubmitted(Component component)
  {
    if (submitted)
    {
      return;
    }
    if (component instanceof InputComponent)
    {
      submitted = ((InputComponent) component).submitted;
      if (submitted)
      {
        return;
      }
    }
    for (Component child : component.getChildren())
    {
      checkSubmitted(child);
    }
  }

  @Override
  public void afterRender()
  {
    super.afterRender();
    submitted = false;
  }
}
