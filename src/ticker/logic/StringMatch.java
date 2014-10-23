package ticker.logic;

public class StringMatch {
	private int taskArrayIndex;
	private float similarityScore;

	public StringMatch(int taskArrayIndex, float similarityScore) {
		this.taskArrayIndex = taskArrayIndex;
		this.similarityScore = similarityScore;
	}

	public int getIndex() {
		return taskArrayIndex;
	}
	
	public float getSimilarityScore() {
		return similarityScore;
	}
}

