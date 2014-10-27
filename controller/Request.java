/*
*  Connor Lavoy
*  cbl013
*  11118147
*  October 2, 2014
*/

package controller;

/**
 * @author Connor Lavoy
 */
public class Request
{

    public enum RequestType { TAKE, GIVE, TRADE, LOGIN }

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

    /**
     * @return the main data load of the request, usually parsed by the controller
     */
    public Message getMessage()
    {
            return message;
    }

    /**
     * changes the data load of the request to a specified instance of the 'Message' class
     * @param message the instance to store in the request
     */
    public void setMessage(Message message)
    {
            this.message = message;
    }
}