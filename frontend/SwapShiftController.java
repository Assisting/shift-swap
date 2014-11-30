
package frontend;

import controller.Shift;
import java.net.URL;
import java.util.LinkedList;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author erik & warren
 */
public class SwapShiftController implements Initializable
{
    
    private int takeIndex;
    
    private int giveIndex;
    
    private LinkedList<Shift> wantList;
    
    private LinkedList<Shift> giveList;
    
    @FXML
    private ListView<String> yourShiftsGrid;
    @FXML
    private ListView<String> availShiftsGrid;
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
    
    @FXML
    void onUpdateButtonPress(ActionEvent event) 
    {
        updateGiveShifts();
    }
    
    private void updateGiveShifts()
    {
        yourShiftsGrid.setItems(grabGiveShifts());
        try
        {
            yourShiftsGrid.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> selectedGiveIndex(yourShiftsGrid.getSelectionModel().getSelectedIndex()));
        }
        catch(IndexOutOfBoundsException e)
        {
            /*Every time you update the list past the first, it will throw this error, as index -1.
            *None of us still know why this is happening, but it doesn't actually affect anything.
            *We can catch this safely without anything bad happening.
            */
        }
    }
    
    private void selectedGiveIndex(int index)
    {
        giveIndex=index;
    }
    
    private ObservableList<String> grabGiveShifts()
    {
        giveList=instance.grabSelfShifts();
        ObservableList<String> shiftData = FXCollections.observableArrayList();
        int i=0;
        while(i<giveList.size())
        {
            String entry=giveList.get(i).toString();
            
            //This parses the data properly.
            int stringCounter=0;
            while(entry.charAt(stringCounter)!=' ')
            {
                stringCounter++;
            }
            entry=entry.substring(stringCounter);
            
            //This will remove one of the non-essential dates
            entry=entry.substring(0, 17)+"-"+entry.substring(31);
            
            
            shiftData.add(entry);
            i=i+1;
        }
        return shiftData;
    }
    
}
