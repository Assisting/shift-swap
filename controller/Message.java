/*
*  Connor Lavoy
*  cbl013
*  11118147
*  October 2, 2014
*/


package controller;

import java.sql.Timestamp;

/**
 * A large container for any data required in executing requests in the Shift Swap program
 * @author Connor Lavoy
 */
public class Message {
    
    String notification;
    byte[] password;
    Timestamp[] shift;
    Employee employee;
    private boolean approval;
    private int requestID;
    
    public Message(String notification, byte[] password, Timestamp shift[], Employee employee, boolean approval, int requestID) {
        if (shift != null && shift.length%2 != 0) throw new IllegalArgumentException("Shift must contain an even number of elements");
        
        this.password = password;
        this.notification = notification;
        this.shift = shift;
        this.employee = employee;
        this.approval = approval;
        this.requestID = requestID;
    }
    
    public String getNotification() {
        return notification;
    }
    
    public byte[] getPassword() {
        return password;
    }
    
    public Timestamp[] getShifts() {
        return shift;
    }
    
    public Employee getEmployee() {
        return employee;
    }

    /**
     * @return the approval
     */
    public boolean isApproved() {
        return approval;
    }

    /**
     * @return the requestID
     */
    public int getRequestID() {
        return requestID;
    }
    
}
