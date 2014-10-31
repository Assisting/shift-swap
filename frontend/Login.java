/*
 * Erik LaBine, ejl389, 11122765
 * ejl389@mail.usask.ca
 * For use in shift-swap project; CMPT 370, University of Saskatchewan
 * 2014
 */

package frontend;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
/**
 * Introductory GUI
 */
public class Login extends Application
{
     @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("LoginPage.fxml"));
        
        Scene scene = new Scene(root);
        
        stage.setScene(scene);
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
