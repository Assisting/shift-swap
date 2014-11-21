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
    @FXML
    private ListView<?> yourShiftsList;
    @FXML
    private ListView<?> availShiftsList;
    @FXML
    private TextField daySearch;
    @FXML
    private TextField userSearch;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
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
	//
    }
    
}
