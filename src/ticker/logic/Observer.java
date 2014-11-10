package ticker.logic;


import java.util.Vector;

import ticker.common.Task;

//@author A0114535M
/** 
 * Description: This interface allows any UI who implements it 
 * to observe the Logic class.
 */
public interface Observer {
	public void setList(Vector<Task> tasksToBeDisplayed);

	public void setNextView(int displayPageKey);

	public void isFileCorrupted(boolean corrupted);

	public void setHelp();
}
