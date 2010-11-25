package com.canoris;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.codehaus.jackson.JsonNode;

import com.canoris.exception.CanorisException;
import com.canoris.resources.CanorisFile;
import com.canoris.resources.CanorisResource;
import com.canoris.resources.Pager;

/*
 * This essentially is just a facade (kind off...) to the CanorisConnManager.
 * TODO: 1) Add javadoc to all methods.
 * 		 2) Add getPage method to get a specific page.
 * 		 3) Externalize strings (Do an interface with constants or something...)
 * 		 4) Imlement configProxy method
 */
/**
 * This class is the main communication point with the back-end. 
 * Through this class you can perform all the operation the back-end offers.
 */
public class CanorisResourceManager {
	
	private static final String COLLECTIONS = "collections";
	private static final String FILE_KEY = "";
	
	private CanorisConnManager canorisRequest;
	
	
	/**
	 * Creates a file resource. You can pass extra parameters if applicable and the method 
	 * will use them as required.
	 * The params parameter is reserved for later use.
	 * 
	 * @param filePath
	 * @param params
	 * @return CanorisResource 
	 * 						The created object.
	 * @throws ClientProtocolException
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	public CanorisResource createFile(String filePath, Map<String,String> params) throws ClientProtocolException, URISyntaxException, IOException {
		CanorisConnManager conManager = CanorisConnManager.getInstance();
		return conManager.uploadFile(filePath);
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
	 * @return Map<String,Object>
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws URISyntaxException 
	 * @throws CanorisException 
	 */
	public Map<String,Object> getFile(CanorisFile file) throws ClientProtocolException, IOException, URISyntaxException, CanorisException {
		if (file == null)
			return null;
		Map<String, String> params = new HashMap<String, String>(2);
		params.put("fileKey", file.getKey());
		return CanorisConnManager.getInstance().getResourceAsMap(params, Constants.URI_FILE);
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
	public InputStream getConversion(CanorisFile file, String conversionName) throws ClientProtocolException, IOException, URISyntaxException {
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
	
	// TODO: this should return a JsonNode since the analysis is a deeply 
	//		 nested Json object. Ask Vincent
	public Map<String, Object> getAnalysis(CanorisFile file, String filter) 
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
		
		return CanorisConnManager.getInstance().getResourceAsMap(params, Constants.URI_FILE_ANALYSIS);
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
		return CanorisConnManager.getInstance().getResource(params, Constants.URI_FILE_VISUALIZATION);
	}
	/**
	 * 
	 * @param file
	 * @return
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
	 * Gets the list of templates. If non exist it will return a map with 
	 * key="template", value="[]"
	 * 
	 * @return Map<String, Object>
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws URISyntaxException 
	 * @throws CanorisException 
	 */
	public Map<String,Object> getTemplates() throws ClientProtocolException, IOException, URISyntaxException, CanorisException {
		Map<String, String> params = new HashMap<String, String>();
		return CanorisConnManager.getInstance().getResources(params, Constants.URI_TEMPLATES);
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
	 * @throws CanorisException 
	 */
	public Map<String,Object> createTemplate(String templateName, String templateContent) 
										throws ClientProtocolException, IOException, URISyntaxException, CanorisException {
		Map<String,String> postParams = new HashMap<String, String>(1);
		postParams.put("name", templateName);
		postParams.put("template", templateContent);
		
		Map<String, String> params = new HashMap<String, String>();
		return CanorisConnManager.getInstance().createResource(params, postParams, Constants.URI_TEMPLATES);
	}
	/**
	 * 
	 * @param template
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
	public void deleteTemplate(String templateName) throws ClientProtocolException, IOException, URISyntaxException {
		Map<String,String> params = new HashMap<String,String>(1);
		params.put("templateName", templateName);
		
		CanorisConnManager.getInstance().deleteResource(params, Constants.URI_TEMPLATE);
	}
	
	/* ***************************** TASKS ***************************** */
	
	/**
	 * Creates with the give taskName, containing the taskContent.
	 * 
	 * @param taskName
	 * @param templateContent
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
	 * @return
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
		
		CanorisConnManager.getInstance().deleteResource(params, Constants.URI_COLLECTION);
	}
	
	/* ***************************** FILE COLLECTION ***************************** */
	/**
	 * Returns a Pager. You can use the pager to access the collection
	 * or navigate through its pages.
	 * 
	 * @param  collectionKey	The key used to retrieve the collection.
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
			uriParams.put("filekey", file.getKey());
			Map<String,String> postParams = new HashMap<String, String>(1);
			
			CanorisConnManager.getInstance().createResource(uriParams, postParams, Constants.URI_COLLECTION_FILES);
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
	public Map<String,Object> getSimilaritySearch(String collectionKey, CanorisFile file,
												  String preset, String results) 
											  throws ClientProtocolException, IOException, URISyntaxException, CanorisException {
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
	// What if there is no after or before page
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
	 * @return
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
	 * 
	 * @param useProxy
	 */
	public void useProxy(boolean useProxy) {
		CanorisConnManager.getInstance().setUseProxy(useProxy);
	}
	public void configProxy(String host, int port, String protocol) {
		CanorisConnManager.getInstance().setupProxy(host, port, protocol);
	}
	
	//------------ GETTERS && SETTERS ---------------
	public CanorisConnManager getCanorisRequest() {
		return canorisRequest;
	}

	public void setCanorisRequest(CanorisConnManager canorisRequest) {
		this.canorisRequest = canorisRequest;
	}
	
}
