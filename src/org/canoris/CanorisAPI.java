package org.canoris;

public class CanorisAPI {
	
	// Hardcoded base URL
	private final static String baseURL = "http://api.canoris.com";
	
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

	// GETTERS && SETTERS
	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}
	
}
