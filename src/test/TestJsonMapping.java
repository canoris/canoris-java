package test;

import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import com.canoris.resources.CanorisFile;

public class TestJsonMapping {
	
	public final static void main(String[] args) throws Exception {
		
		String testJsonObj = "{" +
			    "\"conversions\": \"http://api.canoris.com/files/70a0620b062b4ba682cff39cf1001f41/conversions\"," +
			    "\"ref\": \"http://api.canoris.com/files/70a0620b062b4ba682cff39cf1001f41\"," +
			    "\"temporary\": false," +
			    "\"name\": \"test\"," +
			    "\"extension\": \".wav\"," +
			    "\"date_modified\": 1283371648.0," +
			    "\"bitrate\": 1378.1114335598338," +
			    "\"serve\": \"http://api.canoris.com/files/70a0620b062b4ba682cff39cf1001f41/serve\"," +
			    "\"analysis\": \"http://api.canoris.com/files/70a0620b062b4ba682cff39cf1001f41/analysis\"," +
			    "\"collections\": [{\"ref\" : \"some\", \"key\" : \"thing\"}]," +
			    "\"channels\": 2," +
			    "\"mime\": \"audio/x-wav; charset=binary\"," +
			    "\"key\": \"70a0620b062b4ba682cff39cf1001f41\"," +
			    "\"visualizations\": \"http://api.canoris.com/files/70a0620b062b4ba682cff39cf1001f41/visualizations\"," +
			    "\"duration\": 10.596," +
			    "\"date_added\": 1283371648.0," +
			    "\"samplerate\": 44100," +
			    "\"bits\": 16" +
			    "}";
				
				// Use Jackson Gson could not to map within map
				ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally
				Map<String,Object> t = mapper.readValue(testJsonObj, new TypeReference<Map<String,Object>>() { });
				CanorisFile canorisFileResource = new CanorisFile();
				canorisFileResource.setProperties(t);
				System.out.println("Parse result : " + canorisFileResource.getProperties().get("collections"));
	 }
}
