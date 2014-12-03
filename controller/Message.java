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
    
    private String notification;
    private byte[] password;
    private Timestamp[] shift;
    private Employee employee;
    private String manager;
    private boolean approval;

    public Message(){}
    
    public void setNotification(String value) {
        notification = value;
    }
    
    public void setPassword(byte[] value) {
        password = value;
    }
    
    public void setShifts(Timestamp[] value) {
        shift = value;
    }
    
    public void setEmployee(Employee value) {
        employee = value;
    }
    
    public void setManager(String value) {
        manager = value;
    }
    
    public void setApproved(boolean value) {
        approval = value;
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
     * @return the manager
     */
    public String getManager() {
        return manager;
    }
    
    /**
     * @return the approval
     */
    public boolean isApproved() {
        return approval;
    }
}
