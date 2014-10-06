// kts192 - temp pass hasher, made from erik's code


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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

	    public static void main (String[] args) {
	    	for (String s: args) {
	    		System.out.println(createHash(s));
	    	}
	    }
}
