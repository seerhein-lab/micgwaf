How Micgwaf processes a HTTP request
====================================

micgwaf generates classes to handle incoming requests and to prodoce the appropriate response.
These classes are called in a servlet environment by the
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

micgwaf obtains the handler chain by calling the method 
getRequestHandlerChain() of the configured Application class.
A custom request handler chain can be created by either overriding this method in the application class
or by modifying the contents of the field de.seerheinlab.micgwaf.config.ApplicationBase.requestHandlers.

The handlers by default do the following:

PRGHandler request processing
-----------------------------

The PRG(Post-Redirect-Get) Handler first checks whether the request is a POST request, 
indicating that the request originated from a POST of a form.
If yes, it starts the process phase. There, the page component from which the POST originated is identified. 
If such a component exists, its process method is called, which returns the component to render, or null,
 in which case the component to render is set to the processed page component. The component to render
 is stored in the session, and a redirect is issued as response, to redirect to the rendered page.
 This concludes handling of the POST, and the handler returns true to indicate the request has been handled.
  
 If no such component exists or the request is no POST process, the render phase is started instead,
 which renders the component stored in the session. If no such component exists, the application class
 is asked to return the appropriate handler for the current path. If no component is returned, the PRG
 handler acknowledges that it could not handle the request and returns false. If a renderable component
 is identified, it is asked to render itself, and then the handler returns true
  
TODO describe step parameter

AjaxHandler request processing
------------------------------

TODO