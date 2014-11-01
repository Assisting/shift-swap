package frontend;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;

/**
 * @author Warren Fehr, wwf594
 */
public class FXMLProntPageController  extends AnchorPane implements Initializable {
    
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
    
    /**
     * We can use this instance to pass data back to the top level.
     */
    private View instance;
    
    public void setApp(View application){
        this.instance = application;
    }
    
    @FXML
    protected void onMonthlySchedulePress(ActionEvent event) {
        System.out.println("You clicked me!");
        sunSchedule.appendText("Testing,Testing,1,2,3");
        //label.setText("Hello World!");
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        satSchedule.appendText("Hello, World!");
    }    
    
}
