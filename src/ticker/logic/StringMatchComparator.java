//@author A0114535M

/* Team ID: W15-3J
 * Project Title: Ticker
 * Class: StringMatchComparator
 * Description: This class implements the Comparator interface and is used 
 * to sort tasks according to their similarity points.
 * 
 * Assumptions: 
 * This class assumes that:
 * -the objects to be compared are StringMatch objects.
 */

package ticker.logic;

import java.util.Comparator;
import java.util.logging.Level;

/**
 * This method compares the tasks by their similarity score.
 */
public class StringMatchComparator implements Comparator<StringMatch> {

	// CONSTANTS
	private static final int SMALLER = -1;
	private static final int EQUAL = 0;
	private static final int BIGGER = 1;
	// Log message
	private static final String LOG_NULL_STRINGMATCH_PASSED = "Null StringMatch got into StringMatchComparator.";

	public int compare(StringMatch sm1, StringMatch sm2) {
		try {
			if (sm1.getSimilarityScore() < sm2.getSimilarityScore()) {
				return BIGGER;
			}
			if (sm1.getSimilarityScore() > sm2.getSimilarityScore()) {
				return SMALLER;
			}
		} catch (NullPointerException npe) {
			Logic.logger.log(Level.WARNING, LOG_NULL_STRINGMATCH_PASSED);
		}
		return EQUAL;
	}
}
