package com.canoris.api.resources;

import java.util.Map;

public class CanorisFile extends CanorisResource {
	
	private final static String CONVERSIONS = "conversions";
	private final static String VISUALIZATIONS = "visualizations";
	private final static String ANALYSIS = "analysis";

	private Map<String, Object> properties = null;
	
	/**
	 * Empty default constructor
	 */
	public CanorisFile() {
		super();
	}
	
	/**
	 * Constructor that checks for "http" at the start of the constructors parameter 
	 * and if found sets the ref of the file. If not it considers it is the key of
	 * the file and sets that.
	 * @param property
	 */
	public CanorisFile(String property) {
		if (property.startsWith("http")) {
			super.setRef(property);
		} else {
			super.setKey(property);
		}
	}
	
	public Map<String, Object> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, Object> properties) {
		this.properties = properties;
	}
	
	/*
	 * Helper methods to get from the properties map the given property
	 */
	public String getConversions() {	
		return (String) properties.get(CONVERSIONS);
	}
	
	public String getVisualizations() {
		return (String) properties.get(VISUALIZATIONS);
	}
	
	public String getAnalysis() {
		return (String) properties.get(ANALYSIS);
	}
	
}
