package com.canoris.api.resources;

import java.util.List;
import java.util.Map;

/**
 * Class to simply implement the paging logic of the CanorisAPI collections
 * 
 * The pager class is used for all resources that return paged results. 
 * Depending on the type of the resource the respective class members will be 
 * null or not. For example when we request files the total_files field will
 * contain a list with all the files returned. If there are more pages the 
 * nextPage will contain the link to be used for retrieving the next page.
 * In case one of the fields is not applicable to the current request it 
 * will simply be null.
 * 
 * @author stelios
 *
 */
/*
 * This class is actually a hack. The logic is this.
 * Since the different types of collections actually return different content we cannot use
 * a generic collection object for all. So what happens now is:
 * FILES RESOURCE
 * 	It will only fill up the files field
 * TEMPLATES RESOURCE
 * 	It will only fill up the templates 
 * 
 * The rest of the fields are common
 * 
 * OPTIONS
 * 1)
 * Custom deserializer so this class would have a contents field 
 * The deserializer should read the files or templates property and set them to the contents field.
 * NOTE that the files is recognized as JSON_ARRAY while the templates as JSON_OBJECT
 * 
 * 2) 
 * Maybe Jackson can understand the following:
 * Having a superclass with the total_files/ref/next/before and subclasses with files/templates etc.
 * Will it use the right subclass? check Jackson Polymorphic Type Handling
 * 
 * 3)
 * Create a custom mapper. Map response to Map<String,Object> and use the customMapper
 * to check keys and maps accordingly
 * 
 */
public class Pager {
	// TODO: check annotation for these type of field names
	private String total_files;
	private String total_collections;
	private String ref;
	private String next;
	private String previous;
	
	private List files;
	private Map templates;
	private List collections;

	public List getFiles() {
		return files;
	}
	public void setFiles(List files) {
		this.files = files;
	}		
	public Map getTemplates() {
		return templates;
	}
	public void setTemplates(Map templates) {
		this.templates = templates;
	}
	public List getCollections() {
		return collections;
	}
	public void setCollections(List collections) {
		this.collections = collections;
	}
	
	//----------- GETTERS && SETTERS
	public String getTotal_files() {
		return total_files;
	}
	public void setTotal_files(String total_files) {
		this.total_files = total_files;
	}
	public String getTotal_collections() {
		return total_collections;
	}
	public void setTotal_collections(String totalCollections) {
		total_collections = totalCollections;
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
	public String getPrevious() {
		return previous;
	}
	public void setPrevious(String previous) {
		this.previous = previous;
	}
}
