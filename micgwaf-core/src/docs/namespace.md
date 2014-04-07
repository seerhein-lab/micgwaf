The micgwaf namespace
===================================

The following attributes in the micgwaf namespace (http://seerhein-lab.de/micgwaf) are recognized by micgwaf:

 * id: Denotes this HTML element as accessible from micgwaf. If the HTML element is an element
   which needs special treatment, a special component is created.
   Elements which create special components are:
   - button, input: create a de.seerheinlab.micgwaf.component.InputComponent.InputComponent
   - form: create a de.seerheinlab.micgwaf.component.InputComponent.InputComponent
   All other components create a  de.seerheinlab.micgwaf.component.HtmlElementComponent.HtmlElementComponent.
   
 * TODO other attributes
 
The following elements in the micgwaf namespace (http://seerhein-lab.de/micgwaf) are recognized by micgwaf:
 
  