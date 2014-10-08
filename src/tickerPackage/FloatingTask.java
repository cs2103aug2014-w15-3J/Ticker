package tickerPackage;

public class FloatingTask extends Task {
	final int id = 1; //used in storage
	
	public FloatingTask(String description, int priority) {
		super(description, null, null, null, null, priority);
	}
}
