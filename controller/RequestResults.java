/*
*  Connor Lavoy
*  cbl013
*  11118147
*  October 29, 2014
*/
package controller;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * Container to store any returned values of system requests
 * @author Connor
 */
public class RequestResults {
    
    Timestamp[] shifts = null;
    private boolean approved = false;
    private String[] names;
    private String messages;
    private int accessLevel;
    private Employee employee;
    
    public RequestResults() {
        
    }
    
    /**
     * set the container to hold a list of shifts
     * @param shifts the list of shift start and end times to assign
     */
    void setShifts(Timestamp[] shifts) {
        this.shifts = shifts;
    }
    
    public Timestamp[] getShifts() {
        return shifts;
    }

    /**
     * @return the approved
     */
    public boolean isApproved() {
        return approved;
    }

    /**
     * @param approved the approved to set
     */
    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    /**
     * @return the names
     */
    public String[] getNames() {
        return names;
    }

    /**
     * @param names the names to set
     */
    public void setNames(String[] names) {
        this.names = names;
    }

    /**
     * @return the messages
     */
    public String getMessages() {
        return messages;
    }

    /**
     * @param messages the messages to set
     */
    public void setMessages(String messages) {
        this.messages = messages;
    }

    /**
     * @return the accessLevel
     */
    public int getAccessLevel() {
        return accessLevel;
    }

    /**
     * @param accessLevel the accessLevel to set
     */
    public void setAccessLevel(int accessLevel) {
        this.accessLevel = accessLevel;
    }

    /**
     * @return the employee
     */
    public Employee getEmployee() {
        return employee;
    }

    /**
     * @param employee the employee to set
     */
    public void setEmployee(Employee employee) {
        this.employee = employee;
    }
    
}
