package frontend;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;

/**
 * @author Warren Fehr wwf594
 */
public class FXMLCalendarController implements Initializable
{
    
    @FXML
    private GridPane calendarGrid;

    @FXML
    void onStuffButtonClicked(ActionEvent event) {

    }

    @FXML
    void onBackButtonClick(ActionEvent event) 
    {
        System.out.println("You clicked me!");
    }
    
     @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }   
}
