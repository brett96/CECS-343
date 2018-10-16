package Controller;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import Model.DBModel;
import Model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public final class Controller 
{
	private static Controller controller;
	private static final String DB_NAME = "cecs343.db";
	private static final String USER_TABLE_NAME = "users";
    private static final String[] USER_FIELD_NAMES = {"name", "email", "password", "appointmentsID"};
    private static final String[] USER_FIELD_TYPES = {"TEXT", "TEXT PRIMARY KEY", "TEXT", "INTEGER"};
    
    private static final String APPOINTMENTS_TABLE_NAME = "appointments";
    private static final String[] APPOINTMENTS_FIELD_NAMES = {"appointmentsID", "appointmentName", "appointmentDate",
    		"appointmentStart", "appointmentEnd"};
    private static final String[] APPOINTMENTS_FIELD_TYPES = {"INTEGER", "TEXT", "TEXT", "TEXT", "TEXT"};
    
    private ObservableList<User> allUsersList;
    private User currentUser;
    private DBModel DB;
    private DBModel usersDB;
    private DBModel userAppointmentsDB;
    
    private Controller() {}
    
    public static Controller getInstance()
    {
    	if(controller == null)
    	{
    		controller = new Controller();
    		controller.allUsersList = FXCollections.observableArrayList();
    		
    		try
            {
            	controller.setUsersDB(new DBModel(DB_NAME, USER_TABLE_NAME, USER_FIELD_NAMES, USER_FIELD_TYPES));
            	ArrayList<ArrayList<String>> resultsList = controller.getUsersDB().getAllRecords();
            	for(ArrayList<String> values : resultsList)
            	{
            		String name = values.get(0);
            		String email = values.get(1);
            		int appointmentsID = Integer.parseInt(values.get(2));
            		controller.allUsersList.add(new User(name, email, appointmentsID));
            	}
            	
            	controller.DB = new DBModel(DB_NAME, USER_TABLE_NAME, USER_FIELD_NAMES, USER_FIELD_TYPES);
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
			if (u.getEmail().equalsIgnoreCase(email))
			{
				try 
				{
					ArrayList<ArrayList<String>> resultsList = controller.getUsersDB().getRecord(String.valueOf(u.getId()));
					String storedPassword = resultsList.get(0).get(2);
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
			if (u.getEmail().equalsIgnoreCase(email))
			{
				try 
				{
					ArrayList<ArrayList<String>> resultsList = controller.getUsersDB().getRecord(String.valueOf(u.getId()));
					String storedPassword = resultsList.get(0).get(2);
					if (password.equals(storedPassword))
					{
						this.currentUser = u;
						return "SUCCESS";
					}
						
						
				} 
				catch (Exception e) {return "Error";}
				return "Incorrect password.  Please try again.";		
			}		
		return "Email address not found.  Please try again.";
	}
    
    /**
     * Signs up a user and adds user data to appropriate tables
     * @param name
     * @param email
     * @param password
     * @return
     */
    public String signUpUser(String name, String email, String password)
    {
    	for(User user : controller.allUsersList)
    		if(user.getEmail().equalsIgnoreCase(email))
    			return "Email already exists";
    	// Add user to database
    	String[] values = {name, email, password};
    	try
    	{
    		int id = controller.getUsersDB().createRecord(Arrays.copyOfRange
    				(USER_FIELD_NAMES, 1, USER_FIELD_NAMES.length), values);
    		User newUser = new User(name, email, id);
    		controller.allUsersList.add(newUser);
    		controller.currentUser = newUser;
    		return "SUCCESS";
    	}
    	catch(SQLException e)
    	{
    		return "Account not created.  Please try again.";
    	}
    }
    
    /**
     * Returns ObservableList of all user data
     * @return
     */
    public ObservableList<User> getAllUsers()
    {
    	return controller.allUsersList;
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
	 * Sets users database table
	 * @param usersDB
	 */
	public void setUsersDB(DBModel usersDB) 
	{
		this.usersDB = usersDB;
	}
}
    
