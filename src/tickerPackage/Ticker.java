package tickerPackage;

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

public class Ticker {
	// Attributes
	private static Ticker ticker;
	private static Parser parser;
	private static Logic logic;
	//private static UI ui;
	//private static Storage storage;
	
	public Ticker() {
	}
	
	public Ticker getTicker() {
		return ticker;
	}
	
	public Logic getLogic() {
		return logic;
	}
	
	public static void main(String[] args) {
		// Initialisation
		ticker = new Ticker();
		parser = new Parser();
		logic = new Logic();
		//ui = new UI();
		//storage = new Storage();
		
		parser.getCommand();
	}
	
}