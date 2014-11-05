//@author A0114535M

/* Team ID: W15-3J
 * Name: Li Jia'En, Nicholette
 * Matric Number: A0114535M
 * Project Title: CE1 TextBuddy
 * Purpose: This class receives text commands from the user and edits a textfile. 
 * The commands are for add, display, delete, clear and exit.
 * Assumptions: 
 * This program assumes that:
 * -the user knows the format for each command
 * -the user input lines in the textfile is not numbered.
 * -(option c) the file is saved to disk when the user exit the program
 */
package ticker.logic;

import java.util.Comparator;

/**
 * This method determines the action for each user command.
 *
 * @param userCommand Command from the user.
 * @param fileName    Name of textfile.
 * @param commandType Type of command from the user.
 * @param input       Name of temporary data structure containing the contents.
 * @return     Message from the action of the userCommand.
 * @throws Error  If commandType is unidentified.
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
