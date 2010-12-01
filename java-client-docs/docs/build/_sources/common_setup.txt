.. _common_setup:

Common Setup
>>>>>>>>>>>>

This section describes the steps that are common and necessary for using the java client library.

=================
Setup the api key
=================

All canoris requests require an api key as an http parameter.

To setup the API key we simply get an instance of the CanorisAPI object and set the key.

::
  
  CanorisAPI.getInstance().setApiKey("<api_key>");

This should be the first step when using the library

==================
Setup the base URL
==================

The base URL has the default value of *api.canoris.com* which should be sufficient.
It is possible to use another base URL in case this one changes or for testing purpose.

To set the base URL:

::

  CanorisAPI.getInstance().setBaseURL("<base_URL>");




