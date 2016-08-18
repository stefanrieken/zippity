package zippity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import zippity.model.Part;

public class Tokenizer {
	
	List<Part> parts = new ArrayList<>();
	
	private int maxPartSize;
	private int minPartSize;

	private int inputSize;

	private EncodingSpecifics encodingSpecifics;

	public Tokenizer(EncodingSpecifics encodingSpecifics) {
		this.encodingSpecifics = encodingSpecifics;
		this.minPartSize = 2; // below 2 is also safe, but makes no sense searching
		this.maxPartSize = 32; // can safely use input.size()/2; but this is much faster. longest token in 'li lingues' is 29.
	}

	public List<Part> split(String input) {
		inputSize = input.length();
		parts.add(new Part(input, false));
		analyzeParts();
		optimizeParts();
		return parts;
	}

	public void optimizeParts() {
		if (parts.size() == 1) return;

		List<Part> result = new ArrayList<Part>();
		result.add(parts.get(0));
		
		for (int i=1; i < parts.size(); i++) {
			Part part = parts.get(i);
			Part preceding = result.get(result.size()-1);
			if (!part.isToken() && !preceding.isToken()) {
				System.out.println("hier");
				preceding.part = preceding.part + part.part;
			} else {
				result.add(part);
			}
		}
		
		parts = result;
	}

	public void analyzeParts() {
		
		String token = findToken();
		
		List<Part> newParts = parts;

		while (token != null && withinEncodingSpecifics(newParts)) {
			parts = newParts;
			newParts = splitOn(parts, token);
			token = findToken();
		}
	}
	
	private boolean withinEncodingSpecifics(List<Part> parts) {
		if (parts.size() > encodingSpecifics.getMaxParts())
			return false;
	
		Map<String, Integer> numOccurrences = new HashMap<String,Integer>();
		for (Part part : parts) {
			Integer occurrence = numOccurrences.get(part.part);
			if (occurrence == null) occurrence = 0;
			occurrence++;
			if (occurrence > encodingSpecifics.getMaxTokens()) return false;
			numOccurrences.put(part.part, occurrence);
		}
		return true;
	}

	public String findToken() {

		for (Part part : parts) {

			int halfPartSize = part.part.length() / 2;
			int maxChunkSize = Math.min(halfPartSize,  maxPartSize);

//			// start-small algorithm:
//			for (int j=0; j<= part.part.length()-minPartSize; j++) {
//				String prevString = "";
//				int prevRepeats = 0;
//				int prevCost = Integer.MAX_VALUE;
//
//				for (int chunkSize=minPartSize; chunkSize <= maxChunkSize && j+chunkSize <= part.part.length(); chunkSize++) {
//					String sub = part.part.substring(j, j+chunkSize);
//					int repeats = findRepeats(part, sub);
//					int cost = encodingSpecifics.getCostEffectiveness(sub, repeats, inputSize);
//
//					if (prevRepeats > 1 && prevCost < 0 && prevCost < cost)
//						return prevString;
//					
//					prevString = sub;
//					prevRepeats = repeats;
//					prevCost = cost;
//				}
//			}
//
//			// trust-the-longest algorithm (works better with smaller files):
//
//			for (int chunkSize=maxChunkSize; chunkSize >= minPartSize; chunkSize--) {
//				for (int j = 0; j <= part.part.length()-chunkSize; j++) {
//					String sub = part.part.substring(j, j+chunkSize);
//					int repeats = findRepeats(part, sub);
//					if (encodingSpecifics.getCostEffectiveness(sub, repeats, inputSize) < 0) {
//						return sub;
//					}
//				}
//			}

			// start-big algorithm:
			for (int j=0; j<= part.part.length()-minPartSize; j++) {
				String prevString = "";
				int prevRepeats = 0;
				int prevCost = Integer.MAX_VALUE;

				for (int chunkSize=(maxChunkSize > part.part.length() - j ? part.part.length() - j : maxChunkSize); chunkSize >= minPartSize; chunkSize--) {
					String sub = part.part.substring(j, j+chunkSize);
					int repeats = findRepeats(part, sub);
					int cost = encodingSpecifics.getCostEffectiveness(sub, repeats, inputSize);

					if (prevRepeats > 1 && prevCost < 0 && prevCost < cost)
						return prevString;
					
					prevString = sub;
					prevRepeats = repeats;
					prevCost = cost;
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
