package zippity.model;

public class Range {
	public int start;
	public int end;

	public Range(int start, int end) {
		this.start = start;
		this.end = end;
	}
	

	public boolean intersects(int start, int end) {
		return end > this.start && start < this.end;
	}
}
