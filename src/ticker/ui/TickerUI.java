package ticker.ui;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Random;
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
import javafx.scene.effect.DropShadow;
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
	//dependency on Logic
	private static TickerUI ticker;
	private Logic logic;

	private Vector <Task> tasksToBeShown = new Vector<Task>();
	private String commandList = getHelp();
	private boolean displayHelp = false;
	double initialX, initialY;

	Scene scene;
	Group root;
	//basic display
	Image background, logo, min, min_, close, close_, trivial, normal, impt;
	ImageView imv1, imv2, imv3, imv4;
	GridPane chart = new GridPane();
	TextField command;
	Text feedback;
	ScrollPane sp;
	//tabs
	int indexTabs = 7;       //tabs is the 7th children that root added
	Group tabs_todo, tabs_ticked, tabs_cmi;
	ImageView imv5, imv6, imv7, imv8;
	Image cmi_1, cmi_2, cmi_3, ticked_1, ticked_2, ticked_3, todo_1, todo_2, todo_3, bar;
	private int currentView = 0;          //??1 for todo_time(default), 2 for todo_priority, 3 for ticked, 4 for cmi, 5 for search
	private int nextView = 0;

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

	public void start(Stage stage) {
		stage.initStyle(StageStyle.UNDECORATED);
		root = new Group();

		//background pic
		background = new Image("ticker/ui/pic/background.png", true);
		imv1 = new ImageView();
		imv1.setImage(background);
		imv1.setFitHeight(650);
		imv1.setFitWidth(490);
		imv1.setX(0);
		imv1.setY(0);
		imv1.setSmooth(true);
		root.getChildren().add(imv1);
		addDragListeners(imv1);

		//logo
		logo = new Image("ticker/ui/pic/logo2.png", true);
		imv2 = new ImageView();
		imv2.setImage(logo);
		imv2.setFitWidth(130);
		imv2.setPreserveRatio(true);
		imv2.setX(15);
		imv2.setY(25);
		imv2.setSmooth(true);
		root.getChildren().add(imv2);

		//command line
		command = new TextField();
		command.setPromptText("Enter your command here...");
		command.setPrefSize(450, 30);
		command.setLayoutX(20);
		command.setLayoutY(580);
		root.getChildren().add(command);

		//display console
		sp = new ScrollPane();
		sp.setPrefSize(450, 450);
		sp.setLayoutX(20);
		sp.setLayoutY(120);
		sp.setContent(chart);
		sp.setOpacity(0.8);
		sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		sp.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
		root.getChildren().add(sp); 


		//add the header for the chart




		//feedback area
		feedback = new Text();
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
		min = new Image("ticker/ui/pic/minimise2.png", true);
		min_ = new Image("ticker/ui/pic/minimiseMouseOn.png", true);
		imv3 = new ImageView();
		imv3.setImage(min);
		imv3.setFitWidth(18);
		imv3.setPreserveRatio(true);
		imv3.setX(438);
		imv3.setY(15);
		imv3.setSmooth(true);
		imv3.setCache(true);
		root.getChildren().add(imv3);

		close = new Image("ticker/ui/pic/close2.png", true);
		close_ = new Image("ticker/ui/pic/closeMouseOn.png", true);
		imv4 = new ImageView();
		imv4.setImage(close);
		imv4.setFitWidth(20);
		imv4.setPreserveRatio(true);
		imv4.setFitWidth(18);
		imv4.setX(464);
		imv4.setY(7);
		imv4.setSmooth(true);
		imv4.setCache(true);
		root.getChildren().add(imv4);

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
			}
		});

		//for the minimise button
		imv3.setOnMouseEntered(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent evt) {
				imv3.setY(13);
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

		
		tabs_todo = new Group();
		tabs_cmi = new Group();
		tabs_ticked = new Group();

		DropShadow ds = new DropShadow();
		ds.setRadius(8.0);
		ds.setOffsetX(0);
		ds.setOffsetY(0);
		ds.setColor(Color.rgb(0, 0, 0, 0.6));

		//tab3 cmi
		cmi_1 = new Image("ticker/ui/pic/CMI_1.png", true);
		cmi_2 = new Image("ticker/ui/pic/CMI_2.png", true);
		cmi_3 = new Image("ticker/ui/pic/CMI_3.png", true);
		imv7 = new ImageView();
		imv7.setFitWidth(80);
		imv7.setPreserveRatio(true);
		imv7.setX(380);
		imv7.setY(95);
		imv7.setSmooth(true);
		imv7.setCache(true);
		imv7.setEffect(ds);

		//tab2 ticked
		ticked_1 = new Image("ticker/ui/pic/Ticked_1.png", true);
		ticked_2 = new Image("ticker/ui/pic/Ticked_2.png", true);
		ticked_3 = new Image("ticker/ui/pic/Ticked_3.png", true);
		imv6 = new ImageView();
		imv6.setFitWidth(80);
		imv6.setPreserveRatio(true);
		imv6.setX(310);
		imv6.setY(95);
		imv6.setSmooth(true);
		imv6.setCache(true);
		imv6.setEffect(ds);


		//tab1 To-do
		todo_1 = new Image("ticker/ui/pic/todo_1.png", true);
		todo_2 = new Image("ticker/ui/pic/todo_2.png", true);
		todo_3 = new Image("ticker/ui/pic/todo_3.png", true);
		imv8 = new ImageView();
		imv8.setFitWidth(80);
		imv8.setPreserveRatio(true);
		imv8.setX(240);
		imv8.setY(95);
		imv8.setSmooth(true);
		imv8.setCache(true);
		imv8.setEffect(ds);

		//imv7.setImage(cmi_2);
		//imv6.setImage(ticked_2);
		//imv8.setImage(todo_1);
		//tabs_todo.getChildren().addAll(imv7, imv6, imv8);
		root.getChildren().add(tabs_todo);  
		buildTabs(currentView);


		bar = new Image("ticker/ui/pic/bar.png", true);
		imv5 = new ImageView();
		imv5.setImage(bar);
		imv5.setFitWidth(450);
		imv5.setPreserveRatio(true);
		imv5.setX(20);
		imv5.setY(114);
		imv5.setSmooth(true);
		imv5.setCache(true);
		root.getChildren().add(imv5);


		//TODO set the content of help and design better looking help page
		//implement the help page
		TextArea help = new TextArea();
		help.setWrapText(true);
		help.setText(commandList);
		help.setVisible(false);
		help.setLayoutX(45);
		help.setLayoutY(75);
		help.setPrefSize(400, 500);
		root.getChildren().add(help);
		
		
		System.out.println(root.getChildren().size());

		trivial = new Image("ticker/ui/pic/trivial.png", true);
		normal = new Image("ticker/ui/pic/normal.png", true);
		impt = new Image("ticker/ui/pic/impt.png", true);

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


		command.setOnAction(new EventHandler<ActionEvent>() 
				{
			public void handle(ActionEvent event) {
				String cmd = command.getText();
				command.clear();
				feedback.setText(logic.getLogic(cmd));
				//any change in view should reflect here
				//nextView is ready
				if(nextView != currentView) {
					buildTabs(nextView);
				}

				if(displayHelp == true) {            //if command is "help"
					help.setVisible(true);
					FadeTransition ft = new FadeTransition(Duration.millis(250), help);               //fade in
					ft.setFromValue(0);
					ft.setToValue(1.0);
					ft.play();

					command.setOnKeyPressed(new EventHandler<KeyEvent>() 
							{
						public void handle(KeyEvent e) {
							KeyCode code = e.getCode();  
							if(code == KeyCode.ENTER){  
								FadeTransition ft = new FadeTransition(Duration.millis(250), help);    //fade out
								ft.setFromValue(1.0);
								ft.setToValue(0);
								ft.play();

								displayHelp = false;

							}  
							//repeat of codes here, try to figure out another way
							else if(code == KeyCode.PAGE_UP){  
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
				} 
				else {		
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

		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.setMaxWidth(490);
		stage.setMinWidth(490);
		stage.setMaxHeight(650);
		stage.setMinHeight(650);
		stage.show();
		displayTasks();

	}
	public static void main(String[] args) {
		launch(args);
		ticker = new TickerUI();
	}



	/*the following methods are for Logic to call*/
	public void setHelp() {
		this.displayHelp = true;
	}

	public void setList(Vector<Task> tasks) {
		this.tasksToBeShown = tasks;
	}

	public void setNextView(int next) {
		this.nextView = next-1;
	}
	/*--------------------------------------------*/

	private String getHelp() {
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
		return helpList;
	}

	private void displayTasks() {
		int prefHeight = 30;
		int maxHeight;
		int widthIndex = 18;
		int widthDes = 240;
		int widthTime = 140;
		int avgCharNum = 40;
		int additionalHeight = 14;

		//ystem.out.println(tasksToBeShown.size() + " tasks!");
		if(tasksToBeShown.size()==0) {
			return;
		} else {
			for(int i = 0; i < tasksToBeShown.size(); i++ ) {

				HBox hb = new HBox(10);
				hb.setPadding(new Insets(10, 15, 10, 5));
				hb.setAlignment(Pos.CENTER_LEFT);

				//index
				Label index = new Label(""+(i+1)+".");
				index.setPrefSize(widthIndex, prefHeight);
				index.setAlignment(Pos.CENTER_RIGHT);

				//priority
				ImageView priority = new ImageView();
				priority.setFitWidth(8);
				priority.setPreserveRatio(true);
				priority.setSmooth(true);
				priority.setCache(true);

				char p = tasksToBeShown.get(i).getPriority();
				if(p=='A') {
					priority.setImage(impt);
				}
				else if(p=='B') {
					priority.setImage(normal);
				}
				else if(p=='C') {
					priority.setImage(trivial);
				}

				//task description
				String newTask = tasksToBeShown.get(i).getDescription();
				int length = newTask.length();
				maxHeight = prefHeight;
				maxHeight += (length/avgCharNum)*additionalHeight;              //adjust the maxHeight accordingly
				Label description = new Label(newTask);
				description.setPrefSize(widthDes, maxHeight);
				description.setWrapText(true);
				description.setAlignment(Pos.CENTER_LEFT);

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
					hb.getChildren().addAll(index, priority, description);
				}
				else if (ed==null && et==null) {
					Label start = new Label("Start: " + ST + " " + SD);
					start.setMaxSize(widthTime, prefHeight);
					start.setAlignment(Pos.CENTER_LEFT);
					hb.getChildren().addAll(index, priority, description, start);
				}
				else if (sd==null && st==null) {
					Label end = new Label("End:  " + ET + " " + ED);
					end.setMaxSize(widthTime, prefHeight);
					end.setAlignment(Pos.CENTER_LEFT);
					hb.getChildren().addAll(index, priority, description, end);
				}
				else {
					Label start = new Label("Start: " + ST + " " + SD);

					Label end = new Label("End:  " + ET + " " + ED);

					VBox time = new VBox(5);
					time.getChildren().add(start);
					time.getChildren().add(end);
					time.setPrefSize(widthTime, prefHeight);
					time.setAlignment(Pos.CENTER_LEFT);
					hb.getChildren().addAll(index, priority, description, time);
				}
				chart.add(hb, 0, i);
			}
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


	private void buildTabs(int view) {
		if(view==0) {
			root.getChildren().remove(7);
			imv7.setImage(cmi_2);
			imv6.setImage(ticked_2);
			imv8.setImage(todo_1);
			tabs_todo.getChildren().addAll(imv7, imv6, imv8);
			root.getChildren().add(7, tabs_todo);  
			currentView = 1;
			
			imv8.setDisable(true);
			imv7.setDisable(false);
			imv6.setDisable(false);
			
			imv6.setOnMouseEntered(new EventHandler<MouseEvent>() {
				public void handle(MouseEvent evt) {
					imv6.setImage(ticked_3);
				}
			});
			imv6.setOnMouseExited(new EventHandler<MouseEvent>() {
				public void handle(MouseEvent evt) {
					imv6.setImage(ticked_2);
				}
			});
			imv6.setOnMouseClicked(new EventHandler<MouseEvent>() {
				public void handle(MouseEvent evt) {
					String autoCommand = "list ticked";
					feedback.setText(logic.getLogic(autoCommand));
					chart.getChildren().clear();
					displayTasks();
					buildTabs(2);
				}
			});

			imv7.setOnMouseEntered(new EventHandler<MouseEvent>() {
				public void handle(MouseEvent evt) {
					imv7.setImage(cmi_3);
				}
			});
			imv7.setOnMouseExited(new EventHandler<MouseEvent>() {
				public void handle(MouseEvent evt) {
					imv7.setImage(cmi_2);
				}
			});
			imv7.setOnMouseClicked(new EventHandler<MouseEvent>() {
				public void handle(MouseEvent evt) {
					String autoCommand = "list cmi";
					feedback.setText(logic.getLogic(autoCommand));
					chart.getChildren().clear();
					displayTasks();
					buildTabs(3);
				}
			});
			
		}
		else if(view==1) {
			root.getChildren().remove(7);
			imv7.setImage(cmi_2);
			imv6.setImage(ticked_2);
			imv8.setImage(todo_1);
			tabs_todo.getChildren().addAll(imv7, imv6, imv8);
			root.getChildren().add(7, tabs_todo);  
			currentView = 1;
			
			imv8.setDisable(true);
			imv7.setDisable(false);
			imv6.setDisable(false);
			
			imv6.setOnMouseEntered(new EventHandler<MouseEvent>() {
				public void handle(MouseEvent evt) {
					imv6.setImage(ticked_3);
				}
			});
			imv6.setOnMouseExited(new EventHandler<MouseEvent>() {
				public void handle(MouseEvent evt) {
					imv6.setImage(ticked_2);
				}
			});
			imv6.setOnMouseClicked(new EventHandler<MouseEvent>() {
				public void handle(MouseEvent evt) {
					String autoCommand = "list ticked";
					feedback.setText(logic.getLogic(autoCommand));
					chart.getChildren().clear();
					displayTasks();
					buildTabs(2);
				}
			});

			imv7.setOnMouseEntered(new EventHandler<MouseEvent>() {
				public void handle(MouseEvent evt) {
					imv7.setImage(cmi_3);
				}
			});
			imv7.setOnMouseExited(new EventHandler<MouseEvent>() {
				public void handle(MouseEvent evt) {
					imv7.setImage(cmi_2);
				}
			});
			imv7.setOnMouseClicked(new EventHandler<MouseEvent>() {
				public void handle(MouseEvent evt) {
					String autoCommand = "list cmi";
					feedback.setText(logic.getLogic(autoCommand));
					chart.getChildren().clear();
					displayTasks();
					buildTabs(3);
				}
			});
			
		}
		
		
		else if(view == 2) {
			root.getChildren().remove(7);
			imv7.setImage(cmi_2);
			imv6.setImage(ticked_1);
			imv8.setImage(todo_2);
			tabs_ticked.getChildren().addAll(imv7, imv8, imv6);
			root.getChildren().add(7, tabs_ticked);  
			currentView = 2;
		
			imv6.setDisable(true);
			imv7.setDisable(false);
			imv8.setDisable(false);
			
			imv8.setOnMouseEntered(new EventHandler<MouseEvent>() {
				public void handle(MouseEvent evt) {
					imv8.setImage(todo_3);
				}
			});
			imv8.setOnMouseExited(new EventHandler<MouseEvent>() {
				public void handle(MouseEvent evt) {
					imv8.setImage(todo_2);
				}
			});
			imv8.setOnMouseClicked(new EventHandler<MouseEvent>() {
				public void handle(MouseEvent evt) {
					String autoCommand = "list time";
					feedback.setText(logic.getLogic(autoCommand));
					chart.getChildren().clear();
					displayTasks();
					buildTabs(0);
				}
			});

			imv7.setOnMouseEntered(new EventHandler<MouseEvent>() {
				public void handle(MouseEvent evt) {
					imv7.setImage(cmi_3);
				}
			});
			imv7.setOnMouseExited(new EventHandler<MouseEvent>() {
				public void handle(MouseEvent evt) {
					imv7.setImage(cmi_2);
				}
			});
			imv7.setOnMouseClicked(new EventHandler<MouseEvent>() {
				public void handle(MouseEvent evt) {
					String autoCommand = "list cmi";
					feedback.setText(logic.getLogic(autoCommand));
					chart.getChildren().clear();
					displayTasks();
					buildTabs(3);
				}
			});

		}
		else if(view == 3) {
			root.getChildren().remove(7);
			imv7.setImage(cmi_1);
			imv6.setImage(ticked_2);
			imv8.setImage(todo_2);
			tabs_cmi.getChildren().addAll(imv8, imv6, imv7);
			root.getChildren().add(7, tabs_cmi);  
			
			imv7.setDisable(true);
			imv8.setDisable(false);
			imv6.setDisable(false);

			currentView = 3;
			
			imv6.setOnMouseEntered(new EventHandler<MouseEvent>() {
				public void handle(MouseEvent evt) {
					imv6.setImage(ticked_3);
				}
			});
			imv6.setOnMouseExited(new EventHandler<MouseEvent>() {
				public void handle(MouseEvent evt) {
					imv6.setImage(ticked_2);
				}
			});
			imv6.setOnMouseClicked(new EventHandler<MouseEvent>() {
				public void handle(MouseEvent evt) {
					String autoCommand = "list ticked";
					feedback.setText(logic.getLogic(autoCommand));
					chart.getChildren().clear();
					displayTasks();
					buildTabs(2);
				}
			});

			imv8.setOnMouseEntered(new EventHandler<MouseEvent>() {
				public void handle(MouseEvent evt) {
					imv8.setImage(todo_3);
				}
			});
			imv8.setOnMouseExited(new EventHandler<MouseEvent>() {
				public void handle(MouseEvent evt) {
					imv8.setImage(todo_2);
				}
			});
			imv8.setOnMouseClicked(new EventHandler<MouseEvent>() {
				public void handle(MouseEvent evt) {
					String autoCommand = "list time";
					feedback.setText(logic.getLogic(autoCommand));
					chart.getChildren().clear();
					displayTasks();
					buildTabs(0);
				}
			});
			
		}
		else if(view==4) {
			root.getChildren().remove(7);
			imv7.setImage(cmi_2);
			imv6.setImage(ticked_2);
			imv8.setImage(todo_2);
			tabs_todo.getChildren().addAll(imv7, imv6, imv8);
			root.getChildren().add(7, tabs_todo);  
			currentView = 4;
			
			imv8.setDisable(false);
			imv7.setDisable(false);
			imv6.setDisable(false);
			
			imv6.setOnMouseEntered(new EventHandler<MouseEvent>() {
				public void handle(MouseEvent evt) {
					imv6.setImage(ticked_3);
				}
			});
			imv6.setOnMouseExited(new EventHandler<MouseEvent>() {
				public void handle(MouseEvent evt) {
					imv6.setImage(ticked_2);
				}
			});
			imv6.setOnMouseClicked(new EventHandler<MouseEvent>() {
				public void handle(MouseEvent evt) {
					String autoCommand = "list ticked";
					feedback.setText(logic.getLogic(autoCommand));
					chart.getChildren().clear();
					displayTasks();
					buildTabs(2);
				}
			});

			imv7.setOnMouseEntered(new EventHandler<MouseEvent>() {
				public void handle(MouseEvent evt) {
					imv7.setImage(cmi_3);
				}
			});
			imv7.setOnMouseExited(new EventHandler<MouseEvent>() {
				public void handle(MouseEvent evt) {
					imv7.setImage(cmi_2);
				}
			});
			imv7.setOnMouseClicked(new EventHandler<MouseEvent>() {
				public void handle(MouseEvent evt) {
					String autoCommand = "list cmi";
					feedback.setText(logic.getLogic(autoCommand));
					chart.getChildren().clear();
					displayTasks();
					buildTabs(3);
				}
			});
			
			imv8.setOnMouseEntered(new EventHandler<MouseEvent>() {
				public void handle(MouseEvent evt) {
					imv6.setImage(todo_3);
				}
			});
			imv8.setOnMouseExited(new EventHandler<MouseEvent>() {
				public void handle(MouseEvent evt) {
					imv6.setImage(todo_2);
				}
			});
			imv8.setOnMouseClicked(new EventHandler<MouseEvent>() {
				public void handle(MouseEvent evt) {
					String autoCommand = "list time";
					feedback.setText(logic.getLogic(autoCommand));
					chart.getChildren().clear();
					displayTasks();
					buildTabs(0);
				}
			});
			
		}
	}

}


