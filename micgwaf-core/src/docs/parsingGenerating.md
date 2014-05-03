Parsing an generating
=====================

Parsing the HTML source files
-----------------------------

The entry point for parsing is the de.seerheinlab.micgwaf.parser.HtmlParser class.
It parses the HTML files in a directory and generates components from them.
Only files directly in the given directory with the suffix .xhtml are parsed, all other files are ignored.
The parsed components can re-render the HTML source file again,
but on rendering execute special actions defined by elements in the m namespace
(m is here an abbreviation of the namespace http://seerhein-lab.de/micgwaf)

For each file, a root component is returned, which then contains child components.
As a rule, a component is created for the HTML file itself and thereafter for each component which either has
a m:id attribute or is itself in the m namespace.
The mapping between elements and components is defined in the class
de.seerheinlab.micgwaf.parser.contenthandler.ContentHandlerRegistry.

Currently, the mapping is as follows

<table>
  <tr>
    <th>Type</th>
    <th>Name</th>
    <th>Resulting Component</th>
    <th>Supported attributes</th>
  </tr>
  <tr>
    <td>Element</td>
    <td>m:componentRef</td>
    <td>RefComponent</td>
    <td>id, refid, and dynamic attributes</td>
  </tr>
  <tr>
    <td>Element</td>
    <td>m:remove</td>
    <td>EmptyComponent</td>
    <td></td>
  </tr>
  <tr>
    <td>Element</td>
    <td>m:insert</td>
    <td>AnyComponent</td>
    <td>name</td>
  </tr>
  <tr>
    <td>Element</td>
    <td>m:useTemplate</td>
    <td>TemplateIntegration</td>
    <td>templateId</td>
  </tr>
  <tr>
    <td>Element</td>
    <td>m:define</td>
    <td>DefineComponent</td>
    <td>name</td>
  </tr>
  <tr>
    <td>Attribute</td>
    <td>m:id</td>
    <td>HtmlElementComponent or ChildListComponent</td>
    <td>m:multiple, and all HTML attributes</td>
  </tr>
</table>

If HTML text is encountered which does not fit into the above categories, a PartListComponent is created.
There are other elements in the m namespace which do not get translated into own components.
These are: TODO

As already mentioned, the parsed component tree can be rendered as is.
This is useful for creating HTML mockups which can later be filled with behavior.

Generation of source files
--------------------------

TODO