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

Note: the component lifecycle is determined by the matching handler.
      Here it is assumed that the default handlers (i.e. AjaxHandler and PRGHandler are used).
      By changing the registered request handlers in the application, the lifecycle can be customized. 

In a normal (non-ajax) request, the components act in two different phases, 
namely the process phase and the render phase. 
(TODO resolve component reference phase)
(TODO note differences for ajax request)

During the process phase, the HTTP request is analyzed and any actions which are triggered by the 
HTTP request are executed. The page to be rendered is also determined in the process phase.

During the render phase, the page to be rendered renders itself. It is considered bad style if the
state of a component changes during the render phase. 

### process phase

(see de.seerheinlab.micgwaf.requesthandler.PRGHandler.process(HttpServletRequest, HttpServletResponse)

The process page is only entered if the request is a HTTP POST request. 
A HTTP GET request does not trigger the process phase, it enters the render phase directly.

In the process phase, the first action is to read the state of the last rendered page component 
from the session.
This is done by inspecting the action parameter (TODO and the mount path, why?) and returning the matching
page component from a component map stored in the session. 
In case that no state can be determined, the process phase is aborted and the render phase is started.

Usually, a state will exist; then the processRequest(HttpServletRequest) method
of the reconstructed page component is called.
A typical page component (or any other "non-special" component) will use the processRequest() method of the
component class, which will call the processRequest() method of all its children and that's it. 
The return value of the processRequest method determines the page to be rendered. 
If null is returned, the current page (possibly with changed state) is rendered.
A typical page component (or any other "non-special" component) determines the return value by using the
last non-null return value of one of its children, or null if no child returns a non-null value.
Typically, only one child returns a non-null value.

Non-typical components are forms, input and button elements TODO describe further

TODO describe state saving and redirecting

### render phase

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
- UseTemplateComponent: Component which binds together the template to be used in a HTML file
  and the template snippet defined in that page. Represents the m:useTemplate tag.
- DefineComponent: Defines a part to be inserted into a placeholder of a template. 
  Represents the m:define tag.
