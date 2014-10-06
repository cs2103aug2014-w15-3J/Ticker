package tickerPackage;

import java.util.Scanner;

//import tickerPackage.Parser;
//import tickerPackage.Logic;
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
	private static Logic logic;
	//private static TickerUI ui;
	//private static Storage storage;

	public Ticker() {
		logic = new Logic();
	}

	public static void main(String[] args) {
		System.out.println("Welcome to ticker");
		Ticker ticker = new Ticker();
		Scanner sc = new Scanner(System.in);

		System.out.println("current time: " + Time.getCurrentTime());
		System.out.println("current date: " + Date.getCurrentDate());
		
		while (sc.hasNext()) {
			String feedback = logic.getLogic(sc.nextLine());
			System.out.printf(feedback);
		}
		
		sc.close();
	}
}
