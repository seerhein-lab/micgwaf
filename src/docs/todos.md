TODO
====

- no model: pages displaying the same information store them in a different way. 
  Find a way to access this information in an uniform way. e.g implement an interface ?
- Find a way to model page templates (only children can be exchanged, not parent)
- How to deal with radiobuttons and checkboxes with same name but different values
- Deal with ajax calls similarly as with normal buttons
  - mark ajax buttons differently (how? m:ajaxId=... or m:ajax="true")
  - overwrite processAjaxRequest for the component in question
  - how to insert javascript component replacement script in rendered page ?
  - where to generate hook method? (there is not always a surrounding form)
