package org.canoris;

public class CanorisAPI {
	
	// base URL
	private String baseURL = "api.canoris.com";
	
	// The api_key for canoris
	private String apiKey = "";
	
	// TODO: check if we need to use some more strict singleton technique
	private static CanorisAPI instance = null;
		protected CanorisAPI() {
	}
		
	public static CanorisAPI getInstance() {
		if(instance == null) {
			instance = new CanorisAPI();
	    }
	    return instance;
	}

	
	/************** GETTERS && SETTERS ***************/
	public String getApiKey() {
		return apiKey;
	}

	public String getBaseURL() {
		return baseURL;
	}

	public void setBaseURL(String baseURL) {
		this.baseURL = baseURL;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}
	
}
