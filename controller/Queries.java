package controller;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

/**
 * A container for the various SQL queries executed by the controller
 * @author Connor
 */
public class Queries {
    
    private static Timestamp nullStamp;
    
    /** deletes a shift transaction
     * @param sender the person initiating the shfit chagne
     * @param shiftstart/shiftend the start and end of the shift.
     * @return the string query */
    static String deleteShiftTransactionQuery(String sender, String recipient,
			Timestamp initstart, Timestamp finalstart) {
                String takeTime = nullStamp.toString();
                if (initstart != null)
                    takeTime = initstart.toString();
		return ("DELETE FROM shifttransaction"
				+ " WHERE "
				+ "initlogin = '" + sender
                                + "' AND finallogin = '" + recipient
				+ "' AND initshiftstart = '" + takeTime
				+ "' AND finalshiftstart = '" + finalstart
				+ "';");
    }
    
    /** returns the entire row of a transaction
     * @param sender the login of the person starting transaction
     * @param start/end the start and end time of the sender
     * @parma return the sql to gather this info**/
    static String getTransactionData(String sender, String recipient, Timestamp initstart, Timestamp finalstart)
    {
        String takeTime;
        if (initstart == null)
            takeTime = nullStamp.toString();
        else
            takeTime = initstart.toString();
        return ("Select * from shifttransaction WHERE "
        		+ "initlogin ='" + sender +"' AND "
        		+ "initshiftstart = '" + takeTime + "' AND "
        		+ "finalshiftstart = '" + finalstart + "';");
    }
    
    /** returns the entire row of a transaction
     * @param sender the login of the person starting transaction
     * @param start/end the start and end time of the sender
     * @parma return the sql to gather this info**/
    static String getTransactionID(String sender, String recipient, Timestamp start, Timestamp end)
    {
        return ("Select transactionid from shifttransaction WHERE "
        		+ "initlogin ='" + sender +"' AND "
        		+ "initshiftstart = '" + start + "' AND "
        		+ "initshiftend = '" + end + "';");
    }
    
    //Generate a query to select the login (username and password) information from the database so that it can be checked for authentication
    static String getloginQuery(String LoginID){
        return "SELECT empLogin, empPassword FROM login WHERE empLogin = '" + LoginID + "'" ;
    }
    
    /**
     * generate a query to get all important non shift information from
     * the database for a given loginID, First Name, Last Name, access level, login, password,
     * email and who their manager is.
     */
    static String getWorkerInfoQuery(String LoginID)
    {
        return "SELECT empfirstname, emplastname, empaccesslevel,"
                + " emplogin, empemail, manager, empwage"
                + " FROM full_employee_info"
                + " WHERE emplogin = '" + LoginID + "'";
    }
    
    /** return the employee login(s) based on first and lastname
     * @param firstName the first name of the employee
     * @param lastName the last name of the employee
     * @return emplogin(s, if multiple). */
    static String getEmployeeLogin(String firstName, String lastName){
    	return "SELECT emplogin from full_employee_info where empfirstname = '" 
    			+ firstName + "' AND emplastname = '" + lastName + "';";
    }
    
    /**
     * Generate a query to get ALL the shift info for a particular person denoted by LoginID
     * @param LoginID ID of the person you want to get the shifts of
     * @return String of the Query needed
     */
    static String getEmployeeShiftInfo(String LoginID)
    {
    return "SELECT shiftemployeelogin, shiftstarttime, shiftendtime"
            + " FROM employeeshifts"
            + " WHERE shiftemployeelogin = '" + LoginID + "'"
            + " ORDER BY shiftstarttime" ;
    }
    
    /**
     *generate a query to update any or all employee info, it is assumed that the loginID cannot be changed
     * because of the need for prepared statements to change the password, i am removing password from this query
     * @param firstName can be null
     * @param lastName can be null
     * @param accesslevel must be set to -1 if not specified
     * @param loginID CANNOT be null
     * @param email can be null
     * @param wage set to -1 if not specified
     * @return a custom string for the update query given the parameters 
     */
    static String updateEmployeeQuery(String firstName, String lastName, int accesslevel, String loginID, String email, float wage){
        boolean needComma = false;
        String ret = "UPDATE employees SET ";
        if(firstName != null){
            ret = ret+ "empfirstname = '"+ firstName +"'";
            needComma = true;
        }
        if(lastName != null){
            if(needComma == true){
                    ret = ret + ", ";
            }
            ret = ret+ "emplastname = '"+ lastName +"' ";
            needComma = true;
        }
        if(accesslevel != -1){
            if(needComma == true){
                    ret = ret + ", ";
            }
            ret = ret+ "empaccesslevel = '"+ accesslevel +"' ";
            needComma = true;
        }
        if(email != null){
            if(needComma == true) {
                    ret = ret + ", ";
            }
            ret = ret+ "empemail = '"+ email +"' ";
            needComma = true;
        }
        if(wage >= 0){
            if(needComma == true){
                    ret = ret + ", ";
            }
            ret = ret+ "empwage = '"+ wage +"' ";
            needComma = true;
        }        
        ret = ret + " WHERE emplogin = '" + loginID +"'";
        return ret;
    }
    
    /**
     * Generate Query to insert into the bossmanager table, employee is the LoginID
     * of the employee and the manager will be set to null.
     * this function is meant to be used at the same time a new employee is created
     * and the updateManagerQuery is to be used when actually assigning a person a manager
     * @param employee LoginID of the employee
     * @return Query
     */
    static String newManagerQuery(String employee)
    {
         return "INSERT INTO bossmanager (employee)"
                + " VALUES ('" + employee + "')";
    }
    
    /**
     * Generate Query to update a employees manager
     * @param employee employee whos manager needs changing
     * @param newManager new managers LoginID
     * @return  the Query
     */
    static String updateManagerQuery(String employee, String newManager)
    {
        return "UPDATE bossmanager "
                + "SET manager = '" + newManager +"' "
                + "WHERE employee = '" + employee + "' ";
    }
    
    /**
     * Generate Query to insert a new message into the inbox table
     * @param sender LoginId of who sent it
     * @param reciever LoginID of who should recieve it
     * @param message  textual part of the message
     * @return the query
     */
    static String newMessageQuery(String sender, String reciever, String message )
    {
        return "INSERT INTO employeeinbox (mssgsender, mssgreciever, mssgtext)"
                + "VALUES("
                + "'" + sender + "', "
                + "'" + reciever + "', "
                + "'" + message + "' "
                + ")";
    }
    
    /**
     * Generate a query to get all the messages in a employees inbox
     * @param LoginID
     * @return the query
     */
    static String getEmployeeMessages(String LoginID)
    {
        return "SELECT mssgreciever, mssgsender, mssgtext, mssgsendtime "
                + "FROM employeeinbox "
                + "WHERE mssgreciever = '" + LoginID + "' "
                + "ORDER BY mssgsendtime";
    }
    
    static String deleteEmployeeMessage(String sender, String reciever, Timestamp sendtime)
    {
        return "DELETE FROM employeeinbox WHERE "
                + "mssgsender = '" + sender + "' AND "
                + "mssgreciever = '" + reciever + "' AND "
                + "mssgsendtime = '" + sendtime + "' ";
    }
    
    /**
     * Generate a query that returns a single entry under the column name "isfound"
     * that is either "t" if the given username is found in the database
     * or "f" if the username is not found in the database.
     * @return 
     */
    static String usernameValidityQuery(String username)
    {
        return "SELECT exists("
                + "SELECT emplogin "
                + "FROM employees "
                + "WHERE emplogin = '" + username + "') "
                + "as isfound";
    }
    
    /**
     * Generate a query  to select a range of shifts by date for a specifc user,
     * or if the LoginID is null, for all users
     *
     * @param startDate start time of the date range
     * @param endDate end time of the date range
     * @param LoginID username 
     * @return 
     */
    static String dateRangeShiftQuery( Timestamp startDate, Timestamp endDate, String LoginID) 
    {
        String ret = "SELECT shiftemployeelogin, shiftstarttime, shiftendtime "
                + "FROM employeeshifts "
                + "WHERE shiftstarttime >= '" + startDate.toString() + "' "
                + "AND shiftstarttime <= '" + endDate.toString() + "' "
                + "AND shiftendtime <= '" + endDate.toString() + "' "
                + "AND shiftendtime >= '" + startDate.toString() + "' ";

        if(LoginID != null)
        {
            ret = ret + "AND shiftemployeelogin = '" + LoginID + "' ";
        }
        // will just generalize  the query if the loginID is null to get all shifts for all people in the range
        ret = ret + "ORDER BY shiftstarttime";
        return ret;
    }
    
    /**
     * Generate a query to get the loginID of all the managers in the database 
     * @return said query
     */
    static String managerListQuery()
    {
        return "SELECT emplogin "
                + "FROM employees "
                + "WHERE empaccesslevel = '2' ";
    }
    
    /**
     * Actually a fairly complex query, this will remove all record of an employee in the system
     * @param employeeLoginID employee to be scrubbed from the system
     * @return 
     */
    static String removeEmployeeQuery(String employeeLoginID)
    {
        //start by removing their record in the bossmanager table
        String step1 = "DELETE FROM bossmanager WHERE employee = '" + employeeLoginID + "'; ";

        //if the employee was anyones manager in the bossmanager table, set those records to null
        String step2 = "UPDATE bossmanager SET manager = NULL WHERE manager = '" + employeeLoginID + "'; ";

        //remove all records associated with the user in the giveshifts table
        String step3 = "DELETE FROM giveshifts WHERE giverlogin = '" + employeeLoginID + "'; ";

        //remove all records in the shifttransaction table where either the initiator or finalizer is employeeLoginID
        String step4a = "DELETE FROM shifttransaction WHERE initlogin = '" + employeeLoginID + "'; ";
        String step4b = "DELETE FROM shifttransaction WHERE finallogin = '" + employeeLoginID + "'; ";

        //remove all shifts in the employeeshifts table associated with the user
        String step5 = "DELETE FROM employeeshifts WHERE shiftemployeelogin = '" + employeeLoginID + "'; ";

        //remove all messages that were recieved by the user (messages sent from the user can stay)
        String step6 = "DELETE FROM employeeinbox WHERE mssgreciever = '" + employeeLoginID + "'; ";

        //finally delete the actual user record from the employees table
        String step7 = "DELETE FROM employees WHERE emplogin = '" + employeeLoginID + "' ";

        return step1 + step2 + step3 + step4a + step4b + step5 + step6 + step7; 
    }
    
    /**
     * Generate a query to insert into the giveshifts table
     * @param giver person that wants to give away a shift
     * @param starttime start time of the shift
     * @param endtime end time of the shift
     * @return 
     */
    static String insertGiveRecordQuery(String giver, Timestamp starttime, Timestamp endtime )
    {
        return "INSERT INTO giveshifts(giverlogin, givershiftstart, givershiftend)"
                + " VALUES("
                + "'" + giver + "', "
                + "'" + starttime.toString() + "', "
                + "'" + endtime.toString() + "' "
                + ")";
    }
    
    /**
     * Generate a query to delete a specific shift from the give table
     * @param giver
     * @param starttime
     * @param endtime
     * @return 
     */
    static String deleteGiveRecordQuery(String giver, Timestamp starttime, Timestamp endtime )
    {
        return "DELETE FROM giveshifts WHERE "
                + "giverlogin = '" + giver + "' AND "
                + "givershiftstart = '" + starttime.toString() + "' AND "
                + "givershiftend = '" + endtime.toString() + "' ";
    }
    
    /**
     * Generate a query to return all rows in the giveshifts table
     * @return String version of the query necessary
     */
    static String getAllGiveRecords()
    {
        return "SELECT giverlogin, givershiftstart, givershiftend FROM giveshifts";
    }
    
    /**
     * Generate a query that returns a single value under the column, requiremanagerapproval which is 
     * TRUE if trades require a managers signoff/permission and false in its not required
     * @param managerlogin 
    */
    static String getManagerApprovalStatus(String managerlogin)
    {
        return "SELECT ma_approval FROM managerapproval WHERE ma_manager = '" + managerlogin + "' ";
    }
    
    /**
     * Generate a query to insert into the transactions table
     * @param initiatorLoginID login of the person who sent/created the trade
     * @param finalizerLoginID login of the reciever/ secondary actor of the transaction
     * @param shiftTimes shiftTimes[0], shiftTimes[1] are the start and end times respectivly of the initiators shift
     * THE TRANSACTIONID IS AUTOMATICALLY GENERATED HERE
     * @param transactionType
     * @param manager2reqd 
     * @param manager1reqd 
     * @return 
     */
    static String insertTradeQuery(String initiatorLoginID, String finalizerLoginID, Timestamp[] shiftTimes, String transactionType, String initiatorManager, boolean initManagerSign, String finalizerManager, boolean finalManagerSign )
    {
        String returnString = "INSERT INTO shifttransaction (initlogin, initshiftstart, initshiftend, finallogin, finalshiftstart, finalshiftend, initmanagerlogin, finalmanagerlogin, initmanagersign, finaltmanagersign, transactiontype) "
                    + "VALUES ( "
                    + "'" + initiatorLoginID + "', ";
        if(shiftTimes[0] != null)
        {
            returnString += "'" + shiftTimes[0].toString() + "', "
                    + "'" + shiftTimes[1].toString() + "', ";
        }
        else //initial shifts are null
        {
            System.out.print("noo");
            returnString += "'" + nullStamp.toString() + "', "
                    + "'" + nullStamp.toString() + "', ";
        }
        returnString += "'" + finalizerLoginID + "', "
                    + "'" + shiftTimes[2].toString() + "', "
                    + "'" + shiftTimes[3].toString() + "', "
                    + "'" + initiatorManager + "', "
                    + "'" + finalizerManager + "', "
                    + "'" + initManagerSign + "', "
                    + "'" + finalManagerSign + "', "
                    + "'" + transactionType + "') ";
        return returnString;
    }
    
    /**
     * Query to Insert a shift into the employeeshifts table
     * @param loginID = login of the employee assiciated with the shift
     * @param startTime = start time of the shift
     * @param endTime = end time of the shift
     * @return String version of  the query
     */
    static String insertShiftQuery(String loginID, Timestamp startTime, Timestamp endTime)
    {
        return "INSERT INTO employeeshifts (shiftemployeelogin, shiftstarttime, shiftendtime) "
                + "VALUES ("
                + "'" + loginID + "', "
                + "'" + startTime.toString() + "', "
                + "'" + endTime.toString() + "' "
                + ")";
    }
    
    /**
     * Query to DELETE a shift into the employeeshifts table
     * @param loginID = login of the employee assiciated with the shift
     * @param startTime = start time of the shift
     * @param endTime = end time of the shift
     * @return String version of  the query
     */
    static String deleteShiftQuery(String loginID, Timestamp startTime, Timestamp endTime)
    {
        return "DELETE FROM employeeshifts WHERE "
                + "shiftemployeelogin = '" + loginID + "' AND "
                + "shiftstarttime = '" + startTime.toString() + "' AND "
                + "shiftendtime = '" + endTime.toString() + "'";
    }
    
    static String approvalStatusChangeQuery(String manager, boolean isApproved)
    {
        return "UPDATE managerapproval SET ma_approval = "
                + "'" + isApproved +"' "
                + "WHERE ma_manager = '" + manager + "' ";
    }
    
    static String getAccessLevelQuery(String empName)
    {
        return "SELECT empaccesslevel FROM employees WHERE emplogin = " + empName;
    }
    
}
