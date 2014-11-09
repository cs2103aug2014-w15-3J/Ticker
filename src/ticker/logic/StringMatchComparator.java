//@author A0114535M

/* Team ID: W15-3J
 * Name: Li Jia'En, Nicholette
 * Matric Number: A0114535M
 * 
 * Project Title: Ticker
 * Class: StringMatchComparator
 * Description: This class implements the Comparator interface and is used to sort tasks according to their similarity points.
 * 
 * Assumptions: 
 * This interface assumes that:
 * -the objects to be compared are StringMatch objects.
 */

package ticker.logic;

import java.util.Comparator;

/**
 * This method compares the tasks by their similarity score.
 */
public class StringMatchComparator implements Comparator<StringMatch> {
	public int compare(StringMatch sm1, StringMatch sm2) {
		try {
			if (sm1.getSimilarityScore() < sm2.getSimilarityScore()) {
				return 1;
			}
			if (sm1.getSimilarityScore() > sm2.getSimilarityScore()) {
				return -1;
			}
		}
		catch (NullPointerException ex) {
			System.out.println("Error with StringMatch");
		}
		return 0;
	}
}
