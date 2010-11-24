package com.canoris;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.codehaus.jackson.JsonNode;

import com.canoris.resources.CanorisFile;
import com.canoris.resources.CanorisResource;
import com.canoris.resources.Pager;

/*
 * This essentially is just a facade (kind off...) to the CanorisConManager.
 * TODO: 1) Add javadoc to all methods.
 * 		 2) Add getPage method to get a specific page.
 * 		 3) Externalize strings (Do an interface with constants or something...)
 * 		 4) Imlement configProxy method
 */
public class CanorisResourceManager {
	
	private static final String COLLECTIONS = "collections";
	
	private CanorisConManager canorisRequest;
	
	
	/**
	 * Creates a file resource. You can pass extra parameters if applicable and the method 
	 * will use them as required
	 * FIXME: params not used at the moment, see possible implementations or remove it
	 * @param filePath
	 * @param params
	 * @return	the created object
	 * @throws ClientProtocolException
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	public CanorisResource createFile(String filePath, Map<String,String> params) throws ClientProtocolException, URISyntaxException, IOException {
		CanorisConManager conManager = CanorisConManager.getInstance();
		return conManager.uploadFile(filePath);
	}
	/**
	 * Returns a Pager object to be used for navigating the pages or 
	 * getting access to a specific element in the page.
	 * 
	 * @return Pager
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws URISyntaxException 
	 */
	public Pager getFiles() throws ClientProtocolException, IOException, URISyntaxException {
		return CanorisConManager.getInstance().getFiles();
	}
	
	/**
	 * Returns a Map representing  the requested file.
	 * 
	 * @param file
	 * @return Map<String,Object>
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws URISyntaxException 
	 */
	public Map<String,Object> getFile(CanorisFile file) throws ClientProtocolException, IOException, URISyntaxException {
		if (file == null)
			return null;
		Map<String, String> params = new HashMap<String, String>(2);
		params.put("fileKey", file.getKey());
		return CanorisConManager.getInstance().getResourceAsMap(params, Constants.URI_FILE);
	}
	/**
	 * Return an InputStream representing the requested file.
	 * The user has to manually handle the inputStream.
	 * 
	 * @param file
	 * @return InputStream
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public InputStream downloadFile(CanorisFile file) throws ClientProtocolException, IOException, URISyntaxException {
		if (file == null)
			return null;
		Map<String, String> params = new HashMap<String, String>(2);
		params.put("fileKey", file.getKey());
		return CanorisConManager.getInstance().getResource(params, Constants.URI_FILE_SERVE);
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
	public InputStream getConversion(CanorisFile file, String conversionName) throws ClientProtocolException, IOException, URISyntaxException {
		if (file == null)
			return null;
		Map<String, String> params = new HashMap<String, String>(2);
		params.put("fileKey", file.getKey());
		params.put("name", conversionName);
		return CanorisConManager.getInstance().getResource(params, Constants.URI_FILE_CONVERSION);
	}
	/**
	 * Gets the conversions of the file you pass
	 * 
	 * @param file
	 * @return Map<String,String> the conversions
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws URISyntaxException 
	 */
	public Map<String, Object> getConversions(CanorisFile file) throws ClientProtocolException, IOException, URISyntaxException {
		if (file == null)
			return null;
		Map<String, String> params = new HashMap<String, String>(1);
		params.put("fileKey", file.getKey());
		return CanorisConManager.getInstance().getResources(params, Constants.URI_FILE_CONVERSIONS);
	}
	
	// TODO: this should return a JsonNode since the analysis is a deeply 
	//		 nested Json object. Ask Vincent
	public Map<String, Object> getAnalysis(CanorisFile file, String filter) throws ClientProtocolException, IOException, URISyntaxException {
		if (file == null)
			return null;
		Map<String, String> params = new HashMap<String, String>(1);
		params.put("fileKey", file.getKey());
		params.put("filter", filter);
		
		return CanorisConManager.getInstance().getResourceAsMap(params, Constants.URI_FILE_ANALYSIS);
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
	public InputStream getVisualization(CanorisFile file, String name) throws ClientProtocolException, IOException, URISyntaxException {
		if (file == null)
			return null;
		Map<String, String> params = new HashMap<String, String>(2);
		params.put("fileKey", file.getKey());
		params.put("name", name);
		return CanorisConManager.getInstance().getResource(params, Constants.URI_FILE_VISUALIZATION);
	}
	/**
	 * 
	 * @param file
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws URISyntaxException 
	 */
	public Map<String,Object> getVisualizations(CanorisFile file) throws ClientProtocolException, IOException, URISyntaxException {
		if (file == null)
			return null;
		Map<String, String> params = new HashMap<String, String>(1);
		params.put("fileKey", file.getKey());
		return CanorisConManager.getInstance().getResources(params, Constants.URI_FILE_VISUALIZATIONS);
	}
	
	/* ***************************** TEMPLATES ***************************** */
	/**
	 * Gets the list of templates. If non exist it will return a map with 
	 * key="template", value="[]"
	 * 
	 * @return Map<String, Object>
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws URISyntaxException 
	 */
	public Map<String,Object> getTemplates() throws ClientProtocolException, IOException, URISyntaxException {
		Map<String, String> params = new HashMap<String, String>();
		return CanorisConManager.getInstance().getResources(params, Constants.URI_TEMPLATES);
	}
	/**
	 * Creates a template and returns the response or null
	 * 
	 * TODO: This should return a JsonNode??
	 * 
	 * @param template
	 * @return Map<String,Object>
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws URISyntaxException 
	 */
	public Map<String,Object> createTemplate(String templateName, String templateContent) 
										throws ClientProtocolException, IOException, URISyntaxException {
		Map<String,String> postParams = new HashMap<String, String>(1);
		postParams.put("name", templateName);
		postParams.put("template", templateContent);
		
		Map<String, String> params = new HashMap<String, String>();
		return CanorisConManager.getInstance().createResource(params, postParams, Constants.URI_TEMPLATES);
	}
	/**
	 * 
	 * @param template
	 * @param templateName
	 * @return JSonNode
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws URISyntaxException 
	 */
	public JsonNode updateTemplate(String templateContent, String templateName) 
												throws ClientProtocolException, IOException, URISyntaxException {
		Map<String,String> urlParams = new HashMap<String, String>(2);
		urlParams.put("templateName", templateName);
		Map<String,String> putParams = new HashMap<String, String>(2);
		putParams.put("template", templateContent);
		
		return CanorisConManager.getInstance().updateResource(urlParams, putParams, Constants.URI_TEMPLATE);
	}
	/**
	 * Deletes the give template
	 * TODO: explain what happens if not found?
	 * 
	 * @param templateName
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws URISyntaxException 
	 */
	public void deleteTemplate(String templateName) throws ClientProtocolException, IOException, URISyntaxException {
		Map<String,String> params = new HashMap<String,String>(1);
		params.put("templateName", templateName);
		
		CanorisConManager.getInstance().deleteResource(params, Constants.URI_TEMPLATE);
	}
	
	/* ***************************** TASKS ***************************** */
	public Map<String,Object> createTask(String taskName, String taskContent) 
									throws ClientProtocolException, IOException, URISyntaxException {
		Map<String,String> params = new HashMap<String,String>();
		Map<String,String> postParams = new HashMap<String,String>(2);
		postParams.put("template", taskName);
		postParams.put("parameters", taskContent);
		
		return CanorisConManager.getInstance().createResource(params, postParams, Constants.URI_TASK);
	}
	/**
	 * Get the task that corresponds to the taskId. 
	 * Null if it finds no task with give taskId ?
	 * 
	 * @param taskId
	 * @return Map<String,Object>
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws URISyntaxException 
	 */
	public Map<String,Object> getTask(String taskId) throws ClientProtocolException, IOException, URISyntaxException {
		Map<String,String> params = new HashMap<String,String>(1);
		params.put("taskId", taskId);

		return CanorisConManager.getInstance().getResourceAsMap(params, Constants.URI_TASK);
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
	 */
	public Map<String,Object> getText2Phon(String text, String language) 
										throws ClientProtocolException, IOException, URISyntaxException {
		Map<String,String> params = new HashMap<String,String>(2);
		params.put("text", text);
		params.put("language", language);
		
		return CanorisConManager.getInstance().getResourceAsMap(params, Constants.URI_PHONEMES);
	}
	
	/* ***************************** COLLECTION ***************************** */
	/**
	 * 
	 */
	public Pager getCollections() throws ClientProtocolException, IOException, URISyntaxException {
		Map<String,String> params = new HashMap<String, String>(0);
		return CanorisConManager.getInstance().getPagedResults(params, Constants.URI_COLLECTIONS);
	}
	/**
	 * 
	 * @param collectionKey
	 * @return Map<String,Object>
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public Map<String,Object> getCollection(String collectionKey) 
										throws ClientProtocolException, IOException, URISyntaxException {
		Map<String,String> params = new HashMap<String,String>(1);
		params.put("collectionKey", collectionKey);
		
		return CanorisConManager.getInstance().getResourceAsMap(params, Constants.URI_COLLECTION);
	}
	/**
	 * 
	 * @param name
	 * @param license
	 * @param visibility
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws URISyntaxException 
	 */
	public Map<String,Object> createCollection(String name, String license, String visibility) 
											throws ClientProtocolException, IOException, URISyntaxException {
		Map<String,String> params = new HashMap<String,String>(1);
		Map<String,String> postParams = new HashMap<String,String>(3);
		postParams.put("name", name);
		postParams.put("license", license);
		postParams.put("public", visibility);
		
		return CanorisConManager.getInstance().createResource(params, postParams, Constants.URI_COLLECTIONS);
	}
	/**
	 * Deletes the collection. This method does not return a value. 
	 * If no exception is thrown the operation will be successful.
	 * 
	 * @param collectionKey
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws URISyntaxException 
	 */
	public void deleteCollection(String collectionKey) 
							throws ClientProtocolException, IOException, URISyntaxException {
		Map<String,String> params = new HashMap<String, String>(1);
		params.put("collectionKey", collectionKey);
		
		CanorisConManager.getInstance().deleteResource(params, Constants.URI_COLLECTION);
	}
	
	/* ***************************** FILE COLLECTION ***************************** */
	/**
	 * 
	 */
	public Pager getCollectionFiles(String collectionKey) throws ClientProtocolException, IOException, URISyntaxException {
		Map<String,String> params = new HashMap<String, String>(1);
		params.put("collectionKey", collectionKey);
		
		return CanorisConManager.getInstance().getPagedResults(params, Constants.URI_COLLECTION_FILES);
	}
	/**
	 * Adds a file to the given collection.
	 * 
	 * @param collectionKey
	 * @param file
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws URISyntaxException 
	 */
	public void addFileToCollection(String collectionKey, CanorisFile file) 
			throws ClientProtocolException, IOException, URISyntaxException {
		if (file != null) {
			Map<String,String> uriParams = new HashMap<String, String>(1);
			uriParams.put("collectionKey", collectionKey);
			uriParams.put("filekey", file.getKey());
			Map<String,String> postParams = new HashMap<String, String>(1);
			
			CanorisConManager.getInstance().createResource(uriParams, postParams, Constants.URI_COLLECTION_FILES);
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
	 */
	public Map<String,Object> getCollectionFile(String collectionKey, CanorisFile file) 
											throws ClientProtocolException, 
												   IOException, 
												   URISyntaxException {
		if (file == null)
			return null;
		Map<String,String> params = new HashMap<String, String>();
		params.put("fileKey", file.getKey());
		params.put("collectionKey", collectionKey);
		
		return CanorisConManager.getInstance().getResourceAsMap(params, Constants.URI_COLLECTION_FILE);
	}
	/* ***************************** FILE COLLECTION ***************************** */
	public Map<String,Object> getSimilaritySearch(String collectionKey, CanorisFile file,
												  String preset, String results) 
											  throws ClientProtocolException, IOException, URISyntaxException {
		if (file == null)
			return null;
		Map<String,String> params = new HashMap<String, String>();
		params.put("collectionKey", collectionKey);
		params.put("fileKey", file.getKey());
		params.put("preset", preset);
		params.put("results", results);
		
		return CanorisConManager.getInstance().getResourceAsMap(params, Constants.URI_COLLECTION_SIMILAR);
	}
	/* ***************************** PAGING ***************************** */
	// What if there is no after or before page
	public Pager getNextPage(Pager pager) 
						throws ClientProtocolException, 
							   IOException, 
							   URISyntaxException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("page", pager.getNext());
		
		return CanorisConManager.getInstance().getPagedResults(params, null);
	}
	public Pager getPreviousPage(Pager pager) 
							throws ClientProtocolException, 
								   IOException, 
								   URISyntaxException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("page", pager.getPrevious());
		
		return CanorisConManager.getInstance().getPagedResults(params, null);
	}
	/**
	 * 
	 * @param useProxy
	 */
	public void useProxy(boolean useProxy) {
		CanorisConManager.getInstance().setUseProxy(useProxy);
	}
	// TODO: implement this on the CanorisConManager class
	public void configProxy(String host, int port, String protocol) {
		
	}
	
	
	//------------ GETTERS && SETTERS ---------------
	public CanorisConManager getCanorisRequest() {
		return canorisRequest;
	}

	public void setCanorisRequest(CanorisConManager canorisRequest) {
		this.canorisRequest = canorisRequest;
	}
	
}
