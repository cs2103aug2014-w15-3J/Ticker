//@author A0114535M

/* Team ID: W15-3J
 * Name: Li Jia'En, Nicholette
 * Matric Number: A0114535M
 * 
 * Project Title: Ticker
 * Interface: Observer
 * Description: This interface
 * 
 * Assumptions: 
 * This program assumes that:
 * -the Parser class will pass Logic class valid processed user input (as an UserInput object) with data at their correct
 * positions.
 * -the Logic class will always be used with classes CRUDManager, TickKIVManager, UndoRedoManager and SearchManager.
 * -the UI using this class knows the key for different task lists.
 */

package ticker.logic;

import java.util.Vector;

import ticker.common.Task;

public interface Observer {
	public void setList(Vector<Task> tasksToBeDisplayed);
	public void setNextView(int displayPageKey);
	public void isFileCorrupted(boolean corrupted);
	public void setHelp();
}
