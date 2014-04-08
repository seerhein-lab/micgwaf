How Micgwaf processes a HTTP request
====================================

The generated classes are accessed from a servlet environment by the
de.seerheinlab.micgwaf.filter.WebappFilter filter.
This filter by default does the following
- It asks the de.seerheinlab.micgwaf.filter.AjaxHandler whether it can process the request.
- If this is not the case, it asks the de.seerheinlab.micgwaf.filter.PRGHandler 
  whether it can process the request
- If not, the filter chain is invoked (i.e. any other resources defined in the webapp which match the request
  are served.
- If no such resource is found, the servlet container handles the missing resource.
micgwaf obtains the handler chain by calling  method 
de.seerheinlab.micgwaf.config.ApplicationBase.getRequestHandlerChain()
A custom request handler chain can be created by either overriding this method in the application class
or by modifying the contents of the field de.seerheinlab.micgwaf.config.ApplicationBase.requestHandlers.

TODO: describe what the handlers do