package org.canoris.resource.types;

public class CanorisResource {
	/**
	 * The reference of the resource. You can use it to the refer to the file
	 * on the server. 
	 */
	private String ref = "";
	// FIXME: what is this?
	private String key = "";
	
	
	public String getRef() {
		return ref;
	}
	public void setRef(String ref) {
		this.ref = ref;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
}
