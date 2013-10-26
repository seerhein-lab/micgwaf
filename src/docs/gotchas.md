Gotchas
=======

- Returning null in Component.processRequest() is the default. 
  Returnung null redisplays the submitted page unchanged except the changes made explicitly in processRequest,
  without creating a new instance of the page