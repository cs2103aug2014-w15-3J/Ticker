package ticker.logic;

//@author A0114535M
import java.util.Vector;

import ticker.common.Task;

public interface Observer {
	public void setList(Vector<Task> tasksToBeDisplayed);

	public void setNextView(int displayPageKey);

	public void isFileCorrupted(boolean corrupted);

	public void setHelp();
}
