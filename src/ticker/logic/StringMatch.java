package ticker.logic;

// TODO: check description by end of project
/*
 * Class: StringMatch
 * Description: Functions as a temporary information storage for the index of a task in the list
 * as well as the level of similarity between the said task and the search key.
 */

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

