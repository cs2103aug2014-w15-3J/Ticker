package ticker.common;

//@author A0114535M
public class FloatingTask extends Task {
	public final int id = 1; // used in storage

	public FloatingTask(String description, char priority, boolean isRepeating) {
		super(description, null, null, null, null, priority, isRepeating);
	}

	/**
	 * This method deep copies a FloatingTask.
	 */
	public FloatingTask copy() {
		return new FloatingTask(this.getDescription(), this.getPriority(),
				this.getRepeat());
	}
}
