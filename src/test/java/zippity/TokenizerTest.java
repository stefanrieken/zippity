package zippity;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.junit.Test;

import zippity.model.HTreeNode;
import zippity.model.Part;

public class TokenizerTest {

	private String NEQUE = "neque porro quisquam est qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit";
	private String LIPSUM = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";

	private String DE_FINIBUS = "Sed ut perspiciatis, unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam eaque ipsa, quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt, explicabo. Nemo enim ipsam voluptatem, quia voluptas sit, aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos, qui ratione voluptatem sequi nesciunt, neque porro quisquam est, qui dolorem ipsum, quia dolor sit amet consectetur adipiscing velit, sed quia non numquam do eius modi tempora inci[di]dunt, ut labore et dolore magnam aliquam quaerat voluptatem. Ut enim ad minima veniam, quis nostrum exercitationem ullam corporis suscipit laboriosam, nisi ut aliquid ex ea commodi consequatur? Quis autem vel eum iure reprehenderit, qui in ea voluptate velit esse, quam nihil molestiae consequatur, vel illum, qui dolorem eum fugiat, quo voluptas nulla pariatur? At vero eos et accusamus et iusto odio dignissimos ducimus, qui blanditiis praesentium voluptatum deleniti atque corrupti, quos dolores et quas molestias excepturi sint, obcaecati cupiditate non provident, similique sunt in culpa, qui officia deserunt mollitia animi, id est laborum et dolorum fuga. Et harum quidem rerum facilis est et expedita distinctio. Nam libero tempore, cum soluta nobis est eligendi optio, cumque nihil impedit, quo minus id, quod maxime placeat, facere possimus, omnis voluptas assumenda est, omnis dolor repellendus. Temporibus autem quibusdam et aut officiis debitis aut rerum necessitatibus saepe eveniet, ut et voluptates repudiandae sint et molestiae non recusandae. Itaque earum rerum hic tenetur a sapiente delectus, ut aut reiciendis voluptatibus maiores alias consequatur aut perferendis doloribus asperiores repellat.";

	private String LILINGUES = "Li Europan lingues es membres del sam familie. Lor separat existentie es un myth. Por scientie, musica, sport etc, litot Europa usa li sam vocabular. Li lingues differe solmen in li grammatica, li pronunciation e li plu commun vocabules. Omnicos directe al desirabilite de un nov lingua franca: On refusa continuar payar custosi traductores. At solmen va esser necessi far uniform grammatica, pronunciation e plu commun paroles. Ma quande lingues coalesce, li grammatica del resultant lingue es plu simplic e regulari quam ti del coalescent lingues. Li nov lingua franca va esser plu simplic e regulari quam li existent Europan lingues. It va esser tam simplic quam Occidental in fact, it va esser Occidental. A un Angleso it va semblar un simplificat Angles, quam un skeptic Cambridge amico dit me que Occidental es.";

	private String LIESJE = "Liesje leerde Lotje lopen op de lange lindelaan.";
	
	private String ASCII = "ASCII stands for American Standard Code for Information Interchange. Computers can only understand numbers, so an ASCII code is the numerical representation of a character such as 'a' or '@' or an action of some sort. ASCII was developed a long time ago and now the non-printing characters are rarely used for their original purpose. Below is the ASCII character table and this includes descriptions of the first 32 non-printing characters. ASCII was actually designed for use with teletypes and so the descriptions are somewhat obscure. If someone says they want your CV however in ASCII format, all this means is they want 'plain' text with no formatting such as tabs, bold or underscoring - the raw format that any computer can understand. This is usually so they can easily import the file into their own applications without issues. Notepad.exe creates ASCII text, or in MS Word you can save a file as 'text only'.";

	private int MAX_PARTS = 95;// we encode in ASCII using characters 32 to 126

	@Test
	public void testSplitter() {
		List<Part> parts = new ArrayList<>();
		parts.add(new Part("heel veel", false));
		parts.add(new Part("scheelt het niet", false));
		
		List<Part> result = new Tokenizer().splitOn(parts, "eel");
		
		for (Part part : result) {
			System.out.println(part.part + ":" + part.isToken());
		}
	}

	@Test
	public void testTotHufftable() {
		String input = LIPSUM;
		List<Part> parts = new Tokenizer().split(input,MAX_PARTS);

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
		System.out.println(input);
	}

	@Test
	public void testAsNumberedList() {
		String input = DE_FINIBUS; //.toLowerCase();
		List<Part> parts = new Tokenizer().split(input, MAX_PARTS);


		List<HTreeNode> freq = new FrequencyAnalyzer().analyzeFrequency(parts);
		
		String result = new Numberedizer().encode(parts, freq, 'X');

		System.out.println("\nInput size  : " + input.length());
		System.out.println("Output size : " + result.length());
		System.out.println("Num parts   : " + parts.size());
		System.out.println("Dict size   : " + freq.size());

		int smallest = Integer.MAX_VALUE;
		int largest = 0;
		for (Part part : parts) {
			if (part.isToken()) {
				int length = part.part.length();
				if (length > largest) largest = length;
				if (length < smallest) smallest = length;
			}
		}

		System.out.println("\nLargest token  : " + largest);
		System.out.println("Smallest token : " + smallest);

		
		System.out.println();
		System.out.println(result);
		System.out.println(input);
	}

	@Test
	public void testAllInputs() {
		String[] inputs = {
				LIPSUM,
				DE_FINIBUS,
//				NEQUE,
//				LIESJE,
				ASCII,
				LILINGUES
			};
		
		for (String input : inputs) {
			input = input.toLowerCase();
			List<Part> parts = new Tokenizer().split(input, MAX_PARTS);
			List<HTreeNode> freq = new FrequencyAnalyzer().analyzeFrequency(parts);
			String result = new Numberedizer().encode(parts, freq, 'X');
			
			System.out.println("\nInput size  : " + input.length());
			System.out.println("Output size : " + result.length());

			System.out.println(result);
			System.out.println(input);
		}
	}
}
