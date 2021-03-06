micgwaf background
==================

In my mind, a java web application framework should be designed along the following principles

1) HTML and Java code should be tightly bound. 
   If java code and HTML do not match, the mismatch should be discovered during build time.
   This makes it e.g. easy to find out where any Java method is used during rendering HTML.
   
2) The framework should be component-oriented.
   A component is anything which renders HTML and processes the request sent back by the browser.
   This might be simple components as input fields, or complicated state-involving components
   such as breadcrumb trails or entire forms.
   Components make re-using parts of an application easy.
   
3) The framework should be simple. 
   It should be easy to debug the path of a HTTP request through the framework and comprehend what happens
   and why it happens.
   
4) The framework should support templating. 
   The needed features are
   - loops
   - conditions
   - page frames
   - ignore parts (for templating)

5) The framework should not use excessive space in the HTML session or create excessive traffic.
   
6) As far as possible, HTML mockups should be easy to be re-used in application code.
   So any mock-ups shown to discuss features of the web application can easily be used
   to build the real application afterwards and are not "lost work".
   
7) The framework should handle a pressed browser back button gracefully, not re-executing the business methods
   which were executed prior to rendering the re-displayed page.

Micgwaf addresses these points in the following fashion:

1) The tight binding between java code and HTML is achieved via code generation.
   The HTML files are the input for micgwaf's code generation
   which produces java code which can render the HTML, processes any form input and 
   provides hooks for changing the HTML output.
   Which part of the HTML is accessible from java code and which part is simply printed
   can be decided in the HTML, by giving the parts which are accessible from micgwaf
   an id attribute in the special micgwaf namespace.
   So the markup still stays as much HTML as possible (it is even valid HTML which can be displayed
   by a web browser).
   
5) Using a component-oriented PRG approach which handles the back button, 
   page states needs to be saved somewhere to be able to re-render a former state of a page. 
   Typical places to store component state are the HTML session or in the client.
   In micgwaf, per default the HTML session is used, and the component tree is saved to be able
   to re-render the page. In contrast to other frameworks, however, the generated classes know all
   state which was defined in the HTML source page from which the page components were generated,
   so the real saved state is just the difference needed to create the difference between source HTML
   and actual generated HTML, which is few information 
   (unlike e.G. JSF which stores the whole component state ih the session or in the client).
   
   
TODO further objectives
