package ticker.logic;

//@author A0114535M
/**
 * Description: Objects of this class stores the index of a task in the task list 
 * and the level of similarity between the said task and the search key.
 */
public class StringMatch {
	private int taskArrayIndex;
	private float similarityScore;

	public StringMatch(int taskArrayIndex, float similarityScore) {
		this.taskArrayIndex = taskArrayIndex;
		this.similarityScore = similarityScore;
	}

	/**
	 * This method returns the index of the task in the tasklist.
	 * 
	 * @return Index of the task in the tasklist.
	 */
	public int getIndex() {
		return taskArrayIndex;
	}

	/**
	 * This method returns the similarity score of the task and the search key.
	 *
	 * @return Similarity score of the task.
	 */
	public float getSimilarityScore() {
		return similarityScore;
	}
}
