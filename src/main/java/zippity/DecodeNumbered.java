package zippity;

import java.util.ArrayList;
import java.util.List;

public class DecodeNumbered {

	int index;

	public String decode(String input, char separator) {
		index = 0;
		
		char[] chars = input.toCharArray();
		List<String> tokens = readTokens(chars, separator);
		if (index < chars.length) {
			return recomposeString(tokens, chars);
		} else {
			return tokens.get(0);
		}
		
	}
	
	List<String> readTokens(char[] chars, char separator) {
		List<String> result = new ArrayList<>();

		StringBuffer current = new StringBuffer();

		while (index < chars.length && chars[index] != separator) {
			while (index < chars.length && chars[index] != separator) {
				current.append(chars[index++]);
			}
			result.add(current.toString());
			current = new StringBuffer();
			index++;
		}
		index++;
		return result;
	}
	
	private String recomposeString(List<String> tokens, char[] chars) {
		StringBuffer result = new StringBuffer();
		while (index < chars.length) {
			int idx = chars[index++] - Numberedizer.BASE;
			result.append(tokens.get(idx));
		}
		return result.toString();
	}
}
