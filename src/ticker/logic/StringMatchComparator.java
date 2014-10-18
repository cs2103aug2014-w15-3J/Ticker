package ticker.logic;

import java.util.Comparator;

public class StringMatchComparator implements Comparator<StringMatch> {
	public int compare(StringMatch sm1, StringMatch sm2) {
		try {
			if (sm1.similarityScore < sm2.similarityScore) {
				return 1;
			}
			if (sm1.similarityScore > sm2.similarityScore) {
				return -1;
			}
		}
		catch (NullPointerException ex) {
			System.out.println("Error with StringMatch");
		}
		return 0;
	}
}
