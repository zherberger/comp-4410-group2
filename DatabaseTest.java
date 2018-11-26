import java.sql.*;

public class DatabaseTest
{
	static final String URL = "jdbc:mysql://localhost:3306/sys?autoReconnect=true&useSSL=false";
	
	public static void main(String[] args)
	{
		Connection connection;
		Statement statement;
		ResultSet resultSet;
		ResultSetMetaData metaData;
		int numberOfColumns;
	
		try
		{
			Class.forName("com.mysql.cj.jdbc.Driver");
			connection = DriverManager.getConnection(URL, "root", "Boggob21@");
			statement = connection.createStatement();
			
			//query database
			resultSet = statement.executeQuery("SELECT * FROM Professor");
			
			//process query results
			metaData = resultSet.getMetaData();
			numberOfColumns = metaData.getColumnCount();
			
			System.out.println("All Professors:\n");
			
			for(int i = 1; i <= numberOfColumns; i++)
				System.out.printf("%-16s\t", metaData.getColumnName(i));
			
			System.out.println();
			
			while(resultSet.next())
			{
				for(int i = 1; i <= numberOfColumns; i++)
					System.out.printf("%-16s\t", resultSet.getObject(i));
				
				System.out.println();
			}
			
			resultSet.close();
			statement.close();
			connection.close();
		}
		
		catch(ClassNotFoundException e)
		{
			System.out.println("Unable to initialize JDBC driver. Exiting...");
			System.exit(0);
		}
		
		catch(SQLException e)
		{
			System.out.println("Encountered SQL Exception.");
			e.printStackTrace();
		}
		
		
	}
}