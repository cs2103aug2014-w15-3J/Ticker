package ticker.ui;

import java.util.Vector;
import java.util.logging.*;

import ticker.logic.Logic;
import ticker.common.*;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class TickerUI extends Application {
	private static TickerUI ticker;
	private Logic logic;
	private String list;
	private Vector<Task> tasksToBeShown; //possible list: sortedTime, sortedPriority, cmi, ticked, search
	private String help;
	private boolean displayHelp=false;
	
	AnimationTimer time;
	
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
		help = help();
	}


	public void start(Stage stage) {
        stage.setTitle("Ticker");
        //AnchorPane main = new AnchorPane();
        Group root = new Group();
        
        Image logo = new Image("file:logo.png");
        ImageView imv = new ImageView();
        imv.setImage(logo);
        
        
		GridPane grid = new GridPane();
		
		
		grid.setAlignment(Pos.TOP_CENTER);
		
		
		grid.setStyle("-fx-background-color: azure;");
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(60,15,10,15));
		

		//Text enterCommand = new Text("Enter your command: ");
		//grid.add(enterCommand, 0, 2);

		final TextField command = new TextField();
		grid.add(command, 0, 2);
		
		

		//Text showResult = new Text("Your To-dos: ");
		//grid.add(showResult, 0, 1);

		final TextArea result = new TextArea();
		result.setWrapText(true);
		result.setPrefSize(470, 400);
		result.setEditable(false);
		
		grid.add(result, 0, 1);
		result.setText(list);

		final Text feedback = new Text();
		grid.add(feedback, 0, 3);

		final Text currentTime = new Text();
		grid.add(currentTime, 0, 0);
		//grid.add(imv, 0, 0);
        
		currentTime.setText(Time.getCurrentTime().toString() + " "+ Date.getCurrentDate().toString());
		
		
		

		Scene scene = new Scene(grid, Color.AZURE);
		
		stage.setScene(scene);
		stage.setMaxWidth(500);
		stage.setMinWidth(500);
		stage.setMaxHeight(600);
		stage.setMinHeight(600);
		
		stage.show();

		command.setOnAction(new EventHandler<ActionEvent>() 
				{
			public void handle(ActionEvent event) {
				logger.log(Level.INFO, "user press enter once");
				String cmd = command.getText();
				assert !cmd.equals("");
				command.clear();
				//result.setText(cmd);
				if(displayHelp == true) {
					result.setText(help);
					displayHelp = false;
				} else {
					result.setText(tasksToBeShown.toString());  // to be changed into Vector of Task
				}
				feedback.setText(logic.getLogic(cmd));
				//feedback should disappear after one second

				
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

	public void setHelp() {
		 this.displayHelp = true;
	}
	private String help() {
		String helpList = "";
		helpList += "HELP FOR USING TICKER\n";
		helpList += "-to add a task: add \"<task name>\" -st <start time> -sd <start date in dd/mm/yy format> "
				+ "-et <end time> -ed <end date in dd/mm/yy format.\n";
		helpList += "-to set a task to repeat, add the flag: -r\n";
		helpList += "-to set a priority for a task, add the flag: to be continued\n";
		helpList += "-to delete a task: delete <index of task>\n";
		helpList += "-to edit a task: to be continued\n";
		helpList += "-to sort the tasks according to time and date: list to be continued\n";
		helpList += "-to sort the tasks according to priority: list to be continued\n";
		helpList += "-to undo the last command: undo\n";
		helpList += "-to redo the last undo: redo\n";
		helpList += "-to mark a task as done: tick <index of task>\n";
		helpList += "-to mark a task as cannot be done: cmi <index of task>\n";
		//edit not done yet
		return helpList;

	}

	public void setList(String list) {
		this.list = list;
	}
	public void setList(Vector<Task> tasks) {
		this.tasksToBeShown = tasks;
	}


	public static void main(String[] args) {
		launch(args);
		ticker = new TickerUI();


	}
}
