package zippity;

public interface EncodingSpecifics {
	
	boolean isCostEffectiveToken(String sub, int repeats);
	int getMaxParts();

}
