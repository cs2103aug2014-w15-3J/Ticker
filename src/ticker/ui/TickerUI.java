package ticker.ui;

//@author A0115288B

import java.util.Calendar;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import ticker.common.Date;
import ticker.common.Task;
import ticker.common.Time;
import ticker.logic.HelpManager;
import ticker.logic.Logic;

public class TickerUI extends Application {

	private static TickerUI ticker;
	private Logic logic;

	private Vector<Task> tasksToBeShown = new Vector<Task>();

	private boolean displayHelp = false;
	private boolean isSearchResult = false;
	double initialX, initialY;

	Scene scene;
	Group root;
	// basic display
	Image min, min_, close, close_, trivial, normal, impt,
	expired;
	ImageView background, logo, min_button, close_button, imv9;
	VBox chart = new VBox();
	ImageView help;
	Image helpPage;
	TextArea helpContent;

	Font content = Font.loadFont(getClass()
			.getResourceAsStream("/ticker/ui/fonts/ARLRDBD_0.TTF"), 13);
	Font heading = new Font("Britannic Bold", 14);

	private TextField command;
	Label feedback;
	ScrollPane sp;
	// tabs
	private static final int INDEX_TABS = 7;                 // tabs is the 7th children that root added
	Group tabs_todo, tabs_todo_p, tabs_ticked, tabs_kiv, tabs_search;
	ImageView bar, tickedTab, kivTab, todoTab;
	Image kiv_1, kiv_2, kiv_3, ticked_1, ticked_2, ticked_3, todo_1, todo_2,
	todo_3;

	private static final int KEY_SORTED_TIME = 1;
	private static final int KEY_SORTED_PRIORITY = 2;
	private static final int KEY_TICKED = 3;
	private static final int KEY_KIV = 4;
	private static final int KEY_SEARCH = 5;
	private static final int KEY_SEARCH_FREE = 6;

	private static int currentView = KEY_SORTED_TIME;
	private int nextView = KEY_SORTED_TIME;

	private static final String[] months = { "Jan", "Feb", "Mar", "Apr",
		"May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };
	private static final String[] dayOfWeek = { "Sunday", "Monday", "Tuesday",
		"Wednesday", "Thursday", "Friday", "Saturday" };

	Calendar c;

	final HBox time = new HBox(1);
	VBox clock = new VBox();

	final Label currentHour = new Label();
	final Label currentMin = new Label();
	final Label currentSec = new Label();
	final Label colon1 = new Label(":");
	final Label colon2 = new Label(":");

	private FadeTransition fadeOut; // = new FadeTransition(Duration.millis(5000), feedback);

	final Label date_string = new Label();

	final HelpManager helpManager = new HelpManager();

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

	public void start(final Stage stage) {
		stage.initStyle(StageStyle.UNDECORATED);
		root = new Group();

		// background pic
		background = new ImageView();
		background.setImage(new Image("ticker/ui/pic/background.png", true));
		background.setFitHeight(650);
		background.setFitWidth(490);
		background.setX(0);
		background.setY(0);
		background.setSmooth(true);
		addDragListeners(background);
		root.getChildren().add(background);


		// logo
		logo = new ImageView(new Image("ticker/ui/pic/logo2.png", true));
		logo.setFitWidth(130);
		logo.setPreserveRatio(true);
		logo.setX(15);
		logo.setY(25);
		logo.setSmooth(true);
		root.getChildren().add(logo);

		// command line
		command = new TextField();
		command.setPromptText("Enter your command here...");
		command.setPrefSize(450, 30);
		command.setLayoutX(20);
		command.setLayoutY(580);
		root.getChildren().add(command);
		setCommandBoxActions(stage);

		// TODO check this part
		// display console
		sp = new ScrollPane();
		sp.setPrefSize(450, 450);
		sp.setLayoutX(20);
		sp.setLayoutY(120);
		sp.setContent(chart);
		sp.setOpacity(0.8);
		sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		sp.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
		root.getChildren().add(sp);


		// feedback area
		feedback = new Label();
		feedback.setLayoutX(22);
		feedback.setLayoutY(617);
		feedback.setPrefWidth(450);
		feedback.setTextFill(Color.BISQUE);
		root.getChildren().add(feedback);

		// add the minimise button
		min = new Image("ticker/ui/pic/minimise2.png", true);
		min_ = new Image("ticker/ui/pic/minimiseMouseOn.png", true);
		min_button = new ImageView();
		min_button.setImage(min);
		min_button.setFitWidth(18);
		min_button.setPreserveRatio(true);
		min_button.setX(438);
		min_button.setY(15);
		min_button.setSmooth(true);
		min_button.setCache(true);
		root.getChildren().add(min_button);
		setActionMinButton(stage);

		// add the close button
		close = new Image("ticker/ui/pic/close2.png", true);
		close_ = new Image("ticker/ui/pic/closeMouseOn.png", true);
		close_button = new ImageView();
		close_button.setImage(close);
		close_button.setFitWidth(20);
		close_button.setPreserveRatio(true);
		close_button.setFitWidth(18);
		close_button.setX(464);
		close_button.setY(7);
		close_button.setSmooth(true);
		close_button.setCache(true);
		root.getChildren().add(close_button);
		setActionCloseButton(stage);


		tabs_todo = new Group();
		tabs_todo_p = new Group();
		tabs_kiv = new Group();
		tabs_ticked = new Group();
		tabs_search = new Group();

		DropShadow ds = new DropShadow();
		ds.setRadius(8.0);
		ds.setOffsetX(0);
		ds.setOffsetY(0);
		ds.setColor(Color.rgb(0, 0, 0, 0.6));

		// tab3 cmi
		kiv_1 = new Image("ticker/ui/pic/KIV_1.png", true);
		kiv_2 = new Image("ticker/ui/pic/KIV_2.png", true);
		kiv_3 = new Image("ticker/ui/pic/KIV_3.png", true);
		kivTab = new ImageView();
		kivTab.setFitWidth(80);
		kivTab.setPreserveRatio(true);
		kivTab.setX(380);
		kivTab.setY(95);
		kivTab.setSmooth(true);
		kivTab.setCache(true);
		kivTab.setEffect(ds);

		// tab2 ticked
		ticked_1 = new Image("ticker/ui/pic/Ticked_1.png", true);
		ticked_2 = new Image("ticker/ui/pic/Ticked_2.png", true);
		ticked_3 = new Image("ticker/ui/pic/Ticked_3.png", true);
		tickedTab = new ImageView();
		tickedTab.setFitWidth(80);
		tickedTab.setPreserveRatio(true);
		tickedTab.setX(310);
		tickedTab.setY(95);
		tickedTab.setSmooth(true);
		tickedTab.setCache(true);
		tickedTab.setEffect(ds);

		// tab1 To-do
		todo_1 = new Image("ticker/ui/pic/todo_1.png", true);
		todo_2 = new Image("ticker/ui/pic/todo_2.png", true);
		todo_3 = new Image("ticker/ui/pic/todo_3.png", true);
		todoTab = new ImageView();
		todoTab.setFitWidth(80);
		todoTab.setPreserveRatio(true);
		todoTab.setX(240);
		todoTab.setY(95);
		todoTab.setSmooth(true);
		todoTab.setCache(true);
		todoTab.setEffect(ds);

		root.getChildren().add(tabs_todo);
		buildTabs(currentView);

		// the bar below all tabs
		bar = new ImageView(new Image("ticker/ui/pic/bar.png", true));
		bar.setFitWidth(450);
		bar.setPreserveRatio(true);
		bar.setX(20);
		bar.setY(114);
		bar.setSmooth(true);
		bar.setCache(true);
		root.getChildren().add(bar);

		// implement the help page
		help = new ImageView();
		help.setImage(new Image("ticker/ui/pic/help_content.png", true));
		help.setVisible(false);
		help.setLayoutX(45);
		help.setLayoutY(75);
		help.setFitWidth(400);
		help.setPreserveRatio(true);
		root.getChildren().add(help);

		clock = buildClock();
		clock.setLayoutX(380);
		clock.setLayoutY(40);
		root.getChildren().add(clock);

		displayTime();
		final Timeline time = new Timeline();
		time.setCycleCount(Timeline.INDEFINITE);
		time.getKeyFrames().add(
				new KeyFrame(Duration.millis(1000),
						new EventHandler<ActionEvent>() {
					public void handle(ActionEvent evt) {
						displayTime();
					}
				}));
		time.play();
		
		fadeOut = new FadeTransition(Duration.millis(5000), feedback);
		fadeOut.setFromValue(1.0);
		fadeOut.setToValue(0);

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

	/* the following methods are for Logic to call */
	public void setHelp() {
		this.displayHelp = true;
	}

	public void setList(Vector<Task> tasks) {
		this.tasksToBeShown = tasks;
	}

	public void setNextView(int next) {
		this.nextView = next;
	}

	/*--------------------------------------------*/
	/**
	 * This method sets the action of the minimise button when mouse enters, exits and clicks
	 *
	 * @param stage      the current stage that is being shown
	 */
	private void setActionMinButton(Stage stage) {
		min_button.setOnMouseEntered(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent evt) {
				min_button.setY(13);
				min_button.setImage(min_);
			}
		});
		min_button.setOnMouseExited(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent evt) {
				min_button.setY(15);
				min_button.setImage(min);
			}
		});
		min_button.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent evt) {
				stage.setIconified(true);
			}
		});
	}

	/**
	 * This method sets the action of the close button when mouse enters, exits and clicks
	 *
	 * @param stage      the current stage that is being shown
	 */
	private void setActionCloseButton(Stage stage) {
		close_button.setOnMouseEntered(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent evt) {
				close_button.setImage(close_);
			}
		});
		close_button.setOnMouseExited(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent evt) {
				close_button.setImage(close);
			}
		});
		close_button.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent evt) {
				stage.close();
			}
		});
	}

	/**
	 * This method sets the action of the command box, including live feedback, send command to Logic and hotkeys
	 *
	 * @param stage      the current stage that is being shown
	 */
	private void setCommandBoxActions(Stage stage) {
		//listen to the content of the command box and show suggested format of input
		command.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue observable, String oldValue,
					String newValue) {
				fadeOut.stop();
				feedback.setOpacity(1);
				feedback.setText(helpManager.getHelp(newValue));

			}
		});

		// can scroll up and down and minimise the window using keyboard
		command.setOnKeyPressed(new EventHandler<KeyEvent>() {
			public void handle(KeyEvent e) {
				KeyCode code = e.getCode();
				if (code == KeyCode.PAGE_UP) {
					sp.setVvalue(sp.getVvalue() - 0.1);
					e.consume();
				} else if (code == KeyCode.PAGE_DOWN) {
					sp.setVvalue(sp.getVvalue() + 0.1);
					e.consume();
				} else if (code == KeyCode.ESCAPE) {
					stage.setIconified(true);
				} else if (code == KeyCode.TAB) {
					if(currentView==KEY_SORTED_TIME || currentView == KEY_SORTED_PRIORITY) {
						String autoCommand = "list ticked";
						feedback.setText(logic.getLogic(autoCommand));
						fadeOut.play();

						chart.getChildren().clear();
						displayTasks();
						buildTabs(KEY_TICKED);
						currentView = KEY_TICKED; 
					} else if(currentView == KEY_TICKED) {
						String autoCommand = "list kiv";
						feedback.setText(logic.getLogic(autoCommand));
						fadeOut.play();

						chart.getChildren().clear();
						displayTasks();
						buildTabs(KEY_KIV);
						currentView = KEY_KIV; 
					} else if(currentView == KEY_KIV) {
						String autoCommand = "list time";
						feedback.setText(logic.getLogic(autoCommand));
						fadeOut.play();

						chart.getChildren().clear();
						displayTasks();
						buildTabs(KEY_SORTED_TIME);
						currentView = KEY_SORTED_TIME;
					}

				}
			}
		});

		command.setOnAction(new EventHandler<ActionEvent>() {      // press Enter
			public void handle(ActionEvent event) {
				String cmd = command.getText();
				command.clear();
				feedback.setText(logic.getLogic(cmd));

				if (nextView != currentView) {
					buildTabs(nextView);
				}

				if (displayHelp == true) { // if command is "help"
					help.setVisible(true);
					FadeTransition ft = new FadeTransition(
							Duration.millis(250), help); // fade in
					ft.setFromValue(0);
					ft.setToValue(0.8);
					ft.play();

					command.setOnKeyPressed(new EventHandler<KeyEvent>() {
						public void handle(KeyEvent e) {
							KeyCode code = e.getCode();
							if (code == KeyCode.ENTER) {
								FadeTransition ft = new FadeTransition(Duration
										.millis(250), help); // fade out
								ft.setFromValue(0.8);
								ft.setToValue(0);
								ft.play();
								help.setVisible(false);
								displayHelp = false;

							} else if (code == KeyCode.PAGE_UP) {
								sp.setVvalue(sp.getVvalue() - 0.1);
								e.consume();
							} else if (code == KeyCode.PAGE_DOWN) {
								sp.setVvalue(sp.getVvalue() + 0.1);
								e.consume();
							} else if (code == KeyCode.ESCAPE) {
								stage.setIconified(true);
							}

						}

					});
				} else {
					chart.getChildren().clear();
					displayTasks();
				}

				// feedback fades off after 5 seconds
				fadeOut.play();

			}
		});
	}
	/**
	 * This method builds the structure of the current tie and date display
	 *
	 * @return clock        a frame for displayTime()
	 */
	private VBox buildClock() {
		VBox clock = new VBox();
		colon1.setTextFill(Color.WHITE);
		colon2.setTextFill(Color.WHITE);
		time.getChildren().addAll(currentHour, colon1, currentMin, colon2,
				currentSec);
		date_string.setTextFill(Color.WHITE);
		clock.getChildren().addAll(time, date_string);
		return clock;
	}

	private void displayTime() {
		c = Calendar.getInstance();

		currentHour.setText(Integer.toString(c.get(Calendar.HOUR_OF_DAY)));
		currentHour.setTextFill(Color.WHITE);

		String minute = "";
		if (c.get(Calendar.MINUTE) < 10) {
			minute = "0" + c.get(Calendar.MINUTE);
		} else {
			minute = Integer.toString(c.get(Calendar.MINUTE));
		}
		currentMin.setText(minute);
		currentMin.setTextFill(Color.WHITE);

		String sec = "";
		if (c.get(Calendar.SECOND) < 10) {
			sec = "0" + c.get(Calendar.SECOND);
		} else {
			sec = Integer.toString(c.get(Calendar.SECOND));
		}
		currentSec.setText(sec);
		currentSec.setTextFill(Color.WHITE);

		date_string.setText(c.get(Calendar.DATE) + " "
				+ months[c.get(Calendar.MONTH)] + ", " + c.get(Calendar.YEAR));

	}

	private void displayTasks() {
		int prefHeight = 30;
		int maxHeight;
		int widthIndex = 18;
		int widthDes = 240;
		int widthTime = 140;
		int avgCharNum = 35;
		int additionalHeight = 14;
		int d = 0; // a way to correct the numbering when listing out search
		// results

		trivial = new Image("ticker/ui/pic/trivial.png", true);
		normal = new Image("ticker/ui/pic/normal.png", true);
		impt = new Image("ticker/ui/pic/impt.png", true);

		for (int i = 0; i < tasksToBeShown.size(); i++) {

			HBox hb = new HBox(10);
			hb.setPadding(new Insets(10, 15, 10, 5));
			hb.setAlignment(Pos.CENTER_LEFT);

			// index
			Label index = new Label("" + (i + 1 - d) + ".");
			index.setPrefSize(widthIndex, prefHeight);
			index.setAlignment(Pos.CENTER_RIGHT);
			// index.setFont(content);

			// priority
			ImageView priority = new ImageView();
			priority.setFitWidth(8);
			priority.setPreserveRatio(true);
			priority.setSmooth(true);
			priority.setCache(true);

			char p = tasksToBeShown.get(i).getPriority();
			if (p == 'A') {
				priority.setImage(impt);
			} else if (p == 'B') {
				priority.setImage(normal);
			} else if (p == 'C') {
				priority.setImage(trivial);
			}

			// task description
			String newTask = tasksToBeShown.get(i).getDescription();

			int length = newTask.length();
			maxHeight = prefHeight;
			maxHeight += (length / avgCharNum) * additionalHeight; // adjust the
			// maxHeight
			// accordingly
			Label description = new Label(newTask);
			description.setPrefSize(widthDes, maxHeight);
			description.setWrapText(true);
			description.setAlignment(Pos.CENTER_LEFT);
			description.setFont(content);

			Date sd = tasksToBeShown.get(i).getStartDate();
			Date ed = tasksToBeShown.get(i).getEndDate();
			Time st = tasksToBeShown.get(i).getStartTime();
			Time et = tasksToBeShown.get(i).getEndTime();

			String SD, ED, ST, ET;

			SD = (sd == null) ? "" : sd.toString();
			ED = (ed == null) ? "" : ed.toString();
			ST = (st == null) ? "" : st.toString();
			ET = (et == null) ? "" : et.toString();

			Label start = new Label();
			Label end = new Label();

			if (tasksToBeShown.get(i).getIsExpired()) { // mark tasks as red to
				// show expired
				index.setTextFill(Color.RED);
				description.setTextFill(Color.RED);
				start.setTextFill(Color.RED);
				end.setTextFill(Color.RED);
			}

			if(newTask.equals("\\***FREE***\\")) {     // if this list is for search empty slot
				description.setText("Empty slot");
				description.setTextFill(Color.DARKGREEN);
				VBox time = new VBox(5);
				start.setText("Start: " + ST + ", " + SD);
				end.setText("End: " + ET + ", " + ED);
				time.getChildren().add(start);
				time.getChildren().add(end);
				time.setPrefSize(widthTime, prefHeight);
				time.setAlignment(Pos.CENTER_LEFT);
				hb.getChildren().addAll(index, priority, description, time);
				chart.getChildren().add(hb);		
			}

			else if ((newTask.equals("\\***TICKED***\\"))) { // this list is search
				// result
				isSearchResult = true;

				Label todo = new Label(
						"   Search results from the To-do section:");
				todo.setPrefHeight(35);
				todo.setAlignment(Pos.BOTTOM_LEFT);
				todo.setFont(heading);
				chart.getChildren().add(0, todo);

				d++;

				Label ticked = new Label(
						"   Search results from the Ticked section:");
				ticked.setPrefHeight(35);
				ticked.setAlignment(Pos.BOTTOM_LEFT);
				ticked.setFont(heading);
				chart.getChildren().add(ticked);

			} else if (newTask.equals("\\***KIV***\\")) {
				d++;
				Label kiv = new Label("   Search results from the KIV section:");
				kiv.setPrefHeight(35);
				kiv.setAlignment(Pos.BOTTOM_LEFT);
				kiv.setFont(heading);
				chart.getChildren().add(kiv);
			}

			else if (tasksToBeShown.get(i).getRepeat()) { // if repeated task
				VBox repeat = new VBox();
				Label day = new Label();
				Label time = new Label();

				if (ED == "") {
					day.setText("every " + dayOfWeek[Date.dayOfWeek(sd)]);
				} else {
					day.setText("every " + dayOfWeek[Date.dayOfWeek(ed)]);
				}
				if (ST == "") {
					time.setText(ET);
				} else if (ET == "") {
					time.setText(ST);
				} else {
					time.setText(ST + " to " + ET);
				}
				repeat.getChildren().addAll(day, time);
				hb.getChildren().addAll(index, priority, description, repeat);
				chart.getChildren().add(hb);

			} else {
				if (sd == null && st == null && ed == null && et == null) {
					hb.getChildren().addAll(index, priority, description);
					chart.getChildren().add(hb);
				} else if (ed == null && et == null) {
					description.setFont(content);
					start.setMaxSize(widthTime, prefHeight);
					start.setAlignment(Pos.CENTER_LEFT);
					start.setText(ST + ", " + SD + " onwards");
					hb.getChildren()
					.addAll(index, priority, description, start);
					chart.getChildren().add(hb);
				} else if (sd == null && st == null) {
					end.setMaxSize(widthTime, prefHeight);
					end.setAlignment(Pos.CENTER_LEFT);
					end.setText("By " + ET + ", " + ED);
					hb.getChildren().addAll(index, priority, description, end);
					chart.getChildren().add(hb);
				} else {
					VBox time = new VBox(5);
					start.setText("Start: " + ST + ", " + SD);
					end.setText("End: " + ET + ", " + ED);
					time.getChildren().add(start);
					time.getChildren().add(end);
					time.setPrefSize(widthTime, prefHeight);
					time.setAlignment(Pos.CENTER_LEFT);
					hb.getChildren().addAll(index, priority, description, time);
					chart.getChildren().add(hb);
				}

			}
			if (isSearchResult == true) {
				Label numResult = new Label("   There are "
						+ (tasksToBeShown.size() - 2) + " result(s) found");
				numResult.setPrefHeight(40);
				numResult.setAlignment(Pos.BOTTOM_LEFT);
				numResult.setFont(heading);
				chart.getChildren().add(0, numResult);
			}
			isSearchResult = false;
		}
	}

	private void addDragListeners(final ImageView node) {

		node.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent me) {
				if (me.getButton() != MouseButton.MIDDLE) {
					initialX = me.getSceneX();
					initialY = me.getSceneY();
				} else {
					node.getScene().getWindow().centerOnScreen();
					initialX = node.getScene().getWindow().getX();
					initialY = node.getScene().getWindow().getY();
				}

			}
		});

		node.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent me) {
				if (me.getButton() != MouseButton.MIDDLE) {
					node.getScene().getWindow()
					.setX(me.getScreenX() - initialX);
					node.getScene().getWindow()
					.setY(me.getScreenY() - initialY);
				}
			}
		});
	}

	
	private void setTodoTab() {
		todoTab.setOnMouseEntered(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent evt) {
				todoTab.setImage(todo_3);
			}
		});
		todoTab.setOnMouseExited(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent evt) {
				todoTab.setImage(todo_2);
			}
		});
		todoTab.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent evt) {
				String autoCommand = "list time";
				feedback.setText(logic.getLogic(autoCommand));
				fadeOut.play();

				chart.getChildren().clear();
				displayTasks();
				buildTabs(KEY_SORTED_TIME);
			}
		});
	}
	private void setTickedTab() {
		tickedTab.setOnMouseEntered(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent evt) {
				tickedTab.setImage(ticked_3);
			}
		});
		tickedTab.setOnMouseExited(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent evt) {
				tickedTab.setImage(ticked_2);
			}
		});
		tickedTab.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent evt) {
				String autoCommand = "list ticked";
				feedback.setText(logic.getLogic(autoCommand));
				fadeOut.play();

				chart.getChildren().clear();
				displayTasks();
				buildTabs(KEY_TICKED);
			}
		});
	}
	private void setKivTab() {
		kivTab.setOnMouseEntered(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent evt) {
				kivTab.setImage(kiv_3);
			}
		});
		kivTab.setOnMouseExited(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent evt) {
				kivTab.setImage(kiv_2);
			}
		});
		kivTab.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent evt) {
				String autoCommand = "list kiv";
				feedback.setText(logic.getLogic(autoCommand));
				fadeOut.play();

				chart.getChildren().clear();
				displayTasks();
				buildTabs(KEY_KIV);
			}
		});
	}
	private void buildTabs(int view) {
		if (view == KEY_SORTED_TIME || view == KEY_SORTED_PRIORITY) { // 1 OR 2
			currentView = view;
			root.getChildren().remove(INDEX_TABS);
			kivTab.setImage(kiv_2);
			tickedTab.setImage(ticked_2);
			todoTab.setImage(todo_1);
			tabs_todo.getChildren().addAll(kivTab, tickedTab, todoTab);
			root.getChildren().add(INDEX_TABS, tabs_todo);
			

			todoTab.setDisable(true);
			kivTab.setDisable(false);
			tickedTab.setDisable(false);

			setTickedTab();
			setKivTab();

		} else if (view == KEY_TICKED) { // 3
			currentView = KEY_TICKED;
			root.getChildren().remove(INDEX_TABS);
			kivTab.setImage(kiv_2);
			tickedTab.setImage(ticked_1);
			todoTab.setImage(todo_2);
			tabs_ticked.getChildren().addAll(kivTab, todoTab, tickedTab);
			root.getChildren().add(INDEX_TABS, tabs_ticked);
			
			tickedTab.setDisable(true);
			kivTab.setDisable(false);
			todoTab.setDisable(false);

			setTodoTab();
			setKivTab();

		} else if (view == KEY_KIV) { // 4
			currentView = KEY_KIV;
			root.getChildren().remove(INDEX_TABS);
			kivTab.setImage(kiv_1);
			tickedTab.setImage(ticked_2);
			todoTab.setImage(todo_2);
			tabs_kiv.getChildren().addAll(todoTab, tickedTab, kivTab);
			root.getChildren().add(INDEX_TABS, tabs_kiv);

			kivTab.setDisable(true);
			todoTab.setDisable(false);
			tickedTab.setDisable(false);

			setTodoTab();
			setTickedTab();
			
		} else if (view == KEY_SEARCH || view == KEY_SEARCH_FREE) {    // 5 or 6
			currentView = view;
			root.getChildren().remove(INDEX_TABS);
			kivTab.setImage(kiv_2);
			tickedTab.setImage(ticked_2);
			todoTab.setImage(todo_2);
			tabs_search.getChildren().addAll(kivTab, tickedTab, todoTab);
			root.getChildren().add(INDEX_TABS, tabs_search);
			
			todoTab.setDisable(false);
			kivTab.setDisable(false);
			tickedTab.setDisable(false);

			setTodoTab();
			setTickedTab();
			setKivTab();
		} else {
			logger.log(Level.WARNING, "No such view mode!");
		}
	}

}
