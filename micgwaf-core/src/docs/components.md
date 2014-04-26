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

Component lifecycle
-------------------

TODO

Framework components
-------------------

The following components are built in in micgwaf:

TODO
