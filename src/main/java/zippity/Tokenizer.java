package zippity;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import zippity.model.HTreeNode;
import zippity.model.Range;

public class Tokenizer {
	
	private String input;

	List<Range> parts = new ArrayList<>();

	public Tokenizer(String input) {
		this.input = input;
	}

	public List<HTreeNode> encode() {
		analyzeParts();
		return partsToTokens();
	}

	public void analyzeParts() {
		
		for (int chunkSize=input.length()/2; chunkSize > 1; chunkSize--) {
			for (int j = 0; j <= input.length()-chunkSize; j++) {
				findRepeatedSubstring(j, j+chunkSize);
			}
		}
	}
	
	private void findRepeatedSubstring(int start, int end) {
		String sub = input.substring(start, end);
		boolean added = false;

		for (int i=0; i+sub.length() <= input.length(); i++) {
			if (i+sub.length() <= start || i >= end) {
				boolean covered = false;
				
				for (Range part : parts) {
					covered |= (i+sub.length() > part.start) && (i < part.end);
				}
				
				if (!covered) {
					String sub2 = input.substring(i, i+sub.length());

					if (sub.equals(sub2)) {
						if (!added) parts.add(new Range(start, end));
						parts.add(new Range(i, i+sub.length()));
						added=true;
					}
				}
			}
		}
	}

	// TODO zijn nu sorted op size, zou moeten zijn op prevalence, dan size
	private Stack<HTreeNode> partsToTokens() {
		Stack<HTreeNode> tokens = new Stack<>();
		
		String prevSub = null;

		for (int i = parts.size()-1; i >=0; i--) {
			Range part = parts.get(i);
			String sub = input.substring(part.start, part.end);

			if (sub.equals(prevSub))
				tokens.get(tokens.size()-1).prevalence++;
			else
				tokens.add(new HTreeNode(sub));

			prevSub = sub;
		}
		return tokens;
	}
}
