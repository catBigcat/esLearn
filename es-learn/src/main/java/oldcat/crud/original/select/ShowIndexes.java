package oldcat.crud.original.select;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;


public class ShowIndexes {
	String host;
	int port;
	
	public ShowIndexes(String host, int port) {
		super();
		this.host = host;
		this.port = port;
		this.reLoad();
	}
	
	List<Map<String,String>> everyIndex ;
	
	public void reLoad() {
		RestTemplate rest = new RestTemplate();
		String url = "http://"+host+":"+port+
				"/"+"/_cat/indices?v&pretty";
		ResponseEntity<String> response = rest.getForEntity(url, String.class);
		String[] likeCsv =  response.getBody().replaceAll(" +", " ") .split("\n");
		String[] head = likeCsv[0].split(" ");
		everyIndex = new ArrayList<Map<String,String>>();
		for(int i =1;i<likeCsv.length;i++) {
			String[] index = likeCsv[i].split(" ");
			HashMap<String,String> indexMap = new HashMap<String,String>();
			for(int j = 0;j<index.length;j++) {
				indexMap.put(head[j],index[j]);
			}
			everyIndex.add(indexMap);
		}		
	}



	public List<String> showIndexes(){
		List<String> result = new ArrayList<String>();
		everyIndex.forEach(   x -> result.add( x.get("index")));
		return result;
	}
	
	
	
	
	public static void main(String...strings) {
		System.out.print( new ShowIndexes("localhost",9200).showIndexes());
	}
	
	
	
}	
