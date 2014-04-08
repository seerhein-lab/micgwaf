How Micgwaf processes a HTTP request
====================================

The generated classes are accessed from a servlet environment by the
de.seerheinlab.micgwaf.filter.WebappFilter filter.
This filter by default asks every RequestHandler in the request handler chain to process the request.
As soon as one of the filters processes the request, looping the request handler chain stops and the 
produces response is returned.
If no RequestHandler processes the request, the standard filter chain is invoked
(i.e. any other resources defined in the webapp which match the request are served.
 If no such resource is found, the servlet container handles the missing resource.

The default handler chain consist of the following Handlers
- de.seerheinlab.micgwaf.requesthandler.AjaxHandler 
- de.seerheinlab.micgwaf.requesthandler.PRGHandler 

micgwaf obtains the handler chain by calling  method 
de.seerheinlab.micgwaf.config.ApplicationBase.getRequestHandlerChain()
A custom request handler chain can be created by either overriding this method in the application class
or by modifying the contents of the field de.seerheinlab.micgwaf.config.ApplicationBase.requestHandlers.

TODO: describe what the handlers do