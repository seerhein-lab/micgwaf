Bookmarkeable links and pages
=============================

To be implemented
-----------------

Desired behavior:
1) in PRG cycle: Get points to bookmarkeable (i.e mounted) url.
   This is achieved by return LinkTo(SomeComponent.class) (finds first mount occurance of SomeComponent)
   or by return LinkTo(String mountPoint) if a decision is needed between several mount points 
   (Alternative to latter: Application.mount returns LinkTo Pointer which can later be used)
1a) Transfer parameters
   Component needs to have constructor with Params annotated with @GET, parameters possibly annotated
   with @QueryParam("paramName"), the parameters which are not annotated are also QueryParameters 
   with name=methodParameterName. LinkTo Constructors take as second parameter Object... so these
   parameters can be transferred.

2) Href points to bookmarkeable page
2a) using mount point: is similar to normal href except relative to servlet context: 
    use m:href which should be context absolute
2b) using component id: use m:targetComponent