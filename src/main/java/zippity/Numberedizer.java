package zippity;

import java.util.List;

import zippity.model.HTreeNode;
import zippity.model.Part;

/**
 * Output a string array containing first the tokens, then the references to these tokens.
 * 
 * If only one token exists, just output that token.
 */
public class Numberedizer {

	public String encode(List<Part> parts, List<HTreeNode> tokens, char separator) {
		StringBuffer result = new StringBuffer();
		result.append(makeDictionary(tokens, separator));
		
		int[] referenceList = makeReferenceList(parts, tokens);

		if (referenceList.length > 1) {
			for (int i=0; i<referenceList.length; i++) {
				result.append((char) (32 + referenceList[i]));
			}
		}

		return result.toString();
	}

	public String makeDictionary(List<HTreeNode> tokens, char separator) {
		StringBuffer result = new StringBuffer();
		
		// don't make the original string longer by dict'ing it
		if (tokens.size() == 1) return tokens.get(0).value;

		for (HTreeNode token : tokens) {
			result.append(token.value);
			result.append(separator);
		}
		
		result.append(separator);
		
		return result.toString();
	}

	public int[] makeReferenceList(List<Part> parts, List<HTreeNode> tokens) {
		int[] result = new int[parts.size()];
		for (int i=0; i< parts.size(); i+=2) {
			result[i] = findPart(parts.get(i).part, tokens);
		}
		return result;
	}
	
	private int findPart(String part, List<HTreeNode> tokens) {
		for (int i=0; i<tokens.size();i++) {
			if (part.equals(tokens.get(i).value)) return i;
		}
		
		throw new RuntimeException("Token cannot be found");
	}
}
