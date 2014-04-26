package de.seerheinlab.micgwaf.component;

/**
 * An interface for a class (typically a Component) which needs to change the HTML id of its children.
 * Typically this needs to be done if a component occurs in a loop, to produce unique HTML Ids.
 */
public interface ChangesChildHtmlId
{
  /**
   * Changes the HTML id of the given child and returns it.
   * 
   * @param child the child which HTML id should be changed, not null.
   * @param htmlId the HTML id to change, not null.
   * 
   * @return the changed HTML id, not null.
   */
  public String changeChildHtmlId(Component child, String htmlId);
}
