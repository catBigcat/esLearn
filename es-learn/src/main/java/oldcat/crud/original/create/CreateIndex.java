package oldcat.crud.original.create;

import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSONObject;

public class CreateIndex {
	private String host;
	private int port;
	private String indexName;
	public CreateIndex(String host, int port,String indexName) {
		super();
		this.host = host;
		this.port = port;
		this.indexName = indexName;
	}
	
	
	
	
	public Map<String,Object> readMappingFromElastic(){
		RestTemplate rest = new RestTemplate();
		String url = "http://"+host+":"+port+
				"/"+this.indexName+"/_mapping";
		
		ResponseEntity<String> response = rest.getForEntity(url, String.class);
		String body = response.getBody();
		JSONObject setting =  (JSONObject) ((JSONObject)JSONObject.parse(body)).get(this.indexName);
		return setting;
	}
	
	
	/**
	 * 应该放入一个index的配置，但是这里没有做太多的检查
	 * @param builder
	 */
	public void put(SettingBuilder builder) {
		RestTemplate rest = new RestTemplate();
		String url = "http://"+host+":"+port+"/"+indexName;
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> request = new HttpEntity<String>(builder.toString(),requestHeaders);
		rest.put( url ,request);
		
	}
	
	
	
	 
	
	public static void main(String...strings) {
		CreateIndex index = new CreateIndex("localhost",9200,"hello_word1");
		SettingBuilder builder = new SettingBuilder();
		builder.setAnalyser("author","text","standard", null, null);
		builder.setAnalyser("msg","text","standard", null, null);
		index.put(builder);
		
	}
	
	
	
	
}
