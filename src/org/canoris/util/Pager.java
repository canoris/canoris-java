package org.canoris.util;

import java.util.Map;

/**
 * Class to simply implement the paging logic of the CanorisAPI collections
 * 
 * @author stelios
 *
 */
public class Pager {
	private String total_files;
	private String ref;
	private String next;
	private String before;
	
	// All the contects are here, different resource types will differ in this
	// hence make it as generic as possible by using a map
	private Object files;
	
	//----------- GETTERS && SETTERS
	public String getTotal_files() {
		return total_files;
	}
	public void setTotal_files(String total_files) {
		this.total_files = total_files;
	}
	public String getRef() {
		return ref;
	}
	public void setRef(String ref) {
		this.ref = ref;
	}
	public String getNext() {
		return next;
	}
	public void setNext(String next) {
		this.next = next;
	}
	public String getBefore() {
		return before;
	}
	public void setBefore(String before) {
		this.before = before;
	}
	public Object getFiles() {
		return files;
	}
	public void setFiles(Object files) {
		this.files = files;
	}
	
	
}
