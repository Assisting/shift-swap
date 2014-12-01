package frontend;

import controller.Inbox;
import java.net.URL;
import java.sql.Timestamp;
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
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
/**
 * @author Warren Fehr, wwf594
 */
public class MessagesPageController extends AnchorPane implements Initializable {
    
    private int selectedIndex;
    
    private LinkedList<Timestamp> sendTimes;

    private LinkedList<String> inbox;
    
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
        disableTradeButtons();
        deleteButton.setVisible(false);
        deleteButton.setDisable(true);
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
                    selectedIndex=inboxGrid.getSelectionModel().getSelectedIndex();
                    deleteButton.setVisible(true);
                    deleteButton.setDisable(false);
                    if(checkIfTradable())
                    {
                        enableTradeButtons();
                    }
                    done=true;
                }
                i=i+1;
            }
        }
    }
    
    private ObservableList<String> grabInbox()
    {
        Inbox temp=instance.grabInbox();
        sendTimes=temp.getSendTimes();
        inbox=temp.getMessages();
        ObservableList<String> messageData = FXCollections.observableArrayList();
        int i=0;
        while(i<inbox.size())
        {
            messageData.add(inbox.get(i));
            i=i+1;
        }
        return messageData;
    }
    
    private void updateInbox()
    {
        inboxGrid.setItems(grabInbox());
        inboxGrid.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> populateMessage(newValue));
    }
    
    private void enableTradeButtons()
    {
        acceptButton.setVisible(true);
        acceptButton.setDisable(false);
        rejectButton.setVisible(true);
        rejectButton.setDisable(false);
    }
    
    private void disableTradeButtons()
    {
        acceptButton.setVisible(false);
        acceptButton.setDisable(true);
        rejectButton.setVisible(false);
        rejectButton.setDisable(true);
    }
    
    private boolean checkIfTradable()
    {
        String checkMessage=inbox.get(selectedIndex);
        boolean foundOpCode=false;
        int i=0;
        while(i<checkMessage.length() && !foundOpCode)
        {
            if(checkMessage.charAt(i)=='>')
            {
               foundOpCode=true;
            }
            i=i+1;
        }
        i=i+1;
        if(foundOpCode)
        {
            if(checkMessage.charAt(i)=='T')
            {
                return true;
            }
        }
        
        return false;
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) 
    {
        
    }
    
    @FXML
    void checkForKeypress(KeyEvent event)
    {
	if(event.getCode() == KeyCode.ENTER) {
	    onUpdateButtonPress();
	}
	else if(event.getCode() == KeyCode.BACK_SPACE) {
	    onBackButtonPress();
	}
    }
}
