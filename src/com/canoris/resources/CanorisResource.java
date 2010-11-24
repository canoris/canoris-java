package com.canoris.resources;

public class CanorisResource {

	/**
	 * The reference of the resource. You can use it to the refer to the file on
	 * the server.
	 */
	private String ref = "";
	/**
	 * The key can be used refer to the file when forming a URI. It is used in
	 * various different parts of the API.
	 */
	private String key = "";

	public CanorisResource() {
		super();
	}

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
