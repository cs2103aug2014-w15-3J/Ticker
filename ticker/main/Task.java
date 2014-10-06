package ticker.main;

public class Task {
	String description;
	
	public Task(String description){
		this.description = description;
	}
	
	public String toString() {
		return description;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String input) {
		description = input;
	}
}
