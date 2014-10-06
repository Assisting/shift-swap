package testconnex;

import java.sql.*;

public class test {

// MAJOR KEY: We need to add a class path to the postgresql jar


	public test() {
		
	}

    public static void main (String[] args) {
    	Connection db1 = null;
    	
    	try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("fail");
		}
    	
    	try {
			db1 = DriverManager.getConnection("jdbc:postgresql://lovett.usask.ca:5432/", "cmpt370_group13", "1truegod");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("fail");
		}

 
    	
    	Statement stmt=null;
    	 try {
			stmt = db1.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
         ResultSet rs = null;
		try {
			rs = stmt.executeQuery( "SELECT * FROM employees;" );
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
         try {
			while ( rs.next() ) {
			    int id = rs.getInt("empnum");
			    String  name = rs.getString("empfirstname");
			    System.out.println( "ID = " + id );
			    System.out.println( "NAME = " + name );
			 }
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }
}