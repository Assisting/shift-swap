/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package frontend;

import controller.Shift;
import java.net.URL;
import java.util.LinkedList;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;

/**
 *
 * @author User
 */
public class TakePageController extends AnchorPane implements Initializable
{
    private View instance;
    
    LinkedList<Shift> shiftList;
    
    LinkedList<Shift> giveList;
    
    private int  currentIndex;
    
    @FXML
    private ListView<String> giveGrid;
    
    @FXML
    private ListView<String> shiftGrid;
    
    @FXML
    private TextArea shiftHeader;
    
    @FXML
    private Button takeButton;
    
    @FXML
    private Button giveButton;
    
    public void setApp(View application){
        this.instance = application;
    }
    
    @FXML
    void onBackButtonPress() {
        instance.swapToProntPage();
    }
    
    @FXML
    void onUpdateButtonPress() {
        updateTakeShifts();
    }
    
    @FXML
    void onTakeButtonPress() 
    {
        instance.sendTakeRequest(shiftList.get(currentIndex));
        updateTakeShifts();
    }
    
    @FXML
    void onGiveButtonPress() {
        
    }
    
    void updateGiveShifts(){
        giveGrid.setItems(grabGiveShifts());
        
    }
    
    private ObservableList<String> grabGiveShifts()
    {
        giveList=instance.grabSelfShifts();
        return null;
    }
    
    void updateTakeShifts(){
        shiftGrid.setItems(grabTakeShifts());     
        try
        {
            shiftGrid.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> populateMessageTake(shiftGrid.getSelectionModel().getSelectedIndex()));
        }
        catch(IndexOutOfBoundsException e)
        {
            /*Every time you update the list past the first, it will throw this error, as index -1.
            *None of us still know why this is happening, but it doesn't actually affect anything.
            *We can catch this safely without anything bad happening.
            */
        }
        takeButton.setDisable(true);
        takeButton.setVisible(false);
    }
    
    private void populateMessageTake(int index)
    {
        takeButton.setDisable(false);
        takeButton.setVisible(true);
        currentIndex=index;
        shiftHeader.setText("Shift is from: "+shiftList.get(index).toString());
    }
    
    private ObservableList<String> grabTakeShifts()
    {
        shiftList=instance.grabGiveShifts();
        ObservableList<String> shiftData = FXCollections.observableArrayList();
        int i=0;
        while(i<shiftList.size())
        {
            String entry=shiftList.get(i).toString();
            
            //This parses the data properly.
            int stringCounter=0;
            while(entry.charAt(stringCounter)!=' ')
            {
                stringCounter++;
            }
            entry=entry.substring(stringCounter);
            
            //This will remove one of the non-essential dates
            entry=entry.substring(0, 17)+"-"+entry.substring(32);
            
            
            shiftData.add(entry);
            i=i+1;
        }
        return shiftData;
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) 
    {    
        
    }  
    
}
