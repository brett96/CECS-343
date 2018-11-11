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
    private static final String[] USER_FIELD_NAMES = {"appointmentsID", "name", "email", "password", "birthYear", "birthMonth", "birthDay"};
    private static final String[] USER_FIELD_TYPES = {"INTEGER", "TEXT", "TEXT PRIMARY KEY", "TEXT", "INTEGER", "INTEGER", "INTEGER"};	// Primary key is actually appointmentsID, NOT email.  Change is in effect but can't be changed here for some reason
    
    private static final String APPOINTMENTS_TABLE_NAME = "appointments";
    private static final String[] APPOINTMENTS_FIELD_NAMES = {"appointmentsID", "appointmentName", "startYear", "startMonth", "startDay", "endYear", "endMonth", "endDay", "startTime", "endTime"};
    private static final String[] APPOINTMENTS_FIELD_TYPES = {"INTEGER", "TEXT", "INTEGER", "INTEGER", "INTEGER", "INTEGER", "INTEGER", "INTEGER", "INTEGER", "INTEGER"};
    
    private ObservableList<User> allUsersList;
    private ObservableList<Appointment> allAppointmentsList;
    private User currentUser;
    private DBModel DB;
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
    		controller.allUsersList = FXCollections.observableArrayList();
    		controller.allAppointmentsList = FXCollections.observableArrayList();
    		
    		try
            {
            	controller.setUsersDB(new DBModel(DB_NAME, USER_TABLE_NAME, USER_FIELD_NAMES, USER_FIELD_TYPES));
            	ArrayList<ArrayList<String>> resultsList = controller.getUsersDB().getAllRecords();
            	for(ArrayList<String> values : resultsList)
            	{
            		int appointmentsID = Integer.parseInt(values.get(0));
            		String name = values.get(1);
            		String email = values.get(2);
            		int year = Integer.parseInt(values.get(4));
            		int month = Integer.parseInt(values.get(5));
            		int day = Integer.parseInt(values.get(6));
            		controller.allUsersList.add(new User(name, email, appointmentsID , year, month, day));
            	}
            	
            	controller.setAppointmentsDB(new DBModel(DB_NAME, APPOINTMENTS_TABLE_NAME, APPOINTMENTS_FIELD_NAMES, APPOINTMENTS_FIELD_TYPES));
            	ArrayList<ArrayList<String>> apptList = controller.getAppointmentsDB().getAllRecords();
            	for(ArrayList<String> values : apptList)
            	{
            		int appointmentsID = Integer.parseInt(values.get(0));
            		String name = values.get(1);
            		int startYear = Integer.parseInt(values.get(2));
            		int startMonth = Integer.parseInt(values.get(3));
            		int startDay = Integer.parseInt(values.get(4));
            		int endYear = Integer.parseInt(values.get(5));
            		int endMonth = Integer.parseInt(values.get(6));
            		int endDay = Integer.parseInt(values.get(7));
            		int startTime = Integer.parseInt(values.get(8));
            		int endTime = Integer.parseInt(values.get(9));
            		controller.allAppointmentsList.add(new Appointment(appointmentsID, name, startYear, startMonth , startDay,  endYear, endMonth, endDay, startTime, endTime));
            	}
            	
            	
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
     * Signs in user if email and password match record and are valid
     * @param email
     * @param password
     * @return
     */
    public User signIn(String email, String password)
    {
    	for (User u : controller.allUsersList)
    	{
    		String userEmail = u.getEmail();
			userEmail = "'" + userEmail + "'";
			if (userEmail.equalsIgnoreCase(email))
			{
				try 
				{
					ArrayList<ArrayList<String>> resultsList = controller.getUsersDB().getRecord(String.valueOf(u.getId()));
					String storedPassword = resultsList.get(0).get(3);
					if (password.equals(storedPassword))
					{
						this.currentUser = u;
						return u;
						
					}										
				} 
				catch (Exception e) 
				{
					e.printStackTrace();
				}
			}
    	}
    	return null;
    }
    
    /**
     * Sign in method for Sign in scene
     * @param email
     * @param password
     * @return
     */
    public String signInUser(String email, String password) {
		for (User u : controller.allUsersList)
		{
			String userEmail = u.getEmail();
			userEmail = "'" + userEmail + "'";
			if (userEmail.equalsIgnoreCase(email))
			{
				try 
				{
					ArrayList<ArrayList<String>> resultsList = controller.getUsersDB().getRecord(String.valueOf(u.getId()));
					String storedPassword = resultsList.get(0).get(3);
					if (password.equals(storedPassword))
					{
						this.currentUser = u;
						controller.addAppointment("Presentation", 2000, 01, 01, 2000, 01, 01, 100, 200);
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
    
    public String resetBirthday(String email, LocalDate currentBDay, int year, int month, int day)
    {
    	try
    	{
	    	if(currentUser != null)
	    	{
	    		controller.getUsersDB().changeBirthday(email, year, month, day);
	    		currentUser.setBirthday(LocalDate.of(year, month, day));
	    		return "SUCCESS";
	    	}
	    	else
	    	{
	    		for(User u : controller.allUsersList)
	        	{
	    			//System.out.println("u.getEmail() = " + u.getEmail() + "; email = " + email);
	    			String userEmail = u.getEmail();
	    			userEmail = "'" + userEmail + "'";
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
    	for(User user : controller.allUsersList)
    	{
    		String userEmail = user.getEmail();
			userEmail = "'" + userEmail + "'";
    		if(userEmail.equalsIgnoreCase(email))
    			return "Email already exists";
    	}
    	// Add user to database
    	String[] values = {name, email, password, Integer.toString(year), Integer.toString(month), Integer.toString(day)};
    	try
    	{
    		int id = controller.getUsersDB().createRecord(Arrays.copyOfRange
    				(USER_FIELD_NAMES, 1, USER_FIELD_NAMES.length), values);
    		User newUser = new User(name, email, id, year, month, day);
    		controller.allUsersList.add(newUser);
    		controller.currentUser = newUser;
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
	    	String[] values = {Integer.toString(id), name, Integer.toString(startYear), Integer.toString(startMonth), Integer.toString(startDay), Integer.toString(endYear), Integer.toString(endMonth), Integer.toString(endDay), Integer.toString(startTime), Integer.toString(endTime)};
	    	try
	    	{
	    		controller.getAppointmentsDB().insertAppointment(APPOINTMENTS_FIELD_NAMES, values);
	    		//int id = currentUser.getId();
	    		Appointment newAppointment = new Appointment(id, name, startYear, startMonth, startDay, endYear, endMonth, endDay, startTime, endTime);
	    		controller.allAppointmentsList.add(newAppointment);  // Causes null pointer for some reason
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
	
	public DBModel getAppointmentsDB()
	{
		return userAppointmentsDB;
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
    
