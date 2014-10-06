/* Team ID: W15-3J
 * Project name: Ticker
 * Group Members: 
 *
 *
 *
 *
 * Purpose:
 * This program assumes that:
 */

/* This code is last edited by Nicholette 28/9/2014 */

package ticker.main;

public class Ticker {
	// Attributes
	private static Ticker ticker;
	private Parser parser;
	private Logic logic;
	//private static TickerUI ui;
	//private static Storage storage;

	public Ticker() {
		// Initialisation
		ticker = this;
		parser = new Parser();
		logic = new Logic();
		ui = new TickerUI();
		//storage = new Storage();
	}

	public static Ticker getTicker() {
		return ticker;
	}

	public Logic getLogic() {
		return logic;
	}

	public static void main(String[] args) {
		System.out.println("Welcome to ticker");
		ticker = new Ticker();
		while (true)
			ticker.parser.getCommand();
	}
}
