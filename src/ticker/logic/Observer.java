//@author A0114535M

/* Team ID: W15-3J
 * Name: Li Jia'En, Nicholette
 * Matric Number: A0114535M
 * 
 * Project Title: Ticker
 * Interface: Observer
 * Description: This interface allows any type or number of UI to observe the Logic class.
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
