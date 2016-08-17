package zippity;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import zippity.model.HTreeNode;
import zippity.model.Part;

public class FrequencyAnalyzer {

	public List<HTreeNode> analyzeFrequency(List<Part> parts) {
		Map<String, Integer> partsByFreq = new HashMap<String,Integer>();
		for (Part part : parts) {
			Integer value = partsByFreq.get(part.part);
			
			if (value == null) value = 0;
			partsByFreq.put(part.part, value+1);
		}
		
		LinkedList<HTreeNode> result = new LinkedList<>();
		for (Map.Entry<String, Integer> entry : partsByFreq.entrySet()) {
			int freq = entry.getValue();
			String part = entry.getKey();

			int insertPoint = 0;
			while(
					insertPoint < result.size() &&
					(result.get(insertPoint).prevalence == freq && result.get(insertPoint).value.length() < part.length() ||
					result.get(insertPoint).prevalence < freq)
			) insertPoint++;
			
			result.add(insertPoint, new HTreeNode(part, freq));
		}
		
		return result;
	}
}
