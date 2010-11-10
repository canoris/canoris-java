package test.manager;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.canoris.CanorisAPI;
import org.canoris.CanorisConManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CanorisRequestTest {
	
	@Before
	public void setup() {
		CanorisAPI api = CanorisAPI.getInstance();
		// api.setBaseURL("api.canoris.com");
		api.setApiKey("4768e6418bda4d81a67656e34df8b89b");
	}
	
	@Test
	public void testUploadFile() {
		CanorisConManager req = CanorisConManager.getInstance();
		req.setUseProxy(true);
		try {
			req.uploadFile("/home/stelios/Downloads/Catalan_Smoke_Signals.mp3");
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/*
	@Test
	public void testGetFileResource() {
		CanorisRequest req = new CanorisRequest();
		req.setUseProxy(true);
		try {
			req.getFileResource(null, null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	*/
	
	@Test
	public void testGetConversions() {
		CanorisConManager req = CanorisConManager.getInstance();
		req.setUseProxy(true);
		try {
			Map<String,String> conv = req.getConversions("badaabbe90a542639b10e047138e18e4");
			
			Assert.assertNotNull(conv);
			Assert.assertTrue(!"".equals(conv));
			
			System.out.println("\n\n -------------------------------------- \n" + conv);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test 
	public void getConversion() {
		CanorisConManager req = CanorisConManager.getInstance();
		req.setUseProxy(true);
		
		try {
			InputStream inStream = req.getFile("badaabbe90a542639b10e047138e18e4", "mp3_128kb", "conversion");
			Assert.assertNotNull(inStream);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
