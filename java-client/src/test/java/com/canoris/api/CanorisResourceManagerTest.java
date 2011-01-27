package com.canoris.api;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.Map;

import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.canoris.api.CanorisAPI;
import com.canoris.api.CanorisResourceManager;
import com.canoris.api.exception.CanorisException;
import com.canoris.api.resources.CanorisFile;
import com.canoris.api.resources.Pager;

/*
 * TODO: 1) Add asserts to make test cases robust
 *             PARTIALY DONE
 *       2) Make tests completely independent
 *             check the method comment on this one ---> getSimilaritySearch
 *       3) Check manualy for errors      
 */
public class CanorisResourceManagerTest {

    private CanorisResourceManager manager = null;
    // N.B. change these to run the tests yourself
    private String taskId = "c334b763f4ee476b995ed67cfaf67dda"; // Use an existing taskId
    private String testFile = "/home/stelios/Downloads/Catalan_Smoke_Signals.mp3";
    private String apiKey = "b35645fbadfc468ba25ed56a20049360";
    private String fileKey = "4d6f3db666f949ed9341e4f2b91b29fe";
    
    @Before
    public void setup() {
        CanorisAPI api = CanorisAPI.getInstance();
        api.setBaseURL("api-test.canoris.com");
        api.setApiKey(this.apiKey);

        this.manager = new CanorisResourceManager();
        manager.useProxy(true);
        manager.configProxy("proxy.upf.edu", 8080, "http");
    }
    
    @Test
    public void testCreateFile() {
        CanorisResourceManager manager = new CanorisResourceManager();
        manager.useProxy(true);

        try {
            // FIXME: casting is shit! avoid it somehow
            CanorisFile file = (CanorisFile) manager.createFile(this.testFile, null);
            // Assertions
            Assert.assertNotNull(file);
            Assert.assertNotNull(file.getProperties());
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
    @Test
    public void testCreateFileFromURL() {
    	try {
			CanorisFile file = (CanorisFile) manager.createFileFromURL("http://student-kmt.hku.nl/~ciska/songs/Echo_Motif.mp3", null);
			Assert.assertNotNull(file);
			Assert.assertNotNull(file.getAnalysis());
			// TODO: add asserts
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (CanorisException e) {
			e.printStackTrace();
		}
    }
    
    /*
     * OLD AND NEW PAGING STYLE
     * Get a pager, if it contains more than one page
     * get next page and then previous page
     */
    @Test
    public void testGetFiles() {
        try {
        	// OLD STYLE
            Pager pager = manager.getFiles();
            Assert.assertNotNull(pager);
            // The files is an array....
            Assert.assertNotNull(pager.getItems().get(0));
            if (pager.getNext() != null) {
                pager = manager.getNextPage(pager);
                Assert.assertNotNull(pager);
                Assert.assertNotNull(pager.getPrevious());
                pager = manager.getPreviousPage(pager);
                Assert.assertNotNull(pager);
            }
            // NEW STYLE
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (CanorisException e) {
            e.printStackTrace();
        }
    }
    
    @Test
    public void testGetPage() {
    	try {
    		Pager pager = new Pager();
    		
    		//------------ OLD STYLE
    		pager.setPageNumber("0");
    		pager = manager.getPage(pager);
    		Assert.assertNotNull(pager);
    		Assert.assertNotNull(pager.getTotal());
    		
    		//------------ NEW STYLE
    		// test getPage(Pager pager)
    		pager.setStart("0");
    		pager.setLimit("2");
			pager = manager.getPage(pager);
			Assert.assertNotNull(pager);
    		Assert.assertNotNull(pager.getTotal());
			
    		// test getPage(String start, String limit)
			pager = null;
			pager = manager.getPage("0", "2");
			Assert.assertNotNull(pager);
    		Assert.assertNotNull(pager.getTotal());
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (CanorisException e) {
			e.printStackTrace();
		}
    }

    @Test
    public void testGetFile() {
        CanorisFile file = new CanorisFile(this.fileKey);
        try {
            // File parameter
            CanorisFile response1 = manager.getFile(file);
            Assert.assertNotNull(response1);
            Double fs = (Double) response1.getProperties().get("samplerate");
            Assert.assertTrue(fs == 44100.0);
            // String parameter
            CanorisFile response2 = manager.getFile(this.fileKey);

            Assert.assertEquals(response1.getKey(), response2.getKey());
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (CanorisException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testDownloadFile() {
        CanorisFile file = new CanorisFile(this.fileKey);
        InputStream in1 = null;
        InputStream in2 = null;
        try {
            // File parameter
            in1 = manager.downloadFile(file);
            Assert.assertNotNull(in1);
            // String parameter
            in2 = manager.downloadFile(file);
            Assert.assertNotNull(in2);
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (CanorisException e) {
            e.printStackTrace();
        } finally {
            try {
                in1.close();
                in2.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testGetConversion() {
        CanorisFile file = new CanorisFile(this.fileKey);
        try {
            InputStream in = manager.getConversion(file, "mp3_128kb");
            Assert.assertNotNull(in);

            // Write file, you have to open it manually to check...
            OutputStream out = new FileOutputStream(new File(this.testFile));
            int read=0;
            byte[] bytes = new byte[1024];
            while((read = in.read(bytes))!= -1){
                out.write(bytes, 0, read);
            }
            in.close();
            out.flush();
            out.close();

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (CanorisException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetConversions() {
        CanorisFile file = new CanorisFile(this.fileKey);
        try {
            Map<String,Object> convs = manager.getConversions(file);
            Assert.assertNotNull(convs);
            Assert.assertTrue(convs.size() == 4);
            Assert.assertNotNull(convs.get("mp3_64kb"));

            System.out.println(convs.toString());
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            manager.useProxy(true);
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (CanorisException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetAnalysis() {
        CanorisFile file = new CanorisFile(this.fileKey);
        try {
            Map<String,Object> analysis = manager.getAnalysis(file, null);
            Assert.assertNotNull(analysis);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (CanorisException e) {
            e.printStackTrace();
        }
    }
    
    @Test
    public void testGetAnalysisFrames() {
    	try {
			JsonNode node = manager.getAnalysisFrames(this.fileKey);
			Assert.assertNotNull(node);
			// TODO: add asserts
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (CanorisException e) {
			e.printStackTrace();
		}
    }

    @Test
    public void testGetVisualizations() {
        CanorisFile file = new CanorisFile(this.fileKey);
        try {
            Map<String,Object> analysis = manager.getVisualizations(file);
            Assert.assertNotNull(analysis);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (CanorisException e) {
            e.printStackTrace();
        }
    }

    // ------------------------ TEMPLATES --------------------------------
    @Test
    public void testGetTemplates() {
        try {
            Map<String,Object> templates = manager.getTemplates();
            Assert.assertNotNull(templates);
            System.out.println(templates.toString());
            // TODO: how to test this well???
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (CanorisException e) {
            e.printStackTrace();
        }
    }
    // Will if template name exists
    @Test
    public void testCreateTemplate() {
        String templateName = "test5";
        String templateContent = "[{\"operation\": \"vocaloid\", \"parameters\": " +
                                    "{\"voice\": \"arnau\", \"sequence\": \"{{ my_test3 }}\"}}]";
        try {
            Map<String, Object> resp = manager.createTemplate(templateName, templateContent);
            Assert.assertNotNull(resp);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (CanorisException e) {
            e.printStackTrace();
        }
    }
    /*
     * We can test this properly only after doing the query language manager.
     * Now we can't get the data easily.
     */
    @Test
    public void testUpdateTemplate() {
        String template = "[{\"operation\": \"vocaloid\", \"parameters\": {\"input\": \"{{ test3_GO }}\"}}]";
        try {
            JsonNode resp = manager.updateTemplate(template, "test5");
            Assert.assertNotNull(resp);
            Assert.assertEquals("\"{{ test3_GO }}\"", resp.findValue("input").toString());
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (CanorisException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testDeleteTemplate() {
        try {
            manager.deleteTemplate("test5");
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (CanorisException e) {
            e.printStackTrace();
        }
    }

    // ------------------------ TASKS --------------------------------
    @Test
    public void testCreateTask() {
        String taskContent = "{\"my_test1\": \"<melody ticklength='0.01'> "
                               + "<note duration='45' pitch='58' velocity='110' phonemes='m a s'/> "
                               + "</melody>\"}";

        try {
            Map<String,Object> response = manager.createTask("template", taskContent);
            Assert.assertNotNull(response);
            taskId = (String) response.get("task_id");
            Assert.assertTrue(!"".equals(taskId));
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (CanorisException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void testGetTask() {
        try {
            Map<String, Object> response = manager.getTask(taskId);
            Assert.assertNotNull(response);
            // FIXME: this test will fail silently
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (CanorisException e) {
            e.printStackTrace();
        }
    }

    // ------------------------ LANGUAGE --------------------------------
    @Test
    public void testText2Phon() {
        String text = "He's%20a%20bad%20man,%20ooooo,%20cruel%20Stagolee";
        String language = "english";
        try {
            Map<String, Object> response = manager.getText2Phon(text, language);
            Assert.assertNotNull(response);
            Assert.assertEquals(language, response.get("language"));
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (CanorisException e) {
            e.printStackTrace();
        }
    }

    // ------------------------ COLLECTION --------------------------------
    @Test
    public void testGetCollections() {
        try {
            Pager pager = manager.getCollections();
            Assert.assertNotNull(pager);
            Assert.assertNotNull(pager.getTotal());
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (CanorisException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void testGetCollection() {
        try {
            Map<String,Object> response = manager.getCollection("4dac2ae8229a4e6982ea01fde18c9f9d");
            Assert.assertNotNull(response);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (CanorisException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void testCreateAndDeleteCollection() {
        try {
            Map<String, Object> response = manager.createCollection("testCollection1", "CC_AT_NC", "1");
            String createdCollectionKey = (String) response.get("key");
            manager.deleteCollection(createdCollectionKey);
            Assert.assertNotNull(response);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (CanorisException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetCollectionFiles() {
        try {
            Pager pager = manager.getCollectionFiles("4dac2ae8229a4e6982ea01fde18c9f9d");
            Assert.assertNotNull(pager);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (CanorisException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void testGetCollectionFile() {
        CanorisFile file = new CanorisFile();
        file.setKey("b03abca7553d4b0d958015219c522914");
        String collectionKey = "4dac2ae8229a4e6982ea01fde18c9f9d";
        try {
            Map<String,Object> response = manager.getCollectionFile(collectionKey,file);
            Assert.assertNotNull(response);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (CanorisException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void testAddFileToCollection() {
        CanorisFile file = new CanorisFile("b03abca7553d4b0d958015219c522914");
        String collectionKey = "4dac2ae8229a4e6982ea01fde18c9f9d";
        try {
            manager.addFileToCollection(collectionKey, file);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (CanorisException e) {
            e.printStackTrace();
        }
    }
    /*
     * This will fail cause we don't have enough files to collection
     * to do a similarity search.
     * TODO: Add files so it can be tested properly
     */
    @Test
    public void testGetSimilaritySearch() {
        CanorisFile file = new CanorisFile("b03abca7553d4b0d958015219c522914");
        String collectionKey = "4dac2ae8229a4e6982ea01fde18c9f9d";
        String preset = "music";
        String results = "5";

        try {
            manager.getSimilaritySearch(collectionKey, file, preset, results);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            // Jackson parse exception is caught here...
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (CanorisException e) {
            e.printStackTrace();
        }

    }
}
