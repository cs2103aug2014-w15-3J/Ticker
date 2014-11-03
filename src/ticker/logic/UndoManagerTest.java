package ticker.logic;

import static org.junit.Assert.*;

import java.util.Vector;

import org.junit.Test;

import ticker.common.Date;
import ticker.common.Task;
import ticker.common.Time;

public class UndoManagerTest {

	@Test
	public void testUndoAfterAddOperation() {
		Vector<Task> storedTasksByPriority = new Vector<Task>();
		Vector<Task> storedTasksByDeadline = new Vector<Task>();
		Vector<Task> storedTasksByTicked = new Vector<Task>();
		Vector<Task> storedTasksByCMI = new Vector<Task>();
		UndoManager uM = UndoManager.getInstance(storedTasksByPriority, 
				storedTasksByDeadline, storedTasksByTicked, storedTasksByCMI);
		
		//just added an Event into the stack
		uM.add(new Event("add", new Task("Kappa", null, null, null, null, 'B', false)));
		
		//add the Task into the list
		storedTasksByPriority.add(new Task("Kappa", null, null, null, null, 'B', false));
		storedTasksByDeadline.add(new Task("Kappa", null, null, null, null, 'B', false));
		assertEquals(false, storedTasksByPriority.isEmpty());
		
		uM.undo();
		//check if the Task is being removed from the list
		assertEquals(true, storedTasksByPriority.isEmpty());
		

		uM.add(new Event("add", new Task("Deadline Task", null, null, new Date(2014,5,12), new Time(12,12), 'B', false)));
		storedTasksByPriority.add(new Task("Deadline Task", null, null, new Date(2014,5,12), new Time(12,12), 'B', false));
		storedTasksByDeadline.add(new Task("Deadline Task", null, null, new Date(2014,5,12), new Time(12,12), 'B', false));
		assertEquals(false, storedTasksByPriority.isEmpty());
		
		uM.undo();
		assertEquals(true, storedTasksByPriority.isEmpty());
		
		uM.clearStateForTesting(); //need to remove the instance from singleton
	}

	@Test
	public void testUndoAfterDeleteOperation() {
		Vector<Task> storedTasksByPriority = new Vector<Task>();
		Vector<Task> storedTasksByDeadline = new Vector<Task>();
		Vector<Task> storedTasksByTicked = new Vector<Task>();
		Vector<Task> storedTasksByCMI = new Vector<Task>();
		UndoManager uM = UndoManager.getInstance(storedTasksByPriority, 
				storedTasksByDeadline, storedTasksByTicked, storedTasksByCMI);
		
		//just added an Event into the stack
		uM.add(new Event("add", new Task("Kappa", null, null, null, null, 'B', false)));
		
		//add the Task into the list
		storedTasksByPriority.add(new Task("Kappa", null, null, null, null, 'B', false));
		storedTasksByDeadline.add(new Task("Kappa", null, null, null, null, 'B', false));
		
		uM.add(new Event("delete", new Task("Kappa", null, null, null, null, 'B', false), "TIME", 0));
		storedTasksByPriority.remove(new Task("Kappa", null, null, null, null, 'B', false));
		storedTasksByDeadline.remove(new Task("Kappa", null, null, null, null, 'B', false));
		
		assertEquals(false, storedTasksByDeadline.contains(new Task("Kappa", null, null, null, null, 'B', false)));
		
		uM.undo();
		
		assertEquals(false, storedTasksByDeadline.isEmpty());
		assertEquals(true, storedTasksByDeadline.contains(new Task("Kappa", null, null, null, null, 'B', false)));
		uM.clearStateForTesting();
	}
	
	@Test
	public void testUndoAfterEditOperation() {
		Vector<Task> storedTasksByPriority = new Vector<Task>();
		Vector<Task> storedTasksByDeadline = new Vector<Task>();
		Vector<Task> storedTasksByTicked = new Vector<Task>();
		Vector<Task> storedTasksByCMI = new Vector<Task>();
		UndoManager uM = UndoManager.getInstance(storedTasksByPriority, 
				storedTasksByDeadline, storedTasksByTicked, storedTasksByCMI);
		
		//just added an Event into the stack
		uM.add(new Event("add", new Task("Kappa", null, null, null, null, 'B', false)));
		
		//add the Task into the list
		storedTasksByPriority.add(new Task("Kappa", null, null, null, null, 'B', false));
		storedTasksByDeadline.add(new Task("Kappa", null, null, null, null, 'B', false));
		assertEquals(false, storedTasksByPriority.isEmpty());
		
		uM.add(new Event("edit", new Task("Kappa", null, null, null, null, 'B', false), new Task("Keepo", null, null, null, null, 'B', false)));
		storedTasksByPriority.remove(new Task("Kappa", null, null, null, null, 'B', false));
		storedTasksByDeadline.remove(new Task("Kappa", null, null, null, null, 'B', false));
		storedTasksByPriority.add(new Task("Keepo", null, null, null, null, 'B', false));
		storedTasksByDeadline.add(new Task("Keepo", null, null, null, null, 'B', false));
		
		uM.undo();
		//check if the Task is being removed from the list
		assertEquals(1, storedTasksByDeadline.size());
		assertEquals(true, storedTasksByDeadline.contains(new Task("Kappa", null, null, null, null, 'B', false)));
		assertEquals(false, storedTasksByDeadline.contains(new Task("Keepo", null, null, null, null, 'B', false)));
		uM.clearStateForTesting(); //need to remove the instance from singleton
	}
	
	@Test
	public void testUndoAfterTickOperation() {
		Vector<Task> storedTasksByPriority = new Vector<Task>();
		Vector<Task> storedTasksByDeadline = new Vector<Task>();
		Vector<Task> storedTasksByTicked = new Vector<Task>();
		Vector<Task> storedTasksByCMI = new Vector<Task>();
		UndoManager uM = UndoManager.getInstance(storedTasksByPriority, 
				storedTasksByDeadline, storedTasksByTicked, storedTasksByCMI);
		
		//just added an Event into the stack
		uM.add(new Event("add", new Task("Kappa", null, null, null, null, 'B', false)));

		//add the Task into the list
		storedTasksByPriority.add(new Task("Kappa", null, null, null, null, 'B', false));
		storedTasksByDeadline.add(new Task("Kappa", null, null, null, null, 'B', false));
		
		
	}
}
