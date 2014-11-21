/*
 * Basic Class to wrap shifts in, with a start and end time for the shift and the employee connected to it
 */
package controller;

import java.sql.Timestamp;

/**
 *
 * @author AndrewMagnus
 */
public class Shift{
    
    private String employeeLogin;
    
    private Timestamp shiftStartTime;
    
    private Timestamp shiftEndTime;

    
    /**
     * Create a Shift which involves a person, and a start time and end time
     * @param empLogin username of the employee of the shift
     * @param start start time of the shift
     * @param end end time of the shift
     */
    public Shift(String empLogin, Timestamp start, Timestamp end)
    {
        employeeLogin = empLogin;
        shiftStartTime = start;
        shiftEndTime = end;
    }
    
    /**
     * Get the username/login of the employee
     * @return 
     */
    public String getEmployeeLogin()
    {
        return employeeLogin; 
    }
    
    /**
     * get start time of the shift
     * @return 
     */
    public Timestamp getShiftStartTime()
    {
        return shiftStartTime; 
    }
    
       /**
     * get end time of the shift
     * @return 
     */
    public Timestamp getShiftEndTime()
    {
        return shiftEndTime;
    }
    
    @Override
    public String toString()
    {
        String slate=employeeLogin+shiftStartTime.toString().substring(0, 16)+
                " - "+shiftEndTime.toString().substring(0, 16);
        return slate;
    }
}
