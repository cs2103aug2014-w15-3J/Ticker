package ticker.logic;

import java.util.Collections;
import java.util.HashMap;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import uk.ac.shef.wit.simmetrics.similaritymetrics.AbstractStringMetric;
import uk.ac.shef.wit.simmetrics.similaritymetrics.MongeElkan;
import uk.ac.shef.wit.simmetrics.similaritymetrics.SmithWaterman;
import uk.ac.shef.wit.simmetrics.similaritymetrics.SmithWatermanGotoh;
import uk.ac.shef.wit.simmetrics.similaritymetrics.SmithWatermanGotohWindowedAffine;

public class HelpManager {
	
	//List of available commands
	private static final String COMMAND_HELP = "help";
	private static final String COMMAND_UNTICK = "untick";
	private static final String COMMAND_TICK = "tick";
	private static final String COMMAND_REDO = "redo";
	private static final String COMMAND_UNDO = "undo";
	private static final String COMMAND_UNKIV = "unkiv";
	private static final String COMMAND_KIV = "kiv";
	private static final String COMMAND_ADD = "add";
	private static final String COMMAND_EDIT = "edit";
	private static final String COMMAND_LIST = "list";
	private static final String COMMAND_CLEAR = "clear";
	private static final String COMMAND_DELETE = "delete";
	private static final String COMMAND_SEARCH = "search";
	private static final String COMMAND_SHOW = "show";
	
	//List of available help messages
	private static final String MESSAGE_HELP = "help";
	private static final String MESSAGE_UNTICK = "untick <index>";
	private static final String MESSAGE_TICK = "tick <index>";
	private static final String MESSAGE_REDO = "redo";
	private static final String MESSAGE_UNDO = "undo";
	private static final String MESSAGE_UNKIV = "unkiv <index>";
	private static final String MESSAGE_KIV = "kiv <index>";
	private static final String MESSAGE_ADD = "add <description> <startDate>-<endDate> <startTime>-<endTime>";
	private static final String MESSAGE_EDIT = "edit <index> <description>";
	private static final String MESSAGE_LIST = "list <listType>";
	private static final String MESSAGE_CLEAR = "clear";
	private static final String MESSAGE_DELETE = "delete <index>";
	private static final String MESSAGE_SEARCH = "search <description> <time> -<priority>";
	private static final String MESSAGE_SHOW = "show <listType>";
	private static final String MESSAGE_EMPTY = "";
	
	//These messages are used by Logger
	private static final String LOGGER_MESSAGE_DISPLAY = "Displaying help message";
	private static final String LOGGER_MESSAGE_HELPMANAGER = "HelpManager";
	
	//HelpManager activation length
	private static final int ACTIVATION_LENGTH = 2;
	
	//Constants used by Search Algorithm
	private static final double SIMILARITY_THRESHOLD = 65.0;
	private static final float SIMILARITY_INDEX_ZERO = 0F;
	private static final float SIMILARITY_INDEX_FOUR = 4.0F;
	private static final float SIMILARITY_INDEX_HUNDRED = 100.0F;
	
	private static final int ARRAY_FIRST = 0;
	
	private static Logger logger;
	private Vector<StringMatch> matchList;
	private HashMap<String, String> helpList;
	private String[] commandListSet;
	
	//@author A0116673A

	public HelpManager() {
		helpList = new HashMap<String, String>();
		matchList = new Vector<StringMatch>();
		initHelpList();
		initCommandListSet();
		logger = Logger.getLogger(LOGGER_MESSAGE_HELPMANAGER);
	}

	/**
	 *	Initialise the helplist with a list of available commands and corresponding
	 *	help messages
	 */
	private void initHelpList() {
		helpList.put(COMMAND_HELP, MESSAGE_HELP);
		helpList.put(COMMAND_UNTICK, MESSAGE_UNTICK);
		helpList.put(COMMAND_TICK, MESSAGE_TICK);
		helpList.put(COMMAND_REDO, MESSAGE_REDO);
		helpList.put(COMMAND_UNDO, MESSAGE_UNDO);
		helpList.put(COMMAND_UNKIV, MESSAGE_UNKIV);
		helpList.put(COMMAND_KIV, MESSAGE_KIV);
		helpList.put(COMMAND_ADD, MESSAGE_ADD);
		helpList.put(COMMAND_EDIT, MESSAGE_EDIT);
		helpList.put(COMMAND_LIST, MESSAGE_LIST);
		helpList.put(COMMAND_CLEAR, MESSAGE_CLEAR);
		helpList.put(COMMAND_DELETE, MESSAGE_DELETE);
		helpList.put(COMMAND_SEARCH, MESSAGE_SEARCH);
		helpList.put(COMMAND_SHOW, MESSAGE_SHOW);
	}

	/**
	 *	Initialise the commandListSet with a list of commands
	*/
	private void initCommandListSet() {
		commandListSet = helpList.keySet().toArray(new String[0]); 
	}

	/**
	 * This method return most likely help message that the user needs based on 
	 * current input in the textfile
	 *
	 * @param   key       		current user input in textfield
	 * @return     most likely help message based on the first word of user input
	 */
	public String getHelp(String key){
		Vector<String> possibleCommands = new Vector<String>();
		matchList.removeAllElements();
		
		if (key.length() < ACTIVATION_LENGTH) {
			return MESSAGE_EMPTY;
		} else {
			calculateSimilarityIndexBasedOnFirstWord(key);
			
			Collections.sort(matchList, new StringMatchComparator());
			
			findElementWithinSimilarityScore(possibleCommands);
			
			logger.log(Level.INFO, LOGGER_MESSAGE_DISPLAY);
			return findMostLikelyHelpMessage(possibleCommands);
		}
	}
	
	/**
	 * Calculate the similarity index of the first word with a lit of commands
	 * 
	 * @param key 	user's input in the textbox
	 */
	private void calculateSimilarityIndexBasedOnFirstWord(String key) {
		String firstWordKey = key.split(" ")[ARRAY_FIRST];
	
		int i = 0;	
		for (String command: commandListSet) {
			float score = getMatchLikelyhood(firstWordKey.toLowerCase(), command);
			matchList.add(new StringMatch(i, score));
			i++;
		}
	}

	/**
	 * Determine a list of possible commands and put them into possibleCommands array
	 * @param possibleCommands a list of possible commands
	 */
	private void findElementWithinSimilarityScore(Vector<String> possibleCommands) {
		for (StringMatch sm : matchList) {
			if (sm.getSimilarityScore() < SIMILARITY_THRESHOLD) {
				break;
			}
			possibleCommands.add(commandListSet[sm.getIndex()]);
		}
	}

	/**
	 * Return the help message based on the most likely command
	 * @param possibleCommands	a list of possible commands
	 * @return	help message
	 */
	private String findMostLikelyHelpMessage(Vector<String> possibleCommands) {
		if (possibleCommands.isEmpty()) {
			return MESSAGE_EMPTY;
		} else {
			return helpList.get(possibleCommands.get(ARRAY_FIRST));
		}
	}

	/**
	 * This method calculates the similarity index between two input strings based 
	 * on the algorithms provided by Simmetrics library
	 *
	 * @param str1		input string 1
	 * @param str2    	input string 2
	 * @return     the similarity index between str1 and str2
	 * @throws Error  If commandType is unidentified.
	 */
	private static float getMatchLikelyhood(final String str1, final String str2) {
		AbstractStringMetric metric;
		float avg = SIMILARITY_INDEX_ZERO, result = SIMILARITY_INDEX_ZERO;
		metric = new SmithWaterman();
		result = metric.getSimilarity(str1, str2);
		avg += result;
		metric = new SmithWatermanGotoh();
		result = metric.getSimilarity(str1, str2);
		avg += result;
		metric = new SmithWatermanGotohWindowedAffine();
		result = metric.getSimilarity(str1, str2);
		avg += result;
		metric = new MongeElkan();
		result = metric.getSimilarity(str1, str2);
		avg += result;
		return (avg / SIMILARITY_INDEX_FOUR) * SIMILARITY_INDEX_HUNDRED;
	}
}