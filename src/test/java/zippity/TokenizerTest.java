package zippity;

import java.util.List;

import org.junit.Test;

import zippity.model.HTreeNode;

public class TokenizerTest {

	@Test
	public void testTokenizer() {
		List<HTreeNode> tokens = new Tokenizer("neque porro quisquam est qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit").encode();
		for (HTreeNode token:tokens) {
			System.out.println(token.value + ":" + token.prevalence);
		}
	}
}
