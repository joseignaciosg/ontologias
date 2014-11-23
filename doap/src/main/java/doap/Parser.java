package doap;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.URL;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class Parser {

	static String[] users = { "ConcoMB", "mannias", "daniel-lobo",
			"akarpovsky", "Dinuuu", "farolfo", "epintos", "FedericoHomovc",
			"mdesanti", "acrespo", "fnmartinez", "gcastigl", "msturla",
			"nloreti", "eordano", "kshmir", "maximovs", "ealtamir" };


	private static String readUrl(String user) throws Exception {

		BufferedReader reader = null;
		try {
			URL url = new URL("https://api.github.com/users/" + user + "/repos");
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
	
			Object obj = JSONValue.parse(readUrl(user));
			JSONArray array = (JSONArray) obj;
	
			System.out.println(array);
			
			br.write("_:" +user+" a foaf:Person ;\n");
			br.write("\t foaf:name \"name\" ;\n");
			br.write("\t foaf:mbox \"mbox\" ;\n");
			br.write("\t foaf:homepage \"https://github.com/"+user+"\" .\n");
			br.write("\n");
		    
	
			for (int n = 0; n < array.size(); n++) {
				JSONObject object = (JSONObject) array.get(n);
				System.out.println(object);
				System.out.println("####NAME :" + object.get("name"));
				
				br.write("doap:Project\n");
				br.write("\t doap:name '" + object.get("name") + "' ;\n");
				br.write("\t doap:homepage \"https://github.com/" + user +"/"+ object.get("name") + "\" ;\n");
				br.write("\t doap:shortdesc '" + object.get("description") + "' ;\n");
				br.write("\t doap:maintainer '" + "_:" + user + "' ;\n");
				br.write("\t doap:repository _:repo .\n");
				br.write("\n");
				
				
			}
		}
		br.close();
		fr.close();

	}

}