/*
*  Connor Lavoy
*  cbl013
*  11118147
*  October 2, 2014
*/

package controller;

public class Request
{

    public enum RequestType { TAKE, GIVE, TRADE, LOGIN }

    private String sender;
    private String approver;
    private boolean approved;
    private String recipient;
    //private Shift shifts;
    private String message;

    private RequestType mode;

    //creates a shift-swap request with a certain 'to' and 'from' line
    public Request(String sender, String recipient, /*Shift shiftset, */String message, RequestType mode)
    {
        this.sender = sender;
        this.recipient = recipient;
        this.message = message;
        this.mode = mode;
    }

//-----Getters and Setters----------------------------------------

    public void setApprover(String name)
    {
        approver = name;
    }

    public void setApproved(boolean value)
    {
        approved = value;
    }

//	public void setShifts (Shift shifts)
//	{
//	}

    public void setRecipient (String name)
    {
        recipient = name;
    }

    public String getSender()
    {
        return sender;
    }

    public String getApprover()
    {
        return approver;
    }

    public boolean isApproved()
    {
        return approved;
    }

    public String getRecipient()
    {
        return recipient;
    }

//	public Shift getShifts()
//	{
//	}

    public RequestType getMode()
    {
        return mode;
    }

    public String getMessage()
    {
            return message;
    }

    public void setMessage(String message)
    {
            this.message = message;
    }
}