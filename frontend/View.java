package frontend;

import controller.Controller;
import java.io.InputStream;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import java.sql.Date;
import java.time.LocalDate;


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
    public Controller controller;
    
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
        currentDate=LocalDate.of(2014,10,18);
        System.out.println(currentDate.toString());
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
        controller = new Controller();
	if(Input.authenticate(username, password, controller)) 
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
    
    protected String grabScheduleWeekly()
    {
        Date[] schedule=Input.getSchedule(userID, controller);
       
        return "Hello";//This works
        //return schedule[0].toString();//This does not, gives null.
        
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
