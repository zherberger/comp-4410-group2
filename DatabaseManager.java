import java.sql.*;

public interface DatabaseManager
{
	public void makeConnection(String login, String pass) throws SQLException;
}