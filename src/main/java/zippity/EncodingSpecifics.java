package zippity;

public interface EncodingSpecifics {
	
	int getCostEffectiveness(String sub, int repeats, int inputSize);

	// in how many parts can we split the string?
	int getMaxParts();

	int getMaxTokens();
}
