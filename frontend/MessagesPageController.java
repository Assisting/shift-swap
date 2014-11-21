package frontend;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.fxml.FXML;
import java.util.LinkedList;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
/**
 * @author Warren Fehr, wwf594
 */
public class MessagesPageController extends AnchorPane implements Initializable {
    
    private int pageOfInbox;
    private boolean endOfList;
    LinkedList<String> inbox;
    
    @FXML
    private TextArea messageArea;

    @FXML
    private TextArea senderField;
    
    @FXML
    private GridPane inboxGrid;
    
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
        inbox=instance.grabInbox();
        int row=0;
        boolean endOfList=false;
        while(row<15 && !endOfList)
        {
           if(row==inbox.size())
           {
               endOfList=true;
           }
           else
           {
                TextArea tempText=new TextArea();
                populateHeader(tempText, row+(pageOfInbox*15));
                inboxGrid.add(tempText,0,row);
                row++;
           }   
        }
        
        
        if(!endOfList)
        {
            pageOfInbox++;
        }
        
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

    
    
    
    @Override
    public void initialize(URL location, ResourceBundle resources) 
    {

    }
}
