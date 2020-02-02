package oldcat.data.options.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import java.util.ArrayDeque;

public class JsonTools {
	
	/**
	 * @param json jsonstr
	 * @param path 忽略数组形式下的path 例如 a.b.c
	 * @return
	 */
	public static List<Object> findAllByPath(String json,String path){
		List collection = new ArrayList(); 
		if(path==null) {
			collection.add( JSONObject.parse(json));
			return collection;
		}
		ArrayDeque jsonDeque = new ArrayDeque();
		Stack<String> pathStack =  new Stack<String>();
		String[] split = path.split("\\.");
		for(int i = split.length-1;i>=0;i--) {
			pathStack.add(split[i]);
		}
		Object init = JSONObject.parse(json);
		jsonDeque.add(init);
		int postion =0;
		while(!jsonDeque.isEmpty()   ) {
			int len = jsonDeque.size();
			for(int i = 0;i<len;i++) {
				Object obj = jsonDeque.pollFirst();
				if(obj instanceof JSONArray) {
					JSONArray ar  = (JSONArray) obj;
					for(Object map : ar) {
						if(postion>=split.length) {collection.add(map);}
						if(map instanceof JSONObject) {
							Object value = ((JSONObject)map).get(split[postion]);
							if(value!=null)jsonDeque.add(value);
						}
						
					}
					continue;
				}
				if(postion>=split.length) {collection.add(obj);}
				if(obj instanceof JSONObject) {
					Object value = ((JSONObject)obj).get(split[postion]);
					if(value!=null)jsonDeque.add(value);
				}	
			}
			postion++;
		}
		return collection;
	}
	
	
	
}
