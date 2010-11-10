package test;

import java.io.IOException;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;

public class GeneralTestingArea {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public static void main(String[] args) throws ClientProtocolException, IOException {
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

	}

}
