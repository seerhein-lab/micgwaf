TODO
====

- no model: pages displaying the same information store them in a different way. 
  Find a way to access this information in an uniform way. e.g implement an interface ?
- Find a way to model page templates (only children can currently be exchanged, not parent)
  similar to facelets:
  child
  <m:composition template="..."><!-- must be root tag -->
    <m:define name="..."/>
  </m:composition>
  parent:
  <m:insert name="..."/>
- How to deal with radiobuttons, multiselects and checkboxes with same name but different values
- Deal with ajax calls similarly as with normal buttons
  - mark ajax buttons differently (how? m:ajaxId=... or m:ajax="true")
  - overwrite processAjaxRequest for the component in question
  - how to insert the javascript component replacement script in rendered page? perhaps via a m:javascript tag?
  - where to generate hook method? 
    There is not always a surrounding form. So either the component itself or the page.
  - should we use a ajaxPressed flag like processing inputs in normal forms? 
    Probably not, delegate to the hook method directly
- implement logging
