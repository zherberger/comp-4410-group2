import java.sql.*;

public interface DatabaseManager
{
	public void login(String username, String pass) throws SQLException;
	public void addMovie(String title, String releaseDate, String director, String[] cast, String genre, int numCopies) throws SQLException;
	public void addGame(String title, String releaseDate, String platform, String version, String genre, int numCopies) throws SQLException;
}