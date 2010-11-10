package org.canoris;

public class ResponseConverter {
	// TODO: check if we need to use some more strict singleton technique
	private static ResponseConverter instance = null;
		protected ResponseConverter() {
	}
		
	public static ResponseConverter getInstance() {
		if(instance == null) {
			instance = new ResponseConverter();
	    }
	    return instance;
	}
	
	
	private String contents;
	
	
	
	
	
	public String getContents() {
		return contents;
	}

	public void setContents(String contents) {
		this.contents = contents;
	}
	
}
