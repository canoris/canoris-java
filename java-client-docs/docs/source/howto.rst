.. _howto-usage:

Simple files howto
>>>>>>>>>>>>>>>>>>

This is a simple walkthrough demonstrating how to:

:ref:`upload_file`

:ref:`retrieve_file`

:ref:`retrieve_all_files`

:ref:`retrieve_all_files`

:ref:`retrieve_file_conversion`

:ref:`handle_jsonnode_results`


First we need to create a CanorisResourceManager object. This object performs all communication with the
Canoris API. 

::

  CanorisResourceManager manager = new CanorisResourceManager();


.. _upload_file:

1) Upload a file
----------------

To upload a file simply do:

::

  CanorisFile file = (CanorisFile) manager.createFile(filePath, null)

*filePath*: the path where the file is located.
For the moment you can ignore the second parameter since it is not used.

This call will return a CanFile object, you will have to cast it since the method returns a CanorisResource object.
Refer to ADD_LINK_TO_JAVADOC for the structure of the CanorisFile object.


.. _retrieve_file:

2) Retrieve a file
------------------

To retrieve a single file you can either use it's file key as an identifier or pass the whole
file to be retrieved (containing already the fileKey).
:ref:`retrieve_all_files`
Create a file using it's file key constructor

::

  CanorisFile file = new CanorisFile("<file_key>");

Then use the CanorisResourceManager to retrieve/populate the file like this:

::

  CanorisFile canFile = manager.getFile(file);

Else you can just use the same method like this:

::

  CanorisFile canFile = manager.getFile(<fileKey>);

If the operation is succesful you can access the various file properties in this manner.

::

  Double fs = (Double) canFile.getProperties().get("samplerate");

Please refer to the JAVADOC_LINK for a more detailed description of the CanorisFile class.


.. _retrieve_all_files:

3) Retrieve all files
---------------------

To get the all files that we have uploaded we simply do:

::

  Page pager = manager.getFiles();

The pager object can be used to access the files and navigate to other pages (if any).


.. _retrieve_file_conversion:

4) Request file conversions
---------------------------

To request a conversion we have to of course first obtain the file. Refer to previous sections for that.
Once we have the file we can get its conversions like this:

::

  Map<String,String> conversions = canorisFile.getConversions();

We can then ask for a specific conversion using the following code:

:: 

  InputStream in = manager.getConversion(file, "mp3_128kb");

  
.. _handle_jsonnode_results:

5) Handle JsonNode results
--------------------------

In some cases the response contains a deeply nested Json response. In those we return a `JsonNode <http://jackson.codehaus.org/1.1.2/javadoc/org/codehaus/jackson/JsonNode.html>`_ object.
Here's an example:

::

  String template = "[{\"operation\": \"vocaloid\", \"parameters\": {\"input\": \"{{ test_input }}\"}}]";
  JsonNode jsonResponse = manager.updateTemplate(template, "test");
  
Of course the jsonResponse will be *null* if template with the given name is found. 
If a template is found you can access its properties in the following way:

::
  
  jsonResponse.findValue("input").toString();

This of course leaves open the possibility of a null pointer exception so you should always check for the existence
of the property you are trying to access. 


