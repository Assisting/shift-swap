package frontend;

import controller.Employee;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.paint.Color;

/**
 * FXML Controller class
 *
 * @author erik
 */
public class ManagerSettingsController implements Initializable
{
    /*
     * I hope this is all mostly self-documenting. If not check it out in
     * JavaFX Scene Builder.
     */
    @FXML
    private TextField newEmpID;
    @FXML
    private TextField newEmpFirstName;
    @FXML
    private TextField newEmpLastName;
    @FXML
    private TextField newEmpEmail;
    @FXML
    private PasswordField newEmpPass;
    @FXML
    private TextField newEmpWage;
    @FXML
    private Button newEmpSubmit;
    @FXML
    private TextField modEmpID;
    @FXML
    private TextField modEmpFirstName;
    @FXML
    private TextField modEmpLastName;
    @FXML
    private TextField modEmpEmail;
    @FXML
    private TextField modEmpWage;
    @FXML
    private Button modEmpSubmit;
    @FXML
    private TextField modAccessID;
    @FXML
    private Button modAccessSubmit;
    @FXML
    private RadioButton modAccessWorker;
    @FXML
    private ToggleGroup modAccess;
    @FXML
    private RadioButton modAccessManager;
    @FXML
    private RadioButton modAccessOwner;
    @FXML
    private TextField setManagerEmployee;
    @FXML
    private TextField setManagerManager;
    @FXML
    private TextField setPasswordID;
    @FXML
    private PasswordField setPasswordNew;
    @FXML
    private PasswordField setPasswordConfirm;
    @FXML
    private Button setPasswordSubmit;
    @FXML
    private TextField removeEmployeeID;
    @FXML
    private Button removeEmployeeSubmit;
    @FXML
    private Label setPasswordConfirmation;
    
    /**
     * We can use this instance to pass data back to the top level.
     */
    private View instance;
    
    public void setApp(View application){
        this.instance = application;
    }

    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
	// This is not the method you are looking for.
    }    

    @FXML
    private void processNewEmployee(ActionEvent event)
    {
	// TODO check that 1 is the access level for a worker
	Employee newEmployee = new Employee(newEmpID.getText(),
					   newEmpFirstName.getText(),
					   newEmpLastName.getText(),
					   1,
					   Input.createHash(newEmpPass.getText()),
					   newEmpEmail.getText(),
					   Float.parseFloat(newEmpWage.getText()));
	
	Input.addNewEmployee(newEmployee);
    }

    /**
     * Until modifyEmployeeInfo is changed this just sets everyone to the 
     * lowest access level. That can be re-changed using the other method.
     */
    @FXML
    private void modifyEmployee(ActionEvent event)
    {
	Input.modifyEmployeeInfo(modEmpID.getText(),
		modEmpFirstName.getText(),
		modEmpLastName.getText(),
		modEmpEmail.getText(),
		1, 
		Float.parseFloat(modEmpWage.getText()));
    }

    @FXML
    private void modifyAccessLevel(ActionEvent event)
    {
	/* If it's not modified correctly, it'll change the user account to the
	 * lowest access level available. */
	int newAccessLevel = 1;
	try {
	    newAccessLevel = getModAccessLevelSelection();
	}
	catch (IllegalArgumentException e) {
	    System.out.println(e.getMessage() + " - Stack trace follows:");
	    e.printStackTrace();
	}
	
	Input.changeEmployeeAccessLevel(modAccessID.getText(), newAccessLevel);
    }

    @FXML
    private void setEmployeeManager(ActionEvent event)
    {
	Input.changeEmployeesManager(setManagerEmployee.getText(),
		setManagerManager.getText());
    }

    @FXML
    private void changeEmployeePassword(ActionEvent event)
    {
	String proposedPassword = setPasswordNew.getText();
	
	if(proposedPassword == setPasswordConfirm.getText()) {
	    setPasswordConfirmation.setText("");
	    
	    Input.changeEmployeePassword(setPasswordID.getText(),
		    proposedPassword);
	}
	else {
	    setPasswordConfirmation.setTextFill(Color.FIREBRICK);
	    setPasswordConfirmation.setText("Passwords do not match");
	}
    }

    @FXML
    private void removeEmployee(ActionEvent event)
    {
	Input.removeEmployee(removeEmployeeID.getText());
    }
    
    private int getModAccessLevelSelection() throws IllegalArgumentException
    {
	if(modAccessWorker.isSelected()) {
	    return 1;
	}
	else if(modAccessManager.isSelected()) {
	    return 2;
	}
	else if(modAccessOwner.isSelected()) {
	    return 3;
	}
	else {
	    // This should be impossible.
	    throw new IllegalArgumentException("No access level selected");
	}
    }
    
}
