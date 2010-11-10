package org.canoris.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.language.DefaultTemplateLexer;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
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
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;
import org.canoris.CanorisAPI;
import org.canoris.Constants;
import org.canoris.resource.types.CanFile;
import org.canoris.resource.types.CanorisResource;
import org.canoris.util.Pager;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

public class CanorisConManager {
	
	// TODO: check if we need to use some more strict singleton technique
	private static CanorisConManager instance = null;
		protected CanorisConManager() {
	}
		
	public static CanorisConManager getInstance() {
		if(instance == null) {
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
	 * @param path
	 * @return CanFile containing the response of the request
	 * @throws URISyntaxException
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public CanFile uploadFile(String path) throws URISyntaxException, ClientProtocolException, IOException {
		HttpClient httpClient;
		HttpHost target = new HttpHost(CanorisAPI.getInstance().getBaseURL(), 80 , "http");
		HttpPost httpPost = new HttpPost("/files/?api_key=" + CanorisAPI.getInstance().getApiKey());
		
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
        // TODO: make a mapper class that encapsulates this process, KEEP IT CLEAN!!!
        if (resEntity != null) {
        	canFile = new CanFile();
            ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally
			Map<String, Object> map = mapper.readValue(EntityUtils.toString(resEntity), 
														new TypeReference<Map<String,Object>>() { });
			canFile.setProperties(map);
			
			// TODO: what exactly does consumeContent do, releases resources???
            resEntity.consumeContent();
        }
        
		return canFile;
	}
	/*
	 * TODO: this doesn't work as expected. The JSON response of this is in this format
	 * {
	 *   files=[
	 *			{
	 *		ref=http://api.canoris.com/files/b03abca7553d4b0d958015219c522914, 
	 *		key=b03abca7553d4b0d958015219c522914
	 *	}, 
	 	{
			ref=http://api.canoris.com/files/bc4841dc1b6943d9b451deeb3c1b319e, 
			key=bc4841dc1b6943d9b451deeb3c1b319e
		}, 
	      ], 
	total_files=10, 
	ref=http://api.canoris.com/files?page=0
}
	 */
	public List<CanorisResource> getFiles() throws ClientProtocolException, IOException {
		Map<String,String> params = new HashMap<String, String>();
		
		HttpResponse response = doRequest(params, "GET", "files");
		
		CanFile canFile = null;
        // TODO: make a wrapper class that encapsulates this process, KEEP IT CLEAN!!!
        if (response.getEntity() != null) {
        	HttpEntity resEntity = response.getEntity();
        	canFile = new CanFile();
            ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally
            // FIXME: Mapping fails as it should, need a custom mapper, there is no files property
            //		  for templates it would be templates property
            //		  Alternative: superclass with total_files,next,previous,ref
            //		  			   and subclass with the specific files/collections/templates field
            Pager map = mapper.readValue(EntityUtils.toString(resEntity), 
														new TypeReference<Pager>() { });
			//canFile.setProperties(map);
			
			// TODO: what exactly does consumeContent do, releases resources???
            resEntity.consumeContent();
        }
	        
		
		
		return null;
	}
	/**
	 * Returns the request file
	 * @param fileKey The key of the file to get the conversion from
	 * @param ref	  The conversion reference
	 * @param resourceType	
	 * @return	InputStream
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public InputStream getFile(String fileKey, String ref, String resourceType) throws ClientProtocolException, IOException {
		HttpClient client = createClient();
		HttpHost target = new HttpHost(CanorisAPI.getInstance().getBaseURL(), 80 , "http");
		Map<String,String> params = new HashMap<String, String>();
		params.put("fileKey", fileKey);
		params.put("ref", ref);
		
		HttpGet httpGet = new HttpGet(constructUri(params, resourceType));
		
		return client.execute(target, httpGet).getEntity().getContent();
	}
	/*
	 * Get the conversions of a file
	 * TODO: refactor, maybe pass the whole file?
	 */
	public Map<String,String> getConversions(String fileKey) throws ClientProtocolException, IOException {
		Map<String,String> params = new HashMap<String,String>();
		params.put("fileKey", fileKey);
		HttpResponse response = doRequest(params, "GET", "conversions");
		Map<String,String> conversions = null;
		
		if (response.getEntity() != null) {
			ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally
			conversions = mapper.readValue(EntityUtils.toString(response.getEntity()), new TypeReference<Map<String,String>>() { });
		}
		return conversions;
	}
	
	
	/*
	 * Dispatch to specific method types GET/POST/DELETE
	 */
	private HttpResponse doRequest(Map<String,String> params, 
							  String methodType, 
							  String resourceType) 
						throws ClientProtocolException, IOException {
		
		// Execute the request
		HttpResponse response = null;
		if ("GET".equals(methodType)) {
			response = doGet(params, resourceType);
			
		} else if ("POST".equals(methodType)){
			
		}
		return response;
	}
	/*
	 * Execute a GET request
	 */
	private HttpResponse doGet(Map<String,String> params, String resourceType) throws ClientProtocolException, IOException {
		HttpClient client = createClient();
		HttpHost target = new HttpHost(CanorisAPI.getInstance().getBaseURL(), 80 , "http");
		HttpGet httpGet = new HttpGet(constructUri(params, resourceType));
		HttpResponse response = client.execute(target, httpGet);
        
		// release resources
        client.getConnectionManager().shutdown();
		
        return response;
	}
	// TODO: implement
	private HttpResponse doPost(Map<String,String> params, String resourceType) {
		return null;
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
	 * Helper to construct the URI to be used in the http request
	 */
	private String constructUri(Map<String,String> params, String resourceType) {
		String uri = null;
		
		if ("file".equals(resourceType)) {
			uri = Constants.URI_FILE + "/?api_key=" + CanorisAPI.getInstance().getApiKey();
		} else if("files".equals(resourceType)) {
			uri = Constants.URI_FILES + "/?api_key=" + CanorisAPI.getInstance().getApiKey();
		} else if ("conversions".equals(resourceType)) {
			StringTemplate stringTemplate = new StringTemplate(Constants.URI_FILE_CONVERSIONS, DefaultTemplateLexer.class);
			stringTemplate.setAttribute("fileKey", params.get("fileKey"));
			uri = stringTemplate.toString() + "?api_key=4768e6418bda4d81a67656e34df8b89b";
		} else if ("conversion".equals(resourceType)) {
			StringTemplate stringTemplate = new StringTemplate(Constants.URI_FILE_CONVERSION, DefaultTemplateLexer.class);
			stringTemplate.setAttribute("fileKey", params.get("fileKey"));
			stringTemplate.setAttribute("conversion", params.get("ref"));
			uri = stringTemplate.toString() + "?api_key=4768e6418bda4d81a67656e34df8b89b";
		}
		return uri;
	}
	
	/*
	 * Helper method to setup proxy 
	 */
	private HttpClient setupProxy(String proxyAddress, Integer port, String protocol) {
		// Create proxy
		HttpHost proxy = new HttpHost(proxyAddress, port, protocol);
		// Schemes
        SchemeRegistry supportedSchemes = new SchemeRegistry();

        // Register the "http" protocol scheme, they are
        // required by the default operator to look up socket factories.
        supportedSchemes.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		// prepare parameters
        HttpParams params = new BasicHttpParams();
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, "UTF-8");
        HttpProtocolParams.setUseExpectContinue(params, true);
        
        ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, supportedSchemes);

        DefaultHttpClient httpClient = new DefaultHttpClient(ccm, params);

        httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
        
		return httpClient;
		
	}
}
