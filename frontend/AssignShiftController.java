package frontend;

import controller.Shift;
import java.net.URL;
import java.sql.Timestamp;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;

/**
 * FXML Controller class
 *
 * @author erik
 */
public class AssignShiftController implements Initializable
{
    @FXML
    private CheckBox sameDayCheck;
    @FXML
    private TextField userIDField;
    @FXML
    private TextField startDay;
    @FXML
    private TextField startMonth;
    @FXML
    private TextField startYear;
    @FXML
    private TextField startHour;
    @FXML
    private TextField startMinute;
    @FXML
    private TextField endDay;
    @FXML
    private TextField endMonth;
    @FXML
    private TextField endYear;
    @FXML
    private TextField endHour;
    @FXML
    private TextField endMinute;
    @FXML
    private Label shiftConfLabel;
    
    // The default font colour
    public Color fontColor;
    
    private View instance;
    
    public void setApp(View newInstance)
    {
	this.instance = newInstance;
	
	startDay.focusedProperty().addListener(new ChangeListener<Boolean>() {
	    @Override
	    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
		if(!newValue) {
		    dayChange();
		}
	    }
	});
	
	startMonth.focusedProperty().addListener(new ChangeListener<Boolean>() {
	    @Override
	    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
		if(!newValue) {
		    monthChange();
		}
	    }
	});
	
	startYear.focusedProperty().addListener(new ChangeListener<Boolean>() {
	    @Override
	    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
		if(!newValue) {
		    yearChange();
		}
	    }
	});
	
	toggleSameDay();
	fontColor = Color.web("#41373D");
    }

    @Override
    public void initialize(URL url, ResourceBundle rb)
    {

    }    

    @FXML
    private void toggleSameDay()
    {
	if(sameDayCheck.isSelected()) {
	    endDay.setText(startDay.getText());
	    endMonth.setText(startMonth.getText());
	    endYear.setText(startYear.getText());
	    
	    endDay.setFocusTraversable(false);
	    endMonth.setFocusTraversable(false);
	    endYear.setFocusTraversable(false);
	    
	    endDay.setEditable(false);
	    endMonth.setEditable(false);
	    endYear.setEditable(false);
	}
	else {
	    endDay.setEditable(true);
	    endMonth.setEditable(true);
	    endYear.setEditable(true);
	    
	    endDay.setFocusTraversable(true);
	    endMonth.setFocusTraversable(true);
	    endYear.setFocusTraversable(true);
	}
    }

    @FXML
    private void onSubmitPress()
    {
	int firstDay = 0;
	int firstMonth = 0;
	int firstYear = 0;
	int firstHour = 0;
	int firstMinute = 0;
	    
	int lastDay = 0;
	int lastMonth = 0;
	int lastYear = 0;
	int lastHour = 0;
	int lastMinute = 0;
	
	boolean canContinue = true;
	
	try {
	    firstDay = Integer.parseInt(startDay.getText());
	    firstMonth = Integer.parseInt(startMonth.getText());
	    firstYear = Integer.parseInt(startYear.getText());
	    firstHour = Integer.parseInt(startHour.getText());
	    firstMinute = Integer.parseInt(startMinute.getText());
	    
	    lastDay = Integer.parseInt(endDay.getText());
	    lastMonth = Integer.parseInt(endMonth.getText());
	    lastYear = Integer.parseInt(endYear.getText());
	    lastHour = Integer.parseInt(endHour.getText());
	    lastMinute = Integer.parseInt(endMinute.getText());
	}
	catch(NumberFormatException e) {
	    shiftConfLabel.setTextFill(Color.FIREBRICK);
	    shiftConfLabel.setText("Non-numeric input detected");
	    
	    canContinue = false;
	}
	
	LocalDateTime shiftStart = LocalDateTime.now();
	LocalDateTime shiftEnd = LocalDateTime.now();
	
	try {
	    shiftStart = LocalDateTime.of(firstYear, firstMonth, firstDay,
		    firstHour, firstMinute);
	    shiftEnd = LocalDateTime.of(lastYear, lastMonth, lastDay,
		    lastHour, lastMinute);
	}
	catch(DateTimeException e) {
	    shiftConfLabel.setTextFill(Color.FIREBRICK);
	    shiftConfLabel.setText("Invalid start or end time");
	    
	    canContinue = false;
	}
	
	if((lastMinute % 15 != 0) || (firstMinute % 15 != 0)) {
	    shiftConfLabel.setTextFill(Color.FIREBRICK);
	    shiftConfLabel.setText("Shift must end on 15 minute boundary");
	    
	    canContinue = false;
	}
	
	if(shiftStart.isAfter(shiftEnd)) {
	    shiftConfLabel.setTextFill(Color.FIREBRICK);
	    shiftConfLabel.setText("Start time must be before end time");
	    
	    canContinue = false;
	}
	
	if(instance.isUserInSystem(userIDField.getText())) {
	    shiftConfLabel.setTextFill(Color.FIREBRICK);
	    shiftConfLabel.setText("User not found");
	    
	    canContinue = false;
	}
	
	Timestamp start = Timestamp.valueOf(shiftStart);
	Timestamp end = Timestamp.valueOf(shiftEnd);
	
	// We got through the gauntlet and can continue
	if(canContinue == true) {
	    Shift newShift = new Shift(userIDField.getText(), start, end);
	    
	    instance.assignShift(newShift);
	    
	    shiftConfLabel.setTextFill(fontColor);
	    shiftConfLabel.setText("Successfully gave shift to " 
				+ userIDField.getText());
	}
    }

    @FXML
    private void onBackPress()
    {
	instance.swapToProntPage();
    }
    
    @FXML
    void dayChange()
    {
	if(sameDayCheck.isSelected()) {
	    endDay.setText(startDay.getText());
	}
    }

    @FXML
    void monthChange()
    {
	if(sameDayCheck.isSelected()) {
	    endMonth.setText(startMonth.getText());
	}
    }

    @FXML
    void yearChange()
    {
	if(sameDayCheck.isSelected()) {
	    endYear.setText(startYear.getText());
	}
    }
}
