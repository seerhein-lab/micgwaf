How micgwaf processes a HTTP request
====================================

micgwaf generates classes to handle incoming requests and to produce the appropriate response.
In a servlet environment, the entry point to access these classes is the
de.seerheinlab.micgwaf.filter.WebappFilter filter.
This filter uses a chain of request handlers to process a request. 
It asks every RequestHandler in its request handler chain to process the request.
As soon as one of the filters processes the request, looping the request handler chain stops and the 
produced response is returned.
If no RequestHandler processes the request, the standard filter chain is invoked
(i.e. any other resources defined in the webapp which match the request are served.
 If no such resource is found, the servlet container handles the missing resource as it is configured
 (e.g. by sending a HTTP 404 response).

The default handler chain consist of the following Handlers
- de.seerheinlab.micgwaf.requesthandler.AjaxHandler 
- de.seerheinlab.micgwaf.requesthandler.PRGHandler 

Here, the Ajax handler checks whether the request is an Ajax request (i.e. starts with the path /ajax),
and handles it accordingly if it is.
If not, the PRG Handler is responsible for the "normal" request processing.

micgwaf obtains the handler chain by calling the method 
getRequestHandlerChain() of the configured Application class.
A custom request handler chain can be created by either overriding this method in the application class
or by modifying the contents of the field de.seerheinlab.micgwaf.config.ApplicationBase.requestHandlers.

The handlers by default do the following:

PRGHandler request processing
-----------------------------

The PRG (Post-Redirect-Get) handler handles two phases: a process phase in which the application logic
is executed (e.g. calling business services), and a render phase in which the response HTML is built.
Each of these phases runs in a separate HTTP request; the process phase in a POST request,
and the render phase in a GET request. The GET request is triggered by the POST request because the result
of the POST request is a HTTP redirect.

Which one of the two phases is currently active is decided by the following algorithm:
The PRG Handler first checks whether the request is a POST request, 
indicating that the request originated from a POST of a form.
If yes, it checks whether there is a component stored in the session which matches the given request.
If yes, the process phase is started, in all other cases, the render phase is started.

In the process phase, the process method of the page component from which the POST originated is called, 
which returns the component to render (or null, in which case the component to render is set
to the processed page component).
The component to render is stored in the session, and a redirect is issued as response,
to redirect to the rendered page.
This concludes handling of the POST, and the handler returns true to indicate the request has been handled.
  
If no component to process exists, or the request is no POST process, the render phase is started instead,
which renders the component stored in the session, and true is returned to indicate that the request 
has been processed.
If no such component exists, the application class is is asked to return the appropriate page component
for the current request path, the newly created component is stored in the session, and a redirect is issued,
which effectively re-starts the render phase (this behavior ensures that each displayed component is stored
in the session so a later request renders exactly the same component).
If the application class does not return a component, the PRG handler acknowledges
that it can not handle the request and returns false, indicating that the request has not been handled
(this is e.g. the case when resources such as images or style sheets are requested by the browser).

If an uncaught exception occurs during processing or rendering, the application's handleException method
is called which returns a component to display an error page. By default, this prints the message
"an error occurred". It is recommended to overwrite the application's handleException() method to provide
a more sophisticated error handling.

The render phase should not modify the state of the component stored in the session. 
If it does, the new state of the component is stored in the session,
which could result in a different HTML output if the page is re-rendered.

If an exception occurs during rendering and the exception handler creates a new component to render,
this new component is NOT stored in the session (i.e. the next time the render URL is called, the same
exception will probably happen again and again the exception handler is invoked).
  
### Redirect details and state Parameter

By default, the PRG handler uses the de.seerheinlab.micgwaf.requesthandler.UrlParamSessionStateHandler
state handler to save state between requests. 
This state handler stores the components as a map in the session.
The key of the map consists of two parts: The mount path of the component which started the conversation,
and the state key which is passed as URL parameter.
The state key is of the form c{conversation index}s{step index}
Each time the state key is not passed or cannot be parsed, a new conversation is started
and the conversation index is increased.
Each time after the process phase, the step index is increased.

To limit the session size, only a limited number of states (100 by default) are kept in the session.
This number can be changed by changing the value of the field 
de.seerheinlab.micgwaf.requesthandler.UrlParamSessionStateHandler.maxStates of the stateHandler instance
kept by the PRGHandler. If the number of states exceeds this number, the earliest state is thrown out of
the session and cannot be accessed any more.

AjaxHandler request processing
------------------------------

TODO

Mounting components
-------------------

Components can be registered with the application using mount paths. 
Mount paths are relative to the web application root.
If micgwaf (more precisely, the PRG handler described above) receives a request for which no component
is stored, the application is asked whether a component is mounted under the request path. 
If yes, the component is instantiated and is rendered.
So, the mount paths define the entry points of the application.