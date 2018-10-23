import java.sql.*;

public interface DatabaseManager
{
	public void makeConnection(String URL, String login, String pass) throws SQLException;
}