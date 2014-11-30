package frontend;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
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
    private TextField rmEmployeeFirst;
    @FXML
    private TextField rmEmployeeLast;
    @FXML
    private Label rmEmployeeConf;
    @FXML
    private Button removeEmployeeSubmit;
    @FXML
    private Label setPasswordConfirmation;
    
    @FXML
    private TabPane managerTabs;
    
    @FXML
    private Tab setManagerTab;

    @FXML
    private Tab addEmployeeTab;

    @FXML
    private Tab setAccessTab;
    
    @FXML
    private Tab modEmployeeTab;

    @FXML
    private Tab setPasswordTab;
    
    @FXML
    private Tab rmEmployeeTab;
    
    private boolean justAddedEmp;
    
    // The default font colour
    public Color fontColor;
    
    /**
     * We can use this instance to pass data back to the top level.
     */
    private View instance;
    
    public void setApp(View application){
        this.instance = application;
	
	fontColor = Color.web("#41373D");
    }

    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
	justAddedEmp = false;
    }    

    @FXML
    private void processNewEmployee()
    {
	instance.addEmployee(newEmpID.getText(), newEmpFirstName.getText(),
		newEmpLastName.getText(), newEmpPass.getText(),
		newEmpEmail.getText(), Float.parseFloat(newEmpWage.getText()));
	justAddedEmp = true;
	managerTabs.getSelectionModel().select(setAccessTab);
    }

    /**
     * Until modifyEmployeeInfo is changed this just sets everyone to the 
     * lowest access level. That can be re-changed using the other method.
     */
    @FXML
    private void modifyEmployee()
    {
	instance.modifyEmployee(modEmpID.getText(),
		modEmpFirstName.getText(),
		modEmpLastName.getText(),
		modEmpEmail.getText(),
		Float.parseFloat(modEmpWage.getText()));
    }

    @FXML
    private void modifyAccessLevel()
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
	
	instance.changeAccessLevel(modAccessID.getText(), newAccessLevel);
	
	if(justAddedEmp == true) {
	    managerTabs.getSelectionModel().select(setManagerTab);
	}
    }

    @FXML
    private void setEmployeeManager()
    {
	instance.setManager(setManagerEmployee.getText(),
		setManagerManager.getText());
	
	if(justAddedEmp == true) {
	    justAddedEmp = false;
	}
    }

    @FXML
    private void changeEmployeePassword()
    {
	String proposedPassword = setPasswordNew.getText();
	
	if(proposedPassword == setPasswordConfirm.getText()) {
	    setPasswordConfirmation.setText("");
	    
	    instance.setPassword(setPasswordID.getText(),
		    proposedPassword);
	}
	else {
	    setPasswordConfirmation.setTextFill(Color.FIREBRICK);
	    setPasswordConfirmation.setText("Passwords do not match");
	}
    }

    @FXML
    private void removeEmployee()
    {
	String userID = removeEmployeeID.getText();
	instance.removeEmployee(userID);
	
	if(instance.isUserInSystem(userID)){
	    rmEmployeeConf.setTextFill(Color.FIREBRICK);
	    rmEmployeeConf.setText("User not successfully removed");
	}
	else {
	    rmEmployeeConf.setTextFill(fontColor);
	    rmEmployeeConf.setText("User successfully removed");
	}
    }
    
    @FXML
    private void onBackPress()
    {
	instance.swapToProntPage();
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
    
    @FXML
    void rmEmployeeSearch() 
    {
	String employeeFirstName = rmEmployeeFirst.getText();
	String employeeLastName = rmEmployeeLast.getText();
	
	String userID = "";
	//userID = Input.getWorkerLogin(employeeFirstName, employeeLastName);
	
	removeEmployeeID.setText(userID);
    }
    
    @FXML
    void checkForEnter(KeyEvent event)
    {
	if(event.getCode() == KeyCode.ENTER) {
	    if(setManagerTab.isSelected()) {
		setEmployeeManager();
	    }
	    else if(setAccessTab.isSelected()) {
		modifyAccessLevel();
	    }
	    else if(addEmployeeTab.isSelected()) {
		processNewEmployee();
	    }
	    else if(modEmployeeTab.isSelected()) {
		modifyEmployee();
	    }
	    else if(setPasswordTab.isSelected()) {
		changeEmployeePassword();
	    }
	    else if(rmEmployeeTab.isSelected()) {
		removeEmployee();
	    }
	    else {
		// This should be impossible
		throw new IllegalArgumentException("No access level selected");
	    }
	}
    }
}
