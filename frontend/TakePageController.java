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
    
    @FXML
    private ListView<String> shiftGrid;
    
    @FXML
    private TextArea shiftHeader;
    
    public void setApp(View application){
        this.instance = application;
    }
    
    @FXML
    void onBackButtonPress() {
        instance.swapToProntPage();
    }
    
    @FXML
    void onUpdateButtonPress() {
         updateShifts();
    }
    
    void updateShifts(){
        shiftGrid.setItems(grabShifts());
        shiftGrid.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> populateMessage(newValue));
    }
    
    private void populateMessage(String message)
    {
        if(message==null)
        {
            shiftHeader.setText("");
        }
        else
        {
           shiftHeader.setText(message);
        }
    }
    
    private ObservableList<String> grabShifts()
    {
        shiftList=instance.grabGiveShifts();
        ObservableList<String> shiftData = FXCollections.observableArrayList();
        int i=0;
        while(i<shiftList.size())
        {
            String stuff;
            shiftData.add(shiftList.get(i).toString());
            i=i+1;
        }
        return shiftData;
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) 
    {    
        
    }  
    
}
