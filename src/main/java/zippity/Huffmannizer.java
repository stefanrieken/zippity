package zippity;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import zippity.model.HTreeNode;

public class Huffmannizer {
	
	public char[] encode(Stack<HTreeNode> tokens, char separator) {
		HTreeNode tree = toTree(tokens);
		List<HTreeNode> flat = postOrder(tree);
		
		return toCharArray(flat, separator);
	}
	
	public HTreeNode toTree(Stack<HTreeNode> tokens) {
		while (tokens.size() > 1) {
			HTreeNode left = popSafe(tokens);
			HTreeNode right = popSafe(tokens);
			tokens.push(new HTreeNode(left, right));
		}
		
		return tokens.pop();
	}
	
	public List<HTreeNode> postOrder(HTreeNode tree) {
		List<HTreeNode> result = new ArrayList<HTreeNode>();
		tree.postOrder(result);
		return result;
	}

	private HTreeNode popSafe(Stack<HTreeNode> tokens) {
		if (tokens.isEmpty()) return null;
		return tokens.pop();
	}

	public char[] toCharArray(List<HTreeNode> flat, char separator) {
		int size = flat.size();
		
		for (HTreeNode node : flat) {
			if (node.value != null)
				size += node.value.length();
		}
		
		char[] result = new char[size];
		
		int index = 0;
		for (HTreeNode node : flat) {
			if (node.value != null) {
				node.value.getChars(0, node.value.length(), result, index);
			result[index + node.value.length()] = separator;
			index += node.value.length() +1;
			} else {
				result[index] = separator;
				index += 1;
			}
		}
		
		return result;
	}
}
