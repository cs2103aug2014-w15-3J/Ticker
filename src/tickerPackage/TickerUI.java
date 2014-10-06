package tickerPackage;

import java.util.Vector;

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
	private static String list;
	
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
		list = null;
		
	}


	public void start(Stage primaryStage) {
        primaryStage.setTitle("Ticker");
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.TOP_LEFT);
        grid.setHgap(0);
        grid.setVgap(10);
        grid.setPadding(new Insets(10,10,10,10));

        Text enterCommand = new Text("Enter your command: ");
        grid.add(enterCommand, 0, 2);

        TextField command = new TextField();
        grid.add(command, 1, 2);
        
        Text showResult = new Text("Your To-dos: ");
        grid.add(showResult, 0, 1);

        TextArea result = new TextArea();
        result.setWrapText(true);
        grid.add(result, 1, 1);
        
        Text feedback = new Text();
        grid.add(feedback, 1, 3);

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
                feedback.setText(logic.getLogic(cmd));
                result.setText(list);
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
	
	String manipulateString(String str) {
		return str.substring(1, 5) + "\n" + str.substring(6, 8) + "\n" + "\n" + "\n" + str.substring(6, 8)+ "\n" + "\n" + "\n" + str.substring(6, 8)+"\n" + "\n" + "\n" + str.substring(6, 8)+"\n" + "\n" + "\n" + str.substring(6, 8) + "\n" + str.substring(6, 8) + "\n" + str.substring(6, 8)+ "\n" + str.substring(6, 8)+ "\n" + str.substring(6, 8);
	}
	
	public void setList(String list) {
		this.list = list;
	}

    public static void main(String[] args) {
    	launch(args);
    	ticker = new TickerUI();
    	
	}
}
