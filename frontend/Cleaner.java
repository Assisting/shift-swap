/*
 * Input Cleaner, refactored out of Input Class.
 */
package frontend;

import controller.Employee;
import controller.Shift;

/**
 * @author Andrew Magnus
 */
public class Cleaner
{
    /**
     * Attempts to clean up input. Currently only replace ; and some comparisons.
     * Future versions could include Regex to avoid all manner of SQL injection
     * attacks, though doing so in a way that doesn't mess too much with private
     * messages is difficult to implement elegantly.
     * @return a cleansed string
     * @param input the string to be cleaned
     */
    public static String clean(String input)
    {
	String returnString = input.replace(">", "more than");
	returnString = returnString.replace("<", "less than");
	returnString = returnString.replace("=", "equals");
	returnString = returnString.replace(";", ":");
	
	return returnString;
    }
    
    /**
     * Usernames may not contain spaces
     * @param input the input username
     * @return the cleaned username
     */
    public static String cleanId(String input)
    {
	String returnString = clean(input);
	
	returnString = returnString.replace(" ", "");
	
	return returnString;
    }
    
    /**
     * Attempts to clean up input.
     * @param input the employee to be cleaned
     * @return a cleansed employee object
     */
    public static Employee clean(Employee input)
    {
	String cleanEmail = clean(input.getEmail());
	String cleanID = cleanId(input.getId());
	String cleanFirstName = clean(input.getFirstName());
	String cleanLastName = clean(input.getLastName());
	
	Employee returnEmployee = new Employee(cleanID, cleanFirstName, 
				cleanLastName, input.getAccessLevel(),
				input.getPassword(), cleanEmail, input.getWage());
	
	return returnEmployee;
    }
    
    /**
     * Attempts to clean up input.
     * @param input the Shift you wish the clean
     * @return a cleansed Shift object
     */
    public static Shift clean(Shift input)
    {
	String cleanID = cleanId(input.getEmployeeLogin());
	
	Shift returnShift = new Shift(cleanID, input.getShiftStartTime(),
				input.getShiftEndTime());
				
	return returnShift;
    }
    
}
