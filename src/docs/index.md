micgwaf Documentation
=====================

Introduction
------------

micgwaf is a framework for creating web applications with java. It aims at simplicity; the programmer can
easily understand what happens when a submit is processed or a page is rendered.
Nonetheless, micgwaf allows for effectively building web applications.

Currently the framework is restricted to web 1.0 applications; micgwaf comes without ajax and javascript
support. However, ajax and javascript frameworks can be integrated easily.

micgwaf uses code generation for developing web applications. 
The starting point for code generation is the xhtml code of the application pages.
In these pages, the elements of which micgwaf should be aware are marked using the m:id attribute 
where the prefix m is bound to the micgwaf namespace 
(which is ignored by browsers, so the xhtml pages are still valid xhtml and can be rendered by a 
browser as-is).
For each root file and for each element which is marked by the m:id attribute, the micgwaf code generator
generates a component, which can render the component and possibly handle submitted forms.
The behavior of these components can be programmed by overriding component methods in component subclasses.
External xhtml files (respectively their components) can be referenced in pages, so re-using of xhtml
code is possible.

For using the components in a web application, a servlet filter needs to be configured and
the components need to be mounted to specific paths.

micgwaf also supports development of HTML code alongside application development.
For this, a special servlet filter is provided, which renders HTML files as-is, but resolves component
references.

Generating
----------

- create all .xhtml files in a directory
  - use namespace http://seitenbau.com/micgwaf for micgwaf 
    (the following assumes xmlns:m="http://seitenbau.com/micgwaf")
  - use an unique m:id for each active component. m:id should start with a lowercase letter.
  - use m:generateExtensionClass="true" for each component which code you want to change.
    By default, for forms,  m:generateExtensionClass is true.
  - use m:defaultRendered="false" for components which you do not render by default
    (you can change this behavior in java code by calling the setRender method of the component or changing its 
    renderSelf or renderChildren attributes).
  - use m:multiple="true" for components which can appear multiple times (so they are referenced as lists
    and not as single references in the parent component).
  - use the element m:componentRef to reference other components. The refid attribute references the name
    of the components. Only components defined in their own xhtml file can be referenced. for these,
    the default id is the file name minus the .xhtml extension.
  - see the xhtml files in the directory src/test/resources/com/seitenbau/micgwaf/page for an example.

- run com.seitenbau.micgwaf.generator.Generator.generate(File, File, File, String) for the generation
  See com.seitenbau.micgwaf.generator.GeneratedSourcesTest for an example.

  Creating the webapp
  -------------------
  TODO