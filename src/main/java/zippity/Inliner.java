package zippity;

import java.util.ArrayList;
import java.util.List;

import zippity.model.HTreeNode;
import zippity.model.Part;

/*
 * Encode 'inline':
 * 
 * - Write all first mentions of a string out full, in sentence order, followed by a separator.
 * - Write all subsequent mentions as a separator followed by the number.
 */
public class Inliner implements EncodingSpecifics {
	public char BASE = 32;

	@Override
	public int getMaxParts() {
		return 95; // because of encoding as readable ASCII
	}

	// tokenizing the repeat is advantageous when:
	//
	// dict entry + (1+n)*separator + n mentions * mentionsize * 2 < n * original string - X
	//
	// a dict entry adds a separator + n extra separators for strings in which it occurs.
	//
	// splitting a string turns one mention into three, so adds n*2 mentions.
	//
	// X is a guess number that says: if the win is so minimal, leave the string as-is;
	// maybe another tokenization yields a better result. Turns out, setting X to 1
	// means you win on some, lose on others.
	//
	// NOTE: this whole calculation is dependent on the final encoding.
	// This instance is optimized for encoding using the Numberedizer.
	// We might improve on this by providing this function through an interface
	// and implement different calculations for different encoders.
	@Override
	public boolean isCostEffectiveToken(String sub, int repeats) {
		 return sub.length() + 1 + (repeats *2) < repeats* sub.length();
	}

	public String encode(List<Part> parts, List<HTreeNode> tokens, char separator) {
		if (parts.size() == 1) return parts.get(0).part;

		List<String> present = new ArrayList<>();
		StringBuffer result = new StringBuffer();
		
		for (Part part : parts) {
			if (!present.contains(part.part)) {
				present.add(part.part);
				result.append(part.part);
				result.append(separator);
			} else {
				result.append(separator);
				result.append((char) (present.indexOf(part.part) + BASE));
			}
		}

		return result.toString();
	}
	
	// decode should really be different class
	int index = 0;
	List<String> tokens = new ArrayList<>();

	public String decode (String input, char separator) {
		index = 0;
		tokens = new ArrayList<>();
		char[] chars = input.toCharArray();
		
		StringBuffer result = new StringBuffer();
		
		String token = readNewToken(chars, separator);
		while (token != null) {
			result.append(token);
			token = readToken(chars, separator);
		}
		
		return result.toString();
	}
	
	public String readToken(char[] chars, char separator) {
		// read either NULL followed by a string, or a mention
		if (index >= chars.length) return null;
		if (chars[index] == separator) {
			index++;
			return tokens.get(chars[index++] - BASE);
		} else {
			return readNewToken(chars, separator);
		}
	}
	
	public String readNewToken(char[] chars, char separator) {
		StringBuffer result = new StringBuffer();
		while (index < chars.length && chars[index] != separator) {
			result.append(chars[index++]);
		}
		index++;
		String token = result.toString();
		tokens.add(token);
		return token;
	}
}
