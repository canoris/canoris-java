package com.canoris;

public class Constants {
	public final static String URI_FILES = "/files";
	public final static String URI_FILE = "/files/$fileKey$";
	
	public final static String URI_FILE_SERVE = "/files/$fileKey$/serve";
	public final static String URI_FILE_ANALYSIS = "/files/$fileKey$/analysis/$filter$";
	
	public final static String URI_FILE_CONVERSIONS = "/files/$fileKey$/conversions";
	public final static String URI_FILE_CONVERSION = "/files/$fileKey$/conversions/$conversion$";
	
	public final static String URI_FILE_VISUALIZATIONS = "/files/$fileKey$/visualizations";
	public final static String URI_FILE_VISUALIZATION = "/files/$fileKey$/visualizations/$visualization$";

	public final static String URI_COLLECTIONS = "/collections";
	public final static String URI_COLLECTION = "/collections/$collectionKey$";
	public final static String URI_COLLECTION_FILES = "/collections/$collectionKey$/files";
	public final static String URI_COLLECTION_FILE = "/collections/$collectionKey$/files/$fileKey$";
	public final static String URI_COLLECTION_SIMILAR = "/collections/$collectionKey$/similar/$fileKey$/$preset$/$results$";
	
	public final static String URI_TEMPLATES = "/processing/templates";
	public final static String URI_TEMPLATE = "/processing/templates/$templateName$";
	
	public final static String URI_TASKS = "/processing/tasks";
	public final static String URI_TASK = "/processing/tasks/$taskId$";
	
	public final static String URI_PHONEMES = "/language/text2phonemes";
}
