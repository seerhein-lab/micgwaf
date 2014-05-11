Components
==========

In micgwaf, a component is a java class which renders HTML and can process the response the browser
sends back as answer to the rendered HTML.
A good example for a component is de.seerheinlab.micgwaf.component.InputComponent. It renders an input field
and processes the value the user types into the input field.
However, components can be more simple (like just rendering HTML, e.g. a header), or more complex 
(e.g. a whole page is also a component im micgwaf.)

In micgwaf, a component needs to follow the component contract, laid out in the interface
de.seerheinlab.micgwaf.component.Component.

A component should have a constructor with the id of the component and the parent component as parameter
(for page components instantiated by micgwaf or for components in component libraries, such a constructor is required).
The id can be null, although for most components this is not recommended.
The parent may be null only for standalone components like page components.
There is a bi-directional relation between parent and child components.
Typically, the component tree is constructed bottom-up (starting from the page component), so it is
the responsibility of the parent component to establish the bi-directional relationship properly. 

A Component has the following responsibilities
- Allow its children to render themselves in the render phase
- Allow its children to process the request in the process phase
- Allow its children to resolve component references. This responsibility is usually taken care of by the 
  method resolveComponentReferences(Map<String, ? extends Component>) of Component itself.
  Components which do NOT use the handling of children implemented in Component need to override this method.
  It is only relevant in HTML Development mode (when serving the HTML files directly)
- render HTML id attributes using the result of the Component.getHtmlId(String) methods. 
  This method generates unique ids even if the component is rendered several times (e.g. in loops).

Component lifecycle
-------------------

The components act in two different phases, nemely the render phase and the process phase. 
(TODO resolve component reference phase)

TODO

Framework components
-------------------

There are a number of components built into the micgwaf framework itself. 
They live in the package de.seerheinlab.micgwaf.component.
This package contains the component interface.
It contains components which are used in "normal" development of Web applications, and components which are 
internally used by micgwaf when parsing HTML files. 
The normal implementations are :

 - SnippetComponent: This component simply prints a HTML snippet as is during the render phase.
   It ignores the process phase.
   
The implementations used mainly during parsing are

- PartListComponent: Contains HTML snippets and child components.
  Is used by parsing whenever plain HTM snippets appear (TODO check: not SnippetComponent?)
- TemplateIntegration: Component which binds together the template to be used in a HTML file
  and the template snippet defined in that page. Represents the m:useTemplate tag.
