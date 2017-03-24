
/*
 * Bioconcept RESTful
 * 
 * version: April 30, 2015	09:26 AM
 * Last revision: March 24, 2017 04:06 PM
 * 
 * Author: Chao-Hsuan Ke
 * Email phelps.ke at gmail dot com
 * Institute: Delta Research Center
 * Company : Delta Electronics Inc. Taiwan
 */

/*
 * https://www.ncbi.nlm.nih.gov/CBBresearch/Lu/Demo/tmTools/RESTfulAPIs.html#
 */

/* JAR
 * org.json.jar
 */


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.json.JSONArray;
import org.json.JSONObject;

public class Bioconcept_RESTful 
{
	private String Pubmed_JSON_path;	
	private String pubmed_path_fix = "https://www.ncbi.nlm.nih.gov/CBBresearch/Lu/Demo/RESTful/tmTool.cgi/";
	private String Bioconcept = "";		// Bioconcept: five kinds of bioconcepts, i.e., Gene, Disease, Chemical, Species, Mutation
	private String PMID;
	private String Format = "";			// BioC (xml), and JSON
	
	private JSONObject jsonObject;
	private JSONObject jsonSpan;
		private int begin;
		private int end;
	
	public Bioconcept_RESTful(String pmid, String bioconcept, String format) throws Exception, UnsupportedEncodingException
	{		
		this.PMID = pmid;
		this.Bioconcept = bioconcept;
		this.Format = format;
		
		Pubmed_JSON_path = pubmed_path_fix + Bioconcept + "/" + PMID + "/" + Format;		
		//System.out.println(Pubmed_JSON_path);
		
		URL url  = new URL(String.format(Pubmed_JSON_path,URLEncoder.encode("", "UTF-8")));
	    URLConnection connection = url.openConnection();
		
	    String line;
        StringBuilder builder = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(),"utf-8"));
        while ((line = reader.readLine()) != null) {
        	builder.append(line);
        }
        
        JSONObject json = new JSONObject(builder.toString());                
        JSONArray ja = json.getJSONArray("denotations");        
        
        if(ja.length()>0){        	
        	for(int i=0;i<ja.length();i++){
        		//System.out.println(ja.getJSONObject(i));
        		jsonObject = (JSONObject) ja.getJSONObject(i);
        		jsonSpan = (JSONObject) ja.getJSONObject(i).get("span");
        		begin = Integer.parseInt(jsonSpan.get("begin").toString());
        		end = Integer.parseInt(jsonSpan.get("end").toString());
            	System.out.println(jsonObject.getString("obj")+"	"+begin+"	"+end);        		
        	}
        }
	}

	
	public static void main(String args[])
	{
//		String pmid = args[0];
//		String bioconcept = args[1];
//		String format = args[2];
		
		String pmid = "19894120";
		String bioconcept = "Bioconcept";
		String format = "JSON";
		
		try {
			Bioconcept_RESTful BR = new Bioconcept_RESTful(pmid, bioconcept, format);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
	
}
