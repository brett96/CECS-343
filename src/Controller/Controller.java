package Controller;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;

import Model.Appointment;
import Model.DBModel;
import Model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public final class Controller 
{
	private static Controller controller;
	private static final String DB_NAME = "cecs343.db";
	private static final String USER_TABLE_NAME = "users";
    private static final String[] USER_FIELD_NAMES = {"userID", "name", "email", "password", "birthYear", "birthMonth", "birthDay"};
    private static final String[] USER_FIELD_TYPES = {"INTEGER", "TEXT", "TEXT PRIMARY KEY", "TEXT", "INTEGER", "INTEGER", "INTEGER"};	// Primary key is actually appointmentsID, NOT email.  Change is in effect but can't be changed here for some reason
    
    private static final String APPOINTMENTS_TABLE_NAME = "appointments";
    private static final String[] APPOINTMENTS_FIELD_NAMES = {"userID", "appointmentName", "startYear", "startMonth", "startDay", "endYear", "endMonth", "endDay", "startTime", "endTime", "aID"};
    private static final String[] APPOINTMENTS_FIELD_TYPES = {"INTEGER", "TEXT", "INTEGER", "INTEGER", "INTEGER", "INTEGER", "INTEGER", "INTEGER", "INTEGER", "INTEGER", "INTEGER"};
    
    private static final String DATA_FILE = "schedule.csv";
    
    private ObservableList<User> allUsersList;
    private ObservableList<Appointment> allAppointmentsList;
    private User currentUser;
    //private DBModel DB;
    private DBModel usersDB;
    private DBModel userAppointmentsDB;
    
    /**
     * Singleton
     */
    private Controller() {}
    
    /**
     * Initializes controller if set to null
     * @return
     */
    public static Controller getInstance()
    {
    	if(controller == null)
    	{
    		controller = new Controller();
    		// Initialize observable lists
    		controller.allUsersList = FXCollections.observableArrayList();	
    		controller.allAppointmentsList = FXCollections.observableArrayList();
    		
    		try
            {
    			// Initialize users table
            	controller.setUsersDB(new DBModel(DB_NAME, USER_TABLE_NAME, USER_FIELD_NAMES, USER_FIELD_TYPES));	
            	// Populate resultsList with all data from users table
            	ArrayList<ArrayList<String>> resultsList = controller.getUsersDB().getAllRecords();	
            	for(ArrayList<String> values : resultsList)
            	{
            		int userID = Integer.parseInt(values.get(0));
            		String name = values.get(1);
            		String email = values.get(2);
            		int year = Integer.parseInt(values.get(4));
            		int month = Integer.parseInt(values.get(5));
            		int day = Integer.parseInt(values.get(6));
            		// Add each user in db to allUsersList
            		controller.allUsersList.add(new User(name, email, userID , year, month, day));	
            	}
            	
            	// Initialize appointments
            	controller.setAppointmentsDB(new DBModel(DB_NAME, APPOINTMENTS_TABLE_NAME, APPOINTMENTS_FIELD_NAMES, APPOINTMENTS_FIELD_TYPES)); 
            	// Get all appointments
            	ArrayList<ArrayList<String>> apptList = controller.getAppointmentsDB().getAllRecords();		
            	for(ArrayList<String> values : apptList)
            	{
            		int userID = Integer.parseInt(values.get(0));
            		String name = values.get(1);
            		int startYear = Integer.parseInt(values.get(2));
            		int startMonth = Integer.parseInt(values.get(3));
            		int startDay = Integer.parseInt(values.get(4));
            		int endYear = Integer.parseInt(values.get(5));
            		int endMonth = Integer.parseInt(values.get(6));
            		int endDay = Integer.parseInt(values.get(7));
            		int startTime = Integer.parseInt(values.get(8));
            		int endTime = Integer.parseInt(values.get(9));
            		int aID = Integer.parseInt(values.get(10));
            		// Add each appointment to list
            		controller.allAppointmentsList.add(new Appointment(userID, name, startYear, startMonth , startDay,  endYear, endMonth, endDay, startTime, endTime, aID));
            	}
            	
            	// Set up DBModels
            	controller.usersDB = new DBModel(DB_NAME, USER_TABLE_NAME, USER_FIELD_NAMES, USER_FIELD_TYPES);
            	controller.userAppointmentsDB = new DBModel(DB_NAME, APPOINTMENTS_TABLE_NAME, APPOINTMENTS_FIELD_NAMES, APPOINTMENTS_FIELD_TYPES);
            	
            }
    		catch (SQLException e)
            {
                e.printStackTrace();
            }
    	}
    	
    	return controller;
    }
    	
    
    /**
     * Returns ID of current user
     * @return id
     */
    public int getCurrentUserID()
    {
    	return this.currentUser.getId();
    }
    
    /**
     * Returns current user
     * @return
     */
    public User getCurrentUser()
    {
    	return this.currentUser;
    }
    
    /**
     * Sets current user
     * @param user
     */
    public void setUser(User user)
    {
    	this.currentUser = user;
    }
    
    
    /**
     * Sign in method for Sign in scene
     * @param email
     * @param password
     * @return
     */
    public String signInUser(String email, String password) {
		for (User u : controller.allUsersList)	// Iterate through every user
		{
			String userEmail = u.getEmail();
			userEmail = "'" + userEmail + "'";	// match email format from table
			if (userEmail.equalsIgnoreCase(email))	// Compare user email to given email
			{
				try 
				{
					// Get iterated user's password
					ArrayList<ArrayList<String>> resultsList = controller.getUsersDB().getRecord(String.valueOf(u.getId()));
					String storedPassword = resultsList.get(0).get(3);
					if (password.equals(storedPassword))	// Check for match to sign in
					{
						this.currentUser = u;
						System.out.println(controller.getAppointmentsForCurrentUser());
						return "SIGNED IN";
					}
						
						
				} 
				catch (Exception e) {
					e.printStackTrace();
					return "There was an error";
				}
				return "Incorrect password.  Please try again.";		
			}	
		}
		return "Email address not found.  Please try again.";
	}
    
    /**
     * If a user is already signed in, the passed parameters are not checked
     * @param newName
     * @param email
     * @param birthday
     * @return
     */
    public String resetName(String newName, String email, LocalDate birthday)
    {
    	try
    	{
    		if(currentUser != null)
    		{
    			controller.getUsersDB().changeName(currentUser.getEmail(), newName);
    			currentUser.setName(newName);
    			return "SUCCESS";
    		}
    		else
    		{
    			for(User u : controller.allUsersList)
    			{
    				String userEmail = u.getEmail();
	    			userEmail = "'" + userEmail + "'";
    				if(userEmail.equalsIgnoreCase(email) && u.getBirthday().equals(birthday))
    				{
    					//System.out.println("email = " + email + "; newName = " + newName);
    					controller.getUsersDB().changeName(email, newName);
    					u.setName(newName);
    					System.out.println(controller.getUsersDB().getRecord(String.valueOf(u.getId()))); 
    					return "SUCCESS";
    				}
    			}
    		}
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	return "FAILED";
    }
    
    /**
     * If user already signed in, parameters not verified & assumed true
     * @param newPass
     * @param email
     * @param birthday
     * @return
     */
    public String resetPassword(String newPass, String email, LocalDate birthday)
    {
    	try
    	{
	    	if(currentUser != null)
	    	{
	    		controller.getUsersDB().changePassword(email, newPass);
	    		return "SUCCESS";
	    	}
	    	else
	    	{
	    		for(User u : controller.allUsersList)
	        	{
	    			//System.out.println("u.getEmail() = " + u.getEmail() + "; email = " + email);
	    			String userEmail = u.getEmail();
	    			userEmail = "'" + userEmail + "'";
	    			//System.out.println("u.getBirthday() = " + u.getBirthday().toString() + "; birthday = " + birthday.toString());
	        		if(userEmail.equalsIgnoreCase(email) && u.getBirthday().equals(birthday))
	        		{
	        			controller.getUsersDB().changePassword(email, newPass);
	        	    	//System.out.println(controller.getUsersDB().getRecord(String.valueOf(u.getId())));    	
	        			return "SUCCESS";
	        		}
	        	}
	    	}
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    		System.out.println("ERROR");
    	}
    	return "FAILED";
    }
    
    /**
     * Checks if newEmail is being used by another user.  If not, user validation attempted, and email updated if passed
     * @param email
     * @param newEmail
     * @param birthday
     * @return
     */
    public String resetEmail(String email, String newEmail, LocalDate birthday)
    {
    	try
    	{
    		boolean validUser = false;
    		
    		for(User u : controller.allUsersList)
    		{
    			String userEmail = u.getEmail();
    			userEmail = "'" + userEmail + "'";
    			// Check if email is associated w/ user.  Can't have same email b/c email is primary key
    			if(userEmail.equalsIgnoreCase(newEmail))
    				return "Email already exists";
    			
    			//System.out.println("userEmail = " + userEmail + "; email = " + email);
    			// Validate user if email and birthday match given email and birthday
    			if(userEmail.equalsIgnoreCase(email) && u.getBirthday().equals(birthday))
    			{
    				validUser = true;
    				currentUser = u;
    			}
    		}
    		
    		// If loop exited, email can be updated if user has been validated
    		if(validUser)
    		{
    			controller.getUsersDB().changeEmail(email, newEmail);
    			currentUser.setEmail(newEmail);
    			return "SUCCESS";
    		}
    		
    	}
    	catch(Exception e) 
    	{
    		e.printStackTrace();
    		return "ERROR";
    	}
    	return "FAILED";
    }
    
    /**
     * Reset birthday of specified user
     * @param email = user's email
     * @param currentBDay = user's current birthday
     * @param year = new birthday year
     * @param month = new birthday month
     * @param day = new birthday day
     * @return status
     */
    public String resetBirthday(String email, LocalDate currentBDay, int year, int month, int day)
    {
    	try
    	{
	    	if(currentUser != null)		// If current user isn't null (someone is signed in), automatically change current's
	    	{
	    		controller.getUsersDB().changeBirthday(email, year, month, day);
	    		currentUser.setBirthday(LocalDate.of(year, month, day));
	    		return "SUCCESS";
	    	}
	    	else	// If user wants to reset birthday but is not logged in
	    	{
	    		for(User u : controller.allUsersList)	// Iterate through all users
	        	{
	    			//System.out.println("u.getEmail() = " + u.getEmail() + "; email = " + email);
	    			String userEmail = u.getEmail();
	    			userEmail = "'" + userEmail + "'";
	    			// Compares user iteration info against provided info
	        		if(userEmail.equalsIgnoreCase(email) && u.getBirthday().equals(currentBDay))	
	        		{
	        			controller.getUsersDB().changeBirthday(email, year, month, day);
	        			u.setBirthday(LocalDate.of(year, month, day));
	        	    	//System.out.println(controller.getUsersDB().getRecord(String.valueOf(u.getId())));    	
	        			return "SUCCESS";
	        		}
	        	}
	    	}
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    		System.out.println("ERROR");
    	}
    	
    	return "FAILED";
    }
    
    /**
     * User will select appointment to change from GUI.  Selected appointment id will be pulled and passed w/ specified newName
     * @param id
     * @param newName
     * @return
     */
    public String resetAppointmentName(int id, String newName)
    {
    	try
    	{
    		controller.getAppointmentsDB().changeAppointmentName(id, newName);
    		return "SUCCESS";
    	}
    	catch (Exception e)
    	{
    		e.printStackTrace();
    		return "ERROR";
    	}
    }
    
    public String resetAppointmentStartDate(int id, int year, int month, int day)
    {
    	try
    	{
    		controller.getAppointmentsDB().changeAppointmentStartDate(id, year, month, day);
    		return "SUCCESS";
    	}
    	catch (Exception e)
    	{
    		e.printStackTrace();
    		return "ERROR";
    	}
    }
    
    public String resetAppointmentEndDate(int id, int year, int month, int day)
    {
    	try
    	{
    		controller.getAppointmentsDB().changeAppointmentEndDate(id, year, month, day);
    		return "SUCCESS";
    	}
    	catch (Exception e)
    	{
    		e.printStackTrace();
    		return "ERROR";
    	}
    }
    
    public String resetAppointmentStartTime(int id, int start)
    {
    	try
    	{
    		controller.getAppointmentsDB().changeAppointmentStartTime(id, start);
    		return "SUCCESS";
    	}
    	catch (Exception e)
    	{
    		e.printStackTrace();
    		return "ERROR";
    	}
    }
    
    /**
     * Set end time of appointment w/ given id
     * @param id = appointment ID of appointment to be modified
     * @param end = end time
     * @return status
     */
    public String resetAppointmentEndTime(int id, int end)
    {
    	try
    	{
    		controller.getAppointmentsDB().changeAppointmentEndTime(id, end);
    		return "SUCCESS";
    	}
    	catch (Exception e)
    	{
    		e.printStackTrace();
    		return "ERROR";
    	}
    }
    
//    public boolean verifyOwner(String email, LocalDate birthday)
//    {
//    	if(currentUser != null)
//    		return true;
//    	else
//    	{
//    		for(User u : controller.allUsersList)
//        	{
//        		if(u.getEmail().equalsIgnoreCase(email) && u.getBirthday().equals(birthday))
//        			return true;
//        	}
//    	}
//    	return false;
//    }
    
    /**
     * Signs up a user and adds user data to appropriate tables
     * @param name
     * @param email
     * @param password
     * @return
     */
    public String signUpUser(String name, String email, String password, int year, int month, int day)
    {
    	for(User user : controller.allUsersList)	// Check for email already in use
    	{
    		String userEmail = user.getEmail();
			userEmail = "'" + userEmail + "'";
    		if(userEmail.equalsIgnoreCase(email))
    			return "Email already exists";
    	}
    	
    	// Array of values to be inserted into db
    	String[] values = {name, email, password, Integer.toString(year), Integer.toString(month), Integer.toString(day)};
    	//System.out.println(email);
    	try
    	{
    		int id = controller.getUsersDB().createRecord(Arrays.copyOfRange	//  Try inserting user into db & get userID
    				(USER_FIELD_NAMES, 1, USER_FIELD_NAMES.length), values);
    		User newUser = new User(name, email, id, year, month, day);		//  Add new user to allUsersList
    		controller.allUsersList.add(newUser);
    		controller.currentUser = newUser;		// Set current user to new user
    		return "SUCCESS";
    	}
    	catch(SQLException e)
    	{
    		e.printStackTrace();
    		return "Account Not Created";
    	}
    	
    }
    
    /**
     * Adds appointment to appointment database
     * @param name
     * @param startYear
     * @param startMonth
     * @param startDay
     * @param endYear
     * @param endMonth
     * @param endDay
     * @param startTime
     * @param endTime
     * @return
     */
    public String addAppointment(String name, int startYear, int startMonth, int startDay, int endYear, int endMonth, int endDay, int startTime, int endTime)
    {
    	if (currentUser != null) 
    	{
    		int id = currentUser.getId();
    		// Array of passed appointment parameters to be inserted to db
	    	String[] values = {Integer.toString(id), name, Integer.toString(startYear), Integer.toString(startMonth), Integer.toString(startDay), Integer.toString(endYear), Integer.toString(endMonth), Integer.toString(endDay), Integer.toString(startTime), Integer.toString(endTime)};
	    	try
	    	{
	    		// Add appointment to db, get aID
	    		int aID = controller.getAppointmentsDB().insertAppointment(Arrays.copyOfRange(APPOINTMENTS_FIELD_NAMES, 0, APPOINTMENTS_FIELD_NAMES.length - 1), values);
	    		// Add appointment to allAppointmentsList
	    		Appointment newAppointment = new Appointment(id, name, startYear, startMonth, startDay, endYear, endMonth, endDay, startTime, endTime, aID);
	    		controller.allAppointmentsList.add(newAppointment); 
	    	}
	    	catch(SQLException e)
	    	{
	    		e.printStackTrace();
	    		return "Account Not Created";
	    	}
	    	return "SUCCESS";
    	}
    	return "You must log in first";
    }
    
    /**
     * Returns ObservableList of all user data
     * @return
     */
    public ObservableList<User> getAllUsers()
    {
    	return controller.allUsersList;
    }
    
    public ObservableList<Appointment> getAllAppointments()
    {
    	return controller.allAppointmentsList;
    }
    
    
	/**
     * Returns users database table
     * @return
     */
	public DBModel getUsersDB() 
	{
		return usersDB;
	}
	
	/**
	 * Returns appointment table
	 * @return
	 */
	public DBModel getAppointmentsDB()
	{
		return userAppointmentsDB;
	}
	
	/**
	 * If currentUser is set, this will get all of the appointments from the db that contain the user's ID
	 * @return
	 */
	public ObservableList<Appointment> getAppointmentsForCurrentUser()
    {
        ObservableList<Appointment> userAppointments = FXCollections.observableArrayList();	// ObservableList that is returned, containing all the current user's appointments
        if (currentUser != null)
        {
            try {
                ArrayList<ArrayList<String>> resultsList = controller.userAppointmentsDB.getRecord(String.valueOf(currentUser.getId()));	// Gets current user's appointments
                ArrayList<Integer> aIDs = new ArrayList<>();	// ArrayList of found appointment IDs
                for (ArrayList<String> values : resultsList)	// Iterate through every appointment
                {
                    int uId = Integer.parseInt(values.get(0));	// Get userID of current appointment
                    for (Appointment a : controller.allAppointmentsList)	// Iterate through all appointments
                    {
                        if (a.getID() == uId && !aIDs.contains(a.getAID()))	// Checks for matching userID's and that aID hasn't already been added 
                        {
                            userAppointments.add(a);	// Adds appointment to current user's appointment list
                            aIDs.add(a.getAID());		// Adds appointment ID to aIDs array to avoid duplicates
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else
        	System.out.println("Current user is set to null");
        return userAppointments;
    }

	/**
	 * Sets users database table
	 * @param usersDB
	 */
	public void setUsersDB(DBModel usersDB) 
	{
		this.usersDB = usersDB;
	}
	
	public void setAppointmentsDB(DBModel appointmentsDB)
	{
		this.userAppointmentsDB = appointmentsDB;
	}
}
    
