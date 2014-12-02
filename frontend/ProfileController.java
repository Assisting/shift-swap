/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package frontend;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.InputMethodEvent;

/**
 * FXML Controller class
 *
 * @author erik
 */
public class ProfileController implements Initializable
{
    @FXML
    private TextField userIDField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField wageField;
    @FXML
    private CheckBox notifyCheckbox;
    
    private View instance;
    
    
    public void setApp(View instance)
    {
	this.instance = instance;
	
	userIDField.setEditable(false);
	emailField.setEditable(false);
	wageField.setEditable(false);
	
	getFields();
	
	notifyCheckbox.selectedProperty().addListener(new ChangeListener<Boolean>() {
	    @Override
	    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
		toggleNotifications(notifyCheckbox.isSelected());
	    }
	});
    }
    
    private void toggleNotifications(boolean value)
    {
	String userID = instance.getLoggedInEmployee();
	instance.setManagerApprovalStatus(userID, value);
    }
    
    private void getFields()
    {
	String userID = instance.getLoggedInEmployee();
	
	float wage = instance.getWage(userID);
	
	userIDField.setText(userID);
	emailField.setText(instance.getEmail(userID));
	wageField.setText(Float.toString(wage));
	
	int accessLevel = instance.getAccessLevel(userID);
	
	// If not a worker
	if(accessLevel > 1) {
	    notifyCheckbox.setVisible(true);
	    
	    boolean checked = instance.getManagerApprovalStatus(userID);
	    notifyCheckbox.setSelected(checked);
	}
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
	
    }

    @FXML
    private void onBackPress(ActionEvent event)
    {
	instance.swapToProntPage();
    }
    
}
