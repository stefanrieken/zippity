package zippity.model;

import java.util.List;

public class HTreeNode implements Comparable<HTreeNode> {
	
	public int prevalence;
	public String value;
	public HTreeNode left;
	public HTreeNode right;

	public HTreeNode(String value) {
		this.value = value;
		this.prevalence = 1;
	}

	public HTreeNode(String value, int prevalence) {
		this.value = value;
		this.prevalence = prevalence;
	}

	public HTreeNode(HTreeNode left, HTreeNode right) {
		this.left = left;
		this.right = right;
		this.prevalence = left.prevalence + right.prevalence;
	}
	
	public void postOrder(List<HTreeNode> result) {
		if (left != null ) left.postOrder(result);
		if (right != null) right.postOrder(result);
		result.add(this);
	}

	@Override
	public int compareTo(HTreeNode o) {
		return this.prevalence - o.prevalence;
	}

}
