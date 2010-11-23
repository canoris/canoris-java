package org.canoris.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.canoris.resource.types.CanFile;
import org.canoris.resource.types.CanorisResource;
import org.canoris.util.Pager;

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
	 */
	public Pager getFiles() throws ClientProtocolException, IOException {
		return CanorisConManager.getInstance().getFiles();
	}
	
	/**
	 * Returns the input stream representing  
	 * the original upload audio file.
	 * 
	 * @param file
	 * @return InputStream
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public InputStream getFile(CanFile file) throws ClientProtocolException, IOException {
		if (file == null)
			return null;
		Map<String, String> params = new HashMap<String, String>(2);
		params.put("fileKey", file.getKey());
		return CanorisConManager.getInstance().getResource(params, "file");
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
	 */
	public InputStream getConversion(CanFile file, String conversionName) throws ClientProtocolException, IOException {
		if (file == null)
			return null;
		Map<String, String> params = new HashMap<String, String>(2);
		params.put("fileKey", file.getKey());
		params.put("name", conversionName);
		return CanorisConManager.getInstance().getResource(params, "conversion");
	}
	/**
	 * Gets the conversions of the file you pass
	 * 
	 * @param file
	 * @return Map<String,String> the conversions
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public Map<String, Object> getConversions(CanFile file) throws ClientProtocolException, IOException {
		if (file == null)
			return null;
		Map<String, String> params = new HashMap<String, String>(1);
		params.put("fileKey", file.getKey());
		return CanorisConManager.getInstance().getResources(params, "conversions");
	}
	
	// TODO: this should return a JsonNode since the analysis is a deeply 
	//		 nested Json object. Ask Vincent
	public Map<String, Object> getAnalysis(CanFile file, String filter) throws ClientProtocolException, IOException {
		if (file == null)
			return null;
		Map<String, String> params = new HashMap<String, String>(1);
		params.put("fileKey", file.getKey());
		params.put("filter", filter);
		
		return CanorisConManager.getInstance().getResourceAsMap(params, "analysis");
	}
	/**
	 * Gets the visualization of the passed file
	 * 
	 * @param file
	 * @param name
	 * @return An InputStream representing the visualization or null
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public InputStream getVisualization(CanFile file, String name) throws ClientProtocolException, IOException {
		if (file == null)
			return null;
		Map<String, String> params = new HashMap<String, String>(2);
		params.put("fileKey", file.getKey());
		params.put("name", name);
		return CanorisConManager.getInstance().getResource(params, "visualization");
	}
	/**
	 * 
	 * @param file
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public Map<String,Object> getVisualizations(CanFile file) throws ClientProtocolException, IOException {
		if (file == null)
			return null;
		Map<String, String> params = new HashMap<String, String>(1);
		params.put("fileKey", file.getKey());
		return CanorisConManager.getInstance().getResources(params, "visualizations");
	}
	
	/* ***************************** TEMPLATES ***************************** */
	/**
	 * Gets the list of templates. If non exist it will return a map with 
	 * key="template", value="[]"
	 * 
	 * @return Map<String, Object>
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public Map<String, Object> getTemplates() throws ClientProtocolException, IOException {
		return CanorisConManager.getInstance().getResources(null, "templates");
	}
	/**
	 * Creates a template and returns the response or null
	 * 
	 * @param template
	 * @return Map<String,Object>
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public Map<String,Object> createTemplate(String templateName, String templateContent) 
										throws ClientProtocolException, IOException {
		Map<String,String> postParams = new HashMap<String, String>(1);
		postParams.put("name", templateName);
		postParams.put("template", templateContent);
		
		return CanorisConManager.getInstance().createResource(null, postParams, "templates");
	}
	/**
	 * 
	 * @param template
	 * @param templateName
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public Map<String,Object> updateTemplate(String templateContent, String templateName) 
												throws ClientProtocolException, IOException {
		Map<String,String> params = new HashMap<String, String>(2);
		params.put("name", templateName);
		params.put("template", templateContent);
		
		return CanorisConManager.getInstance().updateResource(params, "template");
	}
	/**
	 * Deletes the give template
	 * TODO: explain what happens if not found?
	 * 
	 * @param templateName
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public void deleteTemplate(String templateName) throws ClientProtocolException, IOException {
		Map<String,String> params = new HashMap<String,String>(1);
		params.put("name", templateName);
		
		CanorisConManager.getInstance().deleteResource(params, "template");
	}
	
	/* ***************************** TASKS ***************************** */
	public Map<String,Object> createTask(String taskName, String taskContent) 
									throws ClientProtocolException, IOException {
		Map<String,String> postParams = new HashMap<String,String>(2);
		postParams.put("template", taskName);
		postParams.put("parameters", taskContent);
		
		return CanorisConManager.getInstance().createResource(null, postParams, "task");
	}
	/**
	 * Get the task that corresponds to the taskId. 
	 * Null if it finds no task with give taskId ?
	 * 
	 * @param taskId
	 * @return Map<String,Object>
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public Map<String,Object> getTask(String taskId) throws ClientProtocolException, IOException {
		Map<String,String> params = new HashMap<String,String>(1);
		params.put("taskId", taskId);

		return CanorisConManager.getInstance().getResourceAsMap(params, "tasks");
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
	 */
	public Map<String,Object> getText2Phon(String text, String language) 
										throws ClientProtocolException, IOException {
		Map<String,String> params = new HashMap<String,String>(2);
		params.put("text", text);
		params.put("language", language);
		
		return CanorisConManager.getInstance().getResourceAsMap(params, "text2Phoneme");
	}
	
	/* ***************************** COLLECTION ***************************** */
	public Pager getCollections() throws ClientProtocolException, IOException {
		Map<String,String> params = new HashMap<String, String>(0);
		return CanorisConManager.getInstance().getPagedResults(params, COLLECTIONS);
	}
	public Map<String,Object> getCollection(String collectionKey) 
										throws ClientProtocolException, IOException {
		Map<String,String> params = new HashMap<String,String>(1);
		params.put("collectionKey", collectionKey);
		
		return CanorisConManager.getInstance().getResourceAsMap(params, "collection");
	}
	public Map<String,Object> createCollection(String name, String license, String visibility) 
											throws ClientProtocolException, IOException {
		Map<String,String> postParams = new HashMap<String,String>(3);
		postParams.put("name", name);
		postParams.put("license", license);
		postParams.put("public", visibility);
		
		return CanorisConManager.getInstance().createResource(null, postParams, COLLECTIONS);
	}
	public void deleteCollection(String collectionKey) 
							throws ClientProtocolException, IOException {
		Map<String,String> params = new HashMap<String, String>(1);
		params.put("collectionKey", collectionKey);
		
		CanorisConManager.getInstance().deleteResource(params, "collection");
	}
	
	/* ***************************** FILE COLLECTION ***************************** */
	public Pager getCollectionFiles(String collectionKey) throws ClientProtocolException, IOException {
		Map<String,String> params = new HashMap<String, String>(1);
		params.put("collectionKey", collectionKey);
		
		return CanorisConManager.getInstance().getPagedResults(params, "collectionFiles");
	}
	public void addFileToCollection(String collectionKey, CanFile file) 
			throws ClientProtocolException, IOException {
		if (file != null) {
			Map<String,String> uriParams = new HashMap<String, String>(1);
			uriParams.put("collectionKey", collectionKey);
			Map<String,String> postParams = new HashMap<String, String>(1);
			postParams.put("filekey", file.getKey());
			
			CanorisConManager.getInstance().createResource(uriParams, postParams, "collectionFiles");
		}
	}
	public Map<String,Object> getCollectionFile(String collectionKey, CanFile file) 
											throws ClientProtocolException, IOException {
		if (file == null)
			return null;
		Map<String,String> params = new HashMap<String, String>();
		params.put("fileKey", file.getKey());
		params.put("collectionKey", collectionKey);
		
		return CanorisConManager.getInstance().getResourceAsMap(params, "collectionFile");
	}
	/* ***************************** FILE COLLECTION ***************************** */
	public Map<String,Object> getSimilaritySearch(String collectionKey, CanFile file,
												  String preset, String results) 
											  throws ClientProtocolException, IOException {
		if (file == null)
			return null;
		Map<String,String> params = new HashMap<String, String>();
		params.put("collectionKey", collectionKey);
		params.put("fileKey", file.getKey());
		params.put("preset", preset);
		params.put("results", results);
		
		return CanorisConManager.getInstance().getResourceAsMap(params, "collectionSimilar");
	}
	/* ***************************** PAGING ***************************** */
	// What if there is no after or before page
	public Pager getNextPage(Pager pager) throws ClientProtocolException, IOException {
		Map<String, String> params = new HashMap<String, String>();
		String page = pager.getNext();
		page = page.substring(page.indexOf(".com/") + 4);
		params.put("page", page);
		
		return CanorisConManager.getInstance().getPagedResults(params, "page");
	}
	public Pager getPreviousPage(Pager pager) throws ClientProtocolException, IOException {
		Map<String, String> params = new HashMap<String, String>();
		String page = pager.getPrevious();
		page = page.substring(page.indexOf(".com/") + 4);
		params.put("page", page);
		
		return CanorisConManager.getInstance().getPagedResults(params, "page");
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
