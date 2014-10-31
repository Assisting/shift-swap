package frontend;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

/**
 * @author Warren Fehr, wwf594
 */
public class FXMLProntPageController implements Initializable {
    
    //These are all the textAreas related to the WeeklySchedule.
    @FXML
    private TextArea friSchedule;
    @FXML
    private TextArea monSchedule;
    @FXML
    private TextArea sunSchedule;
    @FXML
    private TextArea thuSchedule;
    @FXML
    private TextArea wedSchedule;
    @FXML
    private TextArea tueSchedule;
    @FXML
    private TextArea satSchedule;
    
    
    @FXML
    protected void onMonthlySchedulePress(ActionEvent event) {
        System.out.println("You clicked me!");
        sunSchedule.appendText("Testing,Testing,1,2,3");
        //label.setText("Hello World!");
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
