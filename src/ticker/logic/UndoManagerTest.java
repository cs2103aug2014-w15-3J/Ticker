package ticker.logic;

import static org.junit.Assert.*;

import java.util.Vector;

import org.junit.Test;

import ticker.common.Date;
import ticker.common.DeadlineTask;
import ticker.common.FloatingTask;
import ticker.common.RepeatingTask;
import ticker.common.Task;
import ticker.common.Time;
import ticker.common.TimedTask;

public class UndoManagerTest {
	private static final String TASKS_FLOATING_DESCRIPTION = "Buy milk from NTUC";
	private static final String TASKS_DEADLINE_DESCRIPTION = "Finish OP2 slides";
	private static final String TASKS_TIMED_DESCRIPTION = "CS2103 V0.4 Demo";
	private static final String TASKS_REPEATING_DESCRIPTION = "Post Lecture Reflection";
	
	private static final String LIST_TIME = "time";
	private static final String LIST_PRIORITY = "priority";
	private static final String LIST_TICKED = "ticked";
	private static final String LIST_CMI = "cmi";
	
	private static final String COMMAND_ADD = "add";
	private static final String COMMAND_DELETE = "delete";
	private static final String COMMAND_EDIT = "edit";
	private static final String COMMAND_TICK = "tick";
	private static final String COMMAND_CMI = "cmi";
	private static final String COMMAND_UNCMI = "uncmi";
	private static final String COMMAND_UNTICK = "untick";
	
	private static int LIST_INDEX_DUMMY = 0;
	
	@Test
	public void testUndoAfterAddOperation() {
		Vector<Task> storedTasksByPriority = new Vector<Task>();
		Vector<Task> storedTasksByDeadline = new Vector<Task>();
		Vector<Task> storedTasksByTicked = new Vector<Task>();
		Vector<Task> storedTasksByCMI = new Vector<Task>();
		UndoManager uM = UndoManager.getInstance(storedTasksByPriority, 
				storedTasksByDeadline, storedTasksByTicked, storedTasksByCMI);
		
		//test undo-ing addition of floating task
		uM.add(new Event(COMMAND_ADD, new FloatingTask(TASKS_FLOATING_DESCRIPTION, 'B', false)));
		storedTasksByPriority.add(new FloatingTask(TASKS_FLOATING_DESCRIPTION, 'B', false));
		storedTasksByDeadline.add(new FloatingTask(TASKS_FLOATING_DESCRIPTION, 'B', false));
		uM.undo();
		assertEquals(true, storedTasksByPriority.isEmpty());
		assertEquals(true, storedTasksByDeadline.isEmpty());
		
		//test undo-ing addition of deadline task
		uM.add(new Event(COMMAND_ADD, new DeadlineTask(TASKS_DEADLINE_DESCRIPTION, new Date(2014, 11, 7), new Time(11, 30), 'A', false)));
		storedTasksByPriority.add(new DeadlineTask(TASKS_DEADLINE_DESCRIPTION, new Date(2014, 11, 7), new Time(11, 30), 'A', false));
		storedTasksByDeadline.add(new DeadlineTask(TASKS_DEADLINE_DESCRIPTION, new Date(2014, 11, 7), new Time(11, 30), 'A', false));
		uM.undo();
		assertEquals(true, storedTasksByPriority.isEmpty());
		assertEquals(true, storedTasksByDeadline.isEmpty());
		
		//test undo-ing addition of timed task
		uM.add(new Event(COMMAND_ADD, new TimedTask(TASKS_TIMED_DESCRIPTION, new Date(2014, 11, 5), new Time(15, 0), new Date(2014, 11, 5), new Time(14, 0), 'A', false)));
		storedTasksByPriority.add(new TimedTask(TASKS_TIMED_DESCRIPTION, new Date(2014, 11, 5), new Time(15, 0), new Date(2014, 11, 5), new Time(14, 0), 'A', false));
		storedTasksByDeadline.add(new TimedTask(TASKS_TIMED_DESCRIPTION, new Date(2014, 11, 5), new Time(15, 0), new Date(2014, 11, 5), new Time(14, 0), 'A', false));
		uM.undo();
		assertEquals(true, storedTasksByPriority.isEmpty());
		assertEquals(true, storedTasksByDeadline.isEmpty());
		
		//test undo-ing addition of repeating task
		uM.add(new Event(COMMAND_ADD, new RepeatingTask(TASKS_REPEATING_DESCRIPTION, new Date(2014, 11, 6), null, null, 'B', true)));
		storedTasksByPriority.add(new RepeatingTask(TASKS_REPEATING_DESCRIPTION, new Date(2014, 11, 6), null, null, 'B', true));
		storedTasksByDeadline.add(new RepeatingTask(TASKS_REPEATING_DESCRIPTION, new Date(2014, 11, 6), null, null, 'B', true));
		uM.undo();
		assertEquals(true, storedTasksByPriority.isEmpty());
		assertEquals(true, storedTasksByDeadline.isEmpty());
		
		//test undo-ing the latest event with multiple items in the Event stack
		
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
		
		//test undo-ing removal of floating task
		uM.add(new Event(COMMAND_ADD, new FloatingTask(TASKS_FLOATING_DESCRIPTION, 'B', false)));
		storedTasksByPriority.add(new FloatingTask(TASKS_FLOATING_DESCRIPTION, 'B', false));
		storedTasksByDeadline.add(new FloatingTask(TASKS_FLOATING_DESCRIPTION, 'B', false));
		uM.add(new Event(COMMAND_DELETE, new FloatingTask(TASKS_FLOATING_DESCRIPTION, 'B', false), LIST_TIME, LIST_INDEX_DUMMY));
		storedTasksByPriority.remove(new FloatingTask(TASKS_FLOATING_DESCRIPTION, 'B', false));
		storedTasksByDeadline.remove(new FloatingTask(TASKS_FLOATING_DESCRIPTION, 'B', false));
		assertEquals(false, storedTasksByDeadline.contains(new FloatingTask(TASKS_FLOATING_DESCRIPTION, 'B', false)));
		uM.undo();
		assertEquals(true, storedTasksByDeadline.contains(new FloatingTask(TASKS_FLOATING_DESCRIPTION, 'B', false)));
		
		//test undo-ing removal of deadline task
		uM.add(new Event(COMMAND_ADD, new DeadlineTask(TASKS_DEADLINE_DESCRIPTION, new Date(2014, 11, 7), new Time(11, 30), 'A', false)));
		storedTasksByPriority.add(new DeadlineTask(TASKS_DEADLINE_DESCRIPTION, new Date(2014, 11, 7), new Time(11, 30), 'A', false));
		storedTasksByDeadline.add(new DeadlineTask(TASKS_DEADLINE_DESCRIPTION, new Date(2014, 11, 7), new Time(11, 30), 'A', false));
		uM.add(new Event(COMMAND_DELETE, new DeadlineTask(TASKS_DEADLINE_DESCRIPTION, new Date(2014, 11, 7), new Time(11, 30), 'A', false), LIST_TIME, LIST_INDEX_DUMMY));
		storedTasksByPriority.remove(new DeadlineTask(TASKS_DEADLINE_DESCRIPTION, new Date(2014, 11, 7), new Time(11, 30), 'A', false));
		storedTasksByDeadline.remove(new DeadlineTask(TASKS_DEADLINE_DESCRIPTION, new Date(2014, 11, 7), new Time(11, 30), 'A', false));
		assertEquals(false, storedTasksByDeadline.contains(new DeadlineTask(TASKS_DEADLINE_DESCRIPTION, new Date(2014, 11, 7), new Time(11, 30), 'A', false)));
		uM.undo();
		assertEquals(true, storedTasksByDeadline.contains(new DeadlineTask(TASKS_DEADLINE_DESCRIPTION, new Date(2014, 11, 7), new Time(11, 30), 'A', false)));
		
		//test undo-ing removal of timed task
		uM.add(new Event(COMMAND_ADD, new TimedTask(TASKS_TIMED_DESCRIPTION, new Date(2014, 11, 5), new Time(15, 0), new Date(2014, 11, 5), new Time(14, 0), 'A', false)));
		storedTasksByPriority.add(new TimedTask(TASKS_TIMED_DESCRIPTION, new Date(2014, 11, 5), new Time(15, 0), new Date(2014, 11, 5), new Time(14, 0), 'A', false));
		storedTasksByDeadline.add(new FloatingTask(TASKS_FLOATING_DESCRIPTION, 'B', false));
		uM.add(new Event(COMMAND_DELETE, new TimedTask(TASKS_TIMED_DESCRIPTION, new Date(2014, 11, 5), new Time(15, 0), new Date(2014, 11, 5), new Time(14, 0), 'A', false), LIST_TIME, LIST_INDEX_DUMMY));
		storedTasksByPriority.remove(new TimedTask(TASKS_TIMED_DESCRIPTION, new Date(2014, 11, 5), new Time(15, 0), new Date(2014, 11, 5), new Time(14, 0), 'A', false));
		storedTasksByDeadline.remove(new TimedTask(TASKS_TIMED_DESCRIPTION, new Date(2014, 11, 5), new Time(15, 0), new Date(2014, 11, 5), new Time(14, 0), 'A', false));
		assertEquals(false, storedTasksByDeadline.contains(new TimedTask(TASKS_TIMED_DESCRIPTION, new Date(2014, 11, 5), new Time(15, 0), new Date(2014, 11, 5), new Time(14, 0), 'A', false)));
		uM.undo();
		assertEquals(true, storedTasksByDeadline.contains(new TimedTask(TASKS_TIMED_DESCRIPTION, new Date(2014, 11, 5), new Time(15, 0), new Date(2014, 11, 5), new Time(14, 0), 'A', false)));
		
		//test undo-ing removal of repeating task
		uM.add(new Event(COMMAND_ADD, new RepeatingTask(TASKS_REPEATING_DESCRIPTION, new Date(2014, 11, 6), null, null, 'B', true)));
		storedTasksByPriority.add(new RepeatingTask(TASKS_REPEATING_DESCRIPTION, new Date(2014, 11, 6), null, null, 'B', true));
		storedTasksByDeadline.add(new RepeatingTask(TASKS_REPEATING_DESCRIPTION, new Date(2014, 11, 6), null, null, 'B', true));
		uM.add(new Event(COMMAND_DELETE, new RepeatingTask(TASKS_REPEATING_DESCRIPTION, new Date(2014, 11, 6), null, null, 'B', true), LIST_TIME, LIST_INDEX_DUMMY));
		storedTasksByPriority.remove(new RepeatingTask(TASKS_REPEATING_DESCRIPTION, new Date(2014, 11, 6), null, null, 'B', true));
		storedTasksByDeadline.remove(new RepeatingTask(TASKS_REPEATING_DESCRIPTION, new Date(2014, 11, 6), null, null, 'B', true));
		assertEquals(false, storedTasksByDeadline.contains(new RepeatingTask(TASKS_REPEATING_DESCRIPTION, new Date(2014, 11, 6), null, null, 'B', true)));
		uM.undo();
		assertEquals(true, storedTasksByDeadline.contains(new RepeatingTask(TASKS_REPEATING_DESCRIPTION, new Date(2014, 11, 6), null, null, 'B', true)));
		
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
		
		//test undo-ing edit of floating task
		uM.add(new Event(COMMAND_ADD, new FloatingTask(TASKS_FLOATING_DESCRIPTION, 'B', false)));
		storedTasksByPriority.add(new FloatingTask(TASKS_FLOATING_DESCRIPTION, 'B', false));
		storedTasksByDeadline.add(new FloatingTask(TASKS_FLOATING_DESCRIPTION, 'B', false));
		uM.add(new Event(COMMAND_EDIT, new FloatingTask(TASKS_FLOATING_DESCRIPTION, 'B', false), new FloatingTask(TASKS_DEADLINE_DESCRIPTION, 'B', false)));
		storedTasksByPriority.remove(new FloatingTask(TASKS_FLOATING_DESCRIPTION, 'B', false));
		storedTasksByDeadline.remove(new FloatingTask(TASKS_FLOATING_DESCRIPTION, 'B', false));
		storedTasksByPriority.add(new FloatingTask(TASKS_DEADLINE_DESCRIPTION, 'B', false));
		storedTasksByDeadline.add(new FloatingTask(TASKS_DEADLINE_DESCRIPTION, 'B', false));
		assertEquals(true, storedTasksByPriority.contains(new FloatingTask(TASKS_DEADLINE_DESCRIPTION, 'B', false)));
		assertEquals(false, storedTasksByPriority.contains(new FloatingTask(TASKS_FLOATING_DESCRIPTION, 'B', false)));
		uM.undo();
		assertEquals(false, storedTasksByPriority.contains(new FloatingTask(TASKS_DEADLINE_DESCRIPTION, 'B', false)));
		assertEquals(true, storedTasksByPriority.contains(new FloatingTask(TASKS_FLOATING_DESCRIPTION, 'B', false)));
		
		uM.clearStateForTesting();
	}
	
	@Test
	public void testUndoAfterTickOperation() {
		Vector<Task> storedTasksByPriority = new Vector<Task>();
		Vector<Task> storedTasksByDeadline = new Vector<Task>();
		Vector<Task> storedTasksByTicked = new Vector<Task>();
		Vector<Task> storedTasksByCMI = new Vector<Task>();
		UndoManager uM = UndoManager.getInstance(storedTasksByPriority, 
				storedTasksByDeadline, storedTasksByTicked, storedTasksByCMI);
		
		//test undo-ing tick of floating task
		uM.add(new Event(COMMAND_ADD, new FloatingTask(TASKS_FLOATING_DESCRIPTION, 'B', false)));
		storedTasksByPriority.add(new FloatingTask(TASKS_FLOATING_DESCRIPTION, 'B', false));
		storedTasksByDeadline.add(new FloatingTask(TASKS_FLOATING_DESCRIPTION, 'B', false));
		uM.add(new Event(COMMAND_TICK, new FloatingTask(TASKS_FLOATING_DESCRIPTION, 'B', false), LIST_TIME, LIST_TICKED));
		storedTasksByPriority.remove(new FloatingTask(TASKS_FLOATING_DESCRIPTION, 'B', false));
		storedTasksByDeadline.remove(new FloatingTask(TASKS_FLOATING_DESCRIPTION, 'B', false));
		storedTasksByTicked.add(new FloatingTask(TASKS_FLOATING_DESCRIPTION, 'B', false));
		storedTasksByDeadline.add(new FloatingTask(TASKS_FLOATING_DESCRIPTION, 'B', false));
		assertEquals(true, storedTasksByPriority.isEmpty());
		assertEquals(true, storedTasksByTicked.contains(new FloatingTask(TASKS_FLOATING_DESCRIPTION, 'B', false)));
		uM.undo();
		assertEquals(true, storedTasksByTicked.isEmpty());
		assertEquals(true, storedTasksByDeadline.contains(new FloatingTask(TASKS_FLOATING_DESCRIPTION, 'B', false)));
			
		
		uM.clearStateForTesting();
	}
}
