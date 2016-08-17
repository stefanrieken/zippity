package zippity;

import java.util.List;

import zippity.model.HTreeNode;
import zippity.model.Part;

/**
 * Output a string array containing first the tokens, then the references to these tokens.
 * 
 * If only one token exists, just output that token.
 */
public class Numberedizer implements EncodingSpecifics {
	public static int BASE = 32; // encode using readable ASCII range 32-126

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
		return sub.length() + (1+repeats) + repeats * 2 < repeats * sub.length() - 1;
	}

	public String encode(List<Part> parts, List<HTreeNode> tokens, char separator) {
		StringBuffer result = new StringBuffer();
		result.append(makeDictionary(tokens, separator));
		
		int[] referenceList = makeReferenceList(parts, tokens);

		if (referenceList.length > 1) {
			for (int i=0; i<referenceList.length; i++) {
				result.append((char) (BASE + referenceList[i]));
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
		for (int i=0; i< parts.size(); i++) {
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
