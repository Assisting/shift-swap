package frontend;


import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

/**
 * @author Warren Fehr,wwf594
 */
public class NewMessagePageController extends AnchorPane implements Initializable {

    @FXML
    private TextArea messageField;
    
    @FXML
    private TextArea recipientField;
    
    @FXML
    private Label messageLabel;
    // The default font colour
    public Color fontColor;

    private View instance;
    
    /**
     * Sets up the scene
     * @param application the top-level view container
     */
    public void setApp(View application){
        this.instance = application;
	
	fontColor = Color.web("#41373D");
	
	messageField.setWrapText(true);
    }
    
    @FXML
    void onSendButtonPress() 
    {
        String message = messageField.getText();
        if(message.length()>255)//If message is over character limit
        {
	    messageLabel.setTextFill(Color.FIREBRICK);
            messageLabel.setText("Message over 255 character limit");
        }
        else if(!instance.sendMessage(message,recipientField.getText()))//If user is not in system
        {
            messageLabel.setTextFill(Color.FIREBRICK);
            messageLabel.setText("Recipient does not exist");
        }
        else
        {
            messageLabel.setTextFill(fontColor);
            messageLabel.setText("Message sent");
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
    
    @FXML
    void checkForKeypress(KeyEvent event)
    {
	if(event.getCode() == KeyCode.ENTER) {
	    // Mustn't interfere with newlines in a message
	    if(!(messageField.isFocused()))
	    {
		onSendButtonPress();
	    }
	}
    }
}