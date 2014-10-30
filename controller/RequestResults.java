/*
*  Connor Lavoy
*  cbl013
*  11118147
*  October 29, 2014
*/
package controller;

import java.sql.Date;

/**
 * Container to store any returned values of system requests
 * @author Connor
 */
public class RequestResults {
    
    Date[] shifts;
    
    public RequestResults() {
        
    }
    
    /**
     * set the container to hold a list of shifts
     * @param shifts the list of shift start and end times to assign
     */
    void setShifts(Date[] shifts) {
        this.shifts = shifts;
    }
    
    public Date[] getShifts() {
        return shifts;
    }
    
}
