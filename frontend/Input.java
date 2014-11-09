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
     * @return true if username is NOT in the database, false if it is.
     */
    public static boolean isUsernameUnique(String username)
    {
        Request validateRequest = Request.UsernameValidateRequest(username);
        Controller controller = new Controller();
        try{
            controller.sendRequest(validateRequest);
        }
        catch(SQLException exception)
        {
            System.out.println("Exception in inUsernameUnique with message: " + exception.getMessage());
        }
        return !validateRequest.isApproved(); //there is a ! here because the database actually passes back true if the given username is found, so thus it is NOT unique 
    }
    
    /**
     * Given a Employee object, this function will ask the controller to insert a new employee into the database
     * @param newEmployee employee data to be used for the insert
     */
    public static void addNewEmployee(Employee newEmployee)
    {  
        Request addNewEmployeeRequest = Request.CreateRequest("PLACEHOLDER", newEmployee);
        Controller controller = new Controller();
        RequestResults results = new RequestResults();
        try
        {
            results = controller.sendRequest(addNewEmployeeRequest);
        }
        catch(SQLException exception)
        {
            System.out.println("Exception in addNewEmployee with message: " + exception.getMessage());
        }
    }
   
    /**
     * Send a request to the controller to change the access level of a employee to newAccessLevel
     * @param username of employee getting promoted/demoted
     * @param newAccessLevel new access level of the employee
     */
    public static void changeEmployeeAccessLevel(String username, int newAccessLevel)
    {
        Request changeAccessLevelRequest = Request.ChangeAccessLevelRequest(username, newAccessLevel);
        Controller controller = new Controller();
        try
        {
            controller.sendRequest(changeAccessLevelRequest);
        }
        catch(SQLException exception)
        {
            System.out.println("Exception in changeEmployeeAccessLevel with message: " + exception.getMessage());
        }    
    }
    
    /**
     * Removes all traces of an employee from the database
     * @param username of employee to be removed
     */
    public static void removeEmployee(String username)
    {
        Request removeEmployeeRequest = Request.RemoveRequest("PLACEHOLDER", username);
        Controller controller = new Controller();
        try
        {
            controller.sendRequest(removeEmployeeRequest);
        }
        catch(SQLException exception)
        {
            System.out.println("Exception in removeEmployee with message: " + exception.getMessage());
        }    
    }
    
    /**
     * modify basic information of a employee in the database
     * @param userToBeChanged CANNOT be null
     * @param newFirstName can be null
     * @param newLastName can be null
     * @param newEmail can be null
     * @param newAccessLevel set to -1 if not specified
     * @param newWage set to -1 of not specified
     */
    public static void modifyEmployeeInfo(String userToBeChanged, String newFirstName, String newLastName, String newEmail, int newAccessLevel, float newWage)
    {
        if(userToBeChanged == null)
        {
            throw new RuntimeException();
        }
        Request modifyEmployeeRequest = Request.ModifyEmployeeInfoRequest(userToBeChanged, newFirstName, newLastName, newEmail, newAccessLevel, newWage);
        Controller controller = new Controller();
        try
        {
            controller.sendRequest(modifyEmployeeRequest);
        }
        catch(SQLException exception)
        {
            System.out.println("Exception in modifyEmployeeInfo with message: " + exception.getMessage());
        } 
    }
    
    /**
     * Given a user and a string for the new password, create a hashed version 
     * of the new password and put it into the database.
     * @param username of employee that password is being changed for
     * @param newPassword string that will be hashed and passed to the database
     */
    public static void changeEmployeePassword(String username, String newPassword)
    {
        byte[] newHashedPassword = Input.createHash(newPassword);
        Request changePasswordRequest = Request.ChangePasswordRequest(username, newHashedPassword);
        Controller controller = new Controller();
        try
        {
            controller.sendRequest(changePasswordRequest);
        }
        catch(SQLException exception)
        {
            System.out.println("Exception in changeEmployeePassword with message: " + exception.getMessage());
        } 
    }
    
    /**
     * Change or set who is the employees manager
     * @param employee  manager is being changed
     * @param newManager login id of the new manager
     */
    public static void changeEmployeesManager(String employee, String newManager)
    {
        Request changeEmployeesManagerRequest = Request.changeEmployeesManagerRequest(employee, newManager);
        Controller controller = new Controller();
        try
        {
            controller.sendRequest(changeEmployeesManagerRequest);
        }
        catch(SQLException exception)
        {
            System.out.println("Exception in changeEmployeesManager with message: " + exception.getMessage());
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
    
    /**
     * creates a give request for an employee and his/her shift
     * @param empName the employees name
     * @param shiftStartEnd an array containing the shifts Start time and shifts End time (0,1 respectively)
     */
    public static void createGiveRequest(String empName, Timestamp[] shiftStartEnd) {
    	Controller cont= new Controller();
    	Request request = Request.GiveRequest(empName, shiftStartEnd);
    	try {
			cont.sendRequest(request);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("createGiveRequest bombed hard");
			e.printStackTrace();
		}
    	
    	/** Sample SQL for creating a give request
    	 * INSERT INTO giveshifts (giverlogin, givershiftstart, givershiftend),
    	 * 	VALUES (empName, shiftStartEnd[0], shiftStartEnd[1]); */
    }
    
    /** Takes two Timestamp arrays, concats and returns them
     * @param senderShifts the senders shifts (start, end) in 0,1
     * @param recipientShifts the recipients shifts (start,end) in 0,1
     * @return returns a larger array containing sender in 0,1 and recipient in 2,3 */
    private static Timestamp[] supermegaultrashiftmergerextraordinare(Timestamp[] senderShifts, Timestamp[] recipientShifts){
    	Timestamp[] combinedShifts = {senderShifts[0], senderShifts[1], recipientShifts[0], recipientShifts[1]};
    	return combinedShifts;
    }
    
    /** Checks if manager approval/signoff is required
     * @return true or false depending on whether the manager needs to sign off or not 
     * */
    private static boolean ManagerApprovalRequired(){
    	Boolean leboolean = true;
    	Controller cont = new Controller();
    	//Request request = Request.ManagerApproval; //TODOwaiting on connor to implementw
    	/* 
    	 * if (request = true)
    	 * 	leboolean = true;
    	 * else
    	 * 	leboolean = false;
    	 * */
    	return leboolean;
    }
    
    /** creates a trade request, the basis for give/take/trade.
     * @param initLogin the employee giving up a shift
     * @param shiftStartEnd the employee giving up a shift's start and end time
     * @param finalLogin the employee taking a shift
     * @param finalshiftStartEnd the employee taking a shift's start and end time
     * @param finalSign employee taking the shift approves of the shift change (false by default)
     * @param managerSign manager signs off on the swap (false by default)
     * @param transcationType the type of transaction (values take and swap, no give as it is processed as a take).
     */
    public static void createTradeRequest(String initLogin, Timestamp[] shiftStartEnd, String finalLogin, Timestamp[] finalshiftStartEnd, 
    										Boolean finalSign, Boolean managerSign, String transactionType){ //TODO should I include managerSign? can probably set it after the fact or via default
    	Controller cont = new Controller();
    	Request request = null;
    	
    	switch(transactionType){
    	case "take":
    		//finalSign is true because giver already signed off, and taker wants the shift
    		//initLogin is the person giving the shift
    		// finalLogin is the person taking the shift, he thus offers null shifts
    		Timestamp[] takeShift = {null, null};
        	request = Request.TradeRequest(initLogin, finalLogin, supermegaultrashiftmergerextraordinare(shiftStartEnd, takeShift), transactionType, true);
        	try {
    			cont.sendRequest(request);
    		} catch (SQLException e) {
    			// TODO Auto-generated catch block
    			System.out.println("createTradeRequest just got smoked");
    			e.printStackTrace();
    		}
        	
        	if (ManagerApprovalRequired()){
            	//TODO send message to shift giver "finalLogin has accepted your shift, waiting for manager to sign off"
            	//		done, wait for manager to process
        	}
        	else{
        		//TODO process
        		//TODO send message to shift giver, "finalLogin has accepted your shift, trade is complete".
        	}
    		break;
    	case "swap":
        	request = Request.TradeRequest(initLogin, finalLogin, supermegaultrashiftmergerextraordinare(shiftStartEnd, finalshiftStartEnd), transactionType, managerSign);
        	try {
    			cont.sendRequest(request);
    		} catch (SQLException e) {
    			// TODO Auto-generated catch block
    			System.out.println("createTRadeRequest just got smoked");
    			e.printStackTrace();
    		}
        	//TODO send message to finalLogin "initLogin has requested a shift swap with you!"
        	
        	/** Sample SQL for creating a give request TODO decide on managerapproval, default instead of include blah blah
        	 * INSERT INTO shifttransaction (initlogin, initshiftstart, initshiftend, finallogin, finalshiftstart, finalshiftend, finalsign, managersign, transactiontype),
        	 * 	VALUES (initLogin, shiftStartEnd[0], shiftStartEnd[1], finalLogin, finalshiftStartEnd[0], finalshiftStartEnd[1], finalSign, managerSign, transactionType); */
        	
    		break;
    	}
    	
    	
    }
}
