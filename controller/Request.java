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
 
    public enum RequestType { GIVE, TRADE, ACCEPT, APPROVE,
                              CREATE, REMOVE, VALIDATE, PASSWORD_CHANGE, UPDATE_EMPLOYEE,
                              LOGIN,
                              SCHEDULE, SHIFT_RANGE }
 
    final private String sender;
    private String approver;
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
 
public static Request LoginRequest(String username, byte[] password) {
        Message message = new Message(null, password, null, null, false, 0);
        return new Request(username, null, message, RequestType.LOGIN);
}
 
public static Request ShiftRequest(String username) {
        return new Request(username, null, null, RequestType.SCHEDULE);
}
 
public static Request CreateRequest(String sender, Employee employee) {
        Message message = new Message(null, null, null, employee, false, 0);
        return new Request(sender, null, message, RequestType.CREATE);
}
 
public static Request RemoveRequest(String sender, String toBeRemoved) {
        return new Request(sender, toBeRemoved, null, RequestType.REMOVE);
}

public static Request GiveRequest(String sender, Timestamp[] times) {
    Message message = new Message(null, null, times, null, false, 0);
    return new Request(sender, null, message, RequestType.GIVE);
}

/**
 * create a new request to trade shifts with another employee
 * @param sender the person initiating the trade
 * @param recipient the person meant to receive the trade
 * @param shifts an array of shifts [senderStart, senderEnd, recipientStart, recipientEnd]
 * @return 
 */
public static Request TradeRequest(String sender, String recipient, Timestamp[] shifts) {
    Message message = new Message(null, null, shifts, null, false, 0);
    return new Request(sender, recipient, message, RequestType.TRADE);
}

public static Request AcceptRequest(int requestID, boolean accepted)
{
    Message message = new Message(null, null, null, null, accepted, requestID);
    return new Request(null, null, message, RequestType.ACCEPT);
}

public static Request ApproveRequest(int requestID, boolean approved)
{
    Message message = new Message(null, null, null, null, approved, requestID);
    return new Request(null, null, message, RequestType.APPROVE);
}

public static Request UsernameValidateRequest(String username)
{
    return new Request(username, null, null, RequestType.VALIDATE);
}

public static Request ShiftRangeRequest(String username, Timestamp start, Timestamp end)
{
    Timestamp[] shifts  = {start, end};
    Message message = new Message(null, null, shifts, null, false, 0);
    return new Request(username, null, message, RequestType.SHIFT_RANGE);
}

/**
 * Generate a request to the controller to change the password of a employee
 * @param username of the employee 
 * @param password new password
 * @return the request that can be sent to the controller
 */
public static Request ChangePasswordRequest(String username, byte[] newPassword)
{
    Message message = new Message(null, newPassword, null, null, false, 0);
    return new Request(username, null, message, RequestType.PASSWORD_CHANGE);
}


/**
 * General Request for updating/ changing user information
 * @param userToBeChanged the loginID of  the user to be changed CANNOT be null
 * @param newFirstName can be null
 * @param newLastName can be null
 * @param newEmail can be null
 * @param newAccessLevel -1 if not specified
 * @param newWage -1 if not specified
 * @return 
 */
public static Request ModifyEmployeeInfoRequest(String userToBeChanged, String newFirstName, String newLastName, String newEmail, int newAccessLevel, float newWage)
{
    Employee employee = new Employee(userToBeChanged, newFirstName, newLastName, newAccessLevel, null, newEmail, newWage);
    Message message = new Message(null, null, null, employee, false, 0);
    return new Request(null, null, message, RequestType.UPDATE_EMPLOYEE);
}
 

/**
 * Generate a request for the controller to change the access level of a 
 * employee in the database
 * Used for Promotions and demotions
 * @param username emplogin of the user to be changed
 * @param newAccesslevel new access level for employee with username
 * @return 
 */
public static Request ChangeAccessLevelRequest(String username, int newAccesslevel)
{
    //TODO not sure if this will work or not, should if modify is done propertly
    //return Request.ModifyEmployeeInfoRequest(username, null, null, null, newAccesslevel, -1);
    return null;
}

/**
 * Create a request to assign an employee a new/different manager
 * @param employeeLoginID loginid of the employee
 * @param managerLoginID loginid of the manager
 * @return 
 */
public static Request changeEmployeesManagerRequest(String employeeLoginID, String managerLoginID)
{
    //TODO
    return null;
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
    
    public boolean isApproved() {
        return message.isApproved();
    }
    
    public int getRequestID() {
        return message.getRequestID();
    }
}