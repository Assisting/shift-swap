package frontend;


import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

/**
 * @author Warren Fehr,wwf594
 */
public class NewMessagePageController extends AnchorPane implements Initializable {

    @FXML
    private Label recipientDoesntExist;

    @FXML
    private Label charcterLimit;

    private View instance;
    
    public void setApp(View application){
        this.instance = application;
    }
    
    @FXML
    void onSendButtonPress() {

    }

    @FXML
    void onBackButtonPress() 
    {
        instance.swapToMessages();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) 
    {
        
    }
    
}