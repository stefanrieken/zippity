package zippity;

import java.util.ArrayList;
import java.util.List;

import zippity.model.HTreeNode;
import zippity.model.Part;

/*
 * Encode 'inline':
 * 
 * - Write all first mentions of a string out full, in sentence order, followed by a separator.
 * - Write all subsequent mentions as a separator followed by the number of its first mention.
 * 
 * Variation:
 * - Precede all first mentions (but the very first one) by a separator.
 * - Write all subsequent mentions without one.
 * 
 * Cost is usually higher because most strings do not reoccur.
 */
public class Inliner implements EncodingSpecifics {
	public char BASE = 32;

	@Override
	public int getMaxParts() {
		return Integer.MAX_VALUE; // due to this (only), Inliner wins from Numberedizer
	}

	@Override
	public int getMaxTokens() {
		return 95; // because of encoding as readable ASCII
	}
	
	// A split introduces:
	// - a dictionary item followed by a separator
	// - an extra separator for each string split by it
	// - an extra separator for each mention
	// - the mention itself
	//
	//  dict entry + separator + (n-1 * mention) < n * string size -X
	//
	// Here again there's a 'X' factor that introduces some heuristic prudence;
	// again, this is win some, lose some. (noticeable loss: 'neque' compresses only at x=0)
	// For this algorithm, '2' seems to work fairly well.
	//
	@Override
	public int getCostEffectiveness(String sub, int repeats, int inputSize) {
		 return (sub.length() + 1 + (repeats*3)) - (repeats * sub.length() - 0);
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
		// read either a(nother) separator followed by a mention, or a new string
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
