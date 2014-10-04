package tickerPackage;

import tickerPackage.Logic;
import tickerPackage.Parser;
import tickerPackage.Ticker;
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
	
	private Logic logic;
	class TickerUI() {
		ticker = this;
		logic = new Logic();
	}


	public void start(Stage primaryStage) {
        primaryStage.setTitle("Ticker");
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.TOP_LEFT);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10,10,10,10));

        Text enterCommand = new Text("Enter your command: ");
        grid.add(enterCommand, 0, 1);

        TextField command = new TextField();
        grid.add(command, 1, 1);
        
        Text showResult = new Text("Your To-dos: ");
        grid.add(showResult, 0, 2);

        TextArea result = new TextArea();
        result.setWrapText(true);
        grid.add(result, 1, 2);

        primaryStage.show();


        Scene scene = new Scene(grid, 650, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
       
        command.setOnAction(new EventHandler<ActionEvent>() 
        		{
        	public void handle(ActionEvent event) {
        		//System.out.println("Hello World");
        		String cmd = command.getText();
                //System.out.println(cmd);
                command.clear();
               // result.setText(cmd);
                result.setText(manipulateString(cmd));
        	}
        		});
        
    }
	
	String manipulateString(String str) {
		return str.substring(1, 5) + "\n" + str.substring(6, 8) + "\n" + "\n" + "\n" + str.substring(6, 8)+ "\n" + "\n" + "\n" + str.substring(6, 8)+"\n" + "\n" + "\n" + str.substring(6, 8)+"\n" + "\n" + "\n" + str.substring(6, 8) + "\n" + str.substring(6, 8) + "\n" + str.substring(6, 8)+ "\n" + str.substring(6, 8)+ "\n" + str.substring(6, 8);
	}

    public static void main(String[] args) {
    	launch(args);
		ticker = new TickerUI();
		while (true)
			ticker.parser.getCommand();
	}
}
