/*
 * Refactored from Input, for better understanding, and High Cohesion
 * ANDREW MAGNUS, amm215
 */
package frontend;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *
 * @author Andrew Magnus
 */
public class PasswordHasher
{
    //made this function public so that adding a new employee can create a hash too
    public static byte[] createHash(String password) {
	byte[] returnHash = null;
	
	try {
	    MessageDigest md = MessageDigest.getInstance("SHA-256");
	    md.update(password.getBytes());
	    returnHash = md.digest();
	     
	    if(returnHash == null) {
		throw new RuntimeException("ReturnHash null in Controller");
	    }
	} catch (NoSuchAlgorithmException nsae) {
	    System.out.println("Exception: " + nsae);
	}
	
	return returnHash;
    }
    
}
