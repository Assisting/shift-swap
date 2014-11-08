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
			}
			case GIVE:
			{
			}
			case TRADE:
			{
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
                            if (validated)
                                    request.setApproved(true);
                            else
                                    request.setApproved(false);
                            break;
			}
                        case CREATE:
                        {
                            PreparedStatement AddUserRequest = dbconnection.prepareStatement(
                                    "INSERT INTO employees (empfirstname, emplastname, empaccesslevel, emplogin, emppassword, empemail, empwage)"
                                    + " VALUES (?, ?, ?, ?, ?, ?, ?)");
                            Employee newEmployee = request.getEmployee();
                            AddUserRequest.setString(0, newEmployee.getFirstName());
                            AddUserRequest.setString(1, newEmployee.getLastName());
                            AddUserRequest.setInt(2, newEmployee.getAccessLevel());
                            AddUserRequest.setString(3, newEmployee.getId());
                            AddUserRequest.setBytes(4, newEmployee.getPassword());
                            AddUserRequest.setString(5, newEmployee.getEmail());
                            AddUserRequest.setFloat(5, newEmployee.getWage());
                            AddUserRequest.execute();
                        }
                        case REMOVE:
                        {
                            
                        }
                        case SCHEDULE:
                        {
                            Statement shiftPullRequest = dbconnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                            ResultSet results = shiftPullRequest.executeQuery(this.getEmployeeShiftInfo(request.getSender()));
                            Timestamp[] resultsList = tabulateShifts(results);
                            returnResults = new RequestResults();
                            returnResults.setShifts(resultsList);
                            return returnResults;
                        }
                        case VALIDATE:
                        {
                            Statement UserValidateRequest= dbconnection.createStatement();
                            ResultSet results = UserValidateRequest.executeQuery(this.usernameValidityQuery(request.getSender()));
                            results.next();
                            request.setApproved(results.getBoolean("isfound"));
                        }
                        case SHIFT_RANGE:
                        {
                            Statement ShiftRangeRequest = dbconnection.createStatement();
                            ResultSet results = ShiftRangeRequest.executeQuery(this.dateRangeShiftQuery(request.getShifts()[0], request.getShifts()[1], request.getSender()));
                            Timestamp[] resultsList = tabulateShifts(results);
                            returnResults = new RequestResults();
                            returnResults.setShifts(resultsList);
                            return returnResults;
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
	private String getMessages() {
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
         * Create a query to insert a new employee into the database
         * @param firstName
         * @param lastName
         * @param accesslevel
         * @param loginID
         * @param password
         * @param email
         * @param wage
         * @return 
         */
        //TODO make the byte array work or get rid of it in this query
        private String newEmployeeQuery(String firstName, String lastName, int accesslevel, String loginID, String password, String email, float wage)
        {
            return "INSERT INTO employees (empfirstname, emplastname, empaccesslevel,"
                    + " emplogin, emppassword, empemail, empwage)"
                    + " VALUES ('" + firstName + "', "
                    + "'" + lastName + "', "
                    + "'" + accesslevel + "', "
                    + "'" + loginID + "', "
                    + "'" + password + "', "
                    + "'" + email + "', "
                    + "'" + wage + "')";
        }
        /**
         *generate a query to update any or all employee info, it is assumed that the empLogin cannot be changed
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
        private String updateEmployeeQuery(String firstName, String lastName, int accesslevel, String loginID, Byte[] password, String email, float wage){
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
            if(password != null){
                if(needComma == true){
                        ret = ret + ", ";
                }
                ret = ret+ "emppassword = '"+ password +"' ";
                needComma = true;
            }
            if(email != null){
                if(needComma == true) {
                        ret = ret + ", ";
                }
                ret = ret+ "empemail = '"+ email +"' ";
                needComma = true;
            }
            if(wage != -1){
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
        
      public static void main (String[] args) {
            
            Controller c = new Controller();
            System.out.println(c.getloginQuery("testUsername"));
	    System.out.println(c.getWorkerInfoQuery("testUsername"));
            System.out.println(c.getEmployeeShiftInfo("testUsername"));
            System.out.println(c.newEmployeeQuery("Elmer", "Fudd", 1, "eFudd", "wabbit", "Fudd@mail.com", (float) 53.232));
           // System.out.println(c.updateEmployeeQuery(null, "buster", 2, "eFudd", "jack", null, -1));
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
                    
                   
        }  
}