Ajax Support
============

Micgwaf supports exchange of parts of the page by using Ajax Requests.
For this, several components are needed:
1) A javascript snippet must be inserted into the page. 
   The snippet fires an ajax request, which result is used to replace the part of the DOM tree.
2) A javascript onclick action must be added to the HTML element which triggers the ajax action.
3) On the server side, code must be written which produces the replacement HTML.

In the following, the steps are explained in detail:

1) The following javascript snippet should be inserted into pages which contains
   one or more ajax functions: 
    <script type="text/javascript">
    function loadXMLDoc(path)
    {
      var xmlhttp;
      if (window.XMLHttpRequest)
      {
        xmlhttp=new XMLHttpRequest();
      }
      else
      {
        xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
      }
      xmlhttp.onreadystatechange=function()
      {
        if (xmlhttp.readyState==4)
        {
          if (xmlhttp.status==200)
          {
            var xmlDoc = xmlhttp.responseXML;
            var documentElement = xmlDoc.documentElement;
            var id = documentElement.id;
            var toReplace = document.getElementById(id);
            toReplace.insertAdjacentHTML('beforebegin', xmlhttp.responseText);
            toReplace.remove();
          }
        }
      }
      xmlhttp.open("GET","/ajax/" + path, true);
      xmlhttp.send();
    }
    </script>

2) In the same page, add the onclick action to the HTML element(s)
   which triggers the ajax action. For example:
   
    <button m:id="ajaxButton" type="button" onclick="loadXMLDoc(this.id);">Do ajax</button>
   
   
3) On the server side, the ajax request must be processed. This is done by ovverriding
   the processAjaxRequest(HttpServletRequest) method in one of the components of the page.
   This can be the page itself, or the surrounding form, or the HTML element which fires
   the ajax request, or... A typical choice is the surrounding form.
   The code needs t oreturn a component, which is then subsequently rendered
   and the rendered HTML is then returned as the result of the ajax call.
   An example for the code is:

  @Override
  public Component processAjaxRequest(HttpServletRequest request)
  {
    String path = request.getServletPath();
    if (path.startsWith("/ajax/ajaxButton"))
    {
      return ajaxButtonPressed(); // returns the new state of the component to render
    }
    return super.processAjaxRequest(request);
  }


What happens in Detail
----------------------

The javascript snippet defines the function loadXMLDoc,
which takes the path to the ajax resource as a parameter.
The path should be the id of the HTML component which triggers the ajax action.
The ajax path is prepended by /ajax/, and a HTTP GET is issued to the server.

The server side knows by the path prefix /ajax/ that the request is an ajax request.
It then restores the last page state from the session, and calls the
method processAjaxRequest(HttpServletRequest) of the restored page component.
The default implementation is to subsequently call the processAjaxRequest(HttpServletRequest)
method of all child components (which, by default, again call the same method of their children),
unless one of them returns a not-null component
(meaning that the child component has processed the ajax request and has produced
an appropriate answer).
Micgwaf then renders the returned component; the result of the rendering is returned
as result of the ajax request. Finally, the new component state is persisted in the session,
so that server state and client state are synchronous.

The client side javascript then analyzes the id of the root node of the ajax response.
The element with the same id in the page is found, and it is replaced by the 
HTML contained in the ajax response.