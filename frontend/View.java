package frontend;

import controller.Employee;
import controller.Inbox;
import controller.RequestResults;
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
import javafx.application.HostServices;



/**
 *
 * @author erik & Warren
 */
public class View extends Application
{
    //A point in the far future we need to reference for get ShiftRange
    private final long END_OF_TIME=100000000000000L;
    
    private Stage curStage;
    private static View instance;
    
    //The user currently logged in.
    private static String userID;
    
    //The current Date.
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
	    
	    int access = Input.getEmployeeAccessLevel(userID);
	    
            homescreen.setApp(instance, access);
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
    
    private void beginSwapShift()
    {
	try {
            SwapShiftController swapShift = (SwapShiftController) sceneTransition("SwapShift.fxml");
            swapShift.setApp(instance);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    
    private void beginAssignShift()
    {
	try {
            AssignShiftController assignShift = (AssignShiftController) sceneTransition("AssignShift.fxml");
            assignShift.setApp(instance);
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
    
    /**
     * Just what is says on the tin; we don't have to worry about security so
     * much with logging out vs logging in since it fails safe.
     */
    public void logOut() {
	userID = null;
	beginLogin();
    }
    
    //These are all just ways for Controllers to use swap screen functions.
    protected void swapToCalendar()
    {
        beginCalendar();
    }
    
    protected void swapToShiftSwapSwapShift()
    {
	beginSwapShift();
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
    
    protected void swapToAssignShift()
    {
	beginAssignShift();
    }
    
    /**
     * Grabs the current date from the computer.
     * @return The current date. 
     */
    protected LocalDate getCurrentDate()
    {
        return currentDate;
    }
    
    /**
     * Grabs the weekly schedule for the user from the database for the
     * currently logged in user. With the advent of the Shift class, this is
     * slightly out of date.
     * @return A linked list holding a Linked List of Timestamps representing a weekly schedule.
     * Time stamps come in pairs, so each of the secondary LinkedLists has a pair of Timestamps representing
     * a range of time. 
     */
    protected LinkedList<LinkedList<Timestamp>> grabScheduleWeekly()
    {
        Timestamp[] schedule=Input.getSchedule(userID);
        if(schedule==null)
        {
            return null;
        }
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
    
    /**
     * Grabs the weekly schedule for the user from the database for the
     * currently logged in user. With the advent of the Shift class, this is
     * slightly out of date.
     * @return A linked list holding a Linked List of Timestamps representing a monthly schedule.
     * Time stamps come in pairs, so each of the secondary LinkedLists has a pair of Timestamps representing
     * a range of time. 
     */
    protected LinkedList<LinkedList<Timestamp>> grabScheduleMonthly()
    {
        Timestamp[] schedule=Input.getSchedule(userID);
        //This will hold the weekly schedule, which is made up of 7 day schedules.
        //Can't do List of Arrays in java, so this will have to do.
        LinkedList<LinkedList<Timestamp>> monthlySchedule= new LinkedList<LinkedList<Timestamp>>();
        if(schedule==null)
        {
            return null;
        }
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
    
    /**
     * Grabs all the messages that the current user has been sent.
     * @return A linked list holding a Linked List of Strings which represent the messages. 
     */
    protected Inbox grabInbox()
    {
        RequestResults input=Input.getEmployeeMessages(userID);
        String messages=input.getMessages();
        Timestamp[] sendTimes=input.getShifts();
        LinkedList<String> inbox = new LinkedList<String>();
        LinkedList<Timestamp> sendTimeBox= new LinkedList<Timestamp>();
        String partial;
        int i=0;
        int stringStart=0;
        
        //Parse messages to fill Inbox
        while(i<messages.length())
        {
            if(messages.charAt(i)=='\n')
            {
                partial=messages.substring(stringStart, i);
                inbox.add(partial);
                stringStart=i+1;
            }
            i=i+1;
        }
        
        i=0;
        while(i<sendTimes.length)
        {
            sendTimeBox.add(sendTimes[i]);
            i=i+1;
        }
        return new Inbox(sendTimeBox,inbox);
    }
    
    /**
     * Grabs all the shifts that are currently available for employees to take.
     * @return A linked list holding all the shift data for each available shift. 
     */
    protected LinkedList<Shift> grabGiveShifts()
    {
        Shift[] shifts=Input.getGiveList();
        LinkedList<Shift> shiftList= new LinkedList<Shift>();
        if(shifts==null)
        {
            return shiftList;
        }
        int i=0;
        while(i<shifts.length)
        {
            shiftList.add(shifts[i]);
            i=i+1;
        }
        
        return shiftList;
    }
    
    /**
     * Grabs all the shifts that are available for the currently logged in employee to give away.
     * @return A linked list holding all the shift data for each available shift. 
     */
    protected LinkedList<Shift> grabSelfShifts()
    {
        LinkedList<Shift> schedule= new LinkedList<Shift>();
        String todaysDate=currentDate.toString()+" 01:00:00";
        Timestamp[] shifts=Input.getRangeSchedule(userID, Timestamp.valueOf(todaysDate), new Timestamp(END_OF_TIME));
        if(shifts==null)
        {
            return schedule;
        }
        int i=0;
        while(i<shifts.length)
        {
            Shift temp= new Shift(userID,shifts[i],shifts[i+1]);
            schedule.add(temp);
            i=i+2;
        }

        return schedule;
    }
    
     /**
     * Grabs all shifts for the given user that can be given away.
     * @param username The desired user's shifts.
     * @return A LinkedList containing all the shifts. 
     */
    protected LinkedList <Shift> grabEmployeesShifts(String username)
    {
        LinkedList<Shift> schedule= new LinkedList<Shift>();
        if(Input.isUsernameUnique(username))//If the user doesn't exist
        {
            return null;
        }
        String todaysDate=currentDate.toString()+" 01:00:00";
        Timestamp[] shifts=Input.getRangeSchedule(username, Timestamp.valueOf(todaysDate), new Timestamp(END_OF_TIME));
        if(shifts==null)
        {
            return schedule;
        }
        int i=0;
        while(i<shifts.length)
        {
            Shift temp= new Shift(username,shifts[i],shifts[i+1]);
            schedule.add(temp);
            i=i+2;
        }

        return schedule;
       
        
    }
    
    protected LinkedList <Shift> grabShiftsOnDay(String parse)
    {
        LinkedList<Shift> shiftList=new LinkedList<Shift>();
        Timestamp dayStart=Timestamp.valueOf(parse);
        String parse2=parse.substring(0, 10);
        parse2=parse2+" 23:59:59";
        Timestamp dayEnd=Timestamp.valueOf(parse2);
        Shift[] shifts=Input.getShiftsOnDay(userID,dayStart,dayEnd);
        if(shifts==null)
        {
            return shiftList;
        }
        int i=0;
        while(i<shifts.length)
        {
            shiftList.add(shifts[i]);
            i=i+1;
        }
        
        return shiftList;
    }
    
    /**
     * Sends a message from the current user to another user.
     * @param message The message you wish to send.
     * @param recipient The person you wish to send the message to.
     * @return True if the message was sent, false otherwise. 
     */
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
    
    protected void deleteMessage(String sender ,Timestamp sendTime)
    {
        Input.deleteMessage(sender,userID,sendTime);
    }
    
    protected void sendTradeRequest(Shift want, Shift giveaway)
    {
        Timestamp[] giveawayTime={giveaway.getShiftStartTime(),giveaway.getShiftEndTime()};
        Timestamp[] wantTime={want.getShiftStartTime(),want.getShiftEndTime()};
        Input.createTradeRequest(userID, giveawayTime, want.getEmployeeLogin(), wantTime);
    }
    
    protected void sendTradeRequestResponse(String asker,Timestamp[] startTimes ,boolean acceptance)
    {
        Input.createAcceptRequest(asker,userID,startTimes,acceptance);
    }
    
    /**
     * Sends a request for the logged in employee to take the given shift.
     * @param take The shift you wish to take.
     */
    protected void sendTakeRequest(Shift take)
    {
        Timestamp[] temp={take.getShiftStartTime(),take.getShiftEndTime()};
        Input.createTakeRequest( userID, take.getEmployeeLogin(), temp);
    }
    
    protected boolean sendGiveRequest(Shift give)
    {
        Timestamp[] temp={give.getShiftStartTime(),give.getShiftEndTime()};
        boolean isOnList=isOnGiveList(give);
        if(isOnGiveList(give))
        {
            return false;
        }
        else
        {
            return Input.createGiveRequest(userID, temp);
        }
    }
    
    protected boolean isOnGiveList(Shift shift)
    {
        return Input.createGiveListCheckRequest(shift);
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
    
    protected void addEmployee(String ID, String firstName, String lastName,
	    String password, String email, float wage)
    {
	Employee newEmployee = new Employee(ID, firstName, lastName, 1,
		Input.createHash(password), email, wage);
	
	Input.addNewEmployee(newEmployee);
    }
    
    /**
     * Assign a shift
     * @param shift the pre-made shift to be assigned
     */
    protected void assignShift(Shift shift)
    {
	Input.assignShifts(shift);
    }
    
    protected void modifyEmployee(String ID, String firstName, String lastName,
	    String email, float wage)
    {
	Input.modifyEmployeeInfo(ID, firstName, lastName, email, 1, wage);
    }
    
    protected void changeAccessLevel(String ID, int accessLevel)
    {
	Input.changeEmployeeAccessLevel(ID, accessLevel);
    }
    
    protected void setManager(String employee, String manager)
    {
	Input.changeEmployeesManager(employee, manager);
    }
    
    protected boolean setPassword(String employee, String password)
    {
	Input.changeEmployeePassword(employee, password);
	
	return Input.authenticate(employee, password);
    }
    
    protected void removeEmployee(String employee)
    {
	Input.removeEmployee(employee);
    }
    
    protected String getLoggedInEmployee()
    {
           return userID;     
    }
    
    /**
     * A quick way to search for users
     * @param userID the login of the employee
     * @return False if user is not in system
     */
    protected boolean isUserInSystem(String userID)
    {
	return !(Input.isUsernameUnique(userID));
    }
    
    /**
     * Opens up a browser tab/window, displays the user guide.
     */
    protected void getHelp()
    {
	HostServices hostServices = getHostServices();
	hostServices.showDocument("http://www.eriklabine.com/shift-swap/index.html");
    }
    
    /**
     * This should never launch if JavaFX works correctly.
     * @param args The command line arguments.
     */
    public static void main(String[] args) {
	launch(args);
    }
}
