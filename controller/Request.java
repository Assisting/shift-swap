/*
*  Connor Lavoy
*  cbl013
*  11118147
*  October 2, 2014
*/
 
package controller;
 
import java.sql.Timestamp;
 
/**
 * An object passed to the controller class in order to execute all functionality of Shift Swap
 * @author Connor Lavoy
 */
public class Request
{
 
    public enum RequestType { TAKE, GIVE, TRADE, LOGIN, CREATE, REMOVE, SCHEDULE }
 
    final private String sender;
    private String approver;
    private boolean approved;
    private String recipient;
    private Message message;
 
    final private RequestType mode;
 
    /**
     * creates a shift-swap request with a certain 'to' and 'from' line
     * @param sender the employee id of the originator of the request
     * @param recipient the employee id of the request target, if known
     * @param message the relevant data for the request, depending on its type
     * @param mode the type of request, indicating to the controller how message data should be parsed
     */
    public Request(String sender, String recipient, Message message, RequestType mode)
    {
        this.sender = sender;
        this.recipient = recipient;
        this.message = message;
        this.mode = mode;
    }
 
//-----Custom Requests------------------------------------------
 
public static Request LoginRequest(String sender, byte[] password) {
        Message message = new Message(null, password, null, null);
        return new Request(sender, null, message, RequestType.LOGIN);
}
 
public static Request ShiftRequest(String sender) {
        return new Request(sender, null, null, RequestType.SCHEDULE);
}
 
public static Request CreateRequest(String sender, Employee employee) {
        Message message = new Message(null, null, null, employee);
        return new Request(sender, null, message, RequestType.CREATE);
}
 
public static Request RemoveRequest(String sender, String toBeRemoved) {
        return new Request(sender, toBeRemoved, null, RequestType.REMOVE);
}

public static Request TakeRequest(String sender, Timestamp[] times) {
    Message message = new Message(null, null, times, null);
    return new Request(sender, null, message, RequestType.TAKE);
}

public static Request GiveRequest(String sender, Timestamp[] times) {
    Message message = new Message(null, null, times, null);
    return new Request(sender, null, message, RequestType.GIVE);
}

public static Request TradeRequest(String sender, String recipient, Timestamp[] shifts) {
    Message message = new Message(null, null, shifts, null);
    return new Request(sender, recipient, message, RequestType.TRADE);
}
 
 
//-----Getters and Setters----------------------------------------
 
    /**
     * set the approver field of the request (usually filled in by the controller)
     * @param name the employee id of the manager which should approve this request
     */
    public void setApprover(String name)
    {
        approver = name;
    }
 
    /**
     * sets the approved value, designating whether or not this request has been approved by the appropriate manager
     * @param value the value to which the variable should be set
     */
    public void setApproved(boolean value)
    {
        approved = value;
    }
 
    /**
     * set the recipient of the request
     * @param name the employee id of the request's recipient
     */
    public void setRecipient (String name)
    {
        recipient = name;
    }
 
    /**
     * @return the employee id of the person who started this request
     */
    public String getSender()
    {
        return sender;
    }
 
    /**
     * @return the employee id of the manager meant to approve this request
     */
    public String getApprover()
    {
        return approver;
    }
 
    /**
     * @return whether or not the request has been approved
     */
    public boolean isApproved()
    {
        return approved;
    }
 
    /**
     * @return  the employee id of the request's recipient
     */
    public String getRecipient()
    {
        return recipient;
    }
 
    /**
     * @return the type of request that this is
     */
    public RequestType getMode()
    {
        return mode;
    }
   
    public String getNotification() {
        return message.getNotification();
    }
   
    public byte[] getPassword() {
        return message.getPassword();
    }
   
    public Timestamp[] getShifts() {
        return message.getShifts();
    }
   
    public Employee getEmployee() {
        return message.getEmployee();
    }
}