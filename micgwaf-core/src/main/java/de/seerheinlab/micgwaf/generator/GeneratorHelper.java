package de.seerheinlab.micgwaf.generator;

/**
 * Helper methods for generators.
 */
public class GeneratorHelper
{
  /**
   * Returns the indent String for an indent number.
   *
   * @param indent the indent index.
   *
   * @return the corresponding indent String, not null.
   */
  public static String getIndentString(int indent)
  {
    // TODO cache result
    StringBuilder result = new StringBuilder(indent);
    for (int i = 0; i < indent; ++i)
    {
      result.append(" ");
    }
    return result.toString();
  }

  /**
   * Indents a String with the given number of spaced.
   *
   * @param toIndent the String to indent, not null.
   * @param indent the indent, not null.
   *
   * @return the indented String.
   */
  public static String indent(String toIndent, int indent)
  {
    String indentString = getIndentString(indent);
    return indentString + toIndent.replace("\n", "\n"+ indentString);
  }
}
