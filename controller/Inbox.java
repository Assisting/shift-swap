package controller;

import java.sql.Timestamp;
import java.util.LinkedList;

/**
 * A simple container for the information handed back from the grabInbox function.
 * @author Warren Fehr, wwf594
 */
public class Inbox 
{
    private LinkedList<Timestamp> sendTimes;
    
    private LinkedList<String> messages;
    
    public Inbox (LinkedList<Timestamp> times, LinkedList<String> message)
    {
        sendTimes=times;
        messages=message;
    }
    
    public LinkedList<Timestamp> getSendTimes()
    {
        return sendTimes;
    }
    
    public LinkedList<String> getMessages()
    {
        return messages;
    }
}
