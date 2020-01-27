package oldcat.crud.original.create;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

public class SettingBuilder {
	private Map<String, Object> setting;

	/**
	 * 给某个属性配置analyser
	 * 
	 * @see <a href=
	 *      'https://www.elastic.co/guide/en/elasticsearch/reference/current/analyzer.html#search-quote-analyzer'></a>
	 * @param treePaths
	 * @param type
	 * @param analyzer  analyzerName
	 * @param old       oldSetting
	 * @return new setting
	 */
	private static Map<String, Object> generateSetFiledAnalyser(String treePaths, String type, String analyzer,
			String searchAnalyzer,String searchQuoteAnalyzer,
			Map<String, Object> old) {
		Map<String, Object> generateMap = old == null ? new HashMap<String, Object>() : old;
		if (generateMap.get("mappings") == null) {
			generateMap.put("mappings", new HashMap<String, Object>());
		}
		Map<String, Object> mappings = (Map<String, Object>) generateMap.get("mappings");
		if (mappings.get("properties") == null) {
			mappings.put("properties", new HashMap<String, Object>());
		}
		;
		Map<String, Object> node = (Map<String, Object>) mappings.get("properties");
		String[] treePath = treePaths.split("\\.");
		for (int i = 0; i < treePath.length - 1; i++) {
			if (node.get(treePath[i]) == null) {
				node.put(treePath[i], new HashMap<String, Object>());
			}
			node = (Map<String, Object>) node.get(treePath[i]);
			if (node.get("fields") == null) {
				node.put("fields", new HashMap<String, Object>());
			}
			node = (Map<String, Object>) node.get("fields");
		}
		if (node.get(treePath[treePath.length - 1]) == null) {
			node.put(treePath[treePath.length - 1], new HashMap<String, Object>());
		}
		node = (Map<String, Object>) node.get(treePath[treePath.length - 1]);
		node.put("type", type);
		node.put("analyzer", analyzer);
		if(searchAnalyzer!=null)node.put("search_analyzer", searchAnalyzer);
		if(searchQuoteAnalyzer!=null)node.put("search_quote_analyzer", searchQuoteAnalyzer);
		return generateMap;
	};

	private  static Map<String, Object> generateAnalyserSetting(String analyzerName, String type, String tokenizer,
			Map<String, Object> map, String... filters) {
		map = map != null ? map : new HashMap<String, Object>();
		String treePaths = "settings.analysis.analyzer." + analyzerName;
		Map<String, Object> node = getIfAbsentThenNewMapByTreePaths(map, treePaths);
		node.put("type", type);
		node.put("tokenizer", tokenizer);
		node.put("filter", Arrays.asList(filters));
		return map;
	}

	private static Map<String, Object> getIfAbsentThenNewMap(Map<String, Object> map, String key) {
		if (map.get(key) == null)
			map.put(key, new HashMap<String, Object>());
		return (Map<String, Object>) map.get(key);
	}

	private static Map<String, Object> getIfAbsentThenNewMapByTreePaths(Map<String, Object> map, String treePath) {
		String[] paths = treePath.split("\\.");
		for (String path : paths) {
			map = getIfAbsentThenNewMap(map, path);
		}
		return map;

	}
/**
 * 
 * @param treePaths 属性名称 例如 person.text
 * @param type
 * @param analyzer 索引分词器
 * @param searchAnalyzer 搜索时分词器
 * @param searchQuoteAnalyzer 搜索时语法分词器
 * @return
 */
	public SettingBuilder setAnalyser(String treePaths, String type, String analyzer,String searchAnalyzer,String searchQuoteAnalyzer ) {
		this.setting = generateSetFiledAnalyser(treePaths, type, analyzer,searchAnalyzer,searchQuoteAnalyzer ,setting);
		return this;
	}

	public SettingBuilder customAnalyser(String analyzerName, String type, String tokenizer, Map<String, Object> map,
			String... filters) {
		this.setting = generateAnalyserSetting(analyzerName, type, tokenizer, this.setting, filters);
		return this;
	}

	public SettingBuilder commonSetting(String treePath, String key, Object value) {
		Map<String, Object> node = getIfAbsentThenNewMapByTreePaths(this.setting, treePath);
		node.put(key, value);
		return this;
	}

	public SettingBuilder customFilter(String filterName, Map<String, Object> filterSetting) {
		return this.commonSetting("settings.analysis.filter", filterName, filterSetting);
	}
	
	public String toString() {
		return JSONObject.toJSONString(this.setting);
	}
	
	
	
	
}
