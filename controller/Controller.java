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
                    case GIVE:
                    {
                        Statement giveRequest = dbconnection.createStatement();
                        giveRequest.execute(insertGiveRecordQuery(request.getSender(), request.getShifts()[0], request.getShifts()[1]));
                        break;
                    }
                    case TRADE:
                    {
                        Statement tradeRequest = dbconnection.createStatement();
                        String manager1 = tradeRequest.executeQuery(getWorkerInfoQuery(request.getSender())).getString("empmanager");
                        boolean manager1reqd = tradeRequest.executeQuery(getManagerApprovalStatus(manager1)).getBoolean("required");
                        String manager2 = tradeRequest.executeQuery(getWorkerInfoQuery(request.getRecipient())).getString("empmanager");
                        boolean manager2reqd = tradeRequest.executeQuery(getManagerApprovalStatus(manager1)).getBoolean("required");
                        

                        if (request.getShifts()[0] == null) //Take request
                        {
                            tradeRequest.execute(insertTradeQuery(request.getSender(), request.getRecipient(), request.getShifts(), "TAKE", manager1, manager1reqd, manager2, manager2reqd));
                            tradeRequest.close();
                            Request giveAccept = Request.AcceptRequest(request.getSender(), request.getRecipient(), new Timestamp[] {request.getShifts()[0], request.getShifts()[2]}, true);
                            sendRequest(giveAccept);
                        }
                        else //trade
                        {
                            tradeRequest.addBatch(insertTradeQuery(request.getSender(), request.getRecipient(), request.getShifts(), "TRADE", manager1, manager1reqd, manager2, manager2reqd));
                            tradeRequest.addBatch(newMessageQuery(request.getSender(), request.getRecipient(), "TRADE: " + request.getSender() + " wants to trade "+ request.getShifts()[0] + " for " + request.getShifts()[2]));
                            tradeRequest.executeBatch();
                            tradeRequest.close();
                        }
                        break;
                    }
                    case ACCEPT:
                    {
                        Statement acceptStatement = dbconnection.createStatement();
                        ResultSet transactionFields = acceptStatement.executeQuery(getTransactionData(request.getSender(), request.getRecipient(), request.getShifts()[0], request.getShifts()[1]));
                        if (!transactionFields.next())
                            throw new SQLException("Data nor found");
                        if (request.isApproved())
                        {
                            boolean manager1Approved = transactionFields.getBoolean("manager1Approval");
                            boolean manager2Approved = transactionFields.getBoolean("manager2Approval");
                            if (!manager1Approved || !manager2Approved)
                            {
                                String giveTime = "nothing";
                                if (transactionFields.getTimestamp("initshiftstart") != null)
                                {
                                    giveTime = transactionFields.getTimestamp("initshiftstart").toString();
                                }
                                if (!manager1Approved)
                                {
                                    String manager = transactionFields.getString("initiatorManager");
                                    acceptStatement.executeQuery(newMessageQuery("Server", manager, "APPROVAL: " + transactionFields.getString("initlogin") + " wants to trade "+ giveTime + " for " + transactionFields.getString("finalshiftstart") + ". This request requires your approval"));
                                }
                                if (!manager2Approved)
                                {
                                    String manager = transactionFields.getString("finalizerManager");
                                    acceptStatement.executeQuery(newMessageQuery("Server", manager, "APPROVAL: " +  transactionFields.getString("finallogin") + " wants to trade "+ transactionFields.getString("finalshiftstart") + " for " + giveTime + ". This request requires your approval"));
                                }
                            }
                            else
                            {
                                makeTrade(transactionFields);
                            }
                        }
                        else
                        {
                            String sender = transactionFields.getString("sender");
                            acceptStatement.addBatch(deleteShiftTransactionQuery(request.getSender(), request.getRecipient(), request.getShifts()[0], request.getShifts()[1])); //remove record
                            acceptStatement.addBatch(newMessageQuery("Server", sender, "TRADE: trading " + request.getShifts()[0] + " was rejected by the recipient")); //notifiy sender
                            acceptStatement.executeBatch();
                        }
                        acceptStatement.close();
                        break;
                    }
                    case APPROVE:
                    {
                        Statement approveRequest = dbconnection.createStatement();
                        ResultSet transactionFields = approveRequest.executeQuery(getTransactionData(request.getSender(), request.getRecipient(), request.getShifts()[0], request.getShifts()[1]));
                        if (!transactionFields.next())
                            throw new SQLException("Data not found");
                        if (request.isApproved())
                        {
                            approveRequest.executeQuery(updateManagerApprovalTransactionsQuery(request.getSender(), request.getRecipient(), request.getShifts()[0], request.getShifts()[1], request.getApprover()));
                            boolean manager1Approved = transactionFields.getBoolean("manager1Approval");
                            boolean manager2Approved = transactionFields.getBoolean("manager2Approval");
                            if (manager1Approved && manager2Approved)
                                makeTrade(transactionFields);
                        }
                        else
                        {
                            String sender = transactionFields.getString("sender");
                            String recipient = transactionFields.getString("recipient");
                            approveRequest.addBatch(deleteShiftTransactionQuery(request.getSender(), request.getRecipient(), request.getShifts()[0], request.getShifts()[1])); //remove record
                            approveRequest.addBatch(newMessageQuery("Server", sender, "TRADE: trading " + request.getShifts()[0] + " was rejected by a manager")); //notifiy sender
                            approveRequest.addBatch(newMessageQuery("Server", sender, "TRADE: trading " + request.getShifts()[1] + " was rejected by a manager")); //notifiy recipient
                            approveRequest.executeBatch();
                        }
                        approveRequest.close();
                        break;
                    }
                    case ADD:
                    {
                        Statement addShift = dbconnection.createStatement();
                        addShift.execute(insertShiftQuery(request.getSender(), request.getShifts()[0], request.getShifts()[1]));
                        addShift.close();
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
                        returnResults = new RequestResults();
                        returnResults.setApproved(validated);
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
                    case DELETE:
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
                        returnResults = new RequestResults();
                        returnResults.setApproved(results.getBoolean("isfound"));
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
                    case GIVELIST:
                    {
                        Statement getGives = dbconnection.createStatement();
                        ResultSet gives = getGives.executeQuery(getAllGiveRecords());
                        String[] names = new String[gives.getRow()];
                        Timestamp[] timePairs = new Timestamp[gives.getRow()*2];
                        int i = 0;
                        while (gives.next())
                        {
                            names[i] = gives.getString("giverlogin");
                            timePairs[i*2] = gives.getTimestamp("givershiftstart");
                            timePairs[i*2+1] = gives.getTimestamp("givershiftend");
                        }
                        gives.close();
                        returnResults = new RequestResults();
                        returnResults.setNames(names);
                        returnResults.setShifts(timePairs);
                        break;
                    }
                    case SEND_MESSAGE:
                    {
                        Statement sendMessage = dbconnection.createStatement();
                        sendMessage.execute(newMessageQuery(request.getSender(), request.getRecipient(), "MESSAGE: " + request.getNotification()));
                        sendMessage.close();
                        break;
                    }
                    case MESSAGES:
                    {
                        Statement getMessages = dbconnection.createStatement();
                        ResultSet messages = getMessages.executeQuery(getEmployeeMessages(request.getSender()));
                        returnResults = new RequestResults();
                        String message = "";
                        while (messages.next())
                        {
                            message += "From: " + messages.getString("mssgsender") + " To: " + messages.getString("mssgreceiver") + " -> " + messages.getString("mssgtext") + "\n";
                        }
                        break;
                    }
                    case MANAGER_CHANGE:
                    {
                        Statement managerChange = dbconnection.createStatement();
                        managerChange.execute( updateManagerQuery(request.getSender(), request.getManager()) );
                        managerChange.close();
                        break;
                    }
                    case APPROVAL_STATUS:
                    {
                        Statement approvalStatus = dbconnection.createStatement();
                        approvalStatus.execute(approvalStatusChangeQuery(request.getManager(), request.isApproved()));
                        approvalStatus.close();
                        break;
                    }
                    case ACCESS_UPDATE:
                    {
                        Statement accessUpdate = dbconnection.createStatement();
                        accessUpdate.execute(updateEmployeeQuery(null, null, request.getEmployee().getAccessLevel(), request.getSender(), null, -1));
                        accessUpdate.close();
                        break;
                    }
                    case ACCESS_LEVEL:
                    {
                        returnResults = new RequestResults();
                        Statement accessLevel = dbconnection.createStatement();
                        ResultSet results = accessLevel.executeQuery(getAccessLevelQuery(request.getSender()));
                        accessLevel.close();
                        returnResults.setAccessLevel(results.getInt("empaccesslevel"));
                        break;
                    }
            }
            return returnResults;
    }

    /** deletes a shift transaction
     * @param sender the person initiating the shfit chagne
     * @param shiftstart/shiftend the start and end of the shift.
     * @return the string query */
    private String deleteShiftTransactionQuery(String sender, String recipient,
			Timestamp shiftstart, Timestamp shiftend) {
		return ("DELETE FROM shifttransaction"
				+ " WHERE "
				+ "initlogin = '" + sender
				+ "' AND initshiftstart = '" + shiftstart
				+ "' AND intshiftend = '" + shiftend
				+ "';");
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
    
    private void makeTrade(ResultSet transactionFields) throws SQLException
    {
        Statement transaction = dbconnection.createStatement();
        if (transactionFields.getString("transactiontype").equals("TRADE"))
        {
            transaction.addBatch(deleteShiftQuery(transactionFields.getString("initlogin"), transactionFields.getTimestamp("initshiftstart"), transactionFields.getTimestamp("initshiftend")));
            transaction.addBatch(deleteShiftQuery(transactionFields.getString("finallogin"), transactionFields.getTimestamp("initshiftstart"), transactionFields.getTimestamp("initshiftend")));
        }
        transaction.addBatch(deleteShiftQuery(transactionFields.getString("finallogin"), transactionFields.getTimestamp("finalshiftstart"), transactionFields.getTimestamp("finalshiftend")));
        transaction.addBatch(insertShiftQuery(transactionFields.getString("initlogin"), transactionFields.getTimestamp("finalshiftstart"), transactionFields.getTimestamp("finalshiftend")));
        transaction.executeBatch();
    }
    
    
    /** returns the entire row of a transaction
     * @param sender the login of the person starting transaction
     * @param start/end the start and end time of the sender
     * @parma return the sql to gather this info**/
    private String getTransactionData(String sender, String recipient, Timestamp start, Timestamp end)
    {
        return ("Select * from shifttransaction WHERE "
        		+ "initlogin ='" + sender +"' AND "
        		+ "initshiftstart = '" + start + "' AND "
        		+ "initshiftend = '" + end + "';");
    }
    
    /** returns the entire row of a transaction
     * @param sender the login of the person starting transaction
     * @param start/end the start and end time of the sender
     * @parma return the sql to gather this info**/
    private String getTransactionID(String sender, String recipient, Timestamp start, Timestamp end)
    {
        return ("Select transactionid from shifttransaction WHERE "
        		+ "initlogin ='" + sender +"' AND "
        		+ "initshiftstart = '" + start + "' AND "
        		+ "initshiftend = '" + end + "';");
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
     * @param email can be null
     * @param wage set to -1 if not specified
     * @return a custom string for the update query given the parameters 
     */
    
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
    
    private String deleteEmployeeMessage(String sender, String reciever, Timestamp sendtime)
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
     * Generate a query to return all rows in the giveshifts table
     * @return String version of the query necessary
     */
    private String getAllGiveRecords()
    {
        return "SELECT giverlogin, givershiftstart, givershiftend FROM giveshifts";
    }

    /**
     * Generate a query that returns a single value under the column, requiremanagerapproval which is 
     * TRUE if trades require a managers signoff/permission and false in its not required
     * @param managerlogin 
    */
    private String getManagerApprovalStatus(String managerlogin)
    {
        return "SELECT ma_approval FROM managerapproval WHERE ma_manager = " + managerlogin;
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
    //TODO check if this function works property
    private String insertTradeQuery(String initiatorLoginID, String finalizerLoginID, Timestamp[] shiftTimes, String transactionType, String initiatorManager, boolean initManagerSign, String finalizerManager, boolean finalManagerSign )
    {

        if(shiftTimes[2] != null)
        {
            return "INSERT INTO shifttransaction (initlogin, initshiftstart, initshiftend, finallogin, finalshiftstart, finalshiftend, initmanagerlogin, finalmanagerlogin, initmanagersign, finalmanagersign, transactiontype,) "
                    + "VALUES ( "
                    + "'" + initiatorLoginID + "', "
                    + "'" + shiftTimes[0].toString() + "', "
                    + "'" + shiftTimes[1].toString() + "', "
                    + "'" + finalizerLoginID + "', "
                    + "'" + shiftTimes[2].toString() + "', "
                    + "'" + shiftTimes[3].toString() + "', "
                    + "'" + initiatorManager + "', "
                    + "'" + finalizerManager + "', "
                    + "'" + initManagerSign + "', "
                    + "'" + finalManagerSign + "', "
                    + "'" + transactionType + "') ";
        }
        else //finalizer shifts are null
        {
           return "INSERT INTO shifttransaction (initlogin, initshiftstart, initshiftend, finallogin, initmanagerlogin, finalmanagerlogin, initmanagersign, finalmanagersign, transactiontype) "
                    + "VALUES ( "
                    + "'" + initiatorLoginID + "', "
                    + "'" + shiftTimes[0].toString() + "', "
                    + "'" + shiftTimes[1].toString() + "', "
                    + "'" + finalizerLoginID + "', "
                    + "'" + initiatorManager + "', "
                    + "'" + finalizerManager + "', "
                    + "'" + initiatorManager + "', "
                    + "'" + finalizerManager + "', "
                    + "'" + transactionType + "') ";
        }

    }

            /**
     * Generate a query to delete from the shift transactions table
     * @param initiatorLoginID login of the person who created the trade
     * @param finalizerLoginID login of the person who was sent the trade
     * @param shiftTimes shiftTimes[0] and shiftTimes[1] are used for start and end time of the initiator shifts
     * @return String query
     */
    private String deleteShiftTransactionQuery(int transactionID)
    {
        return "DELETE FROM shifttransaction WHERE "
                + "transactionid = '" + transactionID + "'";
    }

   /**
    * Generates query that updates one or both manager approval fields to true
    * if the manager is the initiators manager, that one will be updated
    * if the manager is the finalizers manager, that one will be updated
    * OR BOTH IF BOTH WOO
	* @param sender the person sending request
	* @param recipient person receiving request
	* @param shiftstart/shiftend the start/end time of the shift
    * @param managerLoginID the id of the manager
    * @return the sQL to update approval 
    * @throws SQLException 
    */
    //TODO testing if this works
    //updateManagerApprovalTransactionsQuery(request.getSender(), request.getRecipient(), request.getShifts()[0], request.getShifts()[1], request.getApprover())
    private String updateManagerApprovalTransactionsQuery(String sender, String recipient, Timestamp shiftstart, Timestamp shiftend, String managerLoginID ) throws SQLException
    {
    	Statement getTrannyID = null;
		try {
			getTrannyID = dbconnection.createStatement();
		} catch (SQLException e) {
			System.out.println("updateManagerApprovalTransactionsQuery died");
			e.printStackTrace();
		}
    	ResultSet results = getTrannyID.executeQuery(this.getTransactionID(sender, recipient, shiftstart, shiftend));
    	int transactionID = results.getInt("Column name here"); // how do I pull the int out?    	
        String initiatorlogin = "UPDATE shifttransaction "
                + "SET initmanagersign = TRUE "
                + "WHERE initlogin = '" + transactionID + "' AND "
                + "initmanagerlogin = '" + managerLoginID + "'; "
                +"UPDATE shifttransaction "
                + "SET finalmanagersign = TRUE "
                + "WHERE transactionid = '" + transactionID + "' AND "
                + "finalmanagerlogin = '" + managerLoginID + "' ";
        return initiatorlogin;
    }
    
    /**
     * Query to Insert a shift into the employeeshifts table
     * @param loginID = login of the employee assiciated with the shift
     * @param startTime = start time of the shift
     * @param endTime = end time of the shift
     * @return String version of  the query
     */
    private String insertShiftQuery(String loginID, Timestamp startTime, Timestamp endTime)
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
    private String deleteShiftQuery(String loginID, Timestamp startTime, Timestamp endTime)
    {
        return "DELETE FROM employeeshifts WHERE "
                + "shiftemployeelogin = '" + loginID + "' AND "
                + "shiftstarttime = '" + startTime.toString() + "' AND "
                + "shiftendtime = '" + endTime.toString() + "'";
    }
    
      private String approvalStatusChangeQuery(String manager, boolean isApproved)
    {
        return "UPDATE managerapproval SET ma_approval = "
                + "'" + isApproved +"' "
                + "WHERE ma_manager = '" + manager + "' ";
    }
      
    private String getAccessLevelQuery(String empName)
    {
        return "SELECT empaccesslevel FROM employees WHERE emplogin = " + empName;
    }
    
    
    /**
    tatement shiftRangeRequest = dbconnection.createStatement();
    ResultSet results = shiftRangeRequest.executeQuery(this.dateRangeShiftQuery(request.getShifts()[0], request.getShifts()[1], request.getSender()));
    Timestamp[] resultsList = tabulateShifts(results);
    returnResults = new RequestResults();
    returnResults.setShifts(resultsList);
    shiftRangeRequest.close();
    break;*/

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
        Timestamp[] b = new Timestamp[4];
        b[0] = Timestamp.valueOf( "2014-10-20 08:45:00" );
         b[1] = Timestamp.valueOf( "2014-10-20 18:30:00" );
       // System.out.println(c.insertShiftTransactionQuery("tmike", "tsanjay", b, "swap"));
         System.out.println(c.insertShiftQuery("rickjames", b[0], b[1]));
         System.out.println(c.deleteShiftQuery("rickjames", b[0], b[1]));
         System.out.println(c.getAllGiveRecords());
         System.out.println(c.deleteEmployeeMessage("tmike", "rickjames", b[0]));

    }  
}



/** Sample SQL for a shift exchange
 * TAKE
 * 		INSERT INTO employeeshifts VALUES (persontaking, shiftStartEnd[0], shiftStartEnd[1]);
 * 		DELETE FROM employeeshifts
 * 		WHERE
 * 			shiftemployeelogin = persongiving AND
 * 			shiftstarttime = shiftStartEnd[0] AND
 * 			shitendtime = shiftStartEnd[1]
 *		;
 *
 * SWAP
 * 		INSERT INTO employeeshifts VALUES (person1, shiftStartEnd[2], shiftStartEnd[3]); //person 2s shifts
 * 		INSERT INTO employeeshifts VALUES (person2, shiftStartEnd[0], shiftStartEnd[1]); //person 1s shifts
 * 		
 * 		DELETE FROM employeeshifts
 * 		WHERE
 * 			shiftemployeelogin = person1 AND	// remove person 1s shifts he traded away
 * 			shiftstarttime = shiftStartEnd[0] AND
 * 			shitendtime = shiftStartEnd[1]
 *		;
 *		DELETE FROM employeeshifts
 * 		WHERE
 * 			shiftemployeelogin = person2 AND	// remove person 2s shifts he gave away
 * 			shiftstarttime = shiftStartEnd[2] AND
 * 			shitendtime = shiftStartEnd[3]
 *		;
 *  */