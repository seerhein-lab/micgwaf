package de.seerheinlab.test.micgwaf.component.parts.validatedInput;


import de.seerheinlab.micgwaf.component.Component;
import de.seerheinlab.micgwaf.util.StringTools;

/**
 * This class represents the HTML element with m:id validatedInput.
 * Instances of this class are used whenever these elements are rendered
 * or when form date from a page containing these elements is processed.
 **/
public class ValidatedInput extends BaseValidatedInput
{
  /** Serial Version UID. */
  private static final long serialVersionUID = 1L;

  private static final String ERROR_DIV_CLASSES = "has-error";

  /**
  * Constructor.
  *
  * @param id the id of this component, or null.
  * @param parent the parent component. Can be null if this is a standalone component (e.g. a page).
  */
  public ValidatedInput(String id, Component parent)
  {
    super(id, parent);
  }

  public void addDivClasses(String text)
  {
    setDivClass(StringTools.addTokens(text, getDivClass() , " "));
  }

  public void removeDivClasses(String text)
  {
    setDivClass(StringTools.removeTokens(text, getDivClass() , " "));
  }

  public void markError()
  {
    addDivClasses(ERROR_DIV_CLASSES);
  }

  public void removeErrorMarker()
  {
    removeDivClasses(ERROR_DIV_CLASSES);
  }
}
