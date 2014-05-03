micgwaf Documentation
=====================

Introduction
------------

Micgwaf is a framework for creating web applications with java. It aims at simplicity; the programmer can
easily understand what happens when a submit is processed or a page is rendered.
Nonetheless, micgwaf allows for effectively building web applications.

Micgwaf has built-in support for partial page rendering via ajax requests.
It does not use any javascript framework, any javascript framework can be integrated with micgwaf.

Micgwaf uses code generation for developing web applications. 
The starting point for code generation is the XHTML code of the application pages.
In these pages, the elements of which micgwaf should be aware are marked using the m:id attribute 
where the prefix m is bound to the micgwaf namespace 
(which is ignored by browsers, so the XHTML pages are still valid XHTML and can be rendered by a 
browser as-is).
For each root file and for each element which is marked by the m:id attribute, the micgwaf code generator
generates a component, which can render the component and possibly handle submitted forms.
The behavior of these components can be programmed by overriding component methods in component subclasses.
External XHTML files (respectively their components) can be referenced in pages, so re-using of XHTML
code is possible.

For using the components in a web application, a servlet filter needs to be configured and
the page components need to be mounted to specific request URLs.

micgwaf also supports development of HTML code alongside application development.
For this, a special servlet filter is provided, which renders HTML files as-is, but resolves component
references.

For reading more on the objectives of micgwaf, see micgwaf's [background](background.md)

Getting started
---------------

To create a Web Application with micgwaf, the following needs to be done:
- Create XHTML files for the pages (you can use micgwaf for preview)
- Generate code from the XHTML pages using the micgwaf code generator
- implement the Application class
- Create a web.xml descriptor with the micgwaf webapp filter
- Implement the behavior of the pages by editing the generated code
- Start the web application

These steps are described below.

Creating XHTML files and Generating Code
----------------------------------------

- create all .xhtml files in one directory
  - use namespace http://seerhein-lab.de/micgwaf for micgwaf 
    (the following assumes xmlns:m="http://seerhein-lab.de/micgwaf")
  - use an unique m:id for each active component. m:id should start with a lowercase letter.
  - use m:generateExtensionClass="true" for each component which code you want to change.
    By default, for forms and page components, m:generateExtensionClass is true, 
    for all other components, it is false.
  - use m:defaultRendered="false" for components which should not render by default
    (you can change this behavior in java code by calling the setRender method of the component
    or by changing its renderSelf or renderChildren attributes).
  - use m:multiple="true" for components which can appear multiple times (so they are referenced as lists
    and not as single references in the parent component).
  - use the element m:componentRef to reference other components. The refid attribute references the name
    of the components. Only components defined in their own xhtml file can be referenced. for these,
    the default id is the file name minus the .xhtml extension.
  - for defining page templates, define a normal html file as template, and use a 
    <m:insert name="..."/> at the places where the templated page should insert its content.
    For templated pages, use <m:template templateId="..."> as root element 
    and <m:define name="..."> elements to define the snippets to insert into the template.
  - see the xhtml files in the directory src/test/resources/de/seerheinlab/micgwaf/page for an example.
  - see the [micgwaf namespace reference](namespace.md) for a reference of the elements and attributes
    in the micgwaf namespace.

TODO describe preview mode

- run de.seerheinlab.micgwaf.generator.Generator.generate(File, File, File, String) for the generation
  - the configuration of the generator can be changed by setting the generatorConfiguration
    field of the Generator class before the generation run.
  - See de.seerheinlab.micgwaf.generator.GeneratedSourcesTest for an example.

Implement the Application class
-------------------------------

Currently, all the Application class does is providing a mapping from URLs to pages and providing an 
error handling hook.

The application class must inherit from de.seerheinlab.micgwaf.config.ApplicationBase.
In the constructor, URLs are mapped to pages by calling the mount method.
The error handling can be overridden by overriding the handleException(Component, Exception, boolean)
method, doing the error handling business logic (e.g. special logging) and returning the target page.

An example for an application class is

    package de.seerheinlab.test.micgwaf;

    import de.seerheinlab.micgwaf.component.Component;
    import de.seerheinlab.micgwaf.config.ApplicationBase;
    import de.seerheinlab.test.micgwaf.component.bookListPage.BookListPage;
    import de.seerheinlab.test.micgwaf.component.errorPage.ErrorPage;

    public class Application extends ApplicationBase
    {

      public Application()
      {
        mount("/", BookListPage.class);
      }
  
      @Override
      public Component handleException(Component component, Exception exception, boolean onRender)
      {
        return new ErrorPage(null, exception);
      }
    }

Creating the web.xml descriptor
-------------------------------
Micgwaf can run in two modes: preview mode and generated mode
These two modes can be switched on by configuring different filters in the web.xml.

Micgwaf's WebappFilter recognizes mounted pages and forwards these requests to generated component classes
A all other URLs are ignored by the filter (i.e. tomcat's standard behavior is invoked).
This filter has the class de.seerheinlab.micgwaf.filter.WebappFilter, needs the init-parameter 
applicationClassName which must be filled with the class name of the Application class.

Micgwaf's HtmlDevelopmentFilter serves micgwaf-enriched XHTML pages without generating. This is specially
useful for the development of XHTML pages, as every change in an XHTML file will show up instantly. 
The HtmlDevelopmentFilter needs the init-parameter htmlDir, the value of which must be set
to the base directory where the HTML files live.

An example for a web.xml file which provides both modes is shown below. For using only one mode,
simply remove the unused filter and filter-mapping:

    <?xml version="1.0" encoding="ISO-8859-1"?>
    <web-app xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
        xmlns="http://java.sun.com/xml/ns/javaee" 
        xsi:schemaLocation="http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-app_2_5.xsd" version="2.5">
      <display-name>sb-test-webapp</display-name>
      <filter>
        <filter-name>micgwaf-webapp</filter-name>
        <filter-class>de.seerheinlab.micgwaf.filter.WebappFilter</filter-class>
        <init-param>
          <param-name>applicationClassName</param-name>
          <param-value>de.seerheinlab.test.micgwaf.Application</param-value>
        </init-param>
      </filter>
      <filter>
        <filter-name>micgwaf-html-development</filter-name>
        <filter-class>de.seerheinlab.micgwaf.filter.HtmlDevelopmentFilter</filter-class>
        <init-param>
          <param-name>htmlDir</param-name>
          <param-value>src/main/pages</param-value>
        </init-param>
      </filter>
      <filter-mapping>
        <filter-name>micgwaf-html-development</filter-name>
        <url-pattern>*.xhtml</url-pattern>
      </filter-mapping>
      <filter-mapping>
        <filter-name>micgwaf-webapp</filter-name>
        <url-pattern>/*</url-pattern>
      </filter-mapping>
    </web-app>

Further references
------------------
[Request processing](requestProcessing.md)

[Components](components.md)

[Parsing and Generating](parsingGenerating.md)

[Gotchas](gotchas.md)

[Dependency Injection](dependencyInjection.md)
  
