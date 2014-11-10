package ticker.logic;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

//@author A0116673A

public class HelpManagerTest {
	
	private static final String MESSAGE_HELP = "help";
	private static final String MESSAGE_UNTICK = "untick <index>";
	private static final String MESSAGE_TICK = "tick <index>";
	private static final String MESSAGE_REDO = "redo";
	private static final String MESSAGE_UNDO = "undo";
	private static final String MESSAGE_UNKIV = "unkiv <index>";
	private static final String MESSAGE_KIV = "kiv <index>";
	private static final String MESSAGE_ADD = "add <description> <startDate>-<endDate> <startTime>-<endTime>";
	private static final String MESSAGE_EDIT = "edit <index> <newParameter>";
	private static final String MESSAGE_LIST = "list <listType>";
	private static final String MESSAGE_CLEAR = "clear";
	private static final String MESSAGE_DELETE = "delete <index>";
	private static final String MESSAGE_SEARCH = "search <description> <time> -<priority>";
	private static final String MESSAGE_SHOW = "show <listType>";
	private static final String MESSAGE_REMOVE = "remove <index>";	
	
	@Test
	public void testGetHelp() {
		HelpManager helpManager = new HelpManager();
		
		//test help command
		String input = "he";
		assertEquals(MESSAGE_HELP, helpManager.getHelp(input));
		
		//test untick command
		input = "unt";
		assertEquals(MESSAGE_UNTICK, helpManager.getHelp(input));
		
		//test tick command
		input = "ti";
		assertEquals(MESSAGE_TICK, helpManager.getHelp(input));
		
		//test redo command
		input = "re";
		assertEquals(MESSAGE_REDO, helpManager.getHelp(input));
		
		//test undo command
		input = "un";
		assertEquals(MESSAGE_UNDO, helpManager.getHelp(input));
		
		//test unkiv command
		input = "unk";
		assertEquals(MESSAGE_UNKIV, helpManager.getHelp(input));
		
		//test kiv command
		input = "ki";
		assertEquals(MESSAGE_KIV, helpManager.getHelp(input));
		
		//test add command
		input = "ad";
		assertEquals(MESSAGE_ADD, helpManager.getHelp(input));
		
		//test edit command
		input = "ed";
		assertEquals(MESSAGE_EDIT, helpManager.getHelp(input));
		
		//test list command
		input = "li";
		assertEquals(MESSAGE_LIST, helpManager.getHelp(input));
		
		//test clear command
		input = "cl";
		assertEquals(MESSAGE_CLEAR, helpManager.getHelp(input));
	
		//test delete command
		input = "de";
		assertEquals(MESSAGE_DELETE, helpManager.getHelp(input));
		
		//test search command
		input = "sea";
		assertEquals(MESSAGE_SEARCH, helpManager.getHelp(input));
		
		//test show command
		input = "show";
		assertEquals(MESSAGE_SHOW, helpManager.getHelp(input));
		
		//test remove command
		input = "rem";
		assertEquals(MESSAGE_REMOVE, helpManager.getHelp(input));
	}

}
