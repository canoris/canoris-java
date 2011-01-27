package com.canoris.api.resources;

import java.util.List;
import java.util.Map;

/**
 * Class to simply implement the paging logic of the CanorisAPI collections
 * 
 * The pager class is used for all resources that return paged results. 
 * If there are more pages the nextPage will contain the link to be used 
 * for retrieving the next page. In case one of the fields is not applicable 
 * to the current request it will simply be null.
 * 
 * In addition the pager can be used for "old" and "new" style of paging.
 * Old has only the pageNumber field
 * New has start (equal to pageNumber) and limit (page size)
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

	private String pageNumber;
	private String total;
	private String ref;
	private String next;
	private String previous;
	private String start;
	private String limit;
	
	private List items;
	
	/**
	 * Empty constructor
	 */
	public Pager() {
	}
	/**
	 * Constructor
	 * 
	 * @param pageNumber
	 */
	public Pager(String pageNumber) {
		this.pageNumber = pageNumber;
	}
	/**
	 * Constructor
	 * 
	 * @param start
	 * @param limit
	 */
	public Pager(String start, String limit) {
		this.start = start;
		this.limit = limit;
	}
	
	//----------- GETTERS && SETTERS
	public List getItems() {
		return items;
	}
	public void setItems(List items) {
		this.items = items;
	}
	public String getTotal() {
		return total;
	}
	public String getPageNumber() {
		return pageNumber;
	}
	public void setPageNumber(String pageNumber) {
		this.pageNumber = pageNumber;
	}
	public void setTotal(String total) {
		this.total = total;
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
	public String getStart() {
		return start;
	}
	public void setStart(String start) {
		this.start = start;
	}
	public String getLimit() {
		return limit;
	}
	public void setLimit(String limit) {
		this.limit = limit;
	}
	
}
