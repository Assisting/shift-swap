package frontend;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.AnchorPane;

/**
 * @author Warren Fehr wwf594
 */
public class FXMLCalendarController extends AnchorPane implements Initializable
{
    
    @FXML
    private GridPane calendarGrid;
    
    private View instance;
    
    public void setApp(View application){
        this.instance = application;
    }

    @FXML
    void onStuffButtonClicked(ActionEvent event) 
    {
    }

    @FXML
    void onBackButtonClick(ActionEvent event) 
    {
        instance.swapToProntPage();
        System.out.println("You clicked me!");
    }
    
     @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }   
}
