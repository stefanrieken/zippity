package zippity.model;

public class Part {
	public String part;
	boolean token;

	public Part(String part, boolean token) {
		this.part = part;
		this.token = token;
	}
	
	public boolean isToken() {
		return token;
	}
}
