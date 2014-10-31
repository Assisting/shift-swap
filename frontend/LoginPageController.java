
package frontend;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

//Warren Fehr, wwf594
public class LoginPageController {

    @FXML
    private TextField usernameField;

    @FXML
    private Button loginButton;

    @FXML
    private TextField passwordField;

    @FXML
    private Label resultLabel;

    @FXML
    void onLoginButtonPress(ActionEvent event) 
    {
        String username = usernameField.getText();
	String password = passwordField.getText();
		
	if(Input.authenticate(username, password)) 
        {
            resultLabel.setText("Successfully logged in");
        } 
        else 
        {
            resultLabel.setText("Login failed");
	}
        System.out.println("You clicked it!");
    }

}
