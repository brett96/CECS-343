package Controller;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.time.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

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
    
    private static final String USER_APPOINTMENTS_TABLE_NAME = "userAppointments";
    private static final String[] USER_APPOINTMENTS_FIELD_NAMES = {"userID", "aID"};
    private static final String[] USER_APPOINTMENTS_FIELD_TYPES = {"INTEGER", "INTEGER"};
    
    private static final String EXPORT_FILE = "schedule.csv";
    private static final String IMPORT_FILE = "import.csv";
    
    private ObservableList<User> allUsersList;
    private ObservableList<Appointment> allAppointmentsList;

    private User currentUser;
    //private DBModel DB;
    private DBModel usersDB;
    private DBModel appointmentsDB;
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
            	
            	//controller.setUserAppointmentsDB(new DBModel(DB_NAME, USER_APPOINTMENTS_TABLE_NAME, USER_APPOINTMENTS_FIELD_NAMES, USER_APPOINTMENTS_FIELD_TYPES));
            	
            	// Set up DBModels
            	controller.usersDB = new DBModel(DB_NAME, USER_TABLE_NAME, USER_FIELD_NAMES, USER_FIELD_TYPES);
            	controller.appointmentsDB = new DBModel(DB_NAME, APPOINTMENTS_TABLE_NAME, APPOINTMENTS_FIELD_NAMES, APPOINTMENTS_FIELD_TYPES);
            	controller.userAppointmentsDB = new DBModel(DB_NAME, USER_APPOINTMENTS_TABLE_NAME, USER_APPOINTMENTS_FIELD_NAMES, USER_APPOINTMENTS_FIELD_TYPES);
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
     * Exports signed in user's schedule to a csv file
     * @return
     * @throws IOException
     */
    public String exportSchedule() throws IOException
    {
    	if(currentUser != null)
    	{
    		FileWriter schedule = new FileWriter(EXPORT_FILE);
    		ObservableList<Appointment> userAppointments = controller.getAppointmentsForCurrentUser();
    		
    		// First line = User Info
			schedule.append(currentUser.getName() + ", " + currentUser.getEmail() + ", " + Integer.toString(currentUser.getId()) + ", " 
					+ Integer.toString(currentUser.getYear()) + ", " + Integer.toString(currentUser.getMonth()) + ", " 
					+ Integer.toString(currentUser.getDay()) + "\n");
    	
			// Remaining lines are appointments
			for(Appointment a : userAppointments)
    		{
    			schedule.append(Integer.toString(a.getID()) + ", "); 			// Add userID
    			schedule.append(a.getName() + ", ");							// Add appointment name
    			schedule.append(Integer.toString(a.getStartYear()) + ", ");		// Start year
    			schedule.append(Integer.toString(a.getStartMonth()) + ", ");	// Start month
    			schedule.append(Integer.toString(a.getStartDay()) + ", ");		// Start day 
    			schedule.append(Integer.toString(a.getEndYear()) + ", ");		// End year
    			schedule.append(Integer.toString(a.getEndMonth()) + ", ");		// End month
    			schedule.append(Integer.toString(a.getEndDay()) + ", ");		// End day 	
    			schedule.append(Integer.toString(a.getStartTime()) + ", ");		// Start Time
    			schedule.append(Integer.toString(a.getEndTime()) + ", ");		// End Time
    			schedule.append(Integer.toString(a.getAID()) + "\n");
    		}
    		schedule.close();
    		return "SUCCESS";
    	}
    	return "FAILED";
    }
    
    public String importSchedule() throws IOException, SQLException
    {
    	BufferedReader schedule = new BufferedReader(new FileReader(IMPORT_FILE));
    	String line = "", name = "";
    	int userID, startYear, startMonth, startDay, endYear, endMonth, endDay, startTime, endTime, aID;

    	line = schedule.readLine();				// Get user info from 1st line
    	String[] userVals = line.split(", ");	// Split up data
    	name = userVals[0];
    	String email = userVals[1];
    	int id = Integer.valueOf(userVals[2]);
    	int year = Integer.valueOf(userVals[3]);
    	int month = Integer.valueOf(userVals[4]);
    	int day = Integer.valueOf(userVals[5]);
    	User newUser = new User(name, email, id, year, month, day);
    	boolean merge = false;
    	for(User u : allUsersList)
    	{
    		if(newUser.equals(u))
    		{
    			// Assign u's userID & each imported aID into userAppointmentsDB
    			merge = true;
    		}
    		
    	}
    	
    	ArrayList<ArrayList<String>> resultsList = controller.getUserAppointmentsDB().getAllRecords();
    	while((line = schedule.readLine()) != null)
    	{
    		String[] values = line.split(", ");
    		
    		userID = Integer.valueOf(values[0]);
    		name = values[1];
    		startYear = Integer.valueOf(values[2]);
    		startMonth = Integer.valueOf(values[3]);
    		startDay = Integer.valueOf(values[4]);
    		endYear = Integer.valueOf(values[5]);
    		endMonth = Integer.valueOf(values[6]);
    		endDay = Integer.valueOf(values[7]);
    		startTime = Integer.valueOf(values[8]);
    		endTime = Integer.valueOf(values[9]);
    		aID = Integer.valueOf(values[10]);
    		
    		boolean exists = false;	// Check if appointment is already in db
    		Appointment newAppt = new Appointment(userID, name, startYear, startMonth, startDay, endYear, endMonth, endDay, startTime, endTime, aID);
  
    		
    		for(Appointment a : allAppointmentsList)
    		{
    			if(a.equals(newAppt))
    			{
    				exists = true;
    				String[] cuaVals = {Integer.toString(currentUser.getId()), Integer.toString(a.getAID())};	// current user appointment vals
    				String[] uaVals = {values[0], Integer.toString(a.getAID())};	// user appointment vals
    				// ArrayList of current userID & checked aID
    				ArrayList<String> currentUAs = new ArrayList<String>(Arrays.asList(Integer.toString(currentUser.getId()), Integer.toString(a.getAID())));
    				if(!resultsList.contains(currentUAs))
    				{
    					controller.getUserAppointmentsDB().insertAppointment(USER_APPOINTMENTS_FIELD_NAMES, cuaVals);
    				}
    				
    				ArrayList<String> uAList = new ArrayList<String>(Arrays.asList(Integer.toString(userID), Integer.toString(a.getAID())));
    				if(merge && (!resultsList.contains(uAList)))	// If user is in db:
    					controller.getUserAppointmentsDB().insertAppointment(USER_APPOINTMENTS_FIELD_NAMES, uaVals);	// Add imported userID & aID to userAppointments
    				
    			}
    		}
    		
    		if(!exists)
    		{
    			int addAppt = controller.getAppointmentsDB().insertAppointment(APPOINTMENTS_FIELD_NAMES, values);	
    			String[] cuaVals = {Integer.toString(currentUser.getId()), Integer.toString(addAppt)};
				String[] uaVals = {values[0], Integer.toString(addAppt)};
				controller.getUserAppointmentsDB().insertAppointment(USER_APPOINTMENTS_FIELD_NAMES, cuaVals);
				if(merge)	// If user is in db:
					controller.getUserAppointmentsDB().insertAppointment(USER_APPOINTMENTS_FIELD_NAMES, uaVals);	// Add imported userID & aID to userAppointments
    			allAppointmentsList.add(new Appointment(currentUser.getDay(), name, startYear, startMonth, startDay, endYear, endMonth, endDay, startTime, endTime, addAppt));
    		}	
    	}
    	schedule.close();
    	return "SUCCESS";
    }
    
    
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
    		
    		
//    		try {
//				System.out.println(controller.importSchedule(DATA_FILE));
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
    		
    		return "SUCCESS";
    	}
    	catch(SQLException e)
    	{
    		e.printStackTrace();
    		return "Account Not Created";
    	}
    	
    }
    
    
    /**
     * Sign in method for Sign in scene
     * @param email
     * @param password
     * @return
     * @throws SQLException 
     */
    public String signInUser(String email, String password) throws SQLException 
    {
    	ArrayList<ArrayList<String>> usersList = controller.getUsersDB().getAllRecords();	// Get all data from users table
		for (ArrayList<String> values : usersList)	// Iterate through every user
		{
			String userEmail = values.get(2);
			userEmail = "'" + userEmail + "'";	// match email format from table
			if (userEmail.equalsIgnoreCase(email))	// Compare user email to given email
			{
				try 
				{
					// Get iterated user's password
					ArrayList<ArrayList<String>> resultsList = controller.getUsersDB().getRecord(String.valueOf(values.get(0)));
					String storedPassword = resultsList.get(0).get(3);
					if (password.equals(storedPassword))	// Check for match to sign in
					{
						this.currentUser = controller.allUsersList.get(Integer.valueOf(values.get(0)) - 1);		// Get index by calculating userID-1
						System.out.println(currentUser.getName() + " is signed in.  Appointments:");
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
	    		LocalDate newAppSDate = LocalDate.of(startYear, startMonth, startDay);
	    		LocalDate newAppEDate = LocalDate.of(endYear, endMonth, endDay);
	    		String start = String.format("%04d", startTime);

	    		int startHour = Integer.valueOf(start.substring(0, 2));
	    		int startMin = Integer.valueOf(start.substring(2, start.length()));
	    		
	    		String end = String.format("%04d", endTime);
	    		System.out.println(end);
	    		int endHour = Integer.valueOf(end.substring(0, 2));
	    		int endMin = Integer.valueOf(end.substring(2, end.length()));
	    		System.out.println(endHour);
	    		System.out.println(endMin);
	    		
	    		
	    		LocalTime newAppStart = LocalTime.of(startHour, startMin);
	    		LocalTime newAppEnd = LocalTime.of(endHour, endMin);
	    		LocalDateTime newAppSDateTime = LocalDateTime.of(newAppSDate, newAppStart);
	    		LocalDateTime newAppEDateTime = LocalDateTime.of(newAppEDate, newAppEnd);
	    		
	    		ObservableList<Appointment> currentApps = controller.getAppointmentsForCurrentUser();
	    		for(Appointment a : currentApps)
	    		{
	    			int sYear = a.getStartYear();
	    			int sMonth = a.getStartMonth();
	    			int sDay = a.getStartDay();
	    			int eYear = a.getEndYear();
	    			int eMonth = a.getEndMonth();
	    			int eDay = a.getEndDay();
	    			
	    			LocalDate aStartDate = LocalDate.of(sYear, sMonth, sDay);
	    			LocalDate aEndDate = LocalDate.of(eYear, eMonth, eDay);	  
	    			
	    			start = String.format("%04d", a.getStartTime());

	    			startHour = Integer.valueOf(start.substring(0, 2));
		    		startMin = Integer.valueOf(start.substring(2, start.length()));
		    		
		    		end = String.format("%04d", a.getEndTime());
		    		endHour = Integer.valueOf(end.substring(0, 2));
		    		endMin = Integer.valueOf(end.substring(2, end.length()));
		    		
		    		LocalTime aStart = LocalTime.of(startHour, startMin);
		    		LocalTime aEnd = LocalTime.of(endHour, endMin);
		    		
		    		LocalDateTime aSDateTime = LocalDateTime.of(aStartDate, aStart);
		    		LocalDateTime aEDateTime = LocalDateTime.of(aEndDate, aEnd);
		    		
		    		if((newAppSDateTime.isAfter(aSDateTime) && newAppSDateTime.isBefore(aEDateTime))
		    				|| (newAppEDateTime.isAfter(aSDateTime)))
		    			return "Time Conflict";
		    		
	    		}
	    		
	    		String[] uaValues = {Integer.toString(id), Integer.toString(aID)};
	    		controller.getUserAppointmentsDB().createRecord(USER_APPOINTMENTS_FIELD_NAMES, uaValues);
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
    			String currentEmail = "'" + currentUser.getEmail() + "'";
    			controller.getUsersDB().changeName(currentEmail, newName);
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
    
    public String deleteAppointment(int id)
    {
    	try
    	{
    		controller.getAppointmentsDB().deleteAppointment(Integer.toString(id));
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
		return appointmentsDB;
	}
	
	public DBModel getUserAppointmentsDB()
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
                ArrayList<ArrayList<String>> resultsList = controller.appointmentsDB.getAppointments(String.valueOf(currentUser.getId()));	// Gets current user's appointments
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
		this.appointmentsDB = appointmentsDB;
	}
	
	public void setUserAppointmentsDB(DBModel userAppointmentsDB)
	{
		this.userAppointmentsDB = userAppointmentsDB;
	}
	
	public void sendEmail(String email, String text)
	{
		String host = "smtp.gmail.com";
		String to = email;
		String from = "cecs343project@gmail.com";
		String password = "343projectpassword";
		String port = "465";
		Properties properties = System.getProperties();
		properties.put("mail.smtp.host", host);
		properties.put("mail.smtp.auth", "true");  
		properties.put("mail.smtp.port", port);  
		properties.put("mail.debug", "true");  
		properties.put("mail.smtp.socketFactory.port", "465");  
		properties.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");  
		properties.put("mail.smtp.socketFactory.fallback", "false");  
		Session session = Session.getDefaultInstance(properties,  
			    new javax.mail.Authenticator() {
			       protected PasswordAuthentication getPasswordAuthentication() {  
			       return new PasswordAuthentication(from,password);  
			   }  
			   });  
		
		try {
	         // Create a default MimeMessage object.
	         MimeMessage message = new MimeMessage(session);

	         // Set From: header field of the header.
	         message.setFrom(new InternetAddress(from));

	         // Set To: header field of the header.
	         message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

	         // Set Subject: header field
	         message.setSubject("Test Email");

	         // Now set the actual message
	         message.setText(text);

	         // Send message
	         Transport.send(message);
	         System.out.println("Sent message successfully....");
	      } catch (MessagingException mex) {
	         mex.printStackTrace();
	      }
	}
}
    
