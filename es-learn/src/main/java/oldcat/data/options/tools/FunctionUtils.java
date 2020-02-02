import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.BiFunction;

public class FunctionUtils {
	public static class CompareArray{
		private Object[] objs;
		public CompareArray(Object... objs) {
			this.objs = objs;
		}
		@Override 
		public boolean equals(Object o) {
			if(o==null)return false;
			if(!(o instanceof CompareArray))return false;
			CompareArray other =(CompareArray) o;
			if(this.objs.length!=other.objs.length)return false;
			for(int i =0;i<objs.length;i++) {
				if(objs[i]==null&&other.objs[i]==null)continue;
				if(objs[i]==null||!objs[i].equals(other.objs[i]))return false;
			}
			return true;
		}
		@Override
		public int hashCode() {
			int value=0;
			for(Object o :objs) {
				if(o==null)value^=0;
				else value^=o.hashCode();
			}
			return value;
		}
		
		
	}

	public static <T,R> Map<R,List<T>> groupBy( List<T> datas ,Function<T,R> func ){
		HashMap<R,List<T>> map = new HashMap<R,List<T>>();
		for(T data:datas) {
			R r = func.apply(data);
			if(r==null)throw new RuntimeException("不能对null做聚合,也就是你生成的key值有null");
			if(map.get(r)==null)map.put(r, new ArrayList<T>());
			map.get(r).add(data);
		}
		return map;
	}
	public static <T1,T2,K,R> List<R> leftJoin( List<T1> left ,List<T2> right,
			Function<T1,K> func1,
			Function<T2,K> func2,
			BiFunction<T1,T2,R> func3
			){
		List<R> result = new ArrayList<R>();
		Map<K,List<T1>> leftMap = groupBy(left,func1);
		Map<K,List<T2>> rightMap = groupBy(right,func2);
		for(K k : leftMap.keySet()) {
			List<T1> list1 = leftMap.get(k);
			List<T2> list2 = rightMap.get(k);
			for(T1 t1 :list1) {
				if(list2==null||list2.size()==0) {
					result.add( func3.apply(t1, null));
				}else {
					for(T2 t2 :list2   ) {
						result.add(func3.apply(t1, t2));
					}
				}	
			}
		}
		return result;
	}
	
	public static <T1,T2,K,R> List<R> innerJoin( List<T1> left ,List<T2> right,
			Function<T1,K> func1,
			Function<T2,K> func2,
			BiFunction<T1,T2,R> func3
			){
		List<R> result = new ArrayList<R>();
		Map<K,List<T1>> leftMap = groupBy(left,func1);
		Map<K,List<T2>> rightMap = groupBy(right,func2);
		for(K k : leftMap.keySet()) {
			List<T1> list1 = leftMap.get(k);
			List<T2> list2 = rightMap.get(k);
			for(T1 t1 :list1) {
				if(list2==null||list2.size()==0) {
				}else {
					for(T2 t2 :list2   ) {
						result.add(func3.apply(t1, t2));
					}
				}	
			}
		}
		return result;
	}
	// 选取第一个
	public static <T,K> List<T> distanctByK(List<T> lists, Function<T,K> func ){
		List<T> result = new ArrayList<T>();
		Set<K> map = new HashSet<K>();
		for(T t :lists) {
			K k = func.apply(t);
			if(map.contains(k))continue;
			else {
				map.add(k);
				result.add(t);
			}
		}
		return result;
	}
	public static <T,V> V[] mapValueAsObjects(Map<T,V> map,Class<V> v ) {
		if(map==null)return null;
		V[] vs = (V[]) Array.newInstance(v, map.size());
		int i =0;
		for(V v1:map.values()) {
			vs[i]=v1;
			i++;
		}
		return vs;
	}
	
	
	
	public static void main(String...strings) {
	
		
		
		
	}
	
	
	
	
}
