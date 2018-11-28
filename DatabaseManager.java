import java.sql.SQLException;
import java.util.Vector;

public interface DatabaseManager
{
	public void login(String username, String pass) throws SQLException;
	public void addMovie(String title, String releaseDate, String director, String[] cast, String genre, int numCopies, int sequelID) throws SQLException;
	public void addGame(String title, String releaseDate, String platform, int version, String genre, int numCopies) throws SQLException;
	public void addMember(String email, String name, String password, long phone, String street, String city, String state, int zip, String plan) throws SQLException;
	public void updateMember(String email, String name, String password, long phone, String street, String city, String state, int zip, String plan) throws SQLException;
	public Vector<String> getMovies();
	public String[] getPlans();
}