package org.canoris.util;

import java.util.Map;

/**
 * Class to simply implement the paging logic of the CanorisAPI collections
 * 
 * @author stelios
 *
 */
public class Pager {
	private int totalFiles;
	private String ref;
	private String next;
	private String before;
	
	// All the contects are here, different resource types will differ in this
	// hence make it as generic as possible by using a map
	private Map<String,Object> contents;
	
	//----------- GETTERS && SETTERS
	public int getTotalFiles() {
		return totalFiles;
	}
	public void setTotalFiles(int totalFiles) {
		this.totalFiles = totalFiles;
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
	public Map<String, Object> getContents() {
		return contents;
	}
	public void setContents(Map<String, Object> contents) {
		this.contents = contents;
	}
	
}
