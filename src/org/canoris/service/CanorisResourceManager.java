package org.canoris.service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.canoris.resource.types.CanFile;
import org.canoris.resource.types.CanorisResource;

/*
 * This essentially is just a facade to the CanorisConManager.
 */
public class CanorisResourceManager {
	
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
	
	public List<CanorisResource> getFiles() throws ClientProtocolException, IOException {
		return CanorisConManager.getInstance().getFiles();
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
