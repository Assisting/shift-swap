/*
*  Connor Lavoy
*  cbl013
*  11118147
*  October 2, 2014
*/


package controller;

/**
 * container for all employee data (used to add employees to the database)
 * @author Connor
 */
public class Employee {
 
    private String Id;
    private String firstName;
    private String lastName;
    private int accessLevel;
    private byte[] password;
    private String email;
    private float wage;
    
    public Employee(String Id, String firstName, String lastName, int accessLevel, byte[] password, String email, float wage) {
        if (Id == null || Id.equals("")) throw new IllegalArgumentException("Invalid employee Id");
        if (firstName == null || firstName.equals("")) throw new IllegalArgumentException("Invalid First Name");
        if (lastName == null || lastName.equals("")) throw new IllegalArgumentException("Invalid Last Name");
        if (accessLevel > 3) throw new IllegalArgumentException("accessLevel is out of range");
        if (password.length != 32) throw new IllegalArgumentException("Password array must be 32-byte array");
        
        this.Id = Id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.accessLevel = accessLevel;
        this.password = password;
        this.email = email;
        this.wage = wage;
    }

    /**
     * @return the employee's Id
     */
    public String getId() {
        return Id;
    }

    /**
     * @return the employee's firstName
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * @return the employee's lastName
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * @return the employee's accessLevel
     */
    public int getAccessLevel() {
        return accessLevel;
    }

    /**
     * @return the employee's password
     */
    public byte[] getPassword() {
        return password;
    }

    /**
     * @return the employee's email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @return the employee's wage
     */
    public float getWage() {
        return wage;
    }
}