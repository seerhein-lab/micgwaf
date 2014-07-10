Parsing an generating
=====================

Parsing the HTML source files
-----------------------------

The entry point for parsing is the de.seerheinlab.micgwaf.parser.HtmlParser class.
It parses the HTML files in a directory and generates components from them.
Only files directly in the given directory with the suffix .xhtml are parsed, all other files are ignored.
The parsed components can re-render the HTML source file again,
but on rendering they can execute special actions defined by elements in the m namespace
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

The following attributes in the m namespace do not create a component on their own, 
but define the behavior of a component or the handling of the component in the parent component: 

<table>
  <tr>
    <th>Name</th>
    <th>Possible values</th>
    <th>Supported by Component</th>
    <th>Description</th>
  </tr>
  <tr>
    <td>multiple</td>
    <td>false (default), true</td>
    <td>HtmlElementComponent</td>
    <td>The component can be repeated multiple (0..n) times in the parent component.</td>
  </tr>
  <tr>
    <td>defaultRender</td>
    <td>true (default), false</td>
    <td>HtmlElementComponent</td>
    <td>Whether by default render this component and its children (true) or not (false).</td>
  </tr>
  <tr>
    <td>defaultRenderSelf</td>
    <td>true (default), false</td>
    <td>HtmlElementComponent</td>
    <td>Whether by default render this component (true) or not (false); rendering the children remains unaffected.</td>
  </tr>
  <tr>
    <td>defaultRenderChildren</td>
    <td>true (default), false</td>
    <td>HtmlElementComponent</td>
    <td>Whether by default render the children of this component (true) or not (false).</td>
  </tr>
  <tr>
    <td>generateExtensionClass</td>
    <td>true (default for form and page root component), false (default for all other)</td>
    <td>HtmlElementComponent</td>
    <td>Whether to create an extension class which can then be overwritten to change its behavior.</td>
  </tr>
</table>


As already mentioned, the parsed component tree can be rendered as is.
This is useful for creating HTML mockups which can later be filled with behavior.

### Parsing Variables

Variables are of the form ${variableName:defaultValue}, where the :defaultValue part can be omitted 
(the default value is set to ${variableName} if not specified).
Variables are used to render variable text or HTML.
Currently, variables are interpreted in XHTML character sections
or in attribute values of elements which do not have a m:id attribute only.
Variables are parsed as a ComponentPart with the variable field set in the PartListComponent.
Variable values can be defined in componentRef tags, where an attribute with the name of the variable
can be used to define the variable value.
These additional attributes are stored in the variableValues field of the RefComponent component.

### Referencing components from Component libs

A Component lib is a jar file which contains component classes intended for being used in projects
which include the lib.
To make such components in a component lib known, the component lib must contain a file 
META-INF/micgwaf-components.properties, which is a properties file which contains the id
of the component as name and the component class name as value.
Namespacing using dots can be used in the key to make them unique.
Deep namespacing is discouraged, one dot should suffice.

Generation of source files
--------------------------

TODO