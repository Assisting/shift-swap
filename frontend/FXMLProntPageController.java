package frontend;

import java.net.URL;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import java.sql.Timestamp;

/**
 * @author Warren Fehr, wwf594
 */
public class FXMLProntPageController  extends AnchorPane implements Initializable {
    
    //These are all the textAreas related to the WeeklySchedule.
   @FXML
    private TextArea day6Schedule;

    @FXML
    private Label day6Label;
    
    @FXML
    private TextArea day5Schedule;
    
    @FXML
    private Label day5Label;

    @FXML
    private TextArea day4Schedule;

    @FXML
    private Label day4Label;

    @FXML
    private TextArea day3Schedule;

    @FXML
    private Label day3Label;
    
    @FXML
    private TextArea day2Schedule;

    @FXML
    private Label day2Label;

    @FXML
    private TextArea day1Schedule;

    @FXML
    private Label day1Label;
    
    @FXML
    private TextArea day0Schedule;

    @FXML
    private Label day0Label;
  
    
    /**
     * We can use this instance to pass data back to the top level.
     */
    private View instance;
    
    public void setApp(View application){
 
       
        this.instance = application;
    }
    
    @FXML
    protected void onMonthlySchedulePress(ActionEvent event) {
        instance.swapToCalendar();
        System.out.println("You clicked me!");
    }
    
    public void populateSchedule()
    {
       LinkedList<LinkedList<Timestamp>> weeklySchedule=instance.grabScheduleWeekly();
       while(weeklySchedule.get(0).size()>0)
       {
           Timestamp temp=weeklySchedule.get(0).remove();
           String parse=temp.toString();
           parse=parse.substring(11, 16);
           day0Schedule.appendText(parse+"-");
           temp=weeklySchedule.get(0).remove();
           parse=temp.toString();
           parse=parse.substring(11, 16);
           day0Schedule.appendText(parse+"\n");
       }
       while(weeklySchedule.get(1).size()>0)
       {
           Timestamp temp=weeklySchedule.get(1).remove();
           String parse=temp.toString();
           parse=parse.substring(11, 16);
           day1Schedule.appendText(parse+"-");
           temp=weeklySchedule.get(1).remove();
           parse=temp.toString();
           parse=parse.substring(11, 16);
           day1Schedule.appendText(parse+"\n");
       }
       while(weeklySchedule.get(2).size()>0)
       {
           Timestamp temp=weeklySchedule.get(2).remove();
           String parse=temp.toString();
           parse=parse.substring(11, 16);
           day2Schedule.appendText(parse+"-");
           temp=weeklySchedule.get(2).remove();
           parse=temp.toString();
           parse=parse.substring(11, 16);
           day2Schedule.appendText(parse+"\n");
       }
       while(weeklySchedule.get(3).size()>0)
       {
           Timestamp temp=weeklySchedule.get(3).remove();
           String parse=temp.toString();
           parse=parse.substring(11, 16);
           day3Schedule.appendText(parse+"-");
           temp=weeklySchedule.get(3).remove();
           parse=temp.toString();
           parse=parse.substring(11, 16);
           day3Schedule.appendText(parse+"\n");
       }
       while(weeklySchedule.get(4).size()>0)
       {
           Timestamp temp=weeklySchedule.get(4).remove();
           String parse=temp.toString();
           parse=parse.substring(11, 16);
           day4Schedule.appendText(parse+"-");
           temp=weeklySchedule.get(4).remove();
           parse=temp.toString();
           parse=parse.substring(11, 16);
           day4Schedule.appendText(parse+"\n");
       }
       while(weeklySchedule.get(5).size()>0)
       {
           Timestamp temp=weeklySchedule.get(5).remove();
           String parse=temp.toString();
           parse=parse.substring(11, 16);
           day5Schedule.appendText(parse+"-");
           temp=weeklySchedule.get(5).remove();
           parse=temp.toString();
           parse=parse.substring(11, 16);
           day5Schedule.appendText(parse+"\n");
       }
       while(weeklySchedule.get(6).size()>0)
       {
           Timestamp temp=weeklySchedule.get(6).remove();
           String parse=temp.toString();
           parse=parse.substring(11, 16);
           day6Schedule.appendText(parse+"-");
           temp=weeklySchedule.get(6).remove();
           parse=temp.toString();
           parse=parse.substring(11, 16);
           day6Schedule.appendText(parse+"\n");
       }
    }
    
    @FXML
    void onScheduleUpdateButtonPress(ActionEvent event) 
    {
        populateSchedule();
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) 
    {    
        
    }    
    
}
