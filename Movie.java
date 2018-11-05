import java.text.SimpleDateFormat;
import java.util.Date;

public class Movie
{
	private String title;
	private Date releaseDate;
	private String director;
	private String genre;
	private int numCopies;
	public String[] cast;
	final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
	public static final String[] GENRES = {"", "Action", "Adventure", "Animation", "Biopic",
										  "Childrens", "Comedy", "Crime", "Documentary",
										  "Drama", "Experimental", "Family", "Fantasy",
										  "Historical", "Horror", "Musical", "Romance",
										  "Sports", "Science Fiction", "Thriller", "War",
										  "Western", "Other"};
	
	Movie(String title, Date releaseDate, String director, String[] cast, String genre, int numCopies)
	{
		this.title = title;
		this.releaseDate = releaseDate;
		this.director = director;
		this.cast = cast;
		this.genre = genre;
		this.numCopies = numCopies;
	}
	
	String[] getFields()
	{
		String[] fields = new String[5];
		String castString = "";
		
		fields[0] = title;
		fields[1] = sdf.format(releaseDate);
		fields[2] = director;
		fields[3] = genre;
		fields[4] = Integer.toString(numCopies);
		
		return fields;
	}
}