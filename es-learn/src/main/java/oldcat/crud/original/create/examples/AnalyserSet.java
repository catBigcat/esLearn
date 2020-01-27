package oldcat.crud.original.create.examples;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import oldcat.crud.original.create.SettingBuilder;

public class AnalyserSet {

	
	public static void main(String...strings) {
		SettingBuilder builder = new SettingBuilder();
		builder.setAnalyser("text.english", "text", "english","my_stop_analyzer",null);
		builder.setAnalyser("text", "text", "english",null,null);
		System.out.print(    builder.toString());
		
	}
	
}
