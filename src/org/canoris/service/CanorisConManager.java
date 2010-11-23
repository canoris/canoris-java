package org.canoris.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.language.DefaultTemplateLexer;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;
import org.canoris.CanorisAPI;
import org.canoris.Constants;
import org.canoris.resource.types.CanFile;
import org.canoris.util.Pager;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

/*
 * TODO: 1) javadoc public methods
 * 		 2) refactor some methods 
 * 			 - constructUri
 * 			 - uploadFile
 * 		 3) Use global ObjectMapper
 * 		 
 */
public class CanorisConManager {

	private static final String DEFAULT_ENCODING = "UTF-8";
	// TODO: check if we need to use some more strict singleton technique
	private static CanorisConManager instance = null;

	protected CanorisConManager() {
	}

	public static CanorisConManager getInstance() {
		if (instance == null) {
			instance = new CanorisConManager();
		}
		return instance;
	}

	private boolean useProxy = false;

	public boolean getUseProxy() {
		return useProxy;
	}

	public void setUseProxy(boolean useProxy) {
		this.useProxy = useProxy;
	}

	/**
	 * Uploads a file to the server.
	 * 
	 * @param path
	 * @return CanFile containing the response of the request
	 * @throws URISyntaxException
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public CanFile uploadFile(String path) throws URISyntaxException,
			ClientProtocolException, IOException {
		HttpClient httpClient;
		HttpHost target = new HttpHost(CanorisAPI.getInstance().getBaseURL(),
				80, "http");
		HttpPost httpPost = new HttpPost("/files/?api_key="
				+ CanorisAPI.getInstance().getApiKey());

		// TODO: setup proxy should be called by the CanorisREsourceManager
		if (useProxy) {
			httpClient = setupProxy("proxy.upf.edu", 8080, "http");
		} else {
			httpClient = new DefaultHttpClient();
		}

		File fileToSend = new File(path);
		FileBody bin = new FileBody(fileToSend);
		StringBody comment = new StringBody("Testing upload java client");

		MultipartEntity reqEntity = new MultipartEntity();
		// "file" <---this is necessary, the backend is coded to check this
		// ie. using "bin" will result in BAD REQUEST
		reqEntity.addPart("file", bin);
		reqEntity.addPart("comment", comment);

		httpPost.setEntity(reqEntity);

		// System.out.println("executing request " + httpPost.getRequestLine());

		HttpResponse response = httpClient.execute(target, httpPost);
		// System.out.println("Response : " + response);

		HttpEntity resEntity = response.getEntity();
		CanFile canFile = null;
		// TODO: make a mapper class that encapsulates this process, KEEP IT
		// CLEAN!!!
		if (resEntity != null) {
			canFile = new CanFile();
			ObjectMapper mapper = new ObjectMapper(); // can reuse, share
			// globally
			Map<String, Object> map = mapper.readValue(EntityUtils
					.toString(resEntity),
					new TypeReference<Map<String, Object>>() {
					});
			canFile.setProperties(map);

			// TODO: what exactly does consumeContent do, releases resources???
			resEntity.consumeContent();
		}

		return canFile;
	}

	/**
	 * Returns a Pager object to be used for navigating the pages or getting
	 * access to a specific element in the page.
	 * 
	 * @return Pager
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public Pager getFiles() throws ClientProtocolException, IOException {
		Map<String, String> params = new HashMap<String, String>();
		HttpResponse response = doGet(params, "files");
		Pager pager = null;
		// Put the object mapper in constructor it should be a singleton
		if (response.getEntity() != null) {
			HttpEntity resEntity = response.getEntity();
			ObjectMapper mapper = new ObjectMapper(); // can reuse, share
			// THIS IS A HACK!!!
			pager = mapper.readValue(EntityUtils.toString(resEntity), Pager.class);
			// release resources
			resEntity.consumeContent();
		}

		return pager;
	}

	/**
	 * Returns the requested file
	 * 
	 * @param fileKey
	 *            The key of the file to get the conversion from
	 * @param ref
	 *            This can be the reference or name of the resource i.e.
	 *            conversion name, file reference, visualization name etc.
	 * @param resourceType
	 * @return InputStream
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public InputStream getResource(Map<String, String> params, String resourceType) 
									throws ClientProtocolException, IOException {
		HttpResponse response = doGet(params, resourceType);
		InputStream in = null;
		if (response.getEntity() != null) {
			in = response.getEntity().getContent();
		}
		return in;
	}
	/**
	 * Returns a resource as a Map<String,Object>. This is eligible only for 
	 * specific type of resources that don't return a "file handler".
	 * 
	 * @param params
	 * @param resourceType
	 * @return Map<String,Object> the representation of the resource
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public Map<String,Object> getResourceAsMap(Map<String, String> params, String resourceType) 
										throws ClientProtocolException, IOException {
		HttpResponse response = doGet(params, resourceType);
		Map<String,Object> responseMap = null;
		if (response.getEntity() != null) {
			ObjectMapper mapper = new ObjectMapper(); // can reuse, share
			// globally
			responseMap = mapper.readValue(EntityUtils.toString(response.getEntity()), 
											new TypeReference<Map<String, Object>>() {});
		}
		return responseMap;
	}
	
	/**
	 * 
	 * @param params
	 * @param resourceType
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public Map<String, Object> getResources(Map<String, String> params,
											String resourceType)
												throws ClientProtocolException, IOException {
		HttpResponse response = doGet(params, resourceType);
		Map<String, Object> resources = null;
		if (response.getEntity() != null) {
			ObjectMapper mapper = new ObjectMapper(); // can reuse, share
			// globally
			resources = mapper.readValue(EntityUtils.toString(response
					.getEntity()), new TypeReference<Map<String, Object>>() {
			});
		}
		return resources;
	}
	/**
	 * Returns the requested resource as a JsonNode. This is the only viable
	 * solution in some case due to the Json results being heavily nested.
	 * 
	 * @param params
	 * @param resourceType
	 * @return JsonNode
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws ParseException
	 * @throws IOException
	 */
	public JsonNode getResourcesAsTree(Map<String, String> params,
									   String resourceType) 
									throws JsonParseException, 
										   JsonMappingException, 
										   ParseException, 
										   IOException {
		HttpResponse response = doGet(params, resourceType);
		JsonNode resources = null;
		if (response.getEntity() != null) {
			ObjectMapper mapper = new ObjectMapper(); // can reuse, share
			// globally
			resources = mapper.readValue(EntityUtils.toString(response.getEntity()), JsonNode.class);
		}
		return resources;
	}
	/**
	 * Creates a resource. It actually executes a POST.
	 * 
	 * @param params
	 * @param resourceType
	 * @return Map<String,Object>
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public Map<String,Object> createResource(Map<String,String> uriParams,
											 Map<String,String> postParams,
											 String resourceType) 
											throws ClientProtocolException, IOException {
		HttpResponse response = doPost(uriParams, postParams, resourceType);
		Map<String, Object> responseMap = null;
		if (response.getEntity() != null) {
			responseMap = new HashMap<String, Object>();
			ObjectMapper mapper = new ObjectMapper(); // can reuse, share
			// globally
			responseMap = mapper.readValue(EntityUtils.toString(response.getEntity()), 
											new TypeReference<Map<String, Object>>() {});
		}
		return responseMap;
	}
	/**
	 * Updates a resource. It actually executes a PUT.
	 * 
	 * @param params
	 * @param resourceType
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public Map<String,Object> updateResource(Map<String,String> params, String resourceType) throws ClientProtocolException, IOException {
		HttpResponse response = doPut(params, resourceType);
		Map<String, Object> responseMap = null;
		if (response.getEntity() != null) {
			responseMap = new HashMap<String, Object>();
			ObjectMapper mapper = new ObjectMapper(); // can reuse, share
			// globally
			responseMap = mapper.readValue(EntityUtils.toString(response.getEntity()), 
											new TypeReference<Map<String, Object>>() {});
		}
		return responseMap;
	}
	public void deleteResource(Map<String,String> params, String resourceType) 
							throws ClientProtocolException, IOException {
		doDelete(params, resourceType);
	}
	public Pager getPagedResults(Map<String,String> params, String resourceType) 
							throws ClientProtocolException, IOException {
		HttpResponse response = doGet(params, resourceType);
		Pager pager = null;
		if (response.getEntity() != null) {
			ObjectMapper mapper = new ObjectMapper(); // can reuse, share
			pager = mapper.readValue(EntityUtils.toString(response.getEntity()), Pager.class);
		}
		return pager;
	}
	/*
	 * These 4 methods below should probably go to another class?
	 * Something is wrong with the design, rethink it.
	 */
	private HttpResponse doGet(Map<String, String> params, String resourceType)
								throws ClientProtocolException, IOException {
		HttpClient client = createClient();
		HttpHost target = new HttpHost(CanorisAPI.getInstance().getBaseURL(), 80, "http");
		HttpGet httpGet = new HttpGet(constructUri(params, resourceType));
		HttpResponse response = client.execute(target, httpGet);

		// TODO: find out about resource releasing, I think the
		// entity.consumeCOntent
		// takes care of this but double check
		// client.getConnectionManager().shutdown();

		return response;
	}
	private HttpResponse doPost(Map<String, String> uriParams, 
								Map<String, String> postParams,
								String resourceType) 
							throws ClientProtocolException, IOException {
		HttpClient client = createClient();
		HttpHost target = new HttpHost(CanorisAPI.getInstance().getBaseURL(), 80, "http");
		HttpPost httpPost = new HttpPost(constructUri(uriParams, resourceType));
		
		StringEntity entity = new StringEntity(createParams(postParams), DEFAULT_ENCODING);
		httpPost.setEntity(entity);
		HttpResponse response = client.execute(target, httpPost);
		
		return response;
	}
	private HttpResponse doPut(Map<String, String> params, String resourceType) 
							throws ClientProtocolException, IOException {
		HttpClient client = createClient();
		HttpHost target = new HttpHost(CanorisAPI.getInstance().getBaseURL(), 80, "http");
		HttpPut httpPut = new HttpPut(constructUri(params, resourceType));
		
		StringEntity entity = new StringEntity(createParams(params), DEFAULT_ENCODING);
		httpPut.setEntity(entity);
		
		return client.execute(target, httpPut);
	}
	private HttpResponse doDelete(Map<String, String> params, String resourceType) 
								throws ClientProtocolException, IOException {
		HttpClient client = createClient();
		HttpHost target = new HttpHost(CanorisAPI.getInstance().getBaseURL(), 80, "http");
		HttpDelete httpDelete = new HttpDelete(constructUri(params, resourceType));
		
		return client.execute(target, httpDelete);
	}

	/*
	 * Setup client
	 */
	private HttpClient createClient() {
		// Create client
		HttpClient httpClient;
		// Check for proxy
		if (useProxy)
			httpClient = setupProxy("proxy.upf.edu", 8080, "http");
		else
			httpClient = new DefaultHttpClient();

		return httpClient;
	}

	/*
	 * Helper to construct the URI to be used in the http requeClientProtocolExceptionst
	 * FIXME: fucking ugly, do something!
	 * 		  ***CHECK THE URIUtils class***
	 */
	private String constructUri(Map<String, String> params, String resourceType) {
		String uri = null;
		StringTemplate strTemplate = new StringTemplate();
		
		if ("file".equals(resourceType)) {
			StringTemplate templ = new StringTemplate(Constants.URI_FILE,
					DefaultTemplateLexer.class);
			strTemplate.setTemplate(Constants.URI_FILE);
			
			templ.setAttribute("fileKey", params.get("fileKey"));
			uri = templ.toString() + "/serve/?api_key="
					+ CanorisAPI.getInstance().getApiKey();
		} else if ("files".equals(resourceType)) {
			uri = Constants.URI_FILES + "/?api_key="
					+ CanorisAPI.getInstance().getApiKey();
		} else if ("conversions".equals(resourceType)) {
			StringTemplate stringTemplate = new StringTemplate(
					Constants.URI_FILE_CONVERSIONS, DefaultTemplateLexer.class);
			stringTemplate.setAttribute("fileKey", params.get("fileKey"));
			uri = stringTemplate.toString()
					+ "?api_key=4768e6418bda4d81a67656e34df8b89b";
		} else if ("conversion".equals(resourceType)) {
			StringTemplate stringTemplate = new StringTemplate(
					Constants.URI_FILE_CONVERSION, DefaultTemplateLexer.class);
			stringTemplate.setAttribute("fileKey", params.get("fileKey"));
			stringTemplate.setAttribute("conversion", params.get("ref"));
			uri = stringTemplate.toString()
					+ "?api_key=4768e6418bda4d81a67656e34df8b89b";
		} else if ("analysis".equals(resourceType)) {
			StringTemplate stringTemplate = new StringTemplate(
					Constants.URI_FILE_ANALYSIS, DefaultTemplateLexer.class);
			stringTemplate.setAttribute("fileKey", params.get("fileKey"));
			if (params.get("filter") != null
					&& !"".equals(params.get("filter"))) {
				stringTemplate.setAttribute("filter", params.get("filter"));
			}
			uri = stringTemplate.toString() + "?api_key="
					+ CanorisAPI.getInstance().getApiKey();
		} else if ("visualizations".equals(resourceType)) {
			StringTemplate stringTemplate = new StringTemplate(
					Constants.URI_FILE_VISUALIZATIONS, DefaultTemplateLexer.class);
			stringTemplate.setAttribute("fileKey", params.get("fileKey"));
			uri = stringTemplate.toString() + "?api_key="
					+ CanorisAPI.getInstance().getApiKey();
		} else if ("templates".equals(resourceType)) {
			if (params != null) {
				StringTemplate stringTemplate = new StringTemplate(
						Constants.URI_TEMPLATES, DefaultTemplateLexer.class);
				stringTemplate.setAttribute("templateName", params.get("name"));
				uri = stringTemplate.toString();
			} else {
				uri = Constants.URI_TEMPLATES;
			}
			uri += "?api_key=" + CanorisAPI.getInstance().getApiKey();
		} else if ("template".equals(resourceType)) {
			StringTemplate stringTemplate = new StringTemplate(
					Constants.URI_TEMPLATE, DefaultTemplateLexer.class);
			stringTemplate.setAttribute("fileKey", params.get("fileKey"));
			stringTemplate.setAttribute("templateName", params.get("name"));
			
			uri = stringTemplate.toString() + "?api_key="
					+ CanorisAPI.getInstance().getApiKey();
		} else if ("task".equals(resourceType)) {
			uri = Constants.URI_TASKS + "?api_key=" + CanorisAPI.getInstance().getApiKey();
		} else if ("tasks".equals(resourceType)) {
			StringTemplate stringTemplate = new StringTemplate(
					Constants.URI_TEMPLATE, DefaultTemplateLexer.class);
			stringTemplate.setAttribute("taskId", params.get("taskId"));
			
			uri = stringTemplate.toString() + "?api_key=" + CanorisAPI.getInstance().getApiKey();
		} else if ("text2Phoneme".equals(resourceType)) {
			uri = Constants.URI_PHONEMES + "?text=" + params.get("text")
					+ "&language=" + params.get("language") 
					+ "&api_key=" + CanorisAPI.getInstance().getApiKey();
		} else if ("collections".equals(resourceType)) {
			uri = Constants.URI_COLLECTIONS + "?api_key=" + CanorisAPI.getInstance().getApiKey();
		} else if ("collection".equals(resourceType)) {
			strTemplate.setTemplate(Constants.URI_COLLECTION);
			strTemplate.setAttribute("collectionKey", params.get("collectionKey"));
			
			uri = strTemplate.toString() + "?api_key=" + CanorisAPI.getInstance().getApiKey();
		} else if ("collectionFiles".equals(resourceType)) {
			strTemplate.setTemplate(Constants.URI_COLLECTION_FILES);
			strTemplate.setAttribute("collectionKey", params.get("collectionKey"));

			uri = strTemplate.toString() + "?api_key=" + CanorisAPI.getInstance().getApiKey();
		} else if ("collectionFile".equals(resourceType)) {
			strTemplate.setTemplate(Constants.URI_COLLECTION_FILE);
			strTemplate.setAttribute("collectionKey", params.get("collectionKey"));
			strTemplate.setAttribute("fileKey", params.get("fileKey"));
			
			uri = strTemplate.toString() + "?api_key=" + CanorisAPI.getInstance().getApiKey();
		} else if ("collectionSimilar".equals(resourceType)) {
			strTemplate.setTemplate(Constants.URI_COLLECTION_SIMILAR);
			strTemplate.setAttribute("collectionKey", params.get("collectionKey"));
			strTemplate.setAttribute("fileKey", params.get("fileKey"));
			strTemplate.setAttribute("preset", params.get("preset"));
			strTemplate.setAttribute("results", params.get("results"));
			
			uri = strTemplate.toString() + "?api_key=" + CanorisAPI.getInstance().getApiKey();
		} 
		// This is a special case since the page contains the part that describes
		// the resource type, we only need to add the api_key part
		else if ("page".equals(resourceType)) {
			uri = params.get("page") + "&api_key=" + CanorisAPI.getInstance().getApiKey();
		}
		return uri;
	}
	// TODO: Look for some util to do this?
	private String createParams(Map<String,String> params) {
		StringBuffer returnParams = new StringBuffer();
		
		for(String key : params.keySet()) {
			returnParams.append(key);
			returnParams.append("=");
			returnParams.append(params.get(key));
			returnParams.append("&");
		}
		return returnParams.substring(0, returnParams.lastIndexOf("&"));
	}
	
	/*
	 * Helper method to setup proxy
	 */
	private HttpClient setupProxy(String proxyAddress, Integer port,
			String protocol) {
		// Create proxy
		HttpHost proxy = new HttpHost(proxyAddress, port, protocol);
		// Schemes
		SchemeRegistry supportedSchemes = new SchemeRegistry();

		// Register the "http" protocol scheme, they are
		// required by the default operator to look up socket factories.
		supportedSchemes.register(new Scheme("http", PlainSocketFactory
				.getSocketFactory(), 80));
		// prepare parameters
		HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_0);
		HttpProtocolParams.setContentCharset(params, DEFAULT_ENCODING);
		HttpProtocolParams.setUseExpectContinue(params, true);

		ClientConnectionManager ccm = new ThreadSafeClientConnManager(params,
				supportedSchemes);

		DefaultHttpClient httpClient = new DefaultHttpClient(ccm, params);

		httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,
				proxy);

		return httpClient;

	}

}
