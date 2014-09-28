package tickerPackage;

import java.util.Vector;

class Logic{
	// Pointer to the Vector currently in display
	Vector<Task> current;

	// Temporary sorted storages
	Vector<Task> sortedTime;
	Vector<Task> sortedPriority;
	Vector<Task> searchResults;

	public Logic(){
		// TODO Transfer data from storage

		// STUB:
		sortedTime = new Vector<Task>();
		sortedPriority = new Vector<Task>();
		searchResults = new Vector<Task>();

		current = sortedTime;

	}	

	public boolean delete(int index) {
		// Exception catching
		if (index >= 0 && index < current.size()) {
			Task deleted = current.remove(index);
			System.out.println(deleted + " has been removed.");
			return true;
		}
		
		System.out.println("Nothing has been deleted.");
		return false;
	}

	public boolean search(String str) {
		// TODO Auto-generated method stub
		System.out.println("search");
		return false;
	}

	public boolean list() {
		// TODO Auto-generated method stub
		System.out.println("list");
		return false;
	}

	public void edit(int index, boolean isAppending, String description) {
		// TODO Auto-generated method stub
		System.out.println("edit");
	}



	public void add(String description, Boolean isRepeating, Date sd, Date ed,
			Time st, Time et) {
		// TODO Auto-generated method stub
		System.out.println("add");
	}
}