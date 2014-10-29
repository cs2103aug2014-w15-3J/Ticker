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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
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
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class TickerUI extends Application {
	private static TickerUI ticker;
	private Logic logic;
	private String list;
	private Vector <Task> tasksToBeShown = new Vector<Task>();
	GridPane chart = new GridPane();
	private String help;
	private boolean displayHelp=false;

	double initialX, initialY;


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
		stage.initStyle(StageStyle.UNDECORATED);
		//stage = new Stage(StageStyle.UNDECORATED);
		//overall structure: root.add background pic, logo, display box, command box, feedback line

		Group root = new Group();

		//background pic
		Image background = new Image("ticker/ui/background.png", true);
		ImageView imv1 = new ImageView();
		imv1.setImage(background);
		//imv1.setFitWidth(500);
		imv1.setFitHeight(650);
		imv1.setFitWidth(490);
		imv1.setX(0);
		imv1.setY(0);
		imv1.setSmooth(true);
		imv1.setCache(true);
		root.getChildren().add(imv1);
		addDragListeners(imv1);

		//load the image of the logo
		Image logo = new Image("ticker/ui/logo2.png", true);
		ImageView imv2 = new ImageView();
		imv2.setImage(logo);
		imv2.setFitWidth(130);
		imv2.setPreserveRatio(true);
		imv2.setX(15);
		imv2.setY(25);
		imv2.setSmooth(true);
		imv2.setCache(true);
		root.getChildren().add(imv2);

		//command line
		final TextField command = new TextField();
		command.setPromptText("Enter your command here...");
		command.setPrefSize(450, 30);
		//command.setMaxWidth(440);
		command.setLayoutX(20);
		command.setLayoutY(580);
		root.getChildren().add(command);

		//display console

		ScrollPane sp = new ScrollPane();
		sp.setPrefSize(450, 460);
		sp.setLayoutX(20);
		sp.setLayoutY(110);
		sp.setContent(chart);
		sp.setOpacity(0.8);
		sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		sp.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
		;

		root.getChildren().add(sp); 


		//add the header for the chart




		//feedback area
		final Text feedback = new Text();
		feedback.setLayoutX(22);
		feedback.setLayoutY(628);
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

		//add the minimise and close button
		Image min = new Image("ticker/ui/minimise2.png", true);
		ImageView imv3 = new ImageView();
		imv3.setImage(min);
		imv3.setFitWidth(18);
		imv3.setPreserveRatio(true);
		imv3.setX(438);
		imv3.setY(15);
		imv3.setSmooth(true);
		imv3.setCache(true);
		root.getChildren().add(imv3);

		Image close = new Image("ticker/ui/close2.png", true);
		ImageView imv4 = new ImageView();
		imv4.setImage(close);
		imv4.setFitWidth(20);
		imv4.setPreserveRatio(true);
		imv4.setFitWidth(18);
		imv4.setX(464);
		imv4.setY(7);
		imv4.setSmooth(true);
		imv4.setCache(true);
		root.getChildren().add(imv4);

		//add in the mouseOn status of those two buttons
		Image min_ = new Image("ticker/ui/minimiseMouseOn.png", true);
		Image close_ = new Image("ticker/ui/closeMouseOn.png", true);

		//for the close button
		imv4.setOnMouseEntered(new EventHandler<MouseEvent>() {

			public void handle(MouseEvent evt) {
				imv4.setImage(close_);
			}
		});
		imv4.setOnMouseExited(new EventHandler<MouseEvent>() {

			public void handle(MouseEvent evt) {
				imv4.setImage(close);
			}
		});
		imv4.setOnMouseClicked(new EventHandler<MouseEvent>() {

			public void handle(MouseEvent evt) {
				System.exit(0);
				//stage.close();
			}
		});

		//for the minimise button
		imv3.setOnMouseEntered(new EventHandler<MouseEvent>() {

			public void handle(MouseEvent evt) {
				imv3.setY(12);
				imv3.setImage(min_);
			}
		});
		imv3.setOnMouseExited(new EventHandler<MouseEvent>() {

			public void handle(MouseEvent evt) {
				imv3.setY(15);
				imv3.setImage(min);
			}
		});
		imv3.setOnMouseClicked(new EventHandler<MouseEvent>() {

			public void handle(MouseEvent evt) {
				stage.setIconified(true);
			}
		});

		Scene scene = new Scene(root);

		stage.setScene(scene);
		stage.setMaxWidth(490);
		stage.setMinWidth(490);
		stage.setMaxHeight(650);
		stage.setMinHeight(650);
		//System.out.println(stage.getWidth());

		stage.show();
		displayTasks();


        //can scroll up and down and minimise the window using keyboard
		command.setOnKeyPressed(new EventHandler<KeyEvent>() 
				{
			
			public void handle(KeyEvent e) {
				
				KeyCode code = e.getCode();  
				if(code == KeyCode.PAGE_UP){  
					sp.setVvalue(sp.getVvalue()-0.1);
					e.consume();  
				}  
				else if(code==KeyCode.PAGE_DOWN) {
					sp.setVvalue(sp.getVvalue()+0.1);
					e.consume(); 
				}
				else if(code==KeyCode.ESCAPE) {
					stage.setIconified(true);
				}
			}
				});



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

				//feedback fades off after 5 seconds
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
		int widthIndex = 20;
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

			String SD, ED, ST, ET;

			SD = (sd==null)? "" : sd.toString();
			ED = (ed==null)? "" : ed.toString();
			ST = (st==null)? "" : st.toString();
			ET = (et==null)? "" : et.toString();




			if(sd==null && st==null && ed==null && et==null) {
				hb.getChildren().addAll(index, description);
			}
			else if (ed==null && et==null) {
				Label start = new Label("Start: " + ST + " " + SD);
				start.setMaxSize(widthTime, prefHeight);
				start.setAlignment(Pos.TOP_LEFT);
				hb.getChildren().addAll(index, description, start);
			}
			else if (sd==null && st==null) {
				Label end = new Label("End: " + ET + " " + ED);
				end.setMaxSize(widthTime, prefHeight);
				end.setAlignment(Pos.TOP_LEFT);
				hb.getChildren().addAll(index, description, end);
			}
			else {
				Label start = new Label("Start: " + ST + " " + SD);
				//start.setMaxSize(widthTime, prefHeight);
				start.setAlignment(Pos.TOP_LEFT);

				Label end = new Label("End: " + ET + " " + ED);
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


	private void addDragListeners(final ImageView node){

		node.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent me) {
				if(me.getButton()!=MouseButton.MIDDLE) {
					initialX = me.getSceneX();
					initialY = me.getSceneY();
				}
				else
				{
					node.getScene().getWindow().centerOnScreen();
					initialX = node.getScene().getWindow().getX();
					initialY = node.getScene().getWindow().getY();
				}

			}
		});

		node.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent me) {
				if(me.getButton()!=MouseButton.MIDDLE)
				{
					node.getScene().getWindow().setX( me.getScreenX() - initialX );
					node.getScene().getWindow().setY( me.getScreenY() - initialY);
				}
			}
		});
	}
}