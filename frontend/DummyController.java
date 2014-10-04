package frontend;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *
 * @author erik
 */
public class DummyController
{
    /**
     * In the for-realsies program this will have to actually return
     * the hash value from the database for the username passed in
     * @param username the username logging in
     * @return the hash of username's password
     */
    public static byte[] getHash(String username) {
	byte[] returnHash = null;
	
	try {
	    MessageDigest md = MessageDigest.getInstance("SHA-256");
	     md.update(username.getBytes());
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
}
    