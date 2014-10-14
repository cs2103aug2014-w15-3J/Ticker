package tickerPackage;

import java.util.Vector;
import java.util.logging.*;
import tickerPackage.Logic;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class TickerUI extends Application {
	private static TickerUI ticker;
	private Logic logic;
	private String list;
	private Vector<Task> tasksToBeShown;
	private static Logger logger = Logger.getLogger("UI");


	public Logic getLogic() {
		return logic;
	}
	public static TickerUI getTickerUI() {
		return ticker;
	}
	public TickerUI() {
		// Initialisation
		ticker = this;
		logic = new Logic(this);


	}


	public void start(Stage primaryStage) {
		primaryStage.setTitle("Ticker");
		GridPane grid = new GridPane();
		grid.setAlignment(Pos.TOP_LEFT);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(10,10,10,10));

		Text enterCommand = new Text("Enter your command: ");
		grid.add(enterCommand, 0, 2);

		final TextField command = new TextField();
		grid.add(command, 1, 2);

		Text showResult = new Text("Your To-dos: ");
		grid.add(showResult, 0, 1);

		final TextArea result = new TextArea();
		result.setWrapText(true);
		grid.add(result, 1, 1);
		result.setText(list);

		final Text feedback = new Text();
		grid.add(feedback, 1, 3);
		
		final Text currentTime = new Text();
		grid.add(currentTime, 0, 0);
		currentTime.setText(Time.getCurrentTime().toString() + " "+ Date.getCurrentDate().toString());

		primaryStage.show();


		Scene scene = new Scene(grid, 650, 300);
		primaryStage.setScene(scene);
		primaryStage.show();

		command.setOnAction(new EventHandler<ActionEvent>() 
				{
			public void handle(ActionEvent event) {
				logger.log(Level.INFO, "user press enter once");
				String cmd = command.getText();
				assert !cmd.equals("");
				command.clear();
				//result.setText(cmd);
				feedback.setText(logic.getLogic(cmd));

				result.setText(list);  // to be changed into Vector of Task
			}
				});

	}

	/*String printOut(Vector<Task> list) {
		String output = null;
		for(int i = 0; i <list.size(); i++) {
			output.concat((i+1) + ". " + list.get(i).getDescription()+"\n");
		}

		return output;
	}*/

	//public void setHelp(String help) {
	//	this.help = help;
	//}

	//public void setList(Vector<Task> tasks) {
	//	this.tasksToBeShown = tasks;
	//}
	public void setList(String list) {
		this.list = list;
	}
	public void setList(Vector<Task> tasks) {
		this.tasksToBeShown = tasks;
	}

	//public void setTask(Vector<Task> tasks) {
	//	this.tasksToBeShown = tasks;
	//}

	public static void main(String[] args) {
		launch(args);
		ticker = new TickerUI();
		

	}
}
