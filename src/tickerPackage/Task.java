package tickerPackage;

public class Task {
	String description;
	
	public Task(String description){
		description = this.description;
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
