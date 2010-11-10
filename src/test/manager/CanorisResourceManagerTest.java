package test.manager;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.canoris.CanorisAPI;
import org.canoris.resource.types.CanFile;
import org.canoris.service.CanorisResourceManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CanorisResourceManagerTest {
	
	@Before
	public void setup() {
		CanorisAPI api = CanorisAPI.getInstance();
		// api.setBaseURL("api.canoris.com");
		api.setApiKey("4768e6418bda4d81a67656e34df8b89b");
	}
	
	@Test
	public void testCreateFile() {
		CanorisResourceManager manager = new CanorisResourceManager();
		manager.useProxy(true);
		
		try {
			// FIXME: casting is shit! avoid it somehow
			CanFile file = (CanFile) manager.createFile("/home/stelios/Downloads/Catalan_Smoke_Signals.mp3", null);
			// Assertions
			Assert.assertNotNull(file);
			Assert.assertNotNull(file.getProperties());
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
	
	@Test
	public void testGetFiles() {
		CanorisResourceManager manager = new CanorisResourceManager();
		manager.useProxy(true);
		
		try {
			List<CanFile> files = manager.getFiles();
			Assert.assertNotNull(files);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
