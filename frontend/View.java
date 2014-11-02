package frontend;

import java.io.InputStream;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.LinkedList;



/**
 *
 * @author erik & Warren
 */
public class View extends Application
{
    private Stage curStage;
    private static View instance;
    private static String userID;
    private static LocalDate currentDate;
    
    public View() {
	this.instance = this;
    }
    
    @Override
    public void start(Stage primaryStage)
    {
	try {
	    curStage = primaryStage;
	    beginLogin();
            setDate();
            primaryStage.show();
	}
	catch (Exception e) {
	    System.out.println(e.getMessage());
	}
    }
    
    private void beginCalendar() {
	try {
            FXMLCalendarController calendar = 
		    (FXMLCalendarController) sceneTransition("CalendarPage.fxml");
            calendar.setApp(instance);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    
    private void beginHomescreen() {
	try {
            FXMLProntPageController homescreen = 
		    (FXMLProntPageController) sceneTransition("FXMLProntPage.fxml");
            homescreen.setApp(instance);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    
    /* Grabs the current date and puts it into our program. Usually we would grab the current date,
    *  but because the shifts are fixed and not being added to right now, we are using October 10th,
    * 2014.
    */
    private void setDate(){
        currentDate=LocalDate.of(2014,10,25);
        System.out.println("Current internal date is: "+currentDate.toString());
    }
    
    private void beginLogin() {
        try {
            LoginPageController login = (LoginPageController) sceneTransition("LoginPage.fxml");
            login.setApp(instance);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    
    /*Checks if the login credentials are true, and if they are, changes to the main page.
    * Otherwise, it returns false, and passes the result back to the login screen.
    */
    public boolean logIn(String username, String password) {
	if(Input.authenticate(username, password)) 
        {
            userID = username;
	    beginHomescreen();
	    return true;
        } 
        else 
        {
	    return false;
	}
    }
    
    //These are all just ways for Controllers to use swap screen functions.
    protected void swapToCalendar()
    {
        beginCalendar();
    }
    
    protected void swapToProntPage()
    {
        beginHomescreen();
    }
    
    protected LinkedList<LinkedList<Timestamp>> grabScheduleWeekly()
    {
        Timestamp[] schedule=Input.getSchedule(userID);
        //This will hold the weekly schedule, which is made up of 7 day schedules.
        //Can't do List of Arrays in java, so this will have to do.
        LinkedList<LinkedList<Timestamp>> weeklySchedule= new LinkedList<LinkedList<Timestamp>>();
        int i=0;
        
        //Populate base LinkedList with day schedules.
        while(i<7)
        {
            weeklySchedule.add(new LinkedList<Timestamp>());
            i=i+1;
        }
        
        int arrLength= schedule.length;
        LocalDate compare=currentDate;
        LocalDate temp;
        String parser;
        int dateChecker=0; //Which day are we looking at putting schedules in?
        i=0;
        while(i<arrLength)//Do we still have shifts to check?
        {
            parser=schedule[i].toString();
            parser=parser.substring(0, 10);
            temp= LocalDate.parse(parser);
            int temp2=compare.compareTo(temp);
            
            //Is it in the day?
            if(compare.compareTo(temp)>0)
            {
                i=i+2;
            }
            else if(compare.compareTo(temp)<0)
            {
                dateChecker=dateChecker+1;
                if(dateChecker==7)//Past end of week?
                {
                    i=arrLength; //Break loop
                }
                else
                {
                    compare=compare.plusDays(1);
                }   
            }
            
            
            else
            {
                //Add start and end times to that days schedule.
                weeklySchedule.get(dateChecker).add(schedule[i]);
                weeklySchedule.get(dateChecker).add(schedule[i+1]);
                i=i+2;
            }
        }
        
        
        
        return weeklySchedule;
    }
    
    private Initializable sceneTransition(String fxml) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        InputStream in = View.class.getResourceAsStream(fxml);
        loader.setBuilderFactory(new JavaFXBuilderFactory());
        loader.setLocation(View.class.getResource(fxml));
        AnchorPane page;
        try {
            page = (AnchorPane) loader.load(in);
        } finally {
            in.close();
        } 
        Scene scene = new Scene(page, 800, 600);
        curStage.setScene(scene);
        curStage.sizeToScene();
        return (Initializable) loader.getController();
    }
    
    /**
     * This should never launch if JavaFX works correctly.
     * @param args The command line arguments.
     */
    public static void main(String[] args) {
	launch(args);
    }
}
