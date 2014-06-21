TODO
====

- no model: pages displaying the same information store them in a different way. 
  Find a way to access this information in an uniform way. e.g implement an interface ?
- implement logging
- implement handling of nested loops (currently only one loop level is supported)
- for attributes in loops must be treated like ids (add :number) 
- find a way how to find components higher up in the tree (e.g. messageBox outside form)
- add note about when classes are generated in class javadoc
- multiple should also work on componentRef
- error should be output when variables are accessed in componentRef which do not exist in referenced component
- handle escape character (backslash) in variables properly or use other escape mechanism
- check whether use builder pattern is useable throughout
- check and document what the generated component registry is for, and remove if unnecessary
- ignore white space in parsing
