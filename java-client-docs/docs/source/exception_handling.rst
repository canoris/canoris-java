.. _exception_handling:


Exception Handling
>>>>>>>>>>>>>>>>>>

The canoris java client library throws quite a few exceptions due to the use of the HttpClient
and Jackson libraries. The exception that is meaningful though and you should be catching is
the LINK---->CanorisException which wraps a `JsonParseException <http://jackson.codehaus.org/1.0.1/javadoc/org/codehaus/jackson/JsonParseException.html>`_.

This exception contains the HTTP error code in case something went wrong.
For example trying to perform a similarity search in a collection that has less than 30 files
will result in the **409** error. Below you can see an example:

::
  
  try {
    manager.getSimilaritySearch(collectionKey, file, preset, results);
  } catch (CanorisException e) {
    e.printStackTrace();
    System.out.println("HTTP_ERROR_CODE : " + e.getHttpErrorCode());
  }  

::


