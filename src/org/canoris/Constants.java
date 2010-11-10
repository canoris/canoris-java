package org.canoris;

public class Constants {
	public final static String URI_FILES = "/files";
	public final static String URI_FILE = "/files/{0}";
	public final static String URI_FILE_SERVE = "/files/{0}/serve";
	public final static String URI_FILE_ANALYSIS = "/files/{0}/analysis/{filter>";
	public final static String URI_FILE_CONVERSIONS = "/files/$fileKey$/conversions";
	public final static String URI_FILE_CONVERSION = "/files/$fileKey$/conversions/$conversion$";
	public final static String URI_FILE_VISUALIZATIONS = "/files/{0}/visualizations";
	public final static String URI_FILE_VISUALIZATION = "/files/{0}/visualizations/{visualization>";
	public final static String URI_COLLECTIONS = "/collections";
	public final static String URI_COLLECTION = "/collections/{0}";
	public final static String URI_COLLECTION_FILES = "/collections/{0}/files";
	public final static String URI_COLLECTION_FILE = "/collections/{0}/files/{file_key>";
	// private final static String URI_COLLECTION_SIMILAR = "/collections/{coll_key>/similar/{file_key>/{preset>/{results>";
	public final static String URI_COLLECTION_SIMILAR = "/collections/{0}/similar/{1}/{2}/{3}";
	public final static String URI_TEMPLATES = "/processing/templates";
	public final static String URI_TEMPLATE = "/processing/templates/{0}";
	public final static String URI_TASKS = "/processing/tasks";
	public final static String URI_TASK = "/processing/tasks/{0}";
	public final static String URI_PHONEMES = "/language/text2phonemes";
}
