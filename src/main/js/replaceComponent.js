function loadXMLDoc(path)
{
  var xmlhttp;
  if (window.XMLHttpRequest)
  {// code for IE7+, Firefox, Chrome, Opera, Safari
    xmlhttp=new XMLHttpRequest();
  }
  else
  {// code for IE6, IE5
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
        //toReplace.parentNode.replace(documentElement, toReplace);
        toReplace.insertAdjacentHTML('beforebegin', xmlhttp.responseText);
        toReplace.remove();
      }
    }
  }
  xmlhttp.open("GET","/ajax/" + path, true);
  xmlhttp.send();
}
