/*
*  Connor Lavoy
*  cbl013
*  11118147
*  October 2, 2014
*/

package controller;

import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

/**
 * @author Connor Lavoy
 */
public class Controller {

	private Connection dbconnection;

	public Controller() {
            //System.out.print("connection");
            try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.out.println("fail");
		}
		try
		{
                    dbconnection = DriverManager.getConnection ("jdbc:postgresql://lovett.usask.ca:5432/", "cmpt370_group13", "1truegod");
		}
		catch(SQLException sqle)
		{
                    System.out.println("Error connecting to Database...");
                    System.out.println(sqle.getMessage());
		}
	}

	/**
	* Called by other pieces of the system to make requests to the database/managers
	* @param request - a Request object which contains information to be parsed by the controller.
        * @return a container of any results sought from the request
        * @throws SQLException
	*/
	public RequestResults sendRequest (Request request) throws SQLException {
           RequestResults returnResults = null;
		switch (request.getMode()) {
			case TAKE:
			{
                            break;
			}
			case GIVE:
			{
                            break;
			}
			case TRADE:
			{
                            Statement tradeRequest = dbconnection.createStatement();
                            //TODO need a way to generate and use requestIds (see error below)
                            tradeRequest.executeQuery(newMessageQuery(request.getSender(), request.getRecipient(), "TRADE " + requestNum + ": " + request.getShifts()[0] + " for " + request.getShifts()[2]));
                            tradeRequest.close();
                            break;
			}
                        case ACCEPT:
                        {
                            String sender;
                            String recipient;
                            int requestID;
                            Statement dataPull = dbconnection.createStatement();
                            //TODO populate sender and recipient from database using requestID (below)
                            dataPull.execute(null);
                            //results processing
                            dataPull.close();
                            Statement acceptStatement = dbconnection.createStatement();
                            if (request.isApproved())
                            {
                                
                            }
                            else
                            {
                               acceptStatement.addBatch(null); //remove record
                               acceptStatement.addBatch(newMessageQuery("Server", sender, "TRADE " + requestNum + ": " + "was rejected by the recipient")); //notifiy sender
                               acceptStatement.executeBatch();
                            }
                            break;
                        }
                        case APPROVE:
                        {
                            String sender;
                            String recipient;
                            int requestID;
                            Statement dataPull = dbconnection.createStatement();
                            //TODO populate sender and recipient from database using requestID (below)
                            dataPull.execute(null);
                            //results processing
                            dataPull.close();
                            Statement approveRequest = dbconnection.createStatement();
                            if (request.isApproved())
                            {
                                //TODO need sql to change managers "approved" field to true
                                approveRequest.executeQuery(null);
                            }
                            else
                            {
                               approveRequest.addBatch(null); //remove record
                               approveRequest.addBatch(newMessageQuery("Server", sender, "TRADE " + requestNum + ": " + "was rejected by a mmanagert")); //notifiy sender
                               approveRequest.executeBatch();
                            }
                            approveRequest.close();
                            break;
                        }
			case LOGIN:
			{
                            boolean validated = false;
                            Statement loginRequest = dbconnection.createStatement();
                            ResultSet results = loginRequest.executeQuery(this.getloginQuery(request.getSender()));
                            while (results.next() && !validated)
                            {
                                byte[] query = request.getPassword();
                                byte[] result = results.getBytes("emppassword");
                                validated = MessageDigest.isEqual(query, result);

                            }
                            request.setApproved(validated);
                            loginRequest.close();
                            break;
			}
                        case CREATE:
                        {
                           
                            PreparedStatement AddUserRequest = dbconnection.prepareStatement(
                                    "INSERT INTO employees (empfirstname, emplastname, empaccesslevel, emplogin, emppassword, empemail, empwage)"
                                    + " VALUES (?, ?, ?, ?, ?, ?, ?)");
                            Employee newEmployee = request.getEmployee();
                            AddUserRequest.setString(1, newEmployee.getFirstName());                           
                            AddUserRequest.setString(2, newEmployee.getLastName());                           
                            AddUserRequest.setInt(3, newEmployee.getAccessLevel()); 
                            AddUserRequest.setString(4, newEmployee.getId());
                            AddUserRequest.setBytes(5, newEmployee.getPassword());
                            AddUserRequest.setString(6, newEmployee.getEmail());
                            AddUserRequest.setFloat(7, newEmployee.getWage());
                            AddUserRequest.execute();
                            AddUserRequest.close();
                            
                            //When a new record is created for an employee a new record also should be created in
                            //the boss manager table (which tells who is that employees mananger) with a null value
                            Statement addUserBossmanagerRecord = dbconnection.createStatement();
                            //executeUpdate is used rather than executeQuery because executeUpdate doesnt throw exceptions when nothing is returned by the query
                            addUserBossmanagerRecord.executeUpdate(this.newManagerQuery(newEmployee.getId()));
                            
                            break;
                        }
                        case REMOVE:
                        {
                            break;
                        }
                        case SCHEDULE:
                        {
                            Statement shiftPullRequest = dbconnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                            ResultSet results = shiftPullRequest.executeQuery(this.getEmployeeShiftInfo(request.getSender()));
                            Timestamp[] resultsList = tabulateShifts(results);
                            returnResults = new RequestResults();
                            returnResults.setShifts(resultsList);
                            shiftPullRequest.close();
                            break;
                        }
                        case VALIDATE:
                        {
                            Statement userValidateRequest= dbconnection.createStatement();
                            ResultSet results = userValidateRequest.executeQuery(this.usernameValidityQuery(request.getSender()));
                            results.next();
                            request.setApproved(results.getBoolean("isfound"));
                            userValidateRequest.close();
                            break;
                        }
                        case SHIFT_RANGE:
                        {
                            Statement shiftRangeRequest = dbconnection.createStatement();
                            ResultSet results = shiftRangeRequest.executeQuery(this.dateRangeShiftQuery(request.getShifts()[0], request.getShifts()[1], request.getSender()));
                            Timestamp[] resultsList = tabulateShifts(results);
                            returnResults = new RequestResults();
                            returnResults.setShifts(resultsList);
                            shiftRangeRequest.close();
                            break;
                        }
                        case PASSWORD_CHANGE:
                        {
                            
                            PreparedStatement passwordChange = dbconnection.prepareStatement("UPDATE employees SET emppassword = ? WHERE emplogin = ? ");
                            passwordChange.setBytes(1, request.getPassword());
                            passwordChange.setString(2, request.getSender());
                            passwordChange.execute();
                            passwordChange.close();
                            break;
                        }
		}
                return returnResults;
	}

        /**
         * takes a set of shift start and end times and puts them back-to-back in a single array
         * @param results the ResultSet received from the database asking for shifts
         * @return an array of Timestamps containing starttime, endtime, starttime, endtime etc.
         * @throws SQLException
         */
        private Timestamp[] tabulateShifts(ResultSet results) throws SQLException
        {
            Timestamp[] resultsList;
            if (results.last())
                resultsList = new Timestamp[results.getRow()*2];
            else
                return null;
            results.beforeFirst();
            for (int i = 0; results.next(); i = i + 2)
            {
                resultsList[i] = results.getTimestamp("shiftstarttime");
                resultsList[i+1] = results.getTimestamp("shiftendtime");
            }
            return resultsList;
        }
        
	/**
	* pulls the messages that are waiting for a user and returns them as a delimited string
	* @returns a ready-to-print string of the messages awaiting a user.
	*/
	private String getMessages()
        {
            //THERE IS A FUNCTION CALLED getEmployeeMessages to query for employee messages
	    return null;
	}
        
        //Generate a query to select the login (username and password) information from the database so that it can be checked for authentication
        private String getloginQuery(String LoginID){
            return "SELECT empLogin, empPassword FROM login WHERE empLogin = '" + LoginID + "'" ;
        }
        
        /**
         * generate a query to get all important non shift information from
         * the database for a given loginID, First Name, Last Name, access level, login, password,
         * email and who their manager is.
         */
        private String getWorkerInfoQuery(String LoginID)
        {
            
            return "SELECT empfirstname, emplastname, empaccesslevel,"
                    + " emplogin, emppassword, empemail, empmanager "
                    + "FROM full_employee_info"
                    + " WHERE emplogin = '" + LoginID + "'";
        }
        
        /**
         * Generate a query to get ALL the shift info for a particular person denoted by LoginID
         * @param LoginID ID of the person you want to get the shifts of
         * @return String of the Query needed
         */
        private String getEmployeeShiftInfo(String LoginID)
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
         * @param password can be null
         * @param email can be null
         * @param wage set to -1 if not specified
         * @return a custom string for the update query given the parameters 
         */
        //TODO make the byte array work or get rid of it in this query
        private String updateEmployeeQuery(String firstName, String lastName, int accesslevel, String loginID, String email, float wage){
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
        private String newManagerQuery(String employee)
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
        private String updateManagerQuery(String employee, String newManager)
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
        private String newMessageQuery(String sender, String reciever, String message )
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
        private String getEmployeeMessages(String LoginID)
        {
            return "SELECT mssgreciever, mssgsender, mssgtext, mssgsendtime "
                    + "FROM employeeinbox "
                    + "WHERE mssgreciever = '" + LoginID + "' "
                    + "ORDER BY mssgsendtime";
        }
        
        /**
         * Generate a query that returns a single entry under the column name "isfound"
         * that is either "t" if the given username is found in the database
         * or "f" if the username is not found in the database.
         * @return 
         */
        private String usernameValidityQuery(String username)
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
        private String dateRangeShiftQuery( Timestamp startDate, Timestamp endDate, String LoginID) 
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
        private String managerListQuery()
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
        private String removeEmployeeQuery(String employeeLoginID)
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
        private String insertGiveRecordQuery(String giver, Timestamp starttime, Timestamp endtime )
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
        private String deleteGiveRecordQuery(String giver, Timestamp starttime, Timestamp endtime )
        {
            return "DELETE FROM giveshifts WHERE "
                    + "giverlogin = '" + giver + "' AND "
                    + "givershiftstart = '" + starttime.toString() + "' AND "
                    + "givershiftend = '" + endtime.toString() + "' ";
        }
        
        /**
         * Generate a query that returns a single value under the column, requiremanagerapproval which is 
         * TRUE if trades require a managers signoff/permission and false in its not required
        */
        private String getManagerApprovalStatus()
        {
            return "SELECT requiremanagerapproval FROM managerapproval";
        }
        
        /**
         * Generate a query to insert into the transactions table
         * @param initiatorLoginID login of the person who sent/created the trade
         * @param finalizerLoginID login of the reciever/ secondary actor of the transaction
         * @param shiftTimes shiftTimes[0], shiftTimes[1] are the start and end times respectivly of the initiators shift
         * 
         * @param transactionType
         * @return 
         */
        //TODO check if this function works property
        private String insertShiftTransactionQuery(String initiatorLoginID, String finalizerLoginID, Timestamp[] shiftTimes, String transactionType )
        {
            return "INSERT INTO shifttransaction (initlogin, initshiftstart, initshiftend, finallogin, finalshiftstart, finalshiftend, transactiontype) "
                    + "VALUES ( "
                    + "'" + initiatorLoginID + "', "
                    + "'" + shiftTimes[0].toString() + "', "
                    + "'" + shiftTimes[1].toString() + "', "
                    + "'" + finalizerLoginID + "', "
                    + "'" + shiftTimes[2].toString() + "', "
                    + "'" + shiftTimes[3].toString() + "', "
                    + "'" + transactionType + "') ";
            
        }
        
      public static void main (String[] args) {
            
            Controller c = new Controller();
            System.out.println(c.getloginQuery("testUsername"));
	    System.out.println(c.getWorkerInfoQuery("testUsername"));
            System.out.println(c.getEmployeeShiftInfo("testUsername"));
           
            System.out.println(c.updateEmployeeQuery(null, "buster", 2, "magnusandy", null, -1));
            System.out.println(c.newManagerQuery("testUsername"));
            System.out.println(c.updateManagerQuery("magnusandy", "oneTrueGod"));
            System.out.println(c.newMessageQuery("magnusandy", "oneTrueGod", "Yoooo dawg lets do this"));
            System.out.println(c.getEmployeeMessages("oneTrueGod"));
            System.out.println(c.usernameValidityQuery("magnusandy"));
            Timestamp x = Timestamp.valueOf("2014-10-22 09:00:00");
            Timestamp y = Timestamp.valueOf("2014-10-27 09:00:00");
            System.out.println(c.dateRangeShiftQuery(x,y,"tmike"));
            System.out.println(c.dateRangeShiftQuery(x,y,null));
            System.out.println(c.managerListQuery());
            System.out.println(c.removeEmployeeQuery("tmike"));
            System.out.println(c.insertGiveRecordQuery("tmike", x, y));
            System.out.println(c.deleteGiveRecordQuery("tmike", x, y));     
            
                   
        }  
}