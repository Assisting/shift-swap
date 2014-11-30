package frontend;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.fxml.FXML;
import java.util.LinkedList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
/**
 * @author Warren Fehr, wwf594
 */
public class MessagesPageController extends AnchorPane implements Initializable {
    


    LinkedList<String> inbox;
    
    @FXML
    private TextArea messageArea;

    @FXML
    private TextArea senderField;
    
    @FXML
    private ListView<String> inboxGrid;
    
    @FXML
    private Button rejectButton;

    @FXML
    private Button acceptButton;
    
    @FXML
    private Button deleteButton;
    
    /**
    * We can use this instance to pass data back to the top level.
    */
    private View instance;
    
    public void setApp(View application)
    {
        this.instance = application;
	updateInbox();
    }
    
    @FXML
    void onUpdateButtonPress() 
    {
       updateInbox();   
    }
    
    private TextArea populateHeader(TextArea tempText, int index)
    {
        boolean done=false;
        tempText.setEditable(false);
        String message=inbox.get(index);
        int i=7;
        while(i<message.length() && !done)
        {
            if(message.charAt(i)==' ')
            {
                String temp=message.substring(6,i);
                tempText.setText(temp);
                done=true;
            }
            i=i+1;
        }
        
        return tempText;
    }
    
    @FXML
    void onBackButtonPress() 
    {
        instance.swapToProntPage();
    }
    
    @FXML
    void onCreateNewMessageButtonPress() 
    {
        instance.swapToNewMessage();
    }

     @FXML
    void onAcceptButtonPress() {

    }

    @FXML
    void onRejectButtonPress() {

    }

    @FXML
    void onDeleteButtonPress() {

    }


    private void populateMessage(String message)
    {
        if(message==null)
        {
            messageArea.setText("");
            senderField.setText("");
        }
        else
        {
            boolean done=false;
            int i=7;
            while(i<message.length() && !done)
            {
                if(message.charAt(i)==' ')
                {
                    senderField.setText(message.substring(6,i));
                    messageArea.setText(message.substring(i));
                    done=true;
                }
                i=i+1;
        }
        }
    }
    
    private ObservableList<String> grabInbox()
    {
        inbox=instance.grabInbox();
        ObservableList<String> messageData = FXCollections.observableArrayList();
        int i=0;
        while(i<inbox.size())
        {
            messageData.add(inbox.get(i));
            i=i+1;
        }
        return messageData;
    }
    
    public void updateInbox()
    {
        inboxGrid.setItems(grabInbox());
        inboxGrid.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> populateMessage(newValue));
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) 
    {
        
    }
}
