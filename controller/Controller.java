/*
*  Connor Lavoy
*  cbl013
*  11118147
*  October 2, 2014
*/

package controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Controller {

	private DriverManager database;
	private Connection dbconnection;

	public Controller() {
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
	*  Called by other pieces of the system to make requests to the database/managers
	*  @param request - a Request object which contains information to be parsed by the controller.
	*/
	public void sendRequest (Request request) throws SQLException {
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
				ResultSet results = loginRequest.executeQuery("select * from employees where employeeID = " + request.getSender());
				while (results.next() && !validated)
				{
					validated = request.getMessage().equals(results.getNString("empPassword"));
				}
				if (validated)
					request.setApproved(true);
				else
					request.setApproved(false);
			}
		}
	}

	/**
	*  pulls the messages that are waiting for a user and returns them as a delimited string
	*  @returns a ready-to-print string of the messages awaiting a user.
	*/
	private String getMessages() {
	    return null;
	}
}