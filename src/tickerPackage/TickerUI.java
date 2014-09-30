package tickerPackage;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import static javafx.geometry.HPos.RIGHT;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class TickerUI extends Application {
	/*
	@Override
	public void start(Stage primaryStage) {


		StackPane root = new StackPane();

		Scene scene = new Scene(root, 300, 500, Color.WHITE);

		primaryStage.setTitle("Ticker");


		GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(25, 25, 25, 25));



		Label inputLine = new Label("Enter your command: ");
		grid.add(inputLine, 0, 1);

		TextField inputTextField = new TextField();
		grid.add(inputTextField, 1, 1);

		Label outputLine = new Label("To-do list: ");
		grid.add(outputLine, 0, 2);

		TextArea outputTextArea = new TextArea();
		grid.add(outputTextArea, 1, 2);


		primaryStage.setScene(scene);
		primaryStage.show();			
	}
	public static void main(String[] args) {
		launch(args);
	}
}

class dummyTest {


	String dummyReturn( String input) {
		return "this will be shown.";
	}*/
	@Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Ticker");
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.TOP_LEFT);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10,10,10,10));

        /*Text scenetitle = new Text("Welcome");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(scenetitle, 0, 0, 2, 1);*/

        Label enterCommand = new Label("Enter your command: ");
        grid.add(enterCommand, 0, 1);

        TextField command = new TextField();
        grid.add(command, 1, 1);

        Label showResult = new Label("Your To-dos: ");
        grid.add(showResult, 0, 2);

        TextArea result = new TextArea();
        grid.add(result, 1, 2);

        /*Button btn = new Button("Sign in");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);
        grid.add(hbBtn, 1, 4);

        final Text actiontarget = new Text();
        grid.add(actiontarget, 0, 6);
        grid.setColumnSpan(actiontarget, 2);
        grid.setHalignment(actiontarget, RIGHT);
        actiontarget.setId("actiontarget");

        btn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                actiontarget.setFill(Color.FIREBRICK);
                actiontarget.setText("Sign in button pressed");
            }
        });*/

        Scene scene = new Scene(grid, 650, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}