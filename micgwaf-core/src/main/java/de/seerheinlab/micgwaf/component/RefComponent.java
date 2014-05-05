package de.seerheinlab.micgwaf.component;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.seerheinlab.micgwaf.component.PartListComponent.ComponentPart;

/**
 * Component which references another component by its id.
 * 
 * WARNING this component temporarily changes the component tree while rendering. 
 */
public class RefComponent extends Component implements ChangesChildHtmlId
{
  /** Serial Version UID. */
  private static final long serialVersionUID = 1L;

  public String refid;
  
  public Component referencedComponent;
  
  public Map<String, String> variableValues = new HashMap<>();
  
  public RefComponent(String refid, String id, Component parent)
  {
    super(id, parent);
    this.refid = refid;
  }
  
  public List<Component> getChildren()
  {
    return new ArrayList<>();
  }

  
  @Override
  public void resolveComponentReferences(Map<String, ? extends Component> allComponents)
  {
    super.resolveComponentReferences(allComponents);
    referencedComponent = allComponents.get(refid);
    if (referencedComponent == null)
    {
      throw new IllegalStateException("unknown refid " + refid);
    }
  }

  /**
   * Renders this component.
   * 
   * NOTE: This method is not fit to be used in multiple instances, because
   * it temporarily changes the referenced component's parent attribute.
   */
  @Override
  public void render(Writer writer) throws IOException
  {
    if (referencedComponent == null)
    {
      throw new IllegalStateException("No component bound to component reference, refid=" + refid);
    }
    Component oldParent= referencedComponent.getParent();
    Map<String, String> referencedComponentOldVariableValues = new HashMap<>();
    referencedComponent.setParent(this);
    setVariablesInPartListComponent(referencedComponent, variableValues, referencedComponentOldVariableValues);
    Map<String, String> childComponentOldVariableValues = new HashMap<>();
    if (referencedComponent instanceof HtmlElementComponent)
    {
      for (Component child : referencedComponent.getChildren())
      {
        setVariablesInPartListComponent(child, variableValues, childComponentOldVariableValues);
      }
    }
    referencedComponent.render(writer);
    // restore original state
    referencedComponent.setParent(oldParent);
    setVariablesInPartListComponent(referencedComponent, referencedComponentOldVariableValues, null);
    if (referencedComponent instanceof HtmlElementComponent)
    {
      for (Component child : referencedComponent.getChildren())
      {
        setVariablesInPartListComponent(child, childComponentOldVariableValues, null);
      }
    }
  }

  private void setVariablesInPartListComponent(
      Component component, 
      Map<String, String> variableValues, 
      Map<String, String> oldVariableValues)
  {
    if (component instanceof PartListComponent)
    {
      for (ComponentPart part : ((PartListComponent) component).parts)
      {
        if (variableValues.containsKey(part.variableName))
        {
          if (oldVariableValues != null)
          {
            oldVariableValues.put(part.variableName, variableValues.get(part.variableName));
          }
          part.variableDefaultValue = variableValues.get(part.variableName);
        }
      }
    }
  }

  @Override
  public String toString()
  {
    return "RefComponent [refid=" + refid + ", referencedComponent="
        + referencedComponent + "]";
  }

  /**
   * If the id of this component and its parent is non null, 
   * the id of this component is added as a prefix to the passed id and returned;
   * otherwise, the passed id is returned unchanged.
   *
   * @param child the child component from which this method is called, not used here.
   * @param htmlId the id to prepend the id to, not null.
   *
   * @returned the prefixed id, not null.
   */
  @Override
  public String changeChildHtmlId(Component child, String htmlId)
  {
    // do not prefix page id (page component has no parent) 
    // or component which is the endpoint of the reference (has same id)
    if (parent != null && !htmlId.equals(refid)) 
    {
      return refid + ":" + htmlId;
    }
    return htmlId;
  }
}
