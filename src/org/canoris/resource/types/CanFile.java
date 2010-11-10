package org.canoris.resource.types;

import java.util.Map;

public class CanFile extends CanorisResource {
	
	private final static String CONVERSIONS = "conversions";
	private final static String VISUALIZATIONS = "visualizations";
	private final static String ANALYSIS = "analysis";

	private Map<String, Object> properties = null;
	
	public Map<String, Object> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, Object> properties) {
		this.properties = properties;
		// this.fileKey = (String) properties.get("key");
	}
	
	
	/*
	 * Helper methods to get from the properties map the given property
	 */
	@SuppressWarnings("unchecked")
	public Map<String, String> getConversions() {
		
		return (Map<String, String>) properties.get(CONVERSIONS);
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, String> getVisualizations() {
		
		return (Map<String, String>) properties.get(VISUALIZATIONS);
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, String> getAnalysis() {
		
		return (Map<String, String>) properties.get(ANALYSIS);
	}
	
}
