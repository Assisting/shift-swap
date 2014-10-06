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
            System.out.print("connection");
            try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
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
	*  Called by other pieces of the system to make requests to the database/managers
	*  @param request - a Request object which contains information to be parsed by the controller.
        * @throws SQLException
	*/
	public void sendRequest (Request request) throws SQLException {
            System.out.print("testing");
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
				ResultSet results = loginRequest.executeQuery("select * from employees where empnum = " + request.getSender());
				while (results.next() && !validated)
				{
                                    System.out.print(request.getSender());
                                    System.out.print(request.getMessage());
                                    validated = request.getMessage().equals(results.getString("emppassword"));
                                    System.out.print(request.getMessage());
				}
                                System.out.print("true");
				if (validated){
                                        System.out.print("true2");
					request.setApproved(true);}
                                else{
                                        System.out.print("false");
					request.setApproved(false);}

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