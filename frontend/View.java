package frontend;

import controller.Shift;
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
    

    
    /* Grabs the current date and puts it into our program. Usually we would grab the current date,
    *  but because the shifts are fixed and not being added to right now, we are using October 25th,
    * 2014.
    */
    private void setDate(){
        currentDate=LocalDate.of(2014,10,25);
        System.out.println("Current internal date is: "+currentDate.toString());
    }
    
    
    //All ways to swap between screens
    private void beginMessages(){
        try {
            MessagesPageController message = (MessagesPageController) sceneTransition("MessagesPage.fxml");
            message.setApp(instance);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    
    private void beginLogin() {
        try {
            LoginPageController login = (LoginPageController) sceneTransition("LoginPage.fxml");
            login.setApp(instance);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    
    private void beginManagerSettings() {
	try {
            ManagerSettingsController managerSettings = 
		    (ManagerSettingsController) sceneTransition("ManagerSettings.fxml");
	    managerSettings.setApp(instance);
        } catch (Exception e) {
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
    
    private void beginNewMessage() {
	try {
            NewMessagePageController message = 
		    (NewMessagePageController) sceneTransition("NewMessagePage.fxml");
            message.setApp(instance);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    
    private void beginTakeShift() {
        try {
            TakePageController take = (TakePageController) sceneTransition("TakePage.fxml");
            take.setApp(instance);
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
    
    protected void swapToTakeShift()
    {
        beginTakeShift();
    }
    
    protected void swapToProntPage()
    {
        beginHomescreen();
    }
    
    protected void swapToNewMessage(){
        beginNewMessage();
    }
    
    protected void swapToManagerSettings() {
	beginManagerSettings();
    }
    
    protected void swapToMessages(){
        beginMessages();
    }
    
    protected LocalDate getCurrentDate()
    {
        return currentDate;
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
    
    protected LinkedList<LinkedList<Timestamp>> grabScheduleMonthly()
    {
        Timestamp[] schedule=Input.getSchedule(userID);
        //This will hold the weekly schedule, which is made up of 7 day schedules.
        //Can't do List of Arrays in java, so this will have to do.
        LinkedList<LinkedList<Timestamp>> monthlySchedule= new LinkedList<LinkedList<Timestamp>>();
        int arrLength= schedule.length;
        LocalDate compare=currentDate;
        LocalDate temp;
        String parser;
        int dateChecker=0; //Which day are we looking at putting schedules in?
        int month=compare.getMonthValue();
        int monthLimit=0; //How many days are in this month?
        int i=0;
         //We need to determine how many days are in this month.
        if(month==2)//If it is February..
        {
            if(compare.isLeapYear())
            {
                monthLimit=29;
            }
            else
            {
                monthLimit=28;
            }
        }
        else if(month==4 || month==6 || month==9 || month==11)
        {
            monthLimit=30;
        }
        else
        {
            monthLimit=31;//All other months have 31.
        }
        
        //Populate base LinkedList with day schedules.
        while(i<monthLimit)
        {
            monthlySchedule.add(new LinkedList<Timestamp>());
            i=i+1;
        }
        
        
        i=0;
        while(i<arrLength)//Do we still have shifts to check?
        {
            parser=schedule[i].toString();
            parser=parser.substring(0, 10);
            temp= LocalDate.parse(parser);
            int test=temp.getDayOfMonth();
            //Is it in the current month?
            if((temp.getMonthValue())<month)
            {
                i=i+2;
            }
            else if((temp.getMonthValue())>month)
            {
                //Break, because all shifts after this will be too far into the future as well.
                i=arrLength;
            }
            
            
            //Is it in the current day?
            else if((temp.getDayOfMonth()-1)>dateChecker)
            {
                dateChecker=dateChecker+1;
                if(dateChecker==monthLimit)//Past end of month? 
                {
                    /*This is technically redundant, though, because it should roll into next month.*/
                    i=arrLength; //Break loop
                }
            }
            
            
            else
            {
                //Add start and end times to that days schedule.
                monthlySchedule.get(dateChecker).add(schedule[i]);
                monthlySchedule.get(dateChecker).add(schedule[i+1]);
                i=i+2;
            }
        }
        
        
        
        return monthlySchedule;
    }
    
    protected LinkedList<String> grabInbox()
    {
        String input=Input.getEmployeeMessages(userID);
        LinkedList<String> inbox = new LinkedList<String>();
        String partial;
        int i=0;
        int stringStart=0;
        while(i<input.length())
        {
            if(input.charAt(i)=='\n')
            {
                partial=input.substring(stringStart, i);
                inbox.add(partial);
                stringStart=i+1;
            }
            i=i+1;
        }
      
        return inbox;
    }
    
    protected LinkedList<Shift> grabGiveShifts()
    {
        Shift[] shifts=Input.getGiveList();
        LinkedList<Shift> shiftList= new LinkedList<Shift>();
        int i=0;
        while(i<shifts.length)
        {
            shiftList.add(shifts[i]);
            i=i+1;
        }
        
        return shiftList;
    }
    
    protected boolean sendMessage(String message, String recipient)
    {
        if(!Input.isUsernameUnique(recipient))
        {
            Input.sendMessage(userID,recipient,message);
            return true;
        }
        else
        {
            return false;
        }
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
