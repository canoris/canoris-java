package test;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

public class GeneralTestingArea {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public static void main(String[] args) throws ClientProtocolException, IOException {
		String content = "{" +
			    "\"ref\": \"http://api.canoris.com/processing/templates/vocaloid\"," +
			    "\"name\": \"vocaloid\"," +
			    "\"template\": [" +
			    				"{" +
			    					"\"operation\": \"vocaloid\"," + 
			    					"\"parameters\": {" +
			    						"\"voice\": \"arnau\"," + 
			    						"\"sequence\": \"{{ my_xml }}\"" +
			    					"}" +
			    				"}" +
			    			  "]" +
			    			"}";
		
		
		ObjectMapper m = new ObjectMapper();
		// can either use mapper.readTree(JsonParser), or bind to JsonNode
		JsonNode rootNode = m.readValue(content, JsonNode.class);
		// ensure that "last name" isn't "Xmler"; if is, change to "Jsoner"
		JsonNode templateNode = rootNode.path("template");
		Object test = templateNode.path("parameters");
		ObjectNode t2 =(ObjectNode) templateNode.get(0);
		Object t3 = t2.path("parameters").path("voice");
		/*
		HttpHost proxy = new HttpHost("proxy.upf.edu", 80, "http");
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
        
        HttpHost target = new HttpHost("api.canoris.com", 80, "http");
        HttpGet get = new HttpGet("/files/?api_key=4768e6418bda4d81a67656e34df8b89b");
        
        HttpResponse res = httpClient.execute(target, get);
		String resString = EntityUtils.toString(res.getEntity());
		*/
	}

}
