/*
 * Erik LaBine, ejl389, 11122765
 * ejl389@mail.usask.ca
 * For use in shift-swap project; CMPT 370, University of Saskatchewan
 * 2014
 */

package frontend;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import controller.*;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * The class of static functions to help handle input.
 */
public class Input
{
    
    
    
    /**
     * Attempts to clean up input. Currently a do-nothing function, really.
     * 
     * @param input the string to be cleaned
     */
    public static String clean(String input) {
	return input;
    }
    
    /**
     * This just ain't secure. Working on it.
     * @param username the username logging in
     * @param password the password provided
     * @return true if they can log in
     */
    public static boolean authenticate(String username, String password) {
	byte[] pwHash = Input.createHash(password);
	Controller cont = new Controller();
	Request loginRequest = Request.LoginRequest(username, pwHash);
	
        try{
        cont.sendRequest(loginRequest);
        }
        catch(Exception e)
        {}

        try {
            Thread.sleep(2000); //wait for connection and login
        }
        catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        
        if(loginRequest.isApproved()) {
            return true;
        }

	return false;
    }
    
    public static Timestamp[] getSchedule(String userID)
    {
        Request request = Request.ShiftRequest(userID);
        Controller cont= new Controller();
        RequestResults schedule = new RequestResults();
        try{
            schedule=cont.sendRequest(request);
        }
        catch(Exception e)
        {}
        return schedule.getShifts();
    }
    
    /**
     * Given a username string, this function sends a request to the database to see
     * if the given username is already found in the database
     * @param username proposed username to get checked
     * @return true if username is not in the database, false if it is.
     */
    public static boolean isUsernameUnique(String username)
    {
        Request validateRequest = Request.UsernameValidateRequest(username);
        Controller cont= new Controller();
        RequestResults results = new RequestResults();
         try{
            results = cont.sendRequest(validateRequest);
        }
        catch(Exception e)
        {
            System.out.println("something horrible has  happrened while trying to validate username");
        }
        return !validateRequest.isApproved(); //there is a ! here because the database actually passes back true if the given username is found, so thus it is NOT unique 
    }
    
    /**
     * Given a Employee object, this function will ask the controller to insert a new employee into the database
     * @param newEmployee employee data to be used for the insert
     */
    public static void addNewEmployee(Employee newEmployee)
    {
        Request addNewEmployeeRequest = Request.CreateRequest("doge", newEmployee);
        Controller cont = new Controller();
        RequestResults results = new RequestResults();
         try{
            results = cont.sendRequest(addNewEmployeeRequest);
        }
        catch(SQLException exception)
        {
            exception.getMessage();
            System.out.println("something horrible has  happrened in addNewEmployee() in Input \n " + exception.getMessage());
        }
    }
    
    //made this function public so that adding a new employee can create a hash too
    public static byte[] createHash(String password) {
	byte[] returnHash = null;
	
	try {
	    MessageDigest md = MessageDigest.getInstance("SHA-256");
	    md.update(password.getBytes());
	    returnHash = md.digest();
	     
	    if(returnHash == null) {
		throw new RuntimeException("ReturnHash null in DummyController");
	    }
	} catch (NoSuchAlgorithmException nsae) {
	    System.out.println("Exception: " + nsae);
	}
	
	return returnHash;
    }
}
