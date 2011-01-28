package com.canoris.api;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;

import com.canoris.api.exception.CanorisException;
import com.canoris.api.resources.CanorisFile;
import com.canoris.api.resources.CanorisResource;
import com.canoris.api.resources.Pager;

/*
 * This essentially is just a facade (kind off...) to the CanorisConnManager.
 * TODO: 1) Add javadoc to all methods.					--DONE
 *       2) Add getPage method to get a specific page.	--DONE
 *       3) Externalize strings (Do an interface with constants or something...)
 *       4) Check which methods should return JsonNode
 */
/**
 * This class is the main communication point with the back-end.
 * Through this class you can perform all the operation the back-end offers.
 */
public class CanorisResourceManager {

    /**
     * Creates a file resource. You can pass extra parameters if applicable and the method
     * will use them as required.
     * The params parameter is reserved for later use.
     *
     * @param filePath
     * @param params
     * @return CanorisResource
     *                         The created object.
     * @throws ClientProtocolException
     * @throws URISyntaxException
     * @throws IOException
     */
    public CanorisResource createFile(String filePath, Map<String,String> params) throws ClientProtocolException, URISyntaxException, IOException {
        CanorisConnManager conManager = CanorisConnManager.getInstance();
        return conManager.uploadFile(filePath);
    }
    
    /**
     * Creates a file from the URL parameter. 
     * Note that creating a file this way will not have a "serve" field so it cannot 
     * be retrieved. The file is thrown away after it's being processed.
     * 
     * @param url
     * @param params
     * @return CanorisResource 
     * @throws ClientProtocolException
     * @throws URISyntaxException
     * @throws IOException
     * @throws CanorisException
     */
    public CanorisResource createFileFromURL(String url, Map<String,String> params) throws ClientProtocolException, URISyntaxException, IOException, CanorisException {
    	return CanorisConnManager.getInstance().uploadFileFromURL(url, Constants.URI_FILES);
    }
    /**
     * Returns a Pager object to be used for navigating the pages or
     * accessing a particular element in the page.
     *
     * @return Pager
     * @throws ClientProtocolException
     * @throws IOException
     * @throws URISyntaxException
     * @throws CanorisException
     */
    public Pager getFiles() throws ClientProtocolException, IOException, URISyntaxException, CanorisException {
        Map<String,String> params = new HashMap<String, String>();
        return CanorisConnManager.getInstance().getPagedResults(params, Constants.URI_FILES);
    }

    /**
     * Returns a Map representing  the requested file.
     *
     * @param file
     * @return CanorisFile
     * @throws ClientProtocolException
     * @throws IOException
     * @throws URISyntaxException
     * @throws CanorisException
     */
    public CanorisFile getFile(CanorisFile file) throws ClientProtocolException, IOException, URISyntaxException, CanorisException {
        if (file == null)
            return null;
        Map<String, String> params = new HashMap<String, String>(2);
        params.put("fileKey", file.getKey());
        return CanorisConnManager.getInstance().getCanorisFile(params, Constants.URI_FILE);
    }
    /**
     * Returns a Map representing  the requested file.
     *
     * @param fileKey
     * @return CanorisFile
     * @throws ClientProtocolException
     * @throws IOException
     * @throws URISyntaxException
     * @throws CanorisException
     */
    public CanorisFile getFile(String fileKey)
        throws ClientProtocolException, IOException, URISyntaxException, CanorisException {
        Map<String, String> params = new HashMap<String, String>(2);
        params.put("fileKey", fileKey);
        return CanorisConnManager.getInstance().getCanorisFile(params, Constants.URI_FILE);
    }
    /**
     * Return an InputStream representing the requested file.
     * You MUST close the stream manually, if not it will block the operation.
     *
     * @param file
     * @return InputStream
     * @throws ClientProtocolException
     * @throws IOException
     * @throws URISyntaxException
     */
    public InputStream downloadFile(CanorisFile file) throws ClientProtocolException, IOException, URISyntaxException, CanorisException {
        if (file == null)
            return null;
        Map<String, String> params = new HashMap<String, String>(2);
        params.put("fileKey", file.getKey());
        return CanorisConnManager.getInstance().getResource(params, Constants.URI_FILE_SERVE);
    }
    /**
     * Return an InputStream representing the requested file.
     * You MUST close the stream manually, if not it will block the operation.
     *
     * @param fileKey
     * @return InputStream
     * @throws ClientProtocolException
     * @throws IOException
     * @throws URISyntaxException
     */
    public InputStream downloadFile(String fileKey) throws ClientProtocolException, IOException, URISyntaxException, CanorisException {
        Map<String, String> params = new HashMap<String, String>(2);
        params.put("fileKey", fileKey);
        return CanorisConnManager.getInstance().getResource(params, Constants.URI_FILE_SERVE);
    }
    /**
     * Returns an input stream representing
     * the conversion type requested.
     *
     * @param file
     * @param conversionName
     * @return InputStream
     * @throws ClientProtocolException
     * @throws IOException
     * @throws URISyntaxException
     */
    public InputStream getConversion(CanorisFile file, String conversionName) throws ClientProtocolException, IOException, URISyntaxException, CanorisException {
        if (file == null)
            return null;
        Map<String, String> params = new HashMap<String, String>(2);
        params.put("fileKey", file.getKey());
        params.put("name", conversionName);
        return CanorisConnManager.getInstance().getResource(params, Constants.URI_FILE_CONVERSION);
    }
    /**
     * Gets the conversions of the file you pass
     *
     * @param file
     * @return Map<String,String> the conversions
     * @throws ClientProtocolException
     * @throws IOException
     * @throws URISyntaxException
     * @throws CanorisException
     */
    public Map<String, Object> getConversions(CanorisFile file) throws ClientProtocolException, IOException, URISyntaxException, CanorisException {
        if (file == null)
            return null;
        Map<String, String> params = new HashMap<String, String>(1);
        params.put("fileKey", file.getKey());
        return CanorisConnManager.getInstance().getResources(params, Constants.URI_FILE_CONVERSIONS);
    }

    /**
     * Returns the analysis of the file as a JsonNode
     * 
     * @param file
     * @param filter
     * @return JsonNode
     * @throws ClientProtocolException
     * @throws IOException
     * @throws URISyntaxException
     * @throws CanorisException
     */
    public JsonNode getAnalysis(CanorisFile file, String filter)
                                        throws
                                            ClientProtocolException,
                                            IOException,
                                            URISyntaxException,
                                            CanorisException {
        if (file == null)
            return null;
        Map<String, String> params = new HashMap<String, String>(1);
        params.put("fileKey", file.getKey());
        params.put("filter", filter);

        return CanorisConnManager.getInstance().getResourcesAsTree(params, Constants.URI_FILE_ANALYSIS);
    }
    /**
     * Returns the analysis frames
     * 
     * @param fileKey
     * @return JsonNode
     * @throws JsonParseException
     * @throws JsonMappingException
     * @throws ParseException
     * @throws IOException
     * @throws URISyntaxException
     * @throws CanorisException
     */
    public JsonNode getAnalysisFrames(String fileKey) 
    								throws JsonParseException, 
    									   JsonMappingException, 
    									   ParseException, 
    									   IOException, 
    									   URISyntaxException, 
    									   CanorisException {
    	Map<String,String> params = new HashMap<String, String>();
    	params.put("fileKey", fileKey);
    	
    	return CanorisConnManager.getInstance().getResourcesAsTree(params, Constants.URI_FILE_ANALYSIS_FRAMES);
    }
    /**
     * Gets the visualization of the passed file
     *
     * @param file
     * @param name
     * @return An InputStream representing the visualization or null
     * @throws ClientProtocolException
     * @throws IOException
     * @throws URISyntaxException
     */
    public InputStream getVisualization(CanorisFile file, String name) throws ClientProtocolException, IOException, URISyntaxException, CanorisException {
        if (file == null)
            return null;
        Map<String, String> params = new HashMap<String, String>(2);
        params.put("fileKey", file.getKey());
        params.put("name", name);
        return CanorisConnManager.getInstance().getResource(params, Constants.URI_FILE_VISUALIZATION);
    }
    /**
     * Return the visualization of the file
     *
     * @param file
     * @return Map<String,Object>
     * @throws ClientProtocolException
     * @throws IOException
     * @throws URISyntaxException
     * @throws CanorisException
     */
    public Map<String,Object> getVisualizations(CanorisFile file) throws ClientProtocolException, IOException, URISyntaxException, CanorisException {
        if (file == null)
            return null;
        Map<String, String> params = new HashMap<String, String>(1);
        params.put("fileKey", file.getKey());
        return CanorisConnManager.getInstance().getResources(params, Constants.URI_FILE_VISUALIZATIONS);
    }

    /* ***************************** TEMPLATES ***************************** */
    /**
     * Gets the list of templates. 
     *
     * @return JsonNode
     * @throws ClientProtocolException
     * @throws IOException
     * @throws URISyntaxException
     * @throws CanorisException
     */
    public JsonNode getTemplates() throws ClientProtocolException, IOException, URISyntaxException, CanorisException {
        Map<String, String> params = new HashMap<String, String>();
        return CanorisConnManager.getInstance().getResourcesAsTree(params, Constants.URI_TEMPLATES);
    }
    /**
     * Creates a template and returns the response or null
     *
     * TODO: This should return a JsonNode??
     *
     * @param templateName
     * @return JsonNode
     * @throws ClientProtocolException
     * @throws IOException
     * @throws URISyntaxException
     * @throws CanorisException
     */
    public JsonNode createTemplate(String templateName, String templateContent)
                                        throws ClientProtocolException, IOException, URISyntaxException, CanorisException {
        Map<String,String> postParams = new HashMap<String, String>(1);
        postParams.put("name", templateName);
        postParams.put("template", templateContent);

        Map<String, String> params = new HashMap<String, String>();
        return CanorisConnManager.getInstance().createResourceAsTree(params, postParams, Constants.URI_TEMPLATES);
    }
    /**
     * Updates the template that matches the templateName.
     * Will throw error if template is not found.
     *
     * @param templateContent
     * @param templateName
     * @return JSonNode
     * @throws ClientProtocolException
     * @throws IOException
     * @throws URISyntaxException
     * @throws CanorisException
     */
    public JsonNode updateTemplate(String templateContent, String templateName)
                                throws
                                    ClientProtocolException,
                                    IOException,
                                    URISyntaxException,
                                    CanorisException {
        Map<String,String> urlParams = new HashMap<String, String>(2);
        urlParams.put("templateName", templateName);
        Map<String,String> putParams = new HashMap<String, String>(2);
        putParams.put("template", templateContent);

        return CanorisConnManager.getInstance().updateResource(urlParams, putParams, Constants.URI_TEMPLATE);
    }
    /**
     * Deletes the give template
     *
     * @param templateName
     * @throws ClientProtocolException
     * @throws IOException
     * @throws URISyntaxException
     */
    public void deleteTemplate(String templateName) throws ClientProtocolException, IOException, URISyntaxException, CanorisException {
        Map<String,String> params = new HashMap<String,String>(1);
        params.put("templateName", templateName);
        CanorisConnManager.getInstance().deleteResource(params, Constants.URI_TEMPLATE);
    }

    /* ***************************** TASKS ***************************** */

    /**
     * Creates with the give taskName, containing the taskContent.
     *
     * @param taskName
     * @param taskContent
     * @throws ClientProtocolException
     * @throws IOException
     * @throws URISyntaxException
     */
    public Map<String,Object> createTask(String taskName, String taskContent)
                                     throws ClientProtocolException, IOException, URISyntaxException, CanorisException {
        Map<String,String> params = new HashMap<String,String>();
        Map<String,String> postParams = new HashMap<String,String>(2);
        postParams.put("template", taskName);
        postParams.put("parameters", taskContent);

        return CanorisConnManager.getInstance().createResource(params, postParams, Constants.URI_TASK);
    }
    /**
     * Get the task that corresponds to the taskId.
     * Null if it finds no task with given taskId ?
     *
     * @param taskId
     * @return Map<String,Object>
     * @throws ClientProtocolException
     * @throws IOException
     * @throws URISyntaxException
     * @throws CanorisException
     */
    public Map<String,Object> getTask(String taskId)
                                    throws
                                        ClientProtocolException,
                                        IOException,
                                        URISyntaxException,
                                        CanorisException {
        Map<String,String> params = new HashMap<String,String>(1);
        params.put("taskId", taskId);

        return CanorisConnManager.getInstance().getResourceAsMap(params, Constants.URI_TASK);
    }

    /* ***************************** LANGUAGE ***************************** */
    /**
     *
     *
     * @param text
     * @param language
     * @return Map<String,Object>
     * @throws ClientProtocolException
     * @throws IOException
     * @throws URISyntaxException
     * @throws CanorisException
     */
    public Map<String,Object> getText2Phon(String text, String language)
                                        throws
                                            ClientProtocolException,
                                            IOException,
                                            URISyntaxException,
                                            CanorisException {
        Map<String,String> params = new HashMap<String,String>(2);
        params.put("text", text);
        params.put("language", language);

        return CanorisConnManager.getInstance().getResourceAsMap(params, Constants.URI_PHONEMES);
    }

    /* ***************************** COLLECTION ***************************** */
    /**
     * @throws CanorisException
     *
     */
    public Pager getCollections() throws ClientProtocolException, IOException, URISyntaxException, CanorisException {
        Map<String,String> params = new HashMap<String, String>(0);
        return CanorisConnManager.getInstance().getPagedResults(params, Constants.URI_COLLECTIONS);
    }
    /**
     *
     * @param collectionKey
     * @return Map<String,Object>
     * @throws ClientProtocolException
     * @throws IOException
     * @throws URISyntaxException
     * @throws CanorisException
     */
    public Map<String,Object> getCollection(String collectionKey)
                                        throws
                                            ClientProtocolException,
                                            IOException,
                                            URISyntaxException,
                                            CanorisException {
        Map<String,String> params = new HashMap<String,String>(1);
        params.put("collectionKey", collectionKey);

        return CanorisConnManager.getInstance().getResourceAsMap(params, Constants.URI_COLLECTION);
    }
    /**
     *
     * @param name
     * @param license
     * @param visibility
     * @return Map<String,Object>
     * @throws ClientProtocolException
     * @throws IOException
     * @throws URISyntaxException
     * @throws CanorisException
     */
    public Map<String,Object> createCollection(String name, String license, String visibility)
                                            throws ClientProtocolException, IOException, URISyntaxException, CanorisException {
        Map<String,String> params = new HashMap<String,String>(1);
        Map<String,String> postParams = new HashMap<String,String>(3);
        postParams.put("name", name);
        postParams.put("license", license);
        postParams.put("public", visibility);

        return CanorisConnManager.getInstance().createResource(params, postParams, Constants.URI_COLLECTIONS);
    }
    /**
     * Deletes the collection. This method does not return a value.
     * If no exception is thrown the operation is considered successful.
     *
     * @param collectionKey
     * @throws ClientProtocolException
     * @throws IOException
     * @throws URISyntaxException
     */
    public void deleteCollection(String collectionKey)
                            throws ClientProtocolException, IOException, URISyntaxException, CanorisException {
        Map<String,String> params = new HashMap<String, String>(1);
        params.put("collectionKey", collectionKey);
        CanorisConnManager.getInstance().deleteResource(params, Constants.URI_COLLECTION);
    }

    /* ***************************** FILE COLLECTION ***************************** */
    /**
     * Returns a Pager. You can use the pager to access the collection
     * or navigate through its pages.
     *
     * @param  collectionKey    The key used to retrieve the collection.
     * @throws ClientProtocolException
     * @throws IOException
     * @throws URISyntaxException
     * @throws CanorisException
     */
    public Pager getCollectionFiles(String collectionKey)
                                throws
                                    ClientProtocolException,
                                    IOException,
                                    URISyntaxException,
                                    CanorisException {
        Map<String,String> params = new HashMap<String, String>(1);
        params.put("collectionKey", collectionKey);

        return CanorisConnManager.getInstance().getPagedResults(params, Constants.URI_COLLECTION_FILES);
    }
    /**
     * Adds a file to the given collection.
     *
     * @param collectionKey
     * @param file
     * @throws ClientProtocolException
     * @throws IOException
     * @throws URISyntaxException
     * @throws CanorisException
     */
    public void addFileToCollection(String collectionKey, CanorisFile file)
            throws ClientProtocolException, IOException, URISyntaxException, CanorisException {
        if (file != null) {
            Map<String,String> uriParams = new HashMap<String, String>(1);
            uriParams.put("collectionKey", collectionKey);
            Map<String,String> postParams = new HashMap<String, String>(1);
            postParams.put("filekey", file.getKey());
            try {
                CanorisConnManager.getInstance().createResource(uriParams, postParams, Constants.URI_COLLECTION_FILES);
            } catch(JsonParseException e) {
                /* N.B. This is a peculiarity of the current version of the API.
                 * The response is valid, but is not JSON, should be fixed in
                 * the next version of Canoris.
                 */
            }
        }
    }
    /**
     *
     * @param collectionKey
     * @param file
     * @return Map<String,Object>
     * @throws ClientProtocolException
     * @throws IOException
     * @throws URISyntaxException
     * @throws CanorisException
     */
    public Map<String,Object> getCollectionFile(String collectionKey, CanorisFile file)
                                            throws ClientProtocolException,
                                                   IOException,
                                                   URISyntaxException, CanorisException {
        if (file == null)
            return null;
        Map<String,String> params = new HashMap<String, String>();
        params.put("fileKey", file.getKey());
        params.put("collectionKey", collectionKey);

        return CanorisConnManager.getInstance().getResourceAsMap(params, Constants.URI_COLLECTION_FILE);
    }

    /* ***************************** FILE COLLECTION ***************************** */

    /**
     * Performs a similarity search.
     *
     * @param collectionKey
     * @param file
     * @param preset
     * @param results
     * @return Map<String,Object>
     * @throws ClientProtocolException
     * @throws IOException
     * @throws URISyntaxException
     * @throws CanorisException
     */
    public Map<String,Object> getSimilaritySearch(String collectionKey, CanorisFile file,
                                                  String preset, String results)
                                              throws ClientProtocolException, IOException,
                                                       URISyntaxException, CanorisException {
        if (file == null)
            return null;
        Map<String,String> params = new HashMap<String, String>();
        params.put("collectionKey", collectionKey);
        params.put("fileKey", file.getKey());
        params.put("preset", preset);
        params.put("results", results);

        return CanorisConnManager.getInstance().getResourceAsMap(params, Constants.URI_COLLECTION_SIMILAR);
    }
    /* ***************************** PAGING ***************************** */
    /**
     * Gets the requested page.
     * Works with old and new paging style depending on parameters passed.
     *
     * @return Pager
     * @throws ClientProtocolException
     * @throws IOException
     * @throws URISyntaxException
     * @throws CanorisException
     */
    public Pager getPage(Pager pager)
    						throws ClientProtocolException,
						           IOException,
						           URISyntaxException,
						           CanorisException {
		Map<String, String> params = new HashMap<String, String>();
		if (pager.getLimit() != null && pager.getStart() != null) {
			params.put("start", pager.getStart());
			params.put("limit", pager.getLimit());
		} else {
			params.put("pageNumber", pager.getPageNumber());
		}

		return CanorisConnManager.getInstance().getPagedResults(params, Constants.URI_PAGING);
    }
    /**
     * Gets the requested page.
     * Returns the page corresponding to the start parameter 
     * with the page size based on the limit parameter
     * 
     * Actually this works like this:
     * page with start=1 limit=2 so next page is 3
     * using start=1 limit=5 next page will be 6 and so on
     *
     * @return Pager
     * @throws ClientProtocolException
     * @throws IOException
     * @throws URISyntaxException
     * @throws CanorisException
     */
    public Pager getPage(String start, String limit)
    						throws ClientProtocolException,
						           IOException,
						           URISyntaxException,
						           CanorisException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("start", start);
		params.put("limit", limit);

		return CanorisConnManager.getInstance().getPagedResults(params, Constants.URI_PAGING);
    }
    /**
     * Gets the next page. It extracts the page number by the pager.
     *
     * @return Pager
     * @throws ClientProtocolException
     * @throws IOException
     * @throws URISyntaxException
     * @throws CanorisException
     */
    public Pager getNextPage(Pager pager)
                        throws ClientProtocolException,
                               IOException,
                               URISyntaxException,
                               CanorisException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("page", pager.getNext());
        
        return CanorisConnManager.getInstance().getPagedResults(params, null);
    }
    /**
     * Gets the previous page. It extracts the page number by the pager.
     *
     * @param pager
     * @return Pager
     * @throws ClientProtocolException
     * @throws IOException
     * @throws URISyntaxException
     * @throws CanorisException
     */
    public Pager getPreviousPage(Pager pager)
                            throws ClientProtocolException,
                                   IOException,
                                   URISyntaxException,
                                   CanorisException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("page", pager.getPrevious());

        return CanorisConnManager.getInstance().getPagedResults(params, null);
    }
    /**
     * Sets if the communication uses a proxy or not
     *
     * @param useProxy
     */
    public void useProxy(boolean useProxy) {
        CanorisConnManager.getInstance().setUseProxy(useProxy);
    }
    /**
     * Configures the proxy
     *
     * @param host
     * @param port
     * @param protocol
     */
    public void configProxy(String host, int port, String protocol) {
        CanorisConnManager.getInstance().setupProxy(host, port, protocol);
    }

}
