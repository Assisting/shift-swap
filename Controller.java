/*
*  Connor Lavoy
*  cbl013
*  11118147
*  October 2, 2014
*/

public class Controller {

	private DriverManager database;
	private Connection dbconnection;

	public Controller() {
		database = new DriverManager();
		try
		{
			dbconnection = database.getConnection ("lovett.usask.ca:PROTOCOL:cmpt370_group13", "cmpt370_group13", "1truegod");
		}
		catch
		{
			System.out.println("Error connecting to Database...");
			return;
		}
	}

	/**
	*  Called by other pieces of the system to make requests to the database/managers
	*  @param request - a Request object which contains information to be parsed by the controller.
	*/
	public void sendRequest (Request request) {
		switch (
			case Request.RequestType.TAKE:
			{
			}
			case Request.RequestType.GIVE:
			{
			}
			case Request.RequestType.TRADE:
			{
			}
			case Request.RequestType.LOGIN:
			{
				booleanean validated;
				Statement loginRequest = dbconnection.createStatement();
				ResultSet results = loginRequest.executeQuery("select * from employees where employeeID = " + request.getSender());
				while (results.next() && !validated)
				{
					validated = request.getMessage() == getNString("empPassword");
				}
				if (validated)
					setApproved(true);
				else
					setApproved(false);
			}
	}

	/**
	*  pulls the messages that are waiting for a user and returns them as a delimited string
	*  @returns a ready-to-print string of the messages awaiting a user.
	*/
	private String getMessages() {
	}
}