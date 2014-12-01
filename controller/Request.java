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
 
    public enum RequestType { GIVE, TRADE, ACCEPT, APPROVE, SEND_MESSAGE,
                              CREATE_USER, DELETE_USER, VALIDATE_UNIQUE, PASSWORD_CHANGE, UPDATE_EMPLOYEE, MANAGER_CHANGE, ACCESS_UPDATE, ACCESS_LEVEL, USER_INFO, GET_USERNAME,
                              LOGIN, GIVELIST, CHECK_GIVES, GET_MESSAGES, DELETE_MESSAGE,
                              APPROVAL_STATUS,
                              SCHEDULE, SHIFT_RANGE, SHIFTS_DAY, ADD_SHIFT, REMOVE_SHIFT }
 
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

/**
 * Make a request to log in to the system
 * @param username the username to check against
 * @param password the password to offer
 * @return the constructed request
 */
public static Request LoginRequest(String username, byte[] password) {
        Message message = new Message();
        message.setPassword(password);
        return new Request(username, null, message, RequestType.LOGIN);
}

/**
 * Ask for the list of Give request in the database
 * @return the request
 */
public static Request GetGivesListRequest()
{
    return new Request(null, null, null, RequestType.GIVELIST);
}

public static Request CeckGiveListRequest(String user, Timestamp start, Timestamp end)
{
    Timestamp[] times = new Timestamp[2];
    times[0] = start;
    times[1] = end;
    Message message = new Message();
    message.setShifts(times);
    return new Request(user, null, message, RequestType.CHECK_GIVES);
}

/**
 * get all the shifts worked on a day, except those by the sender
 * @param sender the person to exclude from the list
 * @return the query
 */
public static Request GetShiftsonDay(String sender, Timestamp start, Timestamp end)
{
    Message message = new Message();
    Timestamp[] times = new Timestamp[2];
    times[0] = start;
    times[1] = end;
    message.setShifts(times);
    return new Request(sender, null, message, RequestType.SHIFTS_DAY);
}

/**
 * ask for an employees outstanding inbox messages
 * @param sender the employee
 * @return the request
 */
public static Request GetMessagesRequest(String sender)
{
    return new Request(sender, null, null, RequestType.GET_MESSAGES);
}

/**
 * send a message from one employee to another
 * @param sender the employee sending the message
 * @param recipient the employee receiving the message
 * @param message the text to send
 * @return the request
 */
public static Request SendMessageRequest(String sender, String recipient, String message)
{
    Message newMessage = new Message();
    newMessage.setNotification(message);
    return new Request(sender, recipient, newMessage, RequestType.SEND_MESSAGE);
}

public static Request DeleteMessageRequest(String sender, String recipient, Timestamp sendTime)
{
        Message message = new Message();
        Timestamp[] timeArray = new Timestamp[1];
        timeArray[0] = sendTime;
        message.setShifts(timeArray);
        return new Request(sender, recipient, message, RequestType.DELETE_MESSAGE);
}

/**
 * get the entire schedule of an employee
 * @param username the employee to use in the query
 * @return the request
 */
public static Request ShiftRequest(String username) {
        return new Request(username, null, null, RequestType.SCHEDULE);
}

/**
 * create an employee in the database
 * @param sender the manager creating the employee
 * @param employee the employee's information
 * @return the request
 */
public static Request CreateRequest(String sender, Employee employee) {
        Message message = new Message();
        message.setEmployee(employee);
        return new Request(sender, null, message, RequestType.CREATE_USER);
}

/**
 * deletes an employee from the database
 * @param sender the manager making the request
 * @param toBeRemoved the ID of the employee to be removed
 * @return the request
 */
public static Request DeleteRequest(String sender, String toBeRemoved) {
        return new Request(sender, toBeRemoved, null, RequestType.DELETE_USER);
}

/**
 * ask anyone who can take a shift to take one from you
 * @param sender the employees ID
 * @param times the start and end time of the shift being given away
 * @return the request
 */
public static Request GiveRequest(String sender, Timestamp[] times) {
    Message message = new Message();
    message.setShifts(times);
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
    Message message = new Message();
    message.setShifts(shifts);
    return new Request(sender, recipient, message, RequestType.TRADE);
}

/**
 * send a message accepting or rejecting a trade request from another employee
 * @param asker the employee ID of the person who initiated the trade
 * @param accepter the employee ID of the person asked to accept the trade
 * @param shifts the shift start times of the person who started the trade, and the one accepting it
 * @param accepted whether or not the trade is accepted
 * @return the request
 */
public static Request AcceptRequest(String asker, String accepter, Timestamp[] shifts, boolean accepted)
{
    Message message = new Message();
    message.setApproved(accepted);
    message.setShifts(shifts);
    return new Request(asker, accepter, message, RequestType.ACCEPT);
}

/**
 * message sent by a manager approving or disapproving a trade
 * @param asker the employee ID of the person who initiated the request
 * @param accepter the ID of the person who accepted the trade
 * @param approver the ID of the manger making this approval
 * @param shifts the start times of the asker and accepters shifts
 * @param approved whether the manager approves
 * @return the request
 */
public static Request ApproveRequest(String asker, String accepter, String approver, Timestamp[] shifts, boolean approved)
{
    Message message = new Message();
    message.setShifts(shifts);
    message.setApproved(approved);
    message.setManager(approver);
    return new Request(null, null, message, RequestType.APPROVE);
}

/**
 * checks if a username is unique in the database
 * @param username the employee ID to check
 * @return the request
 */
public static Request UsernameValidateRequest(String username)
{
    return new Request(username, null, null, RequestType.VALIDATE_UNIQUE);
}

/**
 * get a set of shifts from a start to end time
 * @param username the user who's schedule should be used
 * @param start the start of the range
 * @param end the end of the range
 * @return the request
 */
public static Request ShiftRangeRequest(String username, Timestamp start, Timestamp end)
{
    Timestamp[] shifts  = {start, end};
    Message message = new Message();
    message.setShifts(shifts);
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
    Message message = new Message();
    message.setPassword(newPassword);
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
    Message message = new Message();
    message.setEmployee(employee);
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
    Employee employee = new Employee(username, " ", " ", newAccesslevel, null, null, 0);
    Message message = new Message();
    message.setEmployee(employee);
    return new Request(username, null, message, RequestType.ACCESS_UPDATE);
}

/**
 * get the access level of an employee
 * @param username the user ID of the checked employee
 * @return the request
 */
public static Request GetAccessLevelRequest(String username)
{
    return new Request(username, null, null, RequestType.ACCESS_LEVEL);
}

/**
 * get the user information of an employee
 * @param username the employee ID
 * @return the request
 */
public static Request GetUserInfoRequest(String username)
{
    return new Request(username, null, null, RequestType.USER_INFO);
}

/**
 * Create a request to assign an employee a new/different manager
 * @param employeeLoginID loginid of the employee
 * @param managerLoginID loginid of the manager
 * @return 
 */
public static Request changeEmployeesManagerRequest(String employeeLoginID, String managerLoginID)
{
    Message message = new Message();
    message.setManager(managerLoginID);
    return new Request(employeeLoginID, null, message, RequestType.MANAGER_CHANGE);
}

/**
 * Create a request to assign shifts to an employee in the database.
 * @param shift
 * @return 
 */
public static Request assignShiftsRequest(Shift shift)
{
    Timestamp[] shifts = new Timestamp[2];
    shifts[0] =  shift.getShiftStartTime();
    shifts[1] = shift.getShiftEndTime();
    Message message = new Message();
    message.setShifts(shifts);
    return new Request(shift.getEmployeeLogin(), null, message, RequestType.ADD_SHIFT);
}

/**
 * Generate a request to change the manager approval status in the database
 * @param manager, login of the manager who wants to change status
 * @param wantToApprove true if the manager wants to approve and false if they do not
 * @return Request, ready to be sent
 */
public static Request changeManagerApprovalStatusRequest(String manager, boolean wantToApprove)
{
    Message message = new Message();
    message.setManager(manager);
    message.setApproved(wantToApprove);
    return new Request(null, null, message, RequestType.APPROVAL_STATUS);
}

/**
 * return the User ID of a employee with the given name
 * @param first the first name of the employee
 * @param last the last name of the employee
 * @return the request
 */
public static Request GetUserLoginRequest(String first, String last)
{
    return new Request(first, last, null, RequestType.GET_USERNAME);
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
    
    public String getManager() {
        return message.getManager();
    }
}