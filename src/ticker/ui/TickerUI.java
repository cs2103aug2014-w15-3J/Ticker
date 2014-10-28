package ticker.ui;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Vector;
import java.util.logging.*;

import ticker.logic.Logic;
import ticker.common.*;
import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class TickerUI extends Application {
	private static TickerUI ticker;
	private Logic logic;
	private String list;
	private Vector <Task> tasksToBeShown = new Vector<Task>();
	GridPane chart = new GridPane();
	private String help;
	private boolean displayHelp=false;

	

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
		//fillInTasks();
	}


	public void start(Stage stage) {
		//overall structure: root.add background pic, logo, display box, command box, feedback line

		stage.setTitle("Ticker");
		Group root = new Group();

		//background pic
		Image background = new Image("ticker/ui/back.png", true);
		ImageView imv1 = new ImageView();
		imv1.setImage(background);
		imv1.setFitWidth(500);
		imv1.setPreserveRatio(true);
		imv1.setX(0);
		imv1.setY(0);
		imv1.setSmooth(true);
		imv1.setCache(true);
		root.getChildren().add(imv1);

		//load the image of the logo
		Image logo = new Image("ticker/ui/logo2.png", true);
		ImageView imv2 = new ImageView();
		imv2.setImage(logo);
		imv2.setFitWidth(130);
		imv2.setPreserveRatio(true);
		imv2.setX(15);
		imv2.setY(20);
		imv2.setSmooth(true);
		imv2.setCache(true);
		root.getChildren().add(imv2);

		//command line
		final TextField command = new TextField();
		command.setPromptText("Enter your command here...");
		command.setPrefSize(450, 30);
		//command.setMaxWidth(440);
		command.setLayoutX(15);
		command.setLayoutY(540);
		root.getChildren().add(command);

		//display console
		final TextArea result = new TextArea();
		//result.setLayoutX(15);
		//result.setLayoutY(80);
		result.setWrapText(true);
		result.setPrefSize(450, 450);
		result.setEditable(false);
		result.setOpacity(0.8);
		result.setText(list);
		//root.getChildren().add(result);
		
		//an alternative of the above display console
		//GridPane chart = new GridPane();
		
		ScrollPane sp = new ScrollPane();
		sp.setPrefSize(450, 450);
		sp.setLayoutX(15);
		sp.setLayoutY(80);
		sp.setContent(chart);
		sp.setOpacity(0.8);
		
		
		root.getChildren().add(sp); 
		
		
		//add the header for the chart
		
		
		

		//feedback area
		final Text feedback = new Text();
		feedback.setLayoutX(17);
		feedback.setLayoutY(590);
		feedback.setFill(Color.BISQUE);
		//feedback.setText("The command you entered is invalid");
		root.getChildren().add(feedback);

        //add the display of the current time
		/*Calendar c = Calendar.getInstance();
		GridPane clock = new GridPane();
		
		final Text currentHour = new Text();
		currentHour.setText(Integer.toString(Time.getCurrentTime().getHour()));
		currentHour.setFill(Color.WHITE);
		
		final Text currentMin = new Text();
		String min = "";
		if(c.get(Calendar.MINUTE) < 10) {
			min = "0" + c.get(Calendar.MINUTE);
		} else {
			min = Integer.toString(c.get(Calendar.MINUTE));
		}
		currentMin.setText(min);
		currentMin.setFill(Color.WHITE);
		
		final Text currentSec = new Text();
		String sec = "";
		if(c.get(Calendar.SECOND) < 10) {
			sec = "0" + c.get(Calendar.SECOND);
		} else {
			sec = Integer.toString(c.get(Calendar.SECOND));
		}
		currentSec.setText(sec);
		currentSec.setFill(Color.WHITE);
		
		final Text colon1 = new Text(":");
		colon1.setFill(Color.WHITE);
		
		final Text colon2 = new Text(":");
		colon2.setFill(Color.WHITE);
		
		clock.add(currentHour, 0, 0);
		clock.add(colon1, 1, 0);
		clock.add(currentMin, 2, 0);
		clock.add(colon2, 3, 0);
		clock.add(currentSec, 4, 0);
		clock.setLayoutX(380);
		clock.setLayoutY(40);
		
		

		final Timeline time = new Timeline();
		time.setCycleCount(Timeline.INDEFINITE);
		
		root.getChildren().add(clock);*/
		// redo?
		/*String dTime;
		function updateTime(): Void {
			var temp = DateTime{} 
			dTime = DateFormat.getTimeInstance(DateFormat.MEDIUM).format(temp.instant);}
		TimeLine {
			repeatCount: Timeline.INDEFINITE
			keyFrames: [
			            KeyFrame {
			            	time: 1s
			            	canSkip: true
			            	action: updateTime
			            }
		]	
		}.play();*/
		//String time;


		Scene scene = new Scene(root);

		stage.setScene(scene);
		stage.setMaxWidth(500);
		stage.setMinWidth(500);
		stage.setMaxHeight(650);
		stage.setMinHeight(650);

		stage.show();
		displayTasks();
		logger.log(Level.INFO, "the stage is set up");

		command.setOnAction(new EventHandler<ActionEvent>() 
				{
			public void handle(ActionEvent event) {
				logger.log(Level.INFO, "user press enter once");
				String cmd = command.getText();
				assert !cmd.equals("");
				command.clear();

				if(displayHelp == true) {
					//maybe use another way to display the help message?
					//result.setText(help);
					displayHelp = false;
				} 
				else {
					feedback.setText(logic.getLogic(cmd));        //this line is correct and will remain unchanged
					//find another way!!!!
					//result.setText(list);
					chart.getChildren().clear();
					displayTasks();
				}

				//feedback fades off after 2 seconds
				FadeTransition ft = new FadeTransition(Duration.millis(5000), feedback);
				ft.setFromValue(1.0);
				ft.setToValue(0);
				ft.play();
			}
				});

	}


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

	//public void setList(String list) {
	//	this.list = list;
	//}
	public void setList(Vector<Task> tasks) {
		this.tasksToBeShown = tasks;
	}


	public static void main(String[] args) {
		launch(args);
		ticker = new TickerUI();

	}
	
	//a fake task list used for testing correctness of display
	public void fillInTasks() {
		Task task1 = new Task("do homework", new Date(2014,11,13), new Time(6, 0), null, null, 'A', false);
		Task task2 = new Task("dfjdksljdklfjlsdfjlsdjflsdjflsjfljdlfjdgjggfdfsfkfdjkjfkjkjskfjkjf3ijrfksfjksfjlsfjv nsnsd,ndsffdfdfhkjhshfkhfjhfkshfksfhksfksn", new Date(2014,11,13), new Time(6, 0), new Date(2014,11,13), new Time(7, 0), 'A', false);
		Task task3 = new Task("blah blah", null, null, new Date(2014,11,16), new Time(18, 3), 'C', false);
		Task task4 = new Task("stats tutorial", null, null, null, null, 'A', false);
		Task task5 = new Task("blah blah", new Date(2014,11,16), new Time(18, 3), null, null, 'B', false);
		tasksToBeShown.add(task1);
		tasksToBeShown.add(task2);
		tasksToBeShown.add(task3);
		tasksToBeShown.add(task4);
		tasksToBeShown.add(task5);
	}
	
	public void displayTasks() {
		int prefHeight = 30;
		int maxHeight = 100;
		int widthIndex = 15;
		int widthDes = 250;
		int widthTime = 140;
		for(int i = 0; i < tasksToBeShown.size(); i++ ) {
			
			HBox hb = new HBox(10);
			hb.setPadding(new Insets(15, 10, 5, 10));

			//index
			Label index = new Label(""+(i+1)+".");
			index.setPrefSize(widthIndex, prefHeight);
			index.setAlignment(Pos.TOP_LEFT);
			
			//task description
			Label description = new Label(tasksToBeShown.get(i).getDescription());
			description.setMaxSize(widthDes, maxHeight);
			description.setPrefSize(widthDes, prefHeight);
			description.setAlignment(Pos.TOP_LEFT);
			description.setWrapText(true);

			Date sd = tasksToBeShown.get(i).getStartDate();
			Date ed = tasksToBeShown.get(i).getEndDate();
			Time st = tasksToBeShown.get(i).getStartTime();
			Time et = tasksToBeShown.get(i).getEndTime();
			
			
			if(sd==null && st==null && ed==null && et==null) {
				hb.getChildren().addAll(index, description);
			}
			else if (ed==null && et==null) {
				Label start = new Label("Start: " + st.toString() + " " + sd.toString());
				start.setMaxSize(widthTime, prefHeight);
				start.setAlignment(Pos.TOP_LEFT);
				hb.getChildren().addAll(index, description, start);
			}
			else if (sd==null && st==null) {
			    Label end = new Label("End: " + et.toString() + " " + ed.toString());
			    end.setMaxSize(widthTime, prefHeight);
				end.setAlignment(Pos.TOP_LEFT);
				hb.getChildren().addAll(index, description, end);
			}
			else {
				Label start = new Label("Start: " + st.toString() + " " + sd.toString());
				//start.setMaxSize(widthTime, prefHeight);
				start.setAlignment(Pos.TOP_LEFT);
				
				Label end = new Label("End: " + et.toString() + " " + ed.toString());
				//end.setMaxSize(widthTime, prefHeight);
				end.setAlignment(Pos.TOP_LEFT);
				
				VBox time = new VBox(5);
				time.getChildren().add(start);
				time.getChildren().add(end);
				time.setPrefSize(widthTime, prefHeight);
				hb.getChildren().addAll(index, description, time);

			}
			
			chart.add(hb, 0, i);
		}
		
	}
}
