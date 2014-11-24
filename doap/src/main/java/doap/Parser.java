package doap;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class Parser {

	static String[] users = { 
//			"ConcoMB",
//			"mannias", 
//			"daniel-lobo",
//			"akarpovsky", 
//			"Dinuuu", 
			"farolfo",
//			"epintos", 
//			"FedericoHomovc", 
//			"mdesanti",
//			"acrespo", 
//			"fnmartinez", 
//			"gcastigl", 
//			"msturla", "nloreti", "eordano",
//			"kshmir", "maximovs", "ealtamir", 
//			"joseignaciosg"
			};


	private static String readUrl(String s) throws Exception {

		BufferedReader reader = null;
		try {
			URL url = new URL(s);
			reader = new BufferedReader(new InputStreamReader(url.openStream()));
			StringBuffer buffer = new StringBuffer();
			int read;
			char[] chars = new char[1024];
			while ((read = reader.read(chars)) != -1)
				buffer.append(chars, 0, read);

			return buffer.toString();
		} finally {
			if (reader != null)
				reader.close();
		}
	}

	public static void main(String[] args) throws Exception {

		File file = new File("projects.ttl");
		FileWriter fr = new FileWriter(file);
		BufferedWriter br = new BufferedWriter(fr);

			
		br.write("@prefix doap: <http://usefulinc.com/ns/doap#> .\n");
		br.write("@prefix foaf: <http://xmlns.com/foaf/0.1/> .\n");
		br.write("\n\n");
		
		for (String user : users) {
	
			Object obj = JSONValue.parse(readUrl("https://api.github.com/users/" + user + "/repos"));
			JSONArray array = (JSONArray) obj;
	
			System.out.println(array);
			
			Object obj2 = JSONValue.parse(readUrl("https://api.github.com/users/" + user));
			JSONObject userObj  = (JSONObject) obj2;
			
						
			br.write("_:" +user+" a foaf:Person ;\n");
			br.write("\t foaf:name \""+userObj.get("name")+"\" ;\n");
			br.write("\t foaf:mbox \""+userObj.get("email")+"\" ;\n");
			br.write("\t foaf:homepage \"https://github.com/"+user+"\" .\n");
			br.write("\n");
		    
	
			for (int n = 0; n < array.size(); n++) {
				JSONObject object = (JSONObject) array.get(n);
				System.out.println(object);
				System.out.println("####NAME :" + object.get("name"));
				
				br.write("doap:Project\n");
				br.write("\t doap:name \"" + object.get("name") + "\" ;\n");
				br.write("\t doap:homepage \"https://github.com/" + user +"/"+ object.get("name") + "\" ;\n");
				br.write("\t doap:description \"" + object.get("description") + "\" ;\n");
				br.write("\t doap:maintainer " + "_:" + user + " ;\n");
				//DEVELOPERS
				String developers =  "";
				if (object.get("contributors_url") != null){
					System.out.println(object.get("contributors_url"));
					Object obj3 = JSONValue.parse(readUrl(object.get("contributors_url").toString()));
					JSONArray contributorArray = (JSONArray) obj3;
					if (contributorArray != null  && contributorArray.size() > 1){
						for (int i = 0; i < contributorArray.size(); i++) {
							JSONObject dev = (JSONObject) contributorArray.get(i);
							developers += "_:"+ dev.get("login");
							if (i < contributorArray.size()-1){
								developers += ", ";	
							}
						}
					}else{
						developers += "_:"  +user;
					}
				}else{
					developers += "_:"  +user;
				}
				br.write("\t doap:developer " + developers + " ;\n");
				//DEVELOPERS END 
				//LANGUAGES
				String langs =  "";
				if (object.get("languages_url") != null){
					System.out.println(object.get("languages_url"));
					Object obj3 = JSONValue.parse(readUrl(object.get("languages_url").toString()));
					JSONObject langsArray = (JSONObject) obj3;
					Collection<String> langcol = langsArray.keySet();
					Iterator<String> it =  langcol.iterator();;
					if (langcol != null  && langcol.size() > 1){
						int i=0;
						while ( it.hasNext() ) {
							langs += "<http://dbpedia.org/resource/"+it.next() +">";
							if (i < langcol.size()-1){
								langs += ", ";	
							}
							i++;
						}
						br.write("\t doap:programming-language  " + langs + " ;\n");
					}
				}
				//DEVELOPERS END 
				br.write("\t doap:repository _:repo .\n");
				br.write("\n");
			}
		}
		
		br.write("_:repo a doap:GitRepository .\n");
		
		br.close();
		fr.close();

	}

}