package frontend;

import java.net.URL;
import java.util.LinkedList;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.Locale;
import javafx.scene.control.Button;

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
    
    @FXML
    private Label youAreALabel;
    
    // Technically this is now the "Manager Settings" button
    @FXML
    private Button addEmployeeButton;
  
    @FXML
    private Button assignShiftsButton;
    
    @FXML
    private Button systemSettingsButton;
  
    
    /**
     * We can use this instance to pass data back to the top level.
     */
    private View instance;
    
    /**
     * 1 = Worker, 2 = Manager, 3 = Owner
     */
    private int access;
    
    public void setApp(View application, int accessLevel) 
    {
        this.instance = application;
	
	onScheduleUpdateButtonPress(null);
	
	this.access = accessLevel;
	
	setActiveButtons();
    }
    
    @FXML
    protected void onMonthlySchedulePress(ActionEvent event) 
    {
        instance.swapToCalendar();
    }
    
    private void populateSchedule(LinkedList<LinkedList<Timestamp>> weeklySchedule,int index,TextArea day)
    {
       day.clear();
       while(weeklySchedule.get(index).size()>0)
       {
           Timestamp temp=weeklySchedule.get(index).remove();
           String parse=temp.toString();
           parse=parse.substring(11, 16);
           day.appendText(parse+"-");
           temp=weeklySchedule.get(index).remove();
           parse=temp.toString();
           parse=parse.substring(11, 16);
           day.appendText(parse+"\n");
       }
    }
    
    private void updateDayHeaders(DayOfWeek day, Label header)
    {
        String temp=day.getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        header.setText(temp);
    }
    
    @FXML
    private void onCheckMessageButtonPress()
    {
        instance.swapToMessages();
    }
    
    @FXML
    private void onSwapShiftButtonPress()
    {
        instance.swapToShiftSwapSwapShift();
    }
    
    private void setActiveButtons()
    {
	if(this.access == 3) {
	    youAreALabel.setText("Owner Settings");
	}
	else if(this.access == 2) {
	    youAreALabel.setText("Manager Settings");
	}
	else {
	    youAreALabel.setText("Worker Settings");
	}
	
	if(this.access < 3) {
	    systemSettingsButton.setVisible(false);
	}
	if(this.access < 2) {
	    addEmployeeButton.setVisible(false);
	    assignShiftsButton.setVisible(false);
	}
    }
    
    @FXML
    void onAddEmployeeButtonPress(ActionEvent event) {
	instance.swapToManagerSettings();
    }
    
    @FXML
    void onAssignShiftsPress(ActionEvent event)
    {
	instance.swapToAssignShift();
    }

    @FXML
    void onSystemSettingsPress(ActionEvent event) {

    }
    
    @FXML
    void onScheduleUpdateButtonPress(ActionEvent event) 
    {
        LinkedList<LinkedList<Timestamp>> weeklySchedule=instance.grabScheduleWeekly();
        //First, we need to figure out what the first day of the week is.
        LocalDate temp=instance.getCurrentDate(); 
        DayOfWeek weekday=temp.getDayOfWeek();
        updateDayHeaders(weekday,day0Label);
        weekday=weekday.plus(1);
        updateDayHeaders(weekday,day1Label);
        weekday=weekday.plus(1);
        updateDayHeaders(weekday,day2Label);
        weekday=weekday.plus(1);
        updateDayHeaders(weekday,day3Label);
        weekday=weekday.plus(1);
        updateDayHeaders(weekday,day4Label);
        weekday=weekday.plus(1);
        updateDayHeaders(weekday,day5Label);
        weekday=weekday.plus(1);
        updateDayHeaders(weekday,day6Label);
        
        if(weeklySchedule!=null)
        {
            populateSchedule(weeklySchedule,0, day0Schedule);
            populateSchedule(weeklySchedule,1, day1Schedule);
            populateSchedule(weeklySchedule,2, day2Schedule);
            populateSchedule(weeklySchedule,3, day3Schedule);
            populateSchedule(weeklySchedule,4, day4Schedule);
            populateSchedule(weeklySchedule,5, day5Schedule);
            populateSchedule(weeklySchedule,6, day6Schedule);
        }
    }
    
    @FXML
    void onCheckAvailableShiftsPress(ActionEvent event) 
    {
        instance.swapToTakeShift();
    }
    
    @FXML
    void logOut() 
    {
	instance.logOut();
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) 
    {    
        
    }    
    
}
