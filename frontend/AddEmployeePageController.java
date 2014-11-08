/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package frontend;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author Andrew Magnus
 */
public class AddEmployeePageController implements Initializable {

    
    @FXML
    private TextField firstNameText;
    
    @FXML
    private Label firstNameLabel;
    
    @FXML
    private TextField lastNameText;
    
    @FXML
    private Label lastNameLabel;
    
    @FXML
    private TextField usernameText;
    
    @FXML
    private Label usernameLabel;
    
    @FXML
    private TextField passwordText;
    
    @FXML
    private Label passwordLabel;
    
    @FXML
    private TextField emailText;
    
    @FXML
    private Label emailLabel;
    
    @FXML
    private TextField startingWageText;
    
    @FXML
    private Label startingWageLabel;
    
    @FXML
    private ChoiceBox accessLevelBox;
    
    @FXML
    private Button cancel;
    
    @FXML
    private Button addEmployee;
    
    @FXML
    private Label errorLabel;
    
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
    public void initialize(URL url, ResourceBundle rb) {
        firstNameLabel.setText(null);
        lastNameLabel.setText(null);
        usernameLabel.setText(null);
        passwordLabel.setText(null);
        emailLabel.setText(null);
        startingWageLabel.setText(null);
        errorLabel.setText(null);
        accessLevelBox.setItems(FXCollections.observableArrayList("Worker", "Manager", "Owner"));
        accessLevelBox.getSelectionModel().select(0); // for somereason this function is deemed unsafe idk why mates
    }  
    
    
    /**
     * When the cancel button is pressed, all work will be erased and the system will go back to the ProntPage
     * preconditions: no preconditions
     * postconditions: all data in the text fields will be deleted before moving on
     * @param event 
     */
    @FXML
    void onCancelButtonClick(ActionEvent event) 
    {
        firstNameText.setText(null);
        lastNameText.setText(null);
        usernameText.setText(null);
        passwordText.setText(null);
        emailText.setText(null);
        startingWageText.setText(null);
        
        System.out.println("CANCEL BUTTON PRESSED");
        instance.swapToProntPage();
    }
    
    /**
     * When the Add employee button is pressed the data in the fields is validated,
     * a request is sent to the database to see if the username is unique, and if it is, 
     * the new user is added to the system.
     * @param event 
     */
     @FXML
    void onAddEmployeeButtonClick(ActionEvent event) 
    {
        if(this.validateFields())
        {
            int accessLevel = 1;
            switch (accessLevelBox.getValue().toString()) {
                case "Manager":
                    accessLevel = 2;
                    break;
                case "Owner":
                    accessLevel = 3;
                    break;
            }
          //TODO make a call to the database to add an employee to the system now that all the information is verified
            //TODO also create a new record in the bossmanager table with the manager set to null
            // some kind of confirmation or message saying it worked to the user, then back off to the main menu!
            
        }
        else
        {
             System.out.println("BADD");
        }
    }
    
    /**
     * private function used by the onAddEmployeeButtonClick function to check if 
     * the data found in all the text fields is valid, if a text fields is not valid
     * then the function will update the error labels accordingly.
     * @return true ALL fields are valid, false otherwise
     */
    private boolean validateFields()
    {
        boolean allGood = true;
        firstNameLabel.setText(null);
        lastNameLabel.setText(null);
        usernameLabel.setText(null);
        passwordLabel.setText(null);
        emailLabel.setText(null);
        startingWageLabel.setText(null);
        errorLabel.setText(null);
        if(firstNameText.getText().isEmpty())
        {
            allGood = false;
            firstNameLabel.setText("*");
        }
        if(lastNameText.getText().isEmpty())
        {
            allGood = false;
            lastNameLabel.setText("*");
        }
        if(usernameText.getText().isEmpty())
        {
            allGood = false;
            usernameLabel.setText("*");
        }
        else //username field is filled, an needs to be validated with the database
        {
            if(Input.isUsernameUnique(usernameText.getText()) == false)
            {
                allGood = false;
                usernameLabel.setText("* already used");
            }
        }
        if(passwordText.getText().isEmpty())
        {
            allGood = false;
            passwordLabel.setText("*");
        }
        if(emailText.getText().isEmpty())
        {
            allGood = false;
            emailLabel.setText("*");
        }
        if(startingWageText.getText().isEmpty())
        {
            allGood = false;
            startingWageLabel.setText("*");
        }
        else//starting wage has some data in it, time to check if its valid
        {
            float wage = 0;
            boolean isNumber = true;
            try
            {
                wage = Float.parseFloat(startingWageText.getText());
            }
            catch (NumberFormatException e)
            {
                allGood = false;
                isNumber = false;
                startingWageLabel.setText("* not a number");
            } 
            if(isNumber)
            {
                if(wage < 0)
                {
                    allGood = false;
                    startingWageLabel.setText("* must be positive");
                }
            }
        }
        if(!allGood)
        {
            errorLabel.setText("Please fill in all fields marked with *");
        }
        
           
        
            return allGood;
    }
    
}
