TODO
====

- no model: pages displaying the same information store them in a different way. 
  Find a way to access this information in an uniform way. e.g implement an interface ?
- implement logging
- for attributes in loops must be treated like ids (add :number) 
- find a way how to find components higher up in the tree (e.g. messageBox outside form)
- add note about when classes are generated in class javadoc
- error should be output when variables are accessed in m:reference which do not exist in referenced component
- handle escape character (backslash) in variables properly or use other escape mechanism
- check whether use builder pattern is useable throughout
- check and document what the generated component registry is for, and remove if unnecessary
- ignore white space in parsing
- generate only class files for classes with an m:id and use inner classes for the rest
  - check where to set GeneratedClass in generationContext (with package and possibly class definition)
- unify generation method parameters and their order
- why does xml header and doctype not get rendered?
- defaultRendered does not work for root elements and m:reference element
- style message box and error page in demo
