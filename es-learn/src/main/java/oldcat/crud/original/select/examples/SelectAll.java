package oldcat.crud.original.select.examples;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;


import oldcat.crud.original.ConstantVar;


public class SelectAll {

	public static void main(String...strings) {
		RestTemplate rest = new RestTemplate();
		String url = "http://"+ConstantVar.HOST+":"+ConstantVar.PORT+
				"/"+ConstantVar.INDEX_NAME+"/"+ConstantVar.TYPE+"/_search";
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> request = new HttpEntity<String>("",requestHeaders);
		ResponseEntity<String> response = rest.postForEntity(url, request,String.class);
		System.out.print(response.getBody());
	}
}
