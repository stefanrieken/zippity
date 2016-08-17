package zippity;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.junit.Test;

import zippity.model.HTreeNode;
import zippity.model.Part;

public class Tokenizer2Test {

	@Test
	public void testSplitter() {
		List<Part> parts = new ArrayList<>();
		parts.add(new Part("heel veel", false));
		parts.add(new Part("scheelt het niet", false));
		
		List<Part> result = new Tokenizer2().splitOn(parts, "eel");
		
		for (Part part : result) {
			System.out.println(part.part + ":" + part.isToken());
		}
	}

	@Test
	public void testSplitter2() {
		List<Part> parts = new Tokenizer2().split("neque porro quisquam est qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit");

//		for (Part part : parts) {
//			System.out.println(part.part);
//		}
		
		List<HTreeNode> freq = new FrequencyAnalyzer().analyzeFrequency(parts);

		Stack<HTreeNode> stack = new Stack<>();
		for (HTreeNode node : freq) {
			stack.push(node);
			System.out.println(node.value + ":" + node.prevalence);
		}

		String huffTable = new String(new Huffmannizer().encode(stack, 'X'));
		System.out.println(huffTable);
	}

}
