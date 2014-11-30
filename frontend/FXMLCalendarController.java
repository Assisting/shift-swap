package frontend;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.AnchorPane;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.DayOfWeek;
import java.util.LinkedList;
import java.time.DayOfWeek;
import javafx.scene.Node;
import javafx.scene.control.TextArea;

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
	
	onStuffButtonClicked(null);
    }

    @FXML
    void onStuffButtonClicked(ActionEvent event) 
    {
        //Grab monthly schedule
        LinkedList<LinkedList<Timestamp>> monthlySchedule= instance.grabScheduleMonthly();
        if(monthlySchedule==null)
        {
            return;
        }
        
        //Get the DayoftheWeek for the start of month for calendar.
        LocalDate temp=instance.getCurrentDate();
        temp=temp.withDayOfMonth(1);
        DayOfWeek startOfMonth=temp.getDayOfWeek();
        int currentDate=0;
        int column=startOfMonth.getValue();
        int row=0;
        TextArea daySchedule;
        daySchedule=populateSchedule(monthlySchedule,currentDate,new TextArea());
        calendarGrid.add(daySchedule,column,row);
        currentDate=currentDate+1;
        if(column==7)
        {
            column=0;
        }
        else
        {
            column=column+1;
        }
        
        while(currentDate<monthlySchedule.size())
        {
            if(column<7)
            {
                TextArea tempText=new TextArea();
                tempText.setEditable(false);
                daySchedule=populateSchedule(monthlySchedule,currentDate,tempText);
                calendarGrid.add(daySchedule,column,row);
                currentDate=currentDate+1;
                column=column+1;
            }
            else
            {
                column=0;
                row=row+1;
            }
        }
        
    }

   
    
    private TextArea populateSchedule(LinkedList<LinkedList<Timestamp>> weeklySchedule,int index,TextArea day)
    {
       day.clear();
       day.appendText((index+1)+"\n");
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
       return day;
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
