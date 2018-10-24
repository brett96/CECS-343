package Model;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

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
    	//mStmt.executeUpdate("DROP TABLE IF EXISTS " + mTableName);	//  Deletes and repopulates the database
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
        StringBuilder updateSQL = new StringBuilder("UPDATE users SET appointmentsID = ").append(mStmt.getGeneratedKeys().getInt(1));
        updateSQL.append(" WHERE email = ").append(values[1]);
        mStmt.executeUpdate(updateSQL.toString());
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
    	StringBuilder updateSQL = new StringBuilder("UPDATE users SET password = ").append(pass);
//    	for(int i = 0; i < fields.length; i++)
//    	{
//    		updateSQL.append(fields[i]).append("=").append(convertToSQLText(fields[i], values[i])).append((i < values.length - 1) ? "," : " ");
//    	}
    	updateSQL.append("WHERE email = ").append(email);
    	mStmt.executeUpdate(updateSQL.toString());
    }
    
    public void changeName(String email, String name) throws SQLException
    {
    	StringBuilder updateSQL = new StringBuilder("UPDATE users SET name = ").append(name);
    	updateSQL.append("WHERE email = ").append(email);
    	mStmt.executeUpdate(updateSQL.toString());
    }
    
    public void changeEmail(String email, String newEmail) throws SQLException
    {
    	StringBuilder updateSQL = new StringBuilder("UPDATE users SET email = ").append(newEmail);
    	updateSQL.append("WHERE email = ").append(email);
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
