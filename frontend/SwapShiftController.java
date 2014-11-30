
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
import javafx.scene.control.Label;
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
    private TextField monthSearch;

    @FXML
    private TextField yearSearch;

    @FXML
    private TextField userSearch;
    
    @FXML
    private Label userFailureLabel;
    
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
    private void onPressSwap()
    {
        if (takeIndex>0 && giveIndex>0)
        {
            instance.sendTradeRequest(wantList.get(takeIndex),giveList.get(giveIndex));
            instance.swapToProntPage();
        }
        else
        {
            userFailureLabel.setText("Need to select a shift to give and a shift to take.");
            userFailureLabel.setVisible(true);
        }
	// Ensure two shifts are selected, one in each box
	
	// Ensure the user isn't selecting an Available Shift they cannot take
	
	// Call to View function to switch the two shifts around
    }

    @FXML
    private void onSearchButtonPressed()
    {
        //If all three day search boxes aren't filled, search by user, otherwise search by date.
        //Search by name:
        if(daySearch.getText().equals("") || monthSearch.getText().equals("") || yearSearch.getText().equals(""))
        {
            String check= instance.getLoggedInEmployee();
            if(check.equals(userSearch.getText()))
            {
                userFailureLabel.setText("You cannot trade with yourself.");
                userFailureLabel.setVisible(true);
            }
            else
            {
                LinkedList<Shift> test=userSearch(userSearch.getText());
                if(test==null)
                {
                    userFailureLabel.setText("User does not exist.");
                    userFailureLabel.setVisible(true);
                }
                else
                {
                    userFailureLabel.setVisible(false);
                    wantList=test;
                    updateTakeShifts();
                }
            }
        }
        //Search by date:
        else
        {
            
        }
	// Query the database to find the shifts in question
	
	// Display these shifts in the available shifts list
    }
    
    private void updateTakeShifts()
    {
        availShiftsGrid.setItems(grabTakeShifts());
        try
        {
            availShiftsGrid.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> selectedTakeIndex(availShiftsGrid.getSelectionModel().getSelectedIndex()));
        }
        catch(IndexOutOfBoundsException e)
        {
            /*Every time you update the list past the first, it will throw this error, as index -1.
            *None of us still know why this is happening, but it doesn't actually affect anything.
            *We can catch this safely without anything bad happening.
            */
        }
    }
    
    private ObservableList<String> grabTakeShifts()
    {
        ObservableList<String> shiftData = FXCollections.observableArrayList();
        int i=0;
        while(i<wantList.size())
        {
            String entry=wantList.get(i).toString();
            
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
    
    private void selectedTakeIndex(int index)
    {
        takeIndex=index;
    }
    
    @FXML
    void onUpdateButtonPress() 
    {
        updateGiveShifts();
    }
    
    private LinkedList<Shift> userSearch(String username)
    {
        return instance.grabEmployeesShifts(username);
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
