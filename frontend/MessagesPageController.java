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
import javafx.scene.control.Label;
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
    private Label cantDeleteLabel;
    
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
    void onAcceptButtonPress() 
    {
        if(checkIfTradable())
        {
            String[] neededValues=parseTimeStampsTrade();
            Timestamp temp1=Timestamp.valueOf(neededValues[1]);
            Timestamp temp2=Timestamp.valueOf(neededValues[2]);
            Timestamp[] startTime={temp1,temp2};
            instance.sendTradeRequestResponse(senderField.getText(),startTime,true);
            instance.deleteMessage(senderField.getText(),sendTimes.get(selectedIndex));
            updateInbox();   
        }
        else if(checkIfApprovable())
        {
            String[] neededValues=parseTimeStampsApproval();
            instance.sendManagerApproval(neededValues, true);
            instance.deleteMessage(senderField.getText(),sendTimes.get(selectedIndex));
            updateInbox();  
        }
    }

    @FXML
    void onRejectButtonPress() 
    {
        if(checkIfTradable())
        {
            String[] neededValues=parseTimeStampsTrade();
            Timestamp temp1=Timestamp.valueOf(neededValues[1]);
            Timestamp temp2=Timestamp.valueOf(neededValues[2]);
            Timestamp[] startTime={temp1,temp2};
            instance.sendTradeRequestResponse(senderField.getText(),startTime,false);
            instance.deleteMessage(senderField.getText(),sendTimes.get(selectedIndex));
            updateInbox();   
        }
        else if(checkIfApprovable())
        {
            String[] neededValues=parseTimeStampsApproval();
            instance.sendManagerApproval(neededValues, false);
            instance.deleteMessage(senderField.getText(),sendTimes.get(selectedIndex));
            updateInbox();  
        }
    }

    @FXML
    void onDeleteButtonPress() 
    {
        if(checkIfTradable())
        {
            cantDeleteLabel.setVisible(true);
        }
        else
        {
            instance.deleteMessage(senderField.getText(),sendTimes.get(selectedIndex));
            updateInbox();
        }
    }

    private String[] parseTimeStampsTrade()
    {
        String sender;
        String recipient;
        String parse=inbox.get(selectedIndex);
        String startTime1;
        String startTime2;
        int startOfEntry;
        int i=32;
        startOfEntry=i;
        //Get Sender
        while(parse.charAt(i)!=' ')
        {
            i=i+1;
        }
        sender=parse.substring(startOfEntry,i);
        
        //Jump forward to timeStamp1;
        i=i+16;
        startOfEntry=i;
        while(parse.charAt(i)!=' ')
        {
            i=i+1;
        }
        i=i+1;
        //One space down....
        while(parse.charAt(i)!='.' && parse.charAt(i) !=' ')
        {
            i=i+1;
        }
        
        startTime1=parse.substring(startOfEntry,i);
        if(parse.charAt(i)=='.')
        {
            while(parse.charAt(i)!=' ')
            {
                i=i+1;
            }
        }
        
        
     
        //Another timeStamp;
        i=i+5;
        startOfEntry=i;
        while(parse.charAt(i)!=' ')
        {
            i=i+1;
        }
        i=i+1;
        //One space down....
        while(parse.charAt(i)!='.' && parse.charAt(i) !=' ')
        {
            i=i+1;
        }
        startTime2=parse.substring(startOfEntry,i);
        
        String[] returnValue = {sender,startTime1,startTime2};
        
        return returnValue;
        
        
    }
    
    private String[] parseTimeStampsApproval()
    {
        String sender;
        String recipient;
        String parse=inbox.get(selectedIndex);
        String startTime1;
        String startTime2;
        int startOfEntry;
        int i=36;
        startOfEntry=i;
        //Get Sender
        while(parse.charAt(i)!=' ')
        {
            i=i+1;
        }
        sender=parse.substring(startOfEntry,i);
        
        //Jump forward to timeStamp1;
        i=i+16;
        startOfEntry=i;
        while(parse.charAt(i)!=' ')
        {
            i=i+1;
        }
        i=i+1;
        //One space down....
        while(parse.charAt(i)!='.' && parse.charAt(i) !=' ')
        {
            i=i+1;
        }
        
        startTime1=parse.substring(startOfEntry,i);
        if(parse.charAt(i)=='.')
        {
            while(parse.charAt(i)!=' ')
            {
                i=i+1;
            }
        }
        
        
        //Next, we move to Recipient.
        i=i+6;
        startOfEntry=i;
        while(parse.charAt(i)!=' ')
        {
            i=i+1;
        }
        recipient=parse.substring(startOfEntry,i);
        
        //Another timeStamp;
        i=i+5;
        startOfEntry=i;
        while(parse.charAt(i)!=' ')
        {
            i=i+1;
        }
        i=i+1;
        //One space down....
        while(parse.charAt(i)!='.' && parse.charAt(i) !=' ')
        {
            i=i+1;
        }
        startTime2=parse.substring(startOfEntry,i);
        
        String[] returnValue = {sender,recipient,startTime1,startTime2};
        
        return returnValue;
        
    }
    
    private void populateMessage(String message)
    {
        disableTradeButtons();
        cantDeleteLabel.setVisible(false);
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
                    else if(checkIfApprovable())
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
        ObservableList<String> messageData = FXCollections.observableArrayList();
        
        sendTimes=temp.getSendTimes();
        inbox=temp.getMessages();
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
        disableTradeButtons();
        deleteButton.setVisible(false);
        deleteButton.setDisable(true);
        cantDeleteLabel.setVisible(false);
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
    
    private boolean checkIfApprovable()
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
            if(checkMessage.charAt(i)=='A')
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
