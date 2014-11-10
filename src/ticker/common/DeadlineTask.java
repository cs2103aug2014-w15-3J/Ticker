package ticker.common;

//@author A0114535M
/**
 * Description: This class is used for deadline tasks.
 */
public class DeadlineTask extends Task {
	// CONSTANTS
	private static final String STRING_DEADLINE = " deadline ";
	private static final String STRING_COMMA = ", ";
	private static final int SMALLER = -1;
	private static final int EQUAL = 0;

	// ATTRIBUTES
	public final int id = 4; // used in storage

	public DeadlineTask(String description, Date endDate, Time endTime,
			char priority, boolean isRepeating) {
		super(description, null, null, endDate, endTime, priority, isRepeating);
	}
	
	/**
	 * This method deep copies a DeadlineTask.
	 */
	public DeadlineTask copy() {
		return new DeadlineTask(this.getDescription(), this.getEndDate(),
				this.getEndTime(), this.getPriority(), this.getRepeat());
	}
	
	/**
	 * This method checks for task expiry.
	 */
	@Override
	public void isExpired() {
		if (this.endDate != null
				&& this.endTime != null
				&& (this.endDate.compareTo(Date.getCurrentDate()) < EQUAL || (this.endDate
						.compareTo(Date.getCurrentDate()) == EQUAL && this.endTime
						.compareTo(Time.getCurrentTime()) == SMALLER))) {
			this.isExpired = true;
			return;
		}
		this.isExpired = false;
		return;
	}
	
	/**
	 * This method prints the DeadlineTask.
	 */
	@Override
	public String toString() {

		String timing = STRING_DEADLINE;

		if (getEndTime() != null) {
			timing += getEndTime() + STRING_COMMA;
		}

		timing += getEndDate();

		return getDescription() + timing;
	}
}
