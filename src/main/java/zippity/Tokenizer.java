package zippity;

import java.util.ArrayList;
import java.util.List;

import zippity.model.Part;

public class Tokenizer {
	
	List<Part> parts = new ArrayList<>();
	
	private int maxParts;
	private int maxPartSize;
	private int minPartSize;

	public Tokenizer() {
	}

	public List<Part> split(String input, int maxParts) {
		this.maxParts = maxParts; // may be as high as encoder can handle
		minPartSize = 2; // below 2 is also safe, but makes no sense searching
		maxPartSize = 32; // can safely use input.size()/2; but this is much faster. longest token in 'li lingues' is 29.
		
		parts.add(new Part(input, false));
		analyzeParts();
		return parts;
	}

	public void analyzeParts() {
		
		String token = findToken();
		
		List<Part> newParts = parts;

		while (token != null && newParts.size() < maxParts) {
			parts = newParts;
			newParts = splitOn(parts, token);
			token = findToken();
		}
	}
	
	public String findToken() {

		for (Part part : parts) {

			int halfPartSize = part.part.length() / 2;
			int maxChunkSize = Math.min(halfPartSize,  maxPartSize);

			for (int chunkSize=maxChunkSize; chunkSize >= minPartSize; chunkSize--) {
				for (int j = 0; j <= part.part.length()-chunkSize; j++) {
					String sub = part.part.substring(j, j+chunkSize);
					int repeats = findRepeats(part, sub);
					if (isCandidateForToken(sub, repeats)) {
						return sub;
					}
				}
			}
		}
		return null;
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
	private boolean isCandidateForToken(String sub, int repeats) {
		return sub.length() + (1+repeats) + repeats * 2 < repeats * sub.length() - 1;
	}

	public int findRepeats(Part current, String sub) {
		
		int repeats = 0;

		for (Part part : parts) {
			if (part.isToken()) continue;

			int index = part.part.indexOf(sub, 0);

			while (index != -1) {
				repeats++;
				index = part.part.indexOf(sub, index+sub.length());
			}
		}
		
		return repeats;
	}

	public List<Part> splitOn(List<Part> parts, String sub) {
		List<Part> result = new ArrayList<Part>();

		for (Part part : parts) {
			if (!part.isToken()) {
			
				int prevIndex = 0;
				int index = part.part.indexOf(sub, prevIndex);
	
				while (index != -1) {
					if (prevIndex != index)
						result.add (new Part(part.part.substring(prevIndex, index), false));
					result.add(new Part(sub, true));
	
					prevIndex = index+sub.length();
					index = part.part.indexOf(sub, index+sub.length());
				}
				
				if(prevIndex != part.part.length())
					result.add(new Part(part.part.substring(prevIndex), false));
			} else {
				result.add(part);
			}
		}
		return result;
	}
}
