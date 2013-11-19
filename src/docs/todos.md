TODO
====

- no model: pages displaying the same information store them in a different way. 
  Find a way to access this information in an uniform way. e.g implement an interface ?
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
- implement handling of nested loops (currently only one loop level is supported)
- more intuitive support of ajax requests
- for attributes in loops must be treated like ids (add :number) 
- find a way how to find components higher up in the tree