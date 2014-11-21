package frontend;


import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;

/**
 * @author Warren Fehr,wwf594
 */
public class NewMessagePageController extends AnchorPane implements Initializable {

    @FXML
    private Label recipientDoesntExist;

    @FXML
    private TextArea messageField;
    
    @FXML
    private Label characterLimit;
    
    @FXML
    private TextArea recipientField;

    @FXML
    private Label successField;

    private View instance;
    
    public void setApp(View application){
        this.instance = application;
    }
    
    @FXML
    void onSendButtonPress() 
    {
        String message=messageField.getText();
        if(message.length()>255)//If message is over character limit
        {
            characterLimit.setVisible(true);
            successField.setVisible(false);
        }
        else if(!instance.sendMessage(message,recipientField.getText()))//If user is not in system
        {
            recipientDoesntExist.setVisible(true);
            successField.setVisible(false);
        }
        else
        {
            successField.setVisible(true);
            recipientDoesntExist.setVisible(false);
            characterLimit.setVisible(false);
            messageField.setText("");
            recipientField.setText("");   
        }
        
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