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
	Request loginRequest = new Request(username, null, pwHash.toString(), Request.RequestType.LOGIN);
	
        try{
        cont.sendRequest(loginRequest);
        }
        catch(Exception e)
        {}
        
	
	    if(loginRequest.isApproved()) {
		return true;
	    }
	
	
	return false;
    }
    
    private static byte[] createHash(String password) {
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
}
