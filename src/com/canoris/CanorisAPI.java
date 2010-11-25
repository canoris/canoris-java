package com.canoris;

/**
 * CanorisAPI class is used to simply store the api_key 
 * and baseURL which are used in all requests.
 * 
 * @author stelios
 */
public class CanorisAPI {
	
	// base URL
	// Can be set for testing purposes
	private String baseURL = "api.canoris.com";
	
	// The api_key used in all requests
	private String apiKey = "";
	
	private static CanorisAPI instance = null;
		protected CanorisAPI() {
	}
		
	public static CanorisAPI getInstance() {
		if(instance == null) {
			instance = new CanorisAPI();
	    }
	    return instance;
	}
	
	/* ************* GETTERS && SETTERS ************** */
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
