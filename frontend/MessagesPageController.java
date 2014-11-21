package frontend;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.fxml.FXML;
import java.util.LinkedList;
/**
 * @author Warren Fehr, wwf594
 */
public class MessagesPageController extends AnchorPane implements Initializable {
    
    String inbox;
    
     /**
     * We can use this instance to pass data back to the top level.
     */
    private View instance;
    
    public void setApp(View application){
 
       
        this.instance = application;
    }
    
    @FXML
    void onUpdateButtonPress() 
    {
        LinkedList<String> stuff=instance.grabInbox();
    }

    @FXML
    void onBackButtonPress() 
    {
        instance.sendMessage("ttori","Hello, Tori!");
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) 
    {

    }
}
