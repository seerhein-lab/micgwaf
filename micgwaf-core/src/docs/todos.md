TODO
====

- no model: pages displaying the same information store them in a different way. 
  Find a way to access this information in an uniform way. e.g implement an interface ?
- implement logging
- implement handling of nested loops (currently only one loop level is supported)
- for attributes in loops must be treated like ids (add :number) 
- find a way how to find components higher up in the tree (e.g. messageBox outside form)
- allow a directory tree for XHTML files (not just flat directory layout)
- how to reference not-generated components in the project or components from a component library?

- multiple should also work on componentRef
- error should be output when variables are accessed in componentRef which do not exist in referenced component
- variables should support default content (${variableName:defaultContent})
- create setters for escaped/unescaped content of variables.
