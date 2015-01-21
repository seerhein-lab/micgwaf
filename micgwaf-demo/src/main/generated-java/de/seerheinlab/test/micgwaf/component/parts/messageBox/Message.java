package de.seerheinlab.test.micgwaf.component.parts.messageBox;

import de.seerheinlab.micgwaf.component.Component;

public interface Message // TODO extends Component
{
  /**
   * Sets the text content of this HTML element.
   * HTML special characters are escaped in the rendered text.
   *
   * @param text the text content, not null.
   *
   * @return this component, not null   */
  public Component setTextContent(String text);

}
