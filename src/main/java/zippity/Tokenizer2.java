package zippity;

import java.util.ArrayList;
import java.util.List;

import zippity.model.Part;

public class Tokenizer2 {
	
	List<Part> parts = new ArrayList<>();
	
	private int minPartSize=3;

	public Tokenizer2() {
	}

	public List<Part> split(String input) {
		parts.add(new Part(input, false));
		analyzeParts();
		return parts;
	}

	public void analyzeParts() {
		
		String repeat = findRepeatingString();
		
		while (repeat != null) {
			parts = splitOn(parts, repeat);
			repeat = findRepeatingString();
		}
	}
	
	public String findRepeatingString() {
		for (Part part : parts) {
			for (int chunkSize=part.part.length()/2; chunkSize >= minPartSize; chunkSize--) {
				for (int j = 0; j <= part.part.length()-chunkSize; j++) {
					String sub = part.part.substring(j, j+chunkSize);
					int repeats = findRepeats(part, sub);
					if (repeats > 1) {
						return sub;
					}
				}
			}
		}
		return null;
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
