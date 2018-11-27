package Model;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

//import Controller.Controller;

public class DBModel 
{
	private String mDBName;
    private String mTableName;
    private String[] mFieldNames;
    private String[] mFieldTypes;
    private Connection mConnection;
    private Statement mStmt;
    
    public DBModel(String dbName, String tableName, String[] fieldNames, String[] fieldTypes) throws SQLException
    {
        super();
        mDBName = dbName;
        mTableName = tableName;
        mFieldNames = fieldNames;
        mFieldTypes = fieldTypes;
        if (mFieldNames == null || mFieldTypes == null || mFieldNames.length == 0
                || mFieldNames.length != mFieldTypes.length)
            throw new SQLException("Database field names and types must exist and have the same number of elements.");
        mConnection = connectToDB();
        mStmt = mConnection.createStatement();
        mStmt.setQueryTimeout(30);
        createTable();
    }
    
    private void createTable() throws SQLException {
//    	mStmt.executeUpdate("DROP TABLE IF EXISTS users");	//  Deletes and repopulates the database
//    	mStmt.executeUpdate("DROP TABLE IF EXISTS appointments");
        StringBuilder createSQL = new StringBuilder("CREATE TABLE IF NOT EXISTS ");
        createSQL.append(mTableName).append("(");
        for (int i = 0; i < mFieldNames.length; i++)
            createSQL.append(mFieldNames[i]).append(" ").append(mFieldTypes[i])
                    .append((i < mFieldNames.length - 1) ? "," : ")");
        mStmt.executeUpdate(createSQL.toString());
    }
    
    /**
     * Returns record with given id
     * @param key
     * @return
     * @throws SQLException
     */
    public ArrayList<ArrayList<String>> getRecord(String key) throws SQLException
    {
    	try(Connection connection = connectToDB();
    			Statement stmt = connection.createStatement();
    			ResultSet rs = stmt.executeQuery("SELECT * FROM " + mTableName + " WHERE " + mFieldNames[0] + " = " + key))
    	{
    		ArrayList<ArrayList<String>> resultsList = new ArrayList<>();
			while(rs.next())
			{
				// Loop through each of the fields
				// Add each value to the ArrayList
				ArrayList<String> values = new ArrayList<>(mFieldNames.length);
				for(String fieldName : mFieldNames)
				{
					values.add(rs.getString(fieldName));
				}
				// Add values to 2D ArrayList
				resultsList.add(values);
			}
			return resultsList;
    	}
    }
    
    /**
     * select * from appointments INNER JOIN userAppointments ON appointments.aID = userAppointments.AID WHERE userAppointments.uID = key
     */
    public ArrayList<ArrayList<String>> getAppointments(String key) throws SQLException
    {
    	try(Connection connection = connectToDB();
    			Statement stmt = connection.createStatement();
    			ResultSet rs = stmt.executeQuery("SELECT * FROM appointments INNER JOIN userAppointments ON appointments.aID = userAppointments.aID "
    					+ "WHERE userAppointments.userID" + " = " + key))
    	{
    		ArrayList<ArrayList<String>> resultsList = new ArrayList<>();
			while(rs.next())
			{
				// Loop through each of the fields
				// Add each value to the ArrayList
				ArrayList<String> values = new ArrayList<>(mFieldNames.length);
				for(String fieldName : mFieldNames)
				{
					values.add(rs.getString(fieldName));
				}
				// Add values to 2D ArrayList
				resultsList.add(values);
			}
			return resultsList;
    	}
    }
    
    /**
     * Returns all records
     * @return
     * @throws SQLException
     */
    public ArrayList<ArrayList<String>> getAllRecords() throws SQLException {
		try(Connection connection = connectToDB();
				Statement stmt = connection.createStatement();
				ResultSet rs = stmt.executeQuery("SELECT * FROM " + mTableName);)
		{
			ArrayList<ArrayList<String>> resultsList = new ArrayList<>();
			while(rs.next())
			{
				// Loop through each of the fields
				// Add each value to the ArrayList
				ArrayList<String> values = new ArrayList<>(mFieldNames.length);
				for(String fieldName : mFieldNames)
				{
					values.add(rs.getString(fieldName));
				}
				// Add values to 2D ArrayList
				resultsList.add(values);
			}
			return resultsList;
		}
	}
    
    /**
     * Returns number of records
     * @return
     * @throws SQLException
     */
    public int getRecordCount() throws SQLException 
    {
		return getAllRecords().size();
	}
    
    public int insertAppointment(String[] fields, String[] values) throws SQLException
    {
    	if(fields == null || values == null || fields.length == 0 || fields.length != values.length)
            return -1;

        StringBuilder insertSQL = new StringBuilder("INSERT INTO ");
        insertSQL.append(mTableName).append("(");
        for(int i = 0; i < fields.length; i++)
            insertSQL.append(fields[i]).append((i < fields.length - 1) ? "," : ") VALUES(");
        for(int i = 0; i < values.length; i++)
            insertSQL.append(convertToSQLText(fields[i], values[i])).append((i < values.length - 1) ? "," : ")");

        mStmt.executeUpdate(insertSQL.toString());
        StringBuilder updateSQL = new StringBuilder("UPDATE appointments SET aID = ").append(mStmt.getGeneratedKeys().getInt(1));
        updateSQL.append(" WHERE aID is null");//.append(values[0]);
        mStmt.executeUpdate(updateSQL.toString());
        System.out.println("done");
        return mStmt.getGeneratedKeys().getInt(1);
    }
    
    public void insertAppointment(String[] fields, String[] values, int key) throws SQLException
    {
    	if(!(fields == null || values == null || fields.length == 0 || fields.length != values.length))
    	{
	        StringBuilder insertSQL = new StringBuilder("INSERT INTO ");
	        insertSQL.append(mTableName).append("(");
	        for(int i = 0; i < fields.length; i++)
	            insertSQL.append(fields[i]).append((i < fields.length - 1) ? "," : ") VALUES(");
	        for(int i = 0; i < values.length; i++)
	            insertSQL.append(convertToSQLText(fields[i], values[i])).append((i < values.length - 1) ? "," : ")");
	
	        mStmt.executeUpdate(insertSQL.toString());
	        StringBuilder updateSQL = new StringBuilder("UPDATE appointments SET aID = ").append(key);
	        updateSQL.append(" WHERE aID is null");//.append(values[0]);
	        mStmt.executeUpdate(updateSQL.toString());
	        System.out.println("done");
    	}
    }
    
    public int createRecord(String[] fields, String[] values) throws SQLException
    {
        if(fields == null || values == null || fields.length == 0 || fields.length != values.length)
            return -1;

        StringBuilder insertSQL = new StringBuilder("INSERT INTO ");
        insertSQL.append(mTableName).append("(");
        for(int i = 0; i < fields.length; i++)
            insertSQL.append(fields[i]).append((i < fields.length - 1) ? "," : ") VALUES(");
        for(int i = 0; i < values.length; i++)
            insertSQL.append(convertToSQLText(fields[i], values[i])).append((i < values.length - 1) ? "," : ")");

        mStmt.executeUpdate(insertSQL.toString());
      StringBuilder updateSQL = new StringBuilder("UPDATE users SET userID = ").append(mStmt.getGeneratedKeys().getInt(1));
      updateSQL.append(" WHERE email = ").append(values[1]);
      mStmt.executeUpdate(updateSQL.toString());
      System.out.println(mStmt.getGeneratedKeys().getInt(1));
      // Minor change in createRecord to return the newly generated primary key (as an int)
        return mStmt.getGeneratedKeys().getInt(1);
    }
    
    /**
     * Updates record
     * @param key
     * @param fields
     * @param values
     * @return
     * @throws SQLException
     */
    public boolean updateRecord(String key, String[] fields, String[] values) throws SQLException
    {
        if(fields == null || values == null || fields.length == 0 || values.length == 0 || fields.length != values.length)
            return false;

        StringBuilder updateSQL = new StringBuilder("UPDATE ");
        updateSQL.append(mTableName).append(" SET ");
        for(int i = 0; i < fields.length; i++)
            updateSQL.append(fields[i]).append("=").append(convertToSQLText(fields[i], values[i])).append((i < values.length - 1) ? "," : " ");

        updateSQL.append("WHERE ").append(mFieldNames[0]).append("=").append(key);
        mStmt.executeUpdate(updateSQL.toString());

        return true;
    }

    public void changePassword(String email, String pass) throws SQLException
    {
    	StringBuilder updateSQL = new StringBuilder("UPDATE users SET password = ").append("'" + pass + "'");
//    	for(int i = 0; i < fields.length; i++)
//    	{
//    		updateSQL.append(fields[i]).append("=").append(convertToSQLText(fields[i], values[i])).append((i < values.length - 1) ? "," : " ");
//    	}
    	updateSQL.append(" WHERE email = ").append(email);
    	//System.out.println(updateSQL);
    	mStmt.executeUpdate(updateSQL.toString());
    }
    
    public void changeName(String email, String name) throws SQLException
    {
    	StringBuilder updateSQL = new StringBuilder("UPDATE users SET name = ").append("'" + name + "'");
    	updateSQL.append(" WHERE email = ").append(email);
    	//System.out.println(updateSQL);
    	mStmt.executeUpdate(updateSQL.toString());
    }
    
    /**
     * Changes appointment name 
     * @param id
     * @param currentName
     * @param newName
     * @param startTime
     * @param endTime
     */
    public void changeAppointmentName(int id, String newName) throws SQLException
    {
    	StringBuilder updateSQL = new StringBuilder("UPDATE appointments SET appointmentName = ").append("'" + newName + "'");
    	updateSQL.append(" WHERE aID = ").append(id);
    	mStmt.executeUpdate(updateSQL.toString());
    }
    
    public void changeAppointmentStartDate(int id, int year, int month, int day) throws SQLException
    {
    	StringBuilder updateSQL = new StringBuilder("UPDATE appointments SET startYear = ").append(year);
    	updateSQL.append(", startMonth = ").append(month).append(", startDay = ").append(day);
    	updateSQL.append(" WHERE aID = ").append(id);
    	mStmt.executeUpdate(updateSQL.toString());
    }
    
    public void changeAppointmentEndDate(int id, int year, int month, int day) throws SQLException
    {
    	StringBuilder updateSQL = new StringBuilder("UPDATE appointments SET endYear = ").append(year);
    	updateSQL.append(", endMonth = ").append(month).append(", endDay = ").append(day);
    	updateSQL.append(" WHERE aID = ").append(id);
    	mStmt.executeUpdate(updateSQL.toString());
    }
    
    public void changeAppointmentStartTime(int id, int start) throws SQLException
    {
    	StringBuilder updateSQL = new StringBuilder("UPDATE appointments SET startTime = ").append(start);
    	updateSQL.append(" WHERE aID = ").append(id);
    	mStmt.executeUpdate(updateSQL.toString());
    }
    
    public void changeAppointmentEndTime(int id, int start) throws SQLException
    {
    	StringBuilder updateSQL = new StringBuilder("UPDATE appointments SET startTime = ").append(start);
    	updateSQL.append(" WHERE aID = ").append(id);
    	mStmt.executeUpdate(updateSQL.toString());
    }
    
    public void changeEmail(String email, String newEmail) throws SQLException
    {
    	StringBuilder updateSQL = new StringBuilder("UPDATE users SET email = ").append(newEmail);
    	updateSQL.append(" WHERE email = ").append(email);
    	mStmt.executeUpdate(updateSQL.toString());
    }
    
    public void changeBirthday(String email, int year, int month, int day) throws SQLException
    {
    	StringBuilder updateSQL = new StringBuilder("UPDATE users SET birthYear = ").append(year);
    	updateSQL.append(", birthMonth = ").append(month).append(", birthDay = ").append(day);
    	updateSQL.append(" WHERE email = ").append(email);
    	mStmt.executeUpdate(updateSQL.toString());
    }
    
    /**
     * Deletes all records
     * @throws SQLException
     */
    public void deleteAllRecords() throws SQLException {
        String deleteSQL = "DELETE FROM " + mTableName;
        mStmt.executeUpdate(deleteSQL);
    }

    /**
     * Deletes record with given id
     * @param key
     * @throws SQLException
     */
    public void deleteRecord(String key) throws SQLException {
        String deleteRecord = "DELETE FROM " + mTableName + " WHERE " + mFieldNames[0] + "=" + key;
        mStmt.executeUpdate(deleteRecord);
    }
    
    public void deleteAppointment(String key) throws SQLException
    {
    	String delete = "DELETE FROM appointments WHERE aID = " + key;
    	mStmt.executeUpdate(delete);
    	delete = "DELETE FROM userAppointments WHERE aID = " + key;
    	mStmt.executeUpdate(delete);
    }
    
    private String convertToSQLText(String field, String value) {
        for (int i = 0; i < mFieldNames.length; i++) {
            if (field.equalsIgnoreCase(mFieldNames[i])) {
                if (mFieldTypes[i].equals("TEXT"))
                    return "'" + value + "'";
                else
                    break;
            }
        }
        return value;
    }

    /**
     * Connects to database
     * @return
     * @throws SQLException
     */
    private Connection connectToDB() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        Connection connection = DriverManager.getConnection("jdbc:sqlite:" + mDBName);
        return connection;
    }

    /**
     * Closes database connection
     * @throws SQLException
     */
    public void close() throws SQLException {
        mConnection.close();
    }

}
