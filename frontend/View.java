package frontend;

import java.io.InputStream;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 *
 * @author erik & Warren
 */
public class View extends Application
{
    private Stage curStage;
    private static View instance;
    private static String userID;
    
    public View() {
	this.instance = this;
    }
    
    @Override
    public void start(Stage primaryStage)
    {
	try {
	    curStage = primaryStage;
	    beginLogin();
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
