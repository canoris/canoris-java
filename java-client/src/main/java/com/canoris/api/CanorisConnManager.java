package com.canoris.api;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.antlr.stringtemplate.StringTemplate;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import com.canoris.api.exception.CanorisException;
import com.canoris.api.resources.CanorisFile;
import com.canoris.api.resources.Pager;

/*
 * TODO: 1) javadoc public methods	DONE
 *       2) refactor uploadFile
 * 	 	 3) entity.consumeContent problem in InputStream cases
 */
/**
 * 
 * Internally used class to perform the HTTPRequests. 
 * In addition JsonParseException(s) are caught here and wrapped 
 * in a CanorisException, which contains the HTTP_ERROR_CODE.
 * 
 */
public class CanorisConnManager {

	private static final String DEFAULT_ENCODING = "UTF-8";
	// TODO: check if we need to use some more strict singleton technique
	private static CanorisConnManager instance = null;
	// The object mapper to be used globally
	private static ObjectMapper mapper;
	
	private String proxyURL = "";
	private int proxyPort = 0;
	private String proxyProtocol = "";

	protected CanorisConnManager() {
	}

	public static CanorisConnManager getInstance() {
		if (instance == null) {
			instance = new CanorisConnManager();
			mapper = new ObjectMapper();
			// Should I create once the httpClient here?
		}
		return instance;
	}

	private boolean useProxy = false;

	public boolean isUseProxy() {
		return useProxy;
	}
	public void setUseProxy(boolean useProxy) {
		this.useProxy = useProxy;
	}
	
	public String getProxyURL() {
		return proxyURL;
	}

	public void setProxyURL(String proxyURL) {
		this.proxyURL = proxyURL;
	}

	public int getProxyPort() {
		return proxyPort;
	}

	public void setProxyPort(int proxyPort) {
		this.proxyPort = proxyPort;
	}

	public String getProxyProtocol() {
		return proxyProtocol;
	}

	public void setProxyProtocol(String proxyProtocol) {
		this.proxyProtocol = proxyProtocol;
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
	public CanorisFile uploadFile(String path) throws URISyntaxException,
			ClientProtocolException, IOException {
		HttpClient httpClient;
		HttpHost target = new HttpHost(CanorisAPI.getInstance().getBaseURL(),
				80, "http");
		HttpPost httpPost = new HttpPost("/files/?api_key="
				+ CanorisAPI.getInstance().getApiKey());

		if (useProxy) {
			// TODO: maybe a bit of a dirty workaround...
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

		HttpResponse response = httpClient.execute(target, httpPost);

		HttpEntity resEntity = response.getEntity();
		CanorisFile canFile = null;

		if (resEntity != null) {
			canFile = new CanorisFile();
			Map<String, Object> map = mapper.readValue(EntityUtils
					.toString(resEntity),
					new TypeReference<Map<String, Object>>() {
					});
			canFile.setProperties(map);

			// release resources
			resEntity.consumeContent();
		}

		return canFile;
	}
	/**
	 * Returns a canorisFile or null if no file is found with the give fileKey.
	 * 
	 * TODO: customMapper so it setups up the properties/key/ref automaticaly 
	 * 
	 * @param params
	 * @param resourceType
	 * @return CanorisFile
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 * @throws IOException
	 * @throws URISyntaxException
	 * @throws CanorisException
	 */
	public CanorisFile getCanorisFile(Map<String, String> params, String resourceType) 
									throws 
										IOException, 
										JsonMappingException, 
										JsonParseException, 
										IOException, 
										URISyntaxException, 
										CanorisException {
		HttpResponse response = doGet(params, resourceType);
		CanorisFile canFile = null;
		if (response.getEntity() != null) {
			try {
				canFile = new CanorisFile();
				Map<String, Object> map = mapper.readValue(EntityUtils.toString(response.getEntity()),
															new TypeReference<Map<String, Object>>() {});
				canFile.setProperties(map);
				canFile.setKey((String) map.get("key"));
				canFile.setRef((String) map.get("ref"));
			} catch (JsonParseException e) {
				throw new CanorisException(response.getStatusLine().getStatusCode(), e);
			}
			response.getEntity().consumeContent();
		}
		return canFile;
	}
	
	
	/**
	 * Returns the requested file
	 * 
	 * @param params
	 * @param resourceType
	 * @return InputStream
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws URISyntaxException 
	 */
	public InputStream getResource(Map<String, String> params, String resourceType) 
									throws 
										ClientProtocolException, 
										IOException, 
										URISyntaxException {
		HttpResponse response = doGet(params, resourceType);
		InputStream in = null;
		if (response.getEntity() != null) {
			in = response.getEntity().getContent();
			// TODO: if I do response.getEntity().consumeContent(); 
			// then I get socketClosed error, how to rectify this?
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
	 * @throws URISyntaxException 
	 * @throws Cano.risException 
	 */
	public Map<String,Object> getResourceAsMap(Map<String, String> params, String resourceType) 
										throws 
											ClientProtocolException, 
											IOException, 
											URISyntaxException, 
											CanorisException {
		HttpResponse response = doGet(params, resourceType);
		Map<String,Object> responseMap = null;
		if (response.getEntity() != null) {
			try {
			responseMap = mapper.readValue(EntityUtils.toString(response.getEntity()), 
											new TypeReference<Map<String, Object>>() {});
			response.getEntity().consumeContent();
			} catch (JsonParseException e) {
				throw new CanorisException(response.getStatusLine().getStatusCode(), e);
			}
		}
		return responseMap;
	}
	
	/**
	 * 
	 * @param params
	 * @param resourceType
	 * @return Map<String, Object>
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws URISyntaxException 
	 * @throws CanorisException 
	 */
	public Map<String, Object> getResources(Map<String, String> params,
											String resourceType)
										throws 
											ClientProtocolException, 
											IOException, 
											URISyntaxException, 
											CanorisException {
		HttpResponse response = doGet(params, resourceType);
		Map<String, Object> resources = null;
		if (response.getEntity() != null) {
			try {
				resources = mapper.readValue(EntityUtils.toString(response.getEntity()), 
											 new TypeReference<Map<String, Object>>() {});
				response.getEntity().consumeContent();
			} catch (JsonParseException e) {
				throw new CanorisException(response.getStatusLine().getStatusCode(), e);
			}
		}
		return resources;
	}
	/**
	 * Returns the requested resource as a JsonNode. This is the only viable
	 * solution in some case due to the JSON results being heavily nested.
	 * 
	 * @param params
	 * @param resourceType
	 * @return JsonNode
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws ParseException
	 * @throws IOException
	 * @throws URISyntaxException 
	 * @throws CanorisException 
	 */
	public JsonNode getResourcesAsTree(Map<String, String> params,
									   String resourceType) 
									throws JsonParseException, 
										   JsonMappingException, 
										   ParseException, 
										   IOException, 
										   URISyntaxException, 
										   CanorisException {
		HttpResponse response = doGet(params, resourceType);
		JsonNode resources = null;
		if (response.getEntity() != null) {
			try {
				resources = mapper.readValue(EntityUtils.toString(response.getEntity()), JsonNode.class);
				response.getEntity().consumeContent();
			} catch (JsonParseException e) {
				throw new CanorisException(response.getStatusLine().getStatusCode(), e);
			}
		}
		return resources;
	}
	/**
	 * Creates a resource by executing a POST request.
	 * 
	 * @param uriParams
	 * @param postParams
	 * @param resourceType
	 * @return Map<String,Object>
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws URISyntaxException 
	 * @throws CanorisException 
	 */
	public Map<String,Object> createResource(Map<String,String> uriParams,
											 Map<String,String> postParams,
											 String resourceType) 
											throws 
												ClientProtocolException, 
												IOException, 
												URISyntaxException, 
												CanorisException {
		HttpResponse response = doPost(uriParams, postParams, resourceType);
		Map<String, Object> responseMap = null;
		if (response.getEntity() != null) {
			responseMap = new HashMap<String, Object>();
			try {
				responseMap = mapper.readValue(EntityUtils.toString(response.getEntity()), 
											   new TypeReference<Map<String, Object>>() {});
				response.getEntity().consumeContent();
			} catch (JsonParseException e) {
				throw new CanorisException(response.getStatusLine().getStatusCode(), e);
			}
		}
		return responseMap;
	}
	/**
	 * Updates a resource by executing a PUT request.
	 * 
	 * @param urlParams
	 * @param putParams
	 * @param resourceType
	 * @return JsonNode
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws URISyntaxException 
	 * @throws CanorisException 
	 */
	public JsonNode updateResource(Map<String,String> urlParams,
											 Map<String,String> putParams, 
											 String resourceType) 
								throws 
									ClientProtocolException, 
									IOException, 
									URISyntaxException, 
									CanorisException {
		HttpResponse response = doPut(urlParams, putParams, resourceType);
		JsonNode jsonNode = null;
		if (response.getEntity() != null) {
			try {
				jsonNode = mapper.readValue(EntityUtils.toString(response.getEntity()), JsonNode.class);
				response.getEntity().consumeContent();
			} catch (JsonParseException e) {
				throw new CanorisException(response.getStatusLine().getStatusCode(), e);
			}
		}
		return jsonNode;
	}
	/**
	 * Delete the resource. Does not return anything.
	 * TODO: what exception does this throw that is useful???
	 * 
	 * @param params
	 * @param resourceType
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws URISyntaxException 
	 */
	public void deleteResource(Map<String,String> params, String resourceType) 
							throws ClientProtocolException, IOException, URISyntaxException {
		doDelete(params, resourceType);
	}
	/**
	 * Returns a pager object holding the results. Depending on the type 
	 * of the request the pager will hold files, templates or collections.
	 * 
	 * @param params
	 * @param resourceType
	 * @return Pager
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws URISyntaxException 
	 * @throws CanorisException 
	 */
	public Pager getPagedResults(Map<String,String> params, String resourceType) 
							throws 
								ClientProtocolException, 
								IOException, 
								URISyntaxException, 
								CanorisException {
		HttpResponse response = doGet(params, resourceType);
		Pager pager = null;
		if (response.getEntity() != null) {
			try {
				pager = mapper.readValue(EntityUtils.toString(response.getEntity()), Pager.class);
				response.getEntity().consumeContent();
			} catch (JsonParseException e) {
				throw new CanorisException(response.getStatusLine().getStatusCode(), e);
			}
		}
		return pager;
	}
	/*
	 * Perform a GET request
	 */
	private HttpResponse doGet(Map<String, String> params, String resourceType)
								throws ClientProtocolException, 
									   IOException, URISyntaxException {
		HttpClient client = createClient();
		HttpGet httpGet = new HttpGet(constructURI(params, resourceType));
		HttpResponse response = client.execute(httpGet);

		return response;
	}
	/*
	 * Perform a POST request
	 */
	private HttpResponse doPost(Map<String, String> uriParams, 
								Map<String, String> postParams,
								String resourceType) 
							throws 
								ClientProtocolException, 
								IOException, 
								URISyntaxException {
		
		HttpClient client = createClient();
		HttpPost httpPost = new HttpPost(constructURI(uriParams, resourceType));
		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(createParams(postParams), "UTF-8");
		httpPost.setEntity(entity);
		
		return client.execute(httpPost);
	}
	/*
	 * Perform a PUT request
	 */
	private HttpResponse doPut(Map<String, String> uriParams, 
							   Map<String, String> putParams, 
							   String resourceType) 
							throws 
								ClientProtocolException, 
								IOException, 
								URISyntaxException {
		
		HttpClient client = createClient();
		HttpPut httpPut = new HttpPut(constructURI(uriParams, resourceType));
		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(createParams(putParams), "UTF-8");
		httpPut.setEntity(entity);
		
		return client.execute(httpPut);
	}
	/*
	 * Perform a DELETE request
	 */
	private HttpResponse doDelete(Map<String, String> params, String resourceType) 
								throws 
									ClientProtocolException, 
									IOException, 
									URISyntaxException {
		HttpClient client = createClient();
		HttpHost target = new HttpHost(CanorisAPI.getInstance().getBaseURL(), 80, "http");
		HttpDelete httpDelete = new HttpDelete(constructURI(params, resourceType));
		
		return client.execute(target, httpDelete);
	}

	/*
	 * Helper method to construct a URI.
	 * TODO: It will fail is params is null, need to fix this.
	 * 		 It's valid to have no params in some cases.
	 */
	private URI constructURI(Map<String,String> params, String resourceType) 
						throws URISyntaxException {
		URI uri = null;
		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
		// Add the api_key (required always)
		qparams.add(new BasicNameValuePair("api_key", CanorisAPI.getInstance().getApiKey()));
		
		// If the ref exists we can use it to construct the URI directly
		if (params.get("ref") != null) {
			return new URI(params.get("ref") + "&api_key=" + CanorisAPI.getInstance().getApiKey());
		} 
		// The page contains the complete URL to use
		else if (params.get("page") != null) {
			return new URI(params.get("page") + "&api_key=" + CanorisAPI.getInstance().getApiKey());
		} 
		// ref/page does not exist so we need to construct the complete URI
		else {
			StringTemplate template = new StringTemplate();
			template.setTemplate(resourceType);
			
			// Need to handle the text2phoneme case separately
			if (Constants.URI_PHONEMES.equals(resourceType)) {
				for (String key : params.keySet()) {
					qparams.add(new BasicNameValuePair(key, params.get(key)));
				}	
			} 
			// Normal case, just replace the placeholders
			else {
				template.setAttributes(params);
			}
			uri = URIUtils.createURI("http", CanorisAPI.getInstance().getBaseURL(), -1,
						   			 template.toString(), URLEncodedUtils.format(qparams, "UTF-8"), null);
		}
		
		return uri;
	}
	/*
	 * Helper to iterate over the params map.
	 */
	private List<NameValuePair> createParams(Map<String,String> params) {
		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
		for (String key : params.keySet()) {
			qparams.add(new BasicNameValuePair(key, params.get(key)));
		}
		return qparams;
	}
	/*
	 * Setup client
	 */
	private HttpClient createClient() {
		HttpClient httpClient;
		// Check for proxy
		if (useProxy)
			httpClient = setupProxy(getProxyURL(), getProxyPort(), getProxyProtocol());
		else
			httpClient = new DefaultHttpClient();

		return httpClient;
	}
	/*
	 * Helper method to setup proxy
	 */
	protected HttpClient setupProxy(String proxyAddress, Integer port, String protocol) {
		// Create proxy
		HttpHost proxy = new HttpHost(proxyAddress, port, protocol);
		SchemeRegistry supportedSchemes = new SchemeRegistry();
		// Register the "http" protocol scheme, they are
		// required by the default operator to look up socket factories.
		supportedSchemes.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		// prepare parameters
		HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_0);
		HttpProtocolParams.setContentCharset(params, DEFAULT_ENCODING);
		HttpProtocolParams.setUseExpectContinue(params, true);

		ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, supportedSchemes);
		DefaultHttpClient httpClient = new DefaultHttpClient(ccm, params);
		httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);

		return httpClient;
	}

}
