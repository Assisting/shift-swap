/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package frontend;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author erik
 */
public class SwapShiftController implements Initializable
{
    //TODO Add back button
    
    @FXML
    private ListView<?> yourShiftsList;
    @FXML
    private ListView<?> availShiftsList;
    @FXML
    private TextField daySearch;
    @FXML
    private TextField userSearch;
    
    /**
     * We can use this instance to pass data back to the top level.
     */
    private View instance;
    
    public void setApp(View application){
 
       
        this.instance = application;
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
    }
    
    @FXML
    private void onBackPressed() {
	instance.swapToProntPage();
    }

    @FXML
    private void onPressSwap(ActionEvent event)
    {
	// Ensure two shifts are selected, one in each box
	
	// Ensure the user isn't selecting an Available Shift they cannot take
	
	// Call to View function to switch the two shifts around
    }

    @FXML
    private void onSearchButtonPressed(ActionEvent event)
    {
	// Check if they are searching by day or by user
	// If they are trying to search by both, default to by day
	
	// Query the database to find the shifts in question
	
	// Display these shifts in the available shifts list
    }
    
}
