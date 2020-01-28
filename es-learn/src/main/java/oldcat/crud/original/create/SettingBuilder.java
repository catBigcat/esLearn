package oldcat.crud.original.create;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

public class SettingBuilder {
	private Map<String, Object> setting;
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
	
	
	private String fieldsRealPath(String path) {
		String[] paths = path.split("\\.");
		StringBuilder builder = new StringBuilder();
		for(int i =0;i<paths.length-1;i++) {
			builder.append(paths[i]);
			builder.append(".fields.");
		}
		builder.append(  paths[paths.length-1]);
		return builder.toString();
	}
	private String propertiesRealPath(String path) {
		String[] paths = path.split("\\.");
		StringBuilder builder = new StringBuilder();
		for(int i =0;i<paths.length-1;i++) {
			builder.append(paths[i]);
			builder.append(".properties.");
		}
		builder.append(  paths[paths.length-1]);
		return builder.toString();
	}
	
	
	public SettingBuilder customNormalizer(String normalizerName,String type,List<String> charFilter,String... filters ) {
		Map<String ,Object> map = new HashMap<String,Object>();
		map.put("type", type);
		map.put("char_filter", charFilter);
		map.put("filter", Arrays.asList(filters));
		this.commonSetting("settings.analysis.normalizer",normalizerName , map);
		return this;
	}
	public SettingBuilder setNormalizer(String fields , String normalizer , String type) {
		this.commonSetting("mappings.properties."+fieldsRealPath(fields),"type", type);
		this.commonSetting("mappings.properties."+fieldsRealPath(fields),"normalizer", normalizer);
		return this;
	}
	public SettingBuilder setFiledsBoost(String fields ,double boost) {
		this.commonSetting("mappings.properties."+fieldsRealPath(fields),"boost", boost);
		return this;
	}
	/**
	 *	 是否会进行类型转化 ，比如把json中字符串转成 int，
	 *  	如果fields为空则设置为全局
	 * @param fields
	 * @param coerce
	 * @return
	 */
	public SettingBuilder setCoerce(String fields , boolean coerce) {
		if(fields==null)this.commonSetting("settings", "index.mapping.coerce", coerce);
		else {
			this.commonSetting("mappings.properties."+fieldsRealPath(fields), "coerce", coerce);
		}
		return this;
	}
	/**
	 * copyTo 类似于一个属性的视图，将多个field 合并成一个可以进行查询的视图
	 * @param orginal
	 * @param aim
	 * @return
	 */
	public SettingBuilder setCopyTo(String orginal , String aim) {
		this.commonSetting("mappings.properties."+fieldsRealPath(orginal), "copy_to", aim);
		getIfAbsentThenNewMapByTreePaths(this.setting,"mappings.properties."+fieldsRealPath(aim) );
		return this;
	}
	/**
	 * false 可以被搜索但是不返回数据
	 * @param fields
	 * @param docuValue
	 * @return
	 */
	public SettingBuilder setDocValues(String fields,boolean docuValue) {
		this.commonSetting("mappings.properties."+fieldsRealPath(fields), "doc_values", docuValue);
		return this;
	}
	/**
	 * 	可以动态的添加新的field
	 * @param fields
	 * @param docuValue
	 * @return
	 */
	public SettingBuilder setDynamic(String fields,boolean dynamic) {
		if(fields==null)this.commonSetting("mappings"+propertiesRealPath(fields), "dynamic", dynamic);
		else this.commonSetting("mappings.properties."+propertiesRealPath(fields), "dynamic", dynamic);
		return this;
	}
	/**
	 * 设置 某field不能被index ，注意这个field 一定是type类型。
	 * @param fields
	 * @param enable
	 * @return
	 */
	public SettingBuilder setEnabled(String fields,boolean enable) {
		this.commonSetting("mappings.properties."+fieldsRealPath(fields), "dynamic", enable);
		return this;
	}
	/**
	 *  全局序列数，优化聚合使用，但是费内存，频繁更新最好不用
	 * @param fields
	 * @param ordinal
	 * @return
	 */
	public SettingBuilder setEagerGlobalOrdinals(String fields,boolean ordinal) {
		this.commonSetting("mappings.properties."+fieldsRealPath(fields), "eager_global_ordinals", ordinal);
		return this;
	}
	/**
	 * text 不能使用doc_value 存储，所以通过fieldData来放入缓存中。其他参数作为过滤条件来筛选哪些需要加载如内存
	 * min 最小词频，max最大词频，minSegmentSize用来排除小的segment
	 * @param fields
	 * @param fielddata
	 * @param min
	 * @param max
	 * @param minSegmentSize
	 * @return
	 */
	public SettingBuilder setFieldData(String fields ,boolean fielddata,double min ,double max, int minSegmentSize) {
		this.commonSetting("mappings.properties."+fieldsRealPath(fields), "fielddata", fielddata);
		this.commonSetting("mappings.properties."+fieldsRealPath(fields)+".fielddata_frequency_filter", "min", min);
		this.commonSetting("mappings.properties."+fieldsRealPath(fields)+".fielddata_frequency_filter", "max", max);
		this.commonSetting("mappings.properties."+fieldsRealPath(fields)+".fielddata_frequency_filter", "min_segment_size", minSegmentSize);
		return this;
	}
	/**
	 * 	过滤词汇，长度大于20则不放入索引
	 * @param fields
	 * @param maxLen
	 * @return
	 */
	public SettingBuilder setIgnoreAbove(String fields ,int maxLen) {
		this.commonSetting("mappings.properties."+fieldsRealPath(fields), "ignore_above", maxLen);
		return this;
	}
	/**
	 * 	类型错误 是否忽略文档
	 * @param fields
	 * @param ignore
	 * @return
	 */
	public SettingBuilder setIgnoreMalformed(String fields , boolean ignore) {
		if(fields==null) {
			this.commonSetting("settings", "index.mapping.ignore_malformed", ignore);
		
		}else {
			this.commonSetting("mappings.properties."+fieldsRealPath(fields), "ignore_malformed", ignore);
		}
		return this;
	}
	public SettingBuilder setIndex(String fields , boolean index) {
		this.commonSetting("mappings.properties."+fieldsRealPath(fields), "index", index);
		return this;
	}
	/**
	 * <a href='https://www.elastic.co/guide/en/elasticsearch/reference/current/index-options.html'>参考</a>
	 * @param fields
	 * @param indexOptions  可选为docs freqs positions offsets
	 * @return
	 */
	public SettingBuilder setIndexOptions(String fields , String indexOptions) {
		this.commonSetting("mappings.properties."+fieldsRealPath(fields), "index_options", indexOptions);
		return this;
	}
	public SettingBuilder setIndexPhrases(String fields , boolean indexPhrases) {
		this.commonSetting("mappings.properties."+fieldsRealPath(fields), "index-phrases", indexPhrases);
		return this;
	}
	/**
	 * speed up prefix searches
	 * @param fields
	 * @param min The minimum prefix length to index
	 * @param max The maximum prefix length to index
	 * @return
	 */
	public SettingBuilder setIndexPrefixes(String fields , int min ,int max) {
		this.commonSetting("mappings.properties."+fieldsRealPath(fields)+".index_prefixes", "min_chars", min);
		this.commonSetting("mappings.properties."+fieldsRealPath(fields)+".index_prefixes", "max_chars", max);
		return this;
	}	
	/**
	 * <p>Norms store various normalization factors that are later used at query time in order to compute the score of a document relatively to a query.</p>
		<p>Although useful for scoring, norms also require quite a lot of disk (typically in the order of one byte per document per field in your index, even for documents that don’t have this specific field). As a consequence, if you don’t need scoring on a specific field, you should disable norms on that field. In particular, this is the case for fields that are used solely for filtering or aggregations.</p>
	 * @param fields
	 * @param norms
	 * @return
	 */
	public SettingBuilder setNorms(String fields , boolean norms) {
		this.commonSetting("mappings.properties."+fieldsRealPath(fields), "norms", norms);
		return this;
	}	
	/**
	 * 如果输入为null ，则用obj作为缺失值
	 * @param fields
	 * @param obj
	 * @return
	 */
	public SettingBuilder setNullValue(String fields , Object obj) {
		this.commonSetting("mappings.properties."+fieldsRealPath(fields), "null_value", obj);
		return this;
	}
	/**
	 * 	分词的间隔，对于数组而言。应用于proximity or phrase queries
	 * 	对于语法查询 可以通过"slop"来控制间隔
	 * @param fields
	 * @param value
	 * @return
	 */
	public SettingBuilder setPositionIncrementGap(String fields , int value) {
		this.commonSetting("mappings.properties."+fieldsRealPath(fields), "position_increment_gap", value);
		return this;
	}
	/**
	 * 
	 * @param fields
	 * @param similarity BM25 , classic , boolean
	 * @return
	 */
	public SettingBuilder setSimilarity(String fields , String similarity) {
		this.commonSetting("mappings.properties."+fieldsRealPath(fields), "similarity", similarity);
		return this;
	}
	/**
	 * _source 是否存储此项
	 * @param fields
	 * @param similarity
	 * @return
	 */
	public SettingBuilder setStore(String fields , boolean store) {
		this.commonSetting("mappings.properties."+fieldsRealPath(fields), "store", store);
		return this;
	}
	/**
	 * <p> no
	 *No term vectors are stored. (default)
	 *</p> <p>
	 *yes
	 *Just the terms in the field are stored.
	 *</p><p>
	 *with_positions	
	 *Terms and positions are stored.
	 *</p><p>
	 *with_offsets
	 *Terms and character offsets are stored.
	 *</p><p>
	 *with_positions_offsets
	 *Terms, positions, and character offsets are stored.
	 *</p><p>
	 *with_positions_payloads
	 *Terms, positions, and payloads are stored. </p>
	 *<p>用于快速高亮</p>
	 * @param fields
	 * @param termVector
	 * @return
	 */
	public SettingBuilder setTermVector(String fields , String termVector) {
		this.commonSetting("mappings.properties."+fieldsRealPath(fields), "term_vector", termVector);
		return this;
	}
}
