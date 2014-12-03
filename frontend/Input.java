/*
 * Erik LaBine, ejl389, 11122765
 * ejl389@mail.usask.ca
 * For use in shift-swap project; CMPT 370, University of Saskatchewan
 * 2014
 *
 * Also worked on by Warren Fehr, wwf594, and Ken Slawinski kts192 and ANDREW MAGNUS amm215.
 *
 */

package frontend;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import controller.*;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import org.postgresql.util.PSQLException;

/**
 * The class of static functions to help handle input.
 */
public class Input
{
    
    static Controller controller = new Controller();
     
    /**
     * This just ain't secure. Working on it.
     * @param username the username logging in
     * @param password the password provided
     * @return true if they can log in
     */
    public static boolean authenticate(String username, String password) {
	byte[] pwHash = Input.createHash(password);
	Request loginRequest = Request.LoginRequest(Cleaner.cleanId(username), pwHash);
	RequestResults results = null;
        try{
            results = controller.sendRequest(loginRequest);
        }
        catch(SQLException e)
        {
            System.out.println("LOGIN ERROR! ABORTING...");
            return false;
        }

        try {
            Thread.sleep(1000); //wait for connection and login
        }
        catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        
        return results.isApproved();
    }
    
    public static Timestamp[] getSchedule(String userID)
    {
        Request request = Request.ShiftRequest(Cleaner.cleanId(userID));
        RequestResults schedule = new RequestResults();
        try{
            schedule=controller.sendRequest(request);
        }
        catch(SQLException e)
        {}
        return schedule.getShifts();
    }
    
    /**
     * Finds the current access level of the employee.
     * @param userID the employee's user ID
     * @return The access level (1 = Worker, 2 = Manager, 3 = Owner)
     */
    public static int getEmployeeAccessLevel(String userID)
    {
	Request request = Request.GetAccessLevelRequest(Cleaner.cleanId(userID));
	RequestResults accessLevel = new RequestResults();
	try {
	    accessLevel = controller.sendRequest(request);
	}
	catch(SQLException e) {
	    e.printStackTrace();
	    System.out.println(e.getMessage());
	}
	
	return accessLevel.getAccessLevel();
    }
    
    public static Timestamp[] getRangeSchedule(String userID, Timestamp start, Timestamp end)
    {
        Request request = Request.ShiftRangeRequest(Cleaner.cleanId(userID), start, end);
        RequestResults schedule = new RequestResults();
        try{
            schedule = controller.sendRequest(request);
        }
        catch(SQLException exception)
        {
            System.out.println("Exception in inUsernameUnique with message: " + exception.getMessage());
            return null;
        }
        //Do stuff here!
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
        Request validateRequest = Request.UsernameValidateRequest(Cleaner.cleanId(username));
        RequestResults results = null;
        try{
            results = controller.sendRequest(validateRequest);
        }
        catch(SQLException exception)
        {
            System.out.println("Exception in inUsernameUnique with message: " + exception.getMessage());
            return false;
        }
        return !results.isApproved(); //there is a ! here because the database actually passes back true if the given username is found, so thus it is NOT unique 
    }
    
    /**
     * Given a Employee object, this function will ask the controller to insert a new employee into the database
     * @param newEmployee employee data to be used for the insert
     */
    public static void addNewEmployee(Employee newEmployee)
    {  
        Request addNewEmployeeRequest = Request.CreateRequest("PLACEHOLDER", Cleaner.clean(newEmployee));
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
        Request changeAccessLevelRequest = Request.ChangeAccessLevelRequest(Cleaner.cleanId(username), newAccessLevel);
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
        Request removeEmployeeRequest = Request.DeleteRequest("PLACEHOLDER", Cleaner.cleanId(username));
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
        Request modifyEmployeeRequest = Request.ModifyEmployeeInfoRequest(
		Cleaner.cleanId(userToBeChanged), Cleaner.clean(newFirstName), Cleaner.clean(newLastName),
		Cleaner.clean(newEmail), newAccessLevel, newWage);
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
        Request changePasswordRequest = Request.ChangePasswordRequest(
		Cleaner.cleanId(username), newHashedPassword);
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
        Request changeEmployeesManagerRequest = Request.changeEmployeesManagerRequest(
		Cleaner.cleanId(employee), Cleaner.clean(newManager));
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
     * Creates a request to accept or reject a trade offer.
     * @param asker the employees name who offered the request
     * @param accepter the employee who is accepting or rejecting.
     * @param shifts the start times of the two shifts. 0=Asker, 1=Accepter.
     * @param response whether they wish to accept or reject the offer.
     */
    
    public static void createAcceptRequest(String asker, String accepter, Timestamp[] shifts, boolean response)
    {
        Request request = Request.AcceptRequest(Cleaner.cleanId(asker),
		Cleaner.cleanId(accepter), shifts, response);
        try 
        {
		controller.sendRequest(request);
	}
        catch (SQLException e) 
        {
            System.out.println("createAcceptRequest bombed hard");
            e.printStackTrace();
	}
    }
    
    public static boolean createGiveListCheckRequest(Shift check)
    {
        Request request= Request.CheckGiveListRequest(check.getEmployeeLogin(), check.getShiftStartTime(), check.getShiftEndTime());
        RequestResults results = null;
        try
        {
            results=controller.sendRequest(request);
        }
         catch (SQLException e) 
        {
		System.out.println("createGiveRequest bombed hard");
		e.printStackTrace();
                return false;
	}
        return results.isApproved();
    }
    
    /**
     * creates a give request for an employee and his/her shift
     * @param empName the employees name
     * @param shiftStartEnd an array containing the shifts Start time and shifts End time (0,1 respectively)
     */
    public static boolean createGiveRequest(String empName, Timestamp[] shiftStartEnd) {
    	Request request = Request.GiveRequest(Cleaner.cleanId(empName), shiftStartEnd);
    	try 
        {
		controller.sendRequest(request);
	}
        catch (SQLException e) 
        {
		System.out.println("createGiveRequest bombed hard");
		e.printStackTrace();
                return false;
	}
    	return true;
    	/** Sample SQL for creating a give request
    	 * INSERT INTO giveshifts (giverlogin, givershiftstart, givershiftend),
    	 * 	VALUES (empName, shiftStartEnd[0], shiftStartEnd[1]); */
    }
    
    /** creates a trade request, the basis for give/take/trade.
     * @param initLogin the person logged in
     * @param finalLogin the employee giving up the shift
     * @param finalshiftStartEnd the shift's start[0] and end[1]
     */
    public static void createTakeRequest(String initLogin, String finalLogin, Timestamp[] finalshiftStartEnd){
    	Request request = null;
        Timestamp[] temp={null,null, finalshiftStartEnd[0],finalshiftStartEnd[1]};
    	request = Request.TradeRequest(Cleaner.cleanId(initLogin), Cleaner.cleanId(finalLogin), temp);
    	try {
			controller.sendRequest(request);
		} catch (SQLException e) {
			System.out.println("createTakeRequest just got smoked");
			e.printStackTrace();
		}
		
    }
    
    /** Takes two Timestamp arrays, concats and returns them
     * @param senderShifts the senders shifts (start, end) in 0,1
     * @param recipientShifts the recipients shifts (start,end) in 0,1
     * @return returns a larger array containing sender in 0,1 and recipient in 2,3 */
    private static Timestamp[] timestampArrayMerge(Timestamp[] senderShifts, Timestamp[] recipientShifts){
    	Timestamp[] combinedShifts = {senderShifts[0], senderShifts[1], recipientShifts[0], recipientShifts[1]};
    	return combinedShifts;
    }
    
    public static void createManagerApprovalRequest(String[] requiredValues, boolean acceptance, String manager)
    {
        Timestamp[] shifts={Timestamp.valueOf(requiredValues[2]),Timestamp.valueOf(requiredValues[3])};
        Request request=Request.ApproveRequest(requiredValues[0], requiredValues[1], manager, shifts, acceptance);
        try 
        {
		controller.sendRequest(request);
	} 
        catch (SQLException e) 
        {
		System.out.println("createManagerApprovalRequest just got smoked");
		e.printStackTrace();
	}
    }
    
    
    /** creates a trade request, the basis for give/take/trade.
     * @param initLogin the employee giving up a shift
     * @param shiftStartEnd the employee giving up a shift's start and end time
     * @param finalLogin the employee taking a shift
     * @param finalshiftStartEnd the employee taking a shift's start and end time
     */
    public static void createTradeRequest(String initLogin, Timestamp[] shiftStartEnd, String finalLogin, Timestamp[] finalshiftStartEnd){
    	Request request = null;
    	request = Request.TradeRequest(Cleaner.cleanId(initLogin), Cleaner.cleanId(finalLogin),
		timestampArrayMerge(shiftStartEnd, finalshiftStartEnd));
    	try {
			controller.sendRequest(request);
		} catch (SQLException e) {
			System.out.println("createTradeRequest just got smoked");
			e.printStackTrace();
		}
		/** Sample SQL for creating a take traderequest 
    	 * INSERT INTO shifttransaction (initlogin, initshiftstart, initshiftend, finallogin, finalshiftstart, finalshiftend, finalsign, managersign, transactiontype),
    	 * 	VALUES (initLogin, shiftStartEnd[0], shiftStartEnd[1], finalLogin, finalshiftStartEnd[0], finalshiftStartEnd[1], TRUE, TRUE, "TAKE"); */
    }
    
    /**
     * Fetches all the messages for a user in an array of strings, ordered by
     * their start time
     * @param username employee whose messages you want to get
     * @return Array of messages 
     */
    public static RequestResults getEmployeeMessages(String username)
    {
        Request messageRequest = Request.GetMessagesRequest(Cleaner.cleanId(username));
        RequestResults results = null;
        try
        {
            results = controller.sendRequest(messageRequest);
        }
        catch(SQLException exception)
        {
            System.out.println("Messages couldn't be found, error is: " + exception.getMessage());
        }
        return results;
    }
    
    
    public static void sendMessage(String sender, String recipent, String message)
    {
        Request messageRequest = Request.SendMessageRequest(Cleaner.cleanId(sender),
		Cleaner.cleanId(recipent), Cleaner.clean(message));
        try
        {
            controller.sendRequest(messageRequest);
        }
        catch(SQLException exception)
        {
            System.out.println("Message couldn't be sent, error is: " + exception.getMessage());
        }
    }
    
    public static void deleteMessage(String sender, String recipient, Timestamp sendTime)
    {
        Request deleteRequest = Request.DeleteMessageRequest(Cleaner.cleanId(sender),
		Cleaner.cleanId(recipient), sendTime);
        try
        {
            controller.sendRequest(deleteRequest);
        }
        catch(SQLException exception)
        {
            System.out.println("Message couldn't be deleted, error is: " + exception.getMessage());
        }
    }
    
    /**
     * Fetch the list of all shifts that people are giving away
     * returns null if there is nothing passed back to the database
     * @return array of shifts
     */
    public static Shift[] getGiveList()
    {
        Request getGiveListRequest = Request.GetGivesListRequest();
        RequestResults results = null;
        try
        {
             results = controller.sendRequest(getGiveListRequest);
        }
        catch(SQLException exception)
        {
            System.out.println("Exception in removeEmployee with message: " + exception.getMessage());
        } 
        if(results != null)
        {
            Shift[] shifts = new Shift[results.getNames().length];
            String[] names = results.getNames();
            Timestamp[] times = results.getShifts();
            for(int i = 0; i < results.getNames().length; i++)
            {
                shifts[i] = new Shift(names[i], times[i*2], times[(i*2)+1]);
            }
             return shifts;
        }
        else
        {
            return null;
        }
    }
   
    /**
     * pass in a shift and create it in the database.
     * @param shift the shift you would like to assign
     */
    public static void assignShifts(Shift shift)
    {
         Request assignRequest = Request.assignShiftsRequest(Cleaner.clean(shift));
         try
         {
            controller.sendRequest(assignRequest);
         }
         catch(SQLException exception)
         {
	    exception.printStackTrace();
            System.out.println("Messages couldn't be found, error is: " + exception.getMessage());
         }
    }
    
    /**
     * Change the need for a manager to be sought for approval
     * @param managerlogin manager who wants to change
     * @param wantToApprove value they want to change to, true for they want to be sought, and false if not.
     */
    public static void changeApprovalStatus(String managerlogin, boolean wantToApprove)
    {
        Request changeStatusRequest = Request.changeManagerApprovalStatusRequest(managerlogin, wantToApprove);
	try
       {
           controller.sendRequest(changeStatusRequest);
       }
       catch(SQLException exception)
       {
           System.out.println("Message couldn't be sent, error is: " + exception.getMessage());
       }
    }
    
    /**
     * Returns the manager's currently listed notification preference for their
     * employees's shift changes.
     * @param managerLogin the user id of the manager
     * @return true if they wish to approve all requests.
     */
    public static boolean getApprovalStatus(String managerLogin)
    {
	// TODO
	
	return false;
    }
    
    /**
     * When a manager wants to be able to approve trades, this function is invoked
     * when the manager approves or denys a trade, 
     * @param manager manager in question who is approving the trade
     * @param initShift first shift in the trade
     * @param finalShift second shift in the trade
     * @param isApproved weather or not the shift is approved
     */
    public static void approveTrade(String manager, Shift initShift, Shift finalShift, boolean isApproved)
    {
        //TODO
    }
    
    /** requests the worker info given a login
     * @param login the login of the employee
     */
   public static Employee getWorkerInfo (String login){
       Request getInfo = Request.GetUserInfoRequest(Cleaner.cleanId(login));
       RequestResults results = new RequestResults();
       try
       {
           results = controller.sendRequest(getInfo);
       }
       catch(SQLException exception)
       {
           System.out.println("Message couldn't be sent, error is: " + exception.getMessage());
       }
       
       return results.getEmployee();
   }
   
   /** requests worker login(s) based on first and last name
    * @param firstName the first name of the employee
    * @param lastName the last name of the employee */
   public static String getWorkerLogin (String firstName, String lastName)
   {
	Request getLogin = Request.GetUserLoginRequest(Cleaner.clean(firstName), 
						     Cleaner.clean(lastName));
	RequestResults results = new RequestResults();
	try
	{
           results = controller.sendRequest(getLogin);
	}
	catch(SQLException exception)
	{
           System.out.println("Message couldn't be sent, error is: " + exception.getMessage());
	}
	
	return results.getMessages();
   }
   
   public static Shift[] getShiftsOnDay (String username, Timestamp dayStart, Timestamp dayEnd)
   {
       Request getShifts=Request.GetShiftsonDay(Cleaner.cleanId(username), dayStart, dayEnd);
       RequestResults results = new RequestResults();
       try
       {
           results=controller.sendRequest(getShifts);
       }
       catch(SQLException exception)
       {
           System.out.println("Couldn't grab shifts, error is: " + exception.getMessage());
       }
       if(results==null)
       {
            return null;
       }
       else
       {
            Shift[] shifts = new Shift[results.getNames().length];
            String[] names = results.getNames();
            Timestamp[] times = results.getShifts();
            for(int i = 0; i < results.getNames().length; i++)
            {
                shifts[i] = new Shift(names[i], times[i*2], times[(i*2)+1]);
            }
             return shifts;
       }
   }
}
