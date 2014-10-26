package ticker.common;

public class FloatingTask extends Task {
	final int id = 1; //used in storage
	
	public FloatingTask(String description, char priority, boolean isRepeating) {
		super(description, null, null, null, null, priority, isRepeating);
	}
	
	public FloatingTask copy() {
		return new FloatingTask(this.getDescription(), this.getPriority(), this.getRepeat());
	}
}
