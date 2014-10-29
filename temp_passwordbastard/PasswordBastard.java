package temp_passwordbastard;// kts192 - temp pass hasher, made from erik's code


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import controller.Controller;

/**
 * The class of static functions to help handle input.
 */
public class PasswordBastard
{

    /**
     * This just ain't secure. Working on it.
     * @param username the username logging in
     * @param password the password provided
     * @return true if they can log in
     */
   
	
	private static Connection dbconnection;

    private static byte[] createHash(String password) {
		byte[] returnHash = null;
	
		try {
		    MessageDigest md = MessageDigest.getInstance("SHA-256");
		     md.update(password.getBytes());
		     returnHash = md.digest();
		     
		     if(returnHash == null) {
			 throw new RuntimeException(
				 "ReturnHash null in DummyController");
		     }
		} catch (NoSuchAlgorithmException nsae) {
		    System.out.println("Exception: " + nsae);
		}
		
		return returnHash;
    }
    
	public static void Controller() {
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
	}}

	    public static void main (String[] args) {

	    	Controller c = new Controller();

	    	Controller();
	    	
	    	
	    	byte[] pass = createHash("doge");
	    	System.out.println("do you believe in miracles?");
	    	
	    	Statement loginRequest = null;
			try {
				loginRequest = dbconnection.createStatement();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	try {
				ResultSet results = loginRequest.executeQuery(c.updateEmployeeQuery(null, null, 1, "rickjames", pass, null, 10));
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    
	    }
}
