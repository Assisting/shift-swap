
package frontend;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

//Warren Fehr, wwf594
public class LoginPageController extends AnchorPane implements Initializable {

    @FXML
    private TextField usernameField;

    @FXML
    private Button loginButton;

    @FXML
    private TextField passwordField;

    @FXML
    private Label resultLabel;
    
    private View instance;
    
    public void setApp(View application){
        this.instance = application;
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        resultLabel.setText("Welcome to Shift Swap");
    }
    
    @FXML
    public void handleEnterPressed(KeyEvent event){
        if (event.getCode() == KeyCode.ENTER) {
            if(instance.logIn(usernameField.getText(), passwordField.getText()) == false) 
            {
    	    resultLabel.setTextFill(Color.FIREBRICK);
                resultLabel.setText("Login failed");
    	}
        }
    }

    @FXML
    void onLoginButtonPress(ActionEvent event) 
    {
        if(instance.logIn(usernameField.getText(), passwordField.getText()) == false) 
        {
	    resultLabel.setTextFill(Color.FIREBRICK);
            resultLabel.setText("Login failed");
	}
    }

}
