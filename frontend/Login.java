/*
 * Erik LaBine, ejl389, 11122765
 * ejl389@mail.usask.ca
 * For use in shift-swap project; CMPT 370, University of Saskatchewan
 * 2014
 */

package frontend;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Introductory GUI
 */
public class Login extends Application
{
    /**
     * This does the heavy lifting for the application. start() always
     * acts as the main point of entry into the code for Java FX.
     * 
     * @param primaryStage the top level container for the GUI
     */
    @Override
    public void start(Stage primaryStage)
    {
	primaryStage.setTitle("Shift Swap - Login to Shift Swap");
	
	GridPane grid = new GridPane();
	grid.setAlignment(Pos.CENTER);
	grid.setHgap(10);
	grid.setVgap(10);
	// Padding order: top, right, bottom, left
	grid.setPadding(new Insets(25, 75, 25, 75));
	
	Text scenetitle = new Text("Log In");
	scenetitle.setId("title");
	// There have been issues setting colours via CSS, thus the override.
	scenetitle.setFill(Color.rgb(65, 55, 61));
	grid.add(scenetitle, 0, 0, 2, 1);

	Label userName = new Label("User Name:");
	// There have been issues setting colours via CSS, thus the override.
	userName.setTextFill(Color.rgb(65, 55, 61));
	grid.add(userName, 0, 1);

	final TextField userTextField = new TextField();
	grid.add(userTextField, 1, 1);

	Label pw = new Label("Password:");
	// There have been issues setting colours via CSS, thus the override.
	pw.setTextFill(Color.rgb(65, 55, 61));
	grid.add(pw, 0, 2);

	final PasswordField pwBox = new PasswordField();
	grid.add(pwBox, 1, 2);
	
	Button btn = new Button("Enter");
	HBox hbBtn = new HBox(10);
	hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
	hbBtn.getChildren().add(btn);
	grid.add(hbBtn, 1, 4);
	
	final Text actiontarget = new Text();
        grid.add(actiontarget, 1, 6);

        btn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
		String username = userTextField.getText();
		String password = pwBox.getText();
		
		if(Input.authenticate(username, password)) {
		    actiontarget.setFill(Color.GREEN);
		    actiontarget.setText("Successfully logged in");
		} else {
		    actiontarget.setFill(Color.FIREBRICK);
		    actiontarget.setText("Login failed");
		}
            }
        });
	
	// The scene contains the working parts of the stage
	Scene scene = new Scene(grid, 800, 500);
	primaryStage.setScene(scene);
	//is line below seems to give me troubles, get null pointer exception, works if i comment it out - Andrew
	scene.getStylesheets().add(Login.class.getResource("Primary.css").toExternalForm());
	
	primaryStage.show();
    }

    /**
     * main() is essentially just a fallback if JavaFX fails to launch.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
	launch(args);
    }
    
}
