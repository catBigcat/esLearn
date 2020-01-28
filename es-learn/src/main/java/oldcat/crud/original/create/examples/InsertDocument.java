package oldcat.crud.original.create.examples;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSONObject;

import oldcat.crud.original.ConstantVar;

public class InsertDocument {
	static class User{
		private String name;
		private String message;
		/**
		 * @return the name
		 */
		public String getName() {
			return name;	
		}
		/**
		 * @param name the name to set
		 * @return 
		 */
		public User setName(String name) {
			this.name = name;
			return this;
		}
		/**
		 * @return the message
		 */
		public String getMessage() {
			return message;
		}
		/**
		 * @param message the message to set
		 * @return 
		 */
		public User setMessage(String message) {
			this.message = message;
			return this;
		}
		public User(String name, String message) {
			super();
			this.name = name;
			this.message = message;
		}
	}
	
	public static void main(String...strings) {
		RestTemplate rest = new RestTemplate();
		String url = "http://"+ConstantVar.HOST+":"+ConstantVar.PORT+
				"/"+ConstantVar.INDEX_NAME+"/"+ConstantVar.TYPE;
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.setContentType(MediaType.APPLICATION_JSON);
		User user = new User("刘习","第一个人");
		JSONObject userWillInEs = (JSONObject) JSONObject.toJSON(user);
		HttpEntity<String> request = new HttpEntity<String>(userWillInEs.toString(),requestHeaders);
		ResponseEntity<String> response = rest.postForEntity(url, request,String.class);
		System.out.print(response.getBody());
		
	}
	
	
	
	
}
