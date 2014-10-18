package ticker.logic;

public class StringMatch {
	private int taskArrayIndex;
	private double similarityScore;

	public StringMatch(int taskArrayIndex, double similarityScore) {
		this.taskArrayIndex = taskArrayIndex;
		this.similarityScore = similarityScore;
	}

	public int getIndex() {
		return taskArrayIndex;
	}
}

