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

TODO: describe what the handlers do