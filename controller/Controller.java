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
    private Timestamp nullStamp;

    public Controller() {
        //System.out.print("connection");
        nullStamp = new Timestamp(0);
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
                        giveRequest.execute(Queries.insertGiveRecordQuery(request.getSender(), request.getShifts()[0], request.getShifts()[1]));
                        break;
                    }
                    case TRADE:
                    {
                        Statement tradeRequest = dbconnection.createStatement();
                        
                        ResultSet manager1Result = tradeRequest.executeQuery(Queries.getWorkerInfoQuery(request.getSender()));//.getString("manager");
                        manager1Result.next();
                        String manager1 = manager1Result.getString("manager");
                        ResultSet manager1Approval = tradeRequest.executeQuery(Queries.getManagerApprovalStatus(manager1));
                        manager1Approval.next();
                        boolean manager1reqd = manager1Approval.getBoolean("ma_approval");
                        
                        ResultSet manager2Result = tradeRequest.executeQuery(Queries.getWorkerInfoQuery(request.getRecipient()));//.getString("manager");
                        manager2Result.next();
                        String manager2 = manager2Result.getString("manager");
                        ResultSet manager2Approval = tradeRequest.executeQuery(Queries.getManagerApprovalStatus(manager1));
                        manager2Approval.next();
                        boolean manager2reqd = manager2Approval.getBoolean("ma_approval");
                        
                        if (request.getShifts()[0] == null) //Take request
                        {
                            tradeRequest.execute(Queries.insertShiftTransactionQuery(request.getSender(), request.getRecipient(), request.getShifts(), "take", manager1, !manager1reqd, manager2, !manager2reqd));
                            tradeRequest.close();
                            Timestamp[] acceptTimes = new Timestamp[2];
                            acceptTimes[0] = request.getShifts()[0];
                            acceptTimes[1] = request.getShifts()[2];
                            Request giveAccept = Request.AcceptRequest(request.getSender(), request.getRecipient(), acceptTimes, true);
                            sendRequest(giveAccept);
                        }
                        else //trade
                        {
                            tradeRequest.addBatch(Queries.insertShiftTransactionQuery(request.getSender(), request.getRecipient(), request.getShifts(), "swap", manager1, !manager1reqd, manager2, !manager2reqd));
                            tradeRequest.addBatch(Queries.newMessageQuery(request.getSender(), request.getRecipient(), "TRADE: " + request.getSender() + " wants to trade "+ request.getShifts()[0] + " for " + request.getShifts()[2]));
                            tradeRequest.executeBatch();
                            tradeRequest.close();
                        }
                        break;
                    }
                    case ACCEPT:
                    {
                        Statement acceptStatement = dbconnection.createStatement();
                        ResultSet transactionFields = acceptStatement.executeQuery(Queries.getTransactionData(request.getSender(), request.getRecipient(), request.getShifts()[0], request.getShifts()[1]));
                        if (!transactionFields.next())
                            throw new SQLException("Data not found");
                        if (request.isApproved())
                        {
                            boolean manager1Approved = transactionFields.getBoolean("initmanagersign");
                            boolean manager2Approved = transactionFields.getBoolean("finaltmanagersign");
                            if (!manager1Approved || !manager2Approved)
                            {
                                String giveTime = "nothing";
                                if (transactionFields.getTimestamp("initshiftstart") != null)
                                {
                                    giveTime = transactionFields.getTimestamp("initshiftstart").toString();
                                }
                                Statement managerChecking = dbconnection.createStatement();
                                if (!manager1Approved)
                                {
                                    String manager = transactionFields.getString("initmanagerlogin");
                                    managerChecking.execute(Queries.newMessageQuery("Server", manager, "APPROVAL: " + transactionFields.getString("initlogin") + " wants to trade "+ giveTime + " for " + transactionFields.getString("finalshiftstart") + ". This request requires your approval"));
                                }
                                if (!manager2Approved)
                                {
                                    String manager = transactionFields.getString("finalmanagerlogin");
                                    managerChecking.execute(Queries.newMessageQuery("Server", manager, "APPROVAL: " +  transactionFields.getString("finallogin") + " wants to trade "+ transactionFields.getString("finalshiftstart") + " for " + giveTime + ". This request requires your approval"));
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
                            acceptStatement.addBatch(Queries.deleteShiftTransactionQuery(request.getSender(), request.getRecipient(), request.getShifts()[0], request.getShifts()[1])); //remove record
                            acceptStatement.addBatch(Queries.newMessageQuery("Server", sender, "TRADE: trading " + request.getShifts()[0] + " was rejected by the recipient")); //notifiy sender
                            acceptStatement.executeBatch();
                        }
                        acceptStatement.close();
                        break;
                    }
                    case APPROVE:
                    {
                        Statement approveRequest = dbconnection.createStatement();
                        ResultSet transactionFields = approveRequest.executeQuery(Queries.getTransactionData(request.getSender(), request.getRecipient(), request.getShifts()[0], request.getShifts()[1]));
                        if (!transactionFields.next())
                            throw new SQLException("Data not found");
                        if (request.isApproved())
                        {
                            Statement managerApprove = dbconnection.createStatement();
                            managerApprove.executeQuery(updateManagerApprovalTransactionsQuery(request.getSender(), request.getRecipient(), request.getShifts()[0], request.getShifts()[1], request.getApprover()));
                            //renew data
                            transactionFields = approveRequest.executeQuery(Queries.getTransactionData(request.getSender(), request.getRecipient(), request.getShifts()[0], request.getShifts()[1]));
                            boolean manager1Approved = transactionFields.getBoolean("initmanagersign");
                            boolean manager2Approved = transactionFields.getBoolean("finaltmanagersign");
                            if (manager1Approved && manager2Approved)
                                makeTrade(transactionFields);
                        }
                        else
                        {
                            String sender = transactionFields.getString("sender");
                            String recipient = transactionFields.getString("recipient");
                            approveRequest.addBatch(Queries.deleteShiftTransactionQuery(request.getSender(), request.getRecipient(), request.getShifts()[0], request.getShifts()[1])); //remove record
                            approveRequest.addBatch(Queries.newMessageQuery("Server", sender, "TRADE: trading " + request.getShifts()[0] + " was rejected by a manager")); //notifiy sender
                            approveRequest.addBatch(Queries.newMessageQuery("Server", recipient, "TRADE: trading " + request.getShifts()[1] + " was rejected by a manager")); //notifiy recipient
                            approveRequest.executeBatch();
                        }
                        approveRequest.close();
                        break;
                    }
                    case ADD_SHIFT:
                    {
                        Statement addShift = dbconnection.createStatement();
                        addShift.execute(Queries.insertShiftQuery(request.getSender(), request.getShifts()[0], request.getShifts()[1]));
                        addShift.close();
                    }
                    case LOGIN:
                    {
                        boolean validated = false;
                        Statement loginRequest = dbconnection.createStatement();
                        ResultSet results = loginRequest.executeQuery(Queries.getloginQuery(request.getSender()));
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
                    case CREATE_USER:
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
                        addUserBossmanagerRecord.executeUpdate(Queries.newManagerQuery(newEmployee.getId()));

                        break;
                    }
                    case DELETE_USER:
                    {
                        Statement deleteUser = dbconnection.createStatement();
                        deleteUser.execute(Queries.removeEmployeeQuery(request.getRecipient()));
                        deleteUser.close();
                        break;
                    }
                    case SCHEDULE:
                    {
                        Statement shiftPullRequest = dbconnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                        ResultSet results = shiftPullRequest.executeQuery(Queries.getEmployeeShiftInfo(request.getSender()));
                        Timestamp[] resultsList = tabulateShifts(results);
                        returnResults = new RequestResults();
                        returnResults.setShifts(resultsList);
                        shiftPullRequest.close();
                        break;
                    }
                    case VALIDATE_UNIQUE:
                    {
                        Statement userValidateRequest= dbconnection.createStatement();
                        ResultSet results = userValidateRequest.executeQuery(Queries.usernameValidityQuery(request.getSender()));
                        if (!results.next())
                            return null;
                        returnResults = new RequestResults();
                        returnResults.setApproved(results.getBoolean("isfound"));
                        userValidateRequest.close();
                        break;
                    }
                    case USER_INFO:
                    {
                        Statement userInfo = dbconnection.createStatement();
                        ResultSet results = userInfo.executeQuery(Queries.getWorkerInfoQuery(request.getSender()));
                        if (!results.next())
                            return null;
                        returnResults = new RequestResults();
                        Employee employee = new Employee(request.getSender(), results.getString("empfirstname"), results.getString("emplastname"), results.getInt("empaccesslevel"), null, results.getString("empemail"), results.getFloat("empwage"));
                        returnResults.setEmployee(employee);
                    }
                    case GET_USERNAME:
                    {
                        Statement username = dbconnection.createStatement();
                        ResultSet results = username.executeQuery(Queries.getEmployeeLogin(request.getSender(), request.getRecipient()));
                        if (!results.next())
                            return null;
                        returnResults = new RequestResults();
                        returnResults.setMessages(results.getString("emplogin"));
                    }
                    case SHIFT_RANGE:
                    {
                        Statement shiftRangeRequest = dbconnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                        ResultSet results = shiftRangeRequest.executeQuery(Queries.dateRangeShiftQuery(request.getShifts()[0], request.getShifts()[1], request.getSender()));
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
                        Statement getGives = dbconnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                        ResultSet gives = getGives.executeQuery(Queries.getAllGiveRecords());
                        String[] names;
                        Timestamp[] timePairs;
                        if (gives.last())
                        {
                            names = new String[gives.getRow()];
                            timePairs = new Timestamp[gives.getRow()*2];
                        }
                        else
                            return null;
                        gives.beforeFirst();
                        
                        int i = 0;
                        while (gives.next())
                        {
                            names[i] = gives.getString("giverlogin");
                            timePairs[i*2] = gives.getTimestamp("givershiftstart");
                            timePairs[i*2+1] = gives.getTimestamp("givershiftend");
                            i++;
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
                        sendMessage.execute(Queries.newMessageQuery(request.getSender(), request.getRecipient(), "MESSAGE: " + request.getNotification()));
                        sendMessage.close();
                        break;
                    }
                    case GET_MESSAGES:
                    {
                        Statement getMessages = dbconnection.createStatement();
                        ResultSet messages = getMessages.executeQuery(Queries.getEmployeeMessages(request.getSender()));
                        returnResults = new RequestResults();
                        String message = "";
                        Timestamp[] times = null;
                        if (messages.last())
                        {
                            times = new Timestamp[messages.getRow()];
                        }
                        else
                            return null;
                        int i = 0;
                        while (messages.next())
                        {
                            message += "From: " + messages.getString("mssgsender") + " To: " + messages.getString("mssgreciever") + " -> " + messages.getString("mssgtext") + "\n";
                            times[i] = messages.getTimestamp("mssgsendtime");
                            i ++;
                        }
                        returnResults.setMessages(message);
                        returnResults.setShifts(times);
                        break;
                    }
                    case MANAGER_CHANGE:
                    {
                        Statement managerChange = dbconnection.createStatement();
                        managerChange.execute( Queries.updateManagerQuery(request.getSender(), request.getManager()) );
                        managerChange.close();
                        break;
                    }
                    case APPROVAL_STATUS:
                    {
                        Statement approvalStatus = dbconnection.createStatement();
                        approvalStatus.execute(Queries.approvalStatusChangeQuery(request.getManager(), request.isApproved()));
                        approvalStatus.close();
                        break;
                    }
                    case ACCESS_UPDATE:
                    {
                        Statement accessUpdate = dbconnection.createStatement();
                        accessUpdate.execute(Queries.updateEmployeeQuery(null, null, request.getEmployee().getAccessLevel(), request.getSender(), null, -1));
                        accessUpdate.close();
                        break;
                    }
                    case ACCESS_LEVEL:
                    {
                        returnResults = new RequestResults();
                        Statement accessLevel = dbconnection.createStatement();
                        ResultSet results = accessLevel.executeQuery(Queries.getAccessLevelQuery(request.getSender()));
                        if (results.next())
                                returnResults.setAccessLevel(results.getInt("empaccesslevel"));
                        accessLevel.close();
                        break;
                    }
                    case SHIFTS_DAY:
                    {
                        Statement shiftsDay = dbconnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                        ResultSet results = shiftsDay.executeQuery(Queries.getAllShiftsNotWorkedByY(request.getShifts()[0], request.getShifts()[1], request.getSender()));
                        String[] names;
                        Timestamp[] timePairs;
                        if (results.last())
                        {
                            names = new String[results.getRow()];
                            timePairs = new Timestamp[results.getRow()*2];
                        }
                        else
                            return null;
                        results.beforeFirst();
                        
                        int i = 0;
                        while (results.next())
                        {
                            names[i] = results.getString("shiftemployeelogin");
                            timePairs[i*2] = results.getTimestamp("shiftstarttime");
                            timePairs[i*2+1] = results.getTimestamp("shiftendtime");
                            i++;
                        }
                        shiftsDay.close();
                        returnResults = new RequestResults();
                        returnResults.setNames(names);
                        returnResults.setShifts(timePairs);
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
    
    private void makeTrade(ResultSet transactionFields) throws SQLException
    {
        Statement transaction = dbconnection.createStatement();
        if (transactionFields.getString("transactiontype").equals("TRADE"))
        {
            transaction.addBatch(Queries.deleteShiftQuery(transactionFields.getString("initlogin"), transactionFields.getTimestamp("initshiftstart"), transactionFields.getTimestamp("initshiftend")));
            transaction.addBatch(Queries.insertShiftQuery(transactionFields.getString("finallogin"), transactionFields.getTimestamp("initshiftstart"), transactionFields.getTimestamp("initshiftend")));
        }
        else
            transaction.addBatch(Queries.deleteGiveRecordQuery(transactionFields.getString("finallogin"), transactionFields.getTimestamp("finalshiftstart"), transactionFields.getTimestamp("finalshiftend")));
        transaction.addBatch(Queries.deleteShiftQuery(transactionFields.getString("finallogin"), transactionFields.getTimestamp("finalshiftstart"), transactionFields.getTimestamp("finalshiftend")));
        transaction.addBatch(Queries.insertShiftQuery(transactionFields.getString("initlogin"), transactionFields.getTimestamp("finalshiftstart"), transactionFields.getTimestamp("finalshiftend")));
        transaction.addBatch(Queries.deleteShiftTransactionQuery(transactionFields.getString("initlogin"), transactionFields.getString("finallogin"), transactionFields.getTimestamp("initshiftstart"), transactionFields.getTimestamp("finalshiftstart")));
        transaction.executeBatch();
        transaction.close();
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
    private String updateManagerApprovalTransactionsQuery(String sender, String recipient, Timestamp initstart, Timestamp finalstart, String managerLoginID ) throws SQLException
    {
    	Statement getTrannyID = null;
        try {
                getTrannyID = dbconnection.createStatement();
        } catch (SQLException e) {
                System.out.println("updateManagerApprovalTransactionsQuery died");
                e.printStackTrace();
        }
    	ResultSet results = getTrannyID.executeQuery(Queries.getTransactionID(sender, recipient, initstart, finalstart));
    	int transactionID = results.getInt("transactionid");  	
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
}