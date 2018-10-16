package Controller;

import java.sql.SQLException;
import java.util.ArrayList;

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
            	
            }
    		catch (SQLException e)
            {
                e.printStackTrace();
            }
    	}
    	
    	return controller;
    }
    	
    	/**
         * Returns users database table
         * @return
         */
    	public DBModel getUsersDB() {
    		return usersDB;
    	}

    	/**
    	 * Sets users database table
    	 * @param usersDB
    	 */
    	public void setUsersDB(DBModel usersDB) {
    		this.usersDB = usersDB;
    	}
}
    
