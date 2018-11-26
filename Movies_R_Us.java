import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Vector;
import java.util.Date;
import java.text.*;

public class Movies_R_Us
{
	public static void main(String[] args)
	{
		new Movies_R_Us_Frame();
	}
}

class Movies_R_Us_Frame extends JFrame implements ActionListener, ListSelectionListener, DatabaseManager
{
	JTextField searchField;
	JButton searchButton;
	JButton addMovieButton;
	JButton addGameButton;
	JButton recentRentalsButton;
	JButton popularTitlesButton;
	JButton rentButton;
	JButton viewSequelsButton;
	JButton rentalHistoryButton;
	JTable resultTable;
	JRadioButton moviesButton;
	JRadioButton gamesButton;
	ButtonGroup titleTypes;
	
	JPanel mainPanel;
	JTextField actorsField;
	JTextField directorsField;
	JComboBox<String> movieGenreBox;
	JComboBox<String> platformBox;
	JComboBox<String> gameGenreBox;
	JCheckBox awardWinningBox;
	JCheckBox newMoviesBox;
	
	Connection connection;
	Statement statement;
	ResultSet resultSet;
	ResultSetMetaData metaData;
	int numberOfColumns;
	final String URL = "jdbc:mysql://falcon-cs.fairmontstate.edu:3306/DB12?useLegacyDatetimeCode=false&serverTimezone=America/New_York&autoReconnect=true&useSSL=false";
	final String login = "dbgroup2";
	final String pass = "dbfall2018";
	final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
	
	String email;
	boolean isAdmin;
	
	Movies_R_Us_Frame()
	{
		JPanel topPanel;
		JPanel searchPanel;
		JPanel buttonPanel;
		JScrollPane outputScrollPane;
		
		topPanel = new JPanel(new BorderLayout());
		
		searchPanel = new JPanel();
		searchField = new JTextField(60);
		searchButton = newButton("Search", "SEARCH", this, true);
		getRootPane().setDefaultButton(searchButton);
		searchPanel.add(searchField);
		searchPanel.add(searchButton);
		topPanel.add(searchPanel, BorderLayout.NORTH);
		
		setupSearchOptions();
		topPanel.add(mainPanel, BorderLayout.CENTER);
		
		buttonPanel = new JPanel();
		addMovieButton = newButton("Add Movie", "ADD_MOVIE", this, false);
		addGameButton = newButton("Add Game", "ADD_GAME", this, false);
		recentRentalsButton = newButton("View Recent Rentals", "RECENT_RENTALS", this, false);
		popularTitlesButton = newButton("View Popular Titles", "POPULAR_TITLES", this, false);
		rentButton = newButton("Rent Title", "RENT", this, false);
		viewSequelsButton = newButton("View Sequels", "VIEW_SEQUELS", this, false);
		rentalHistoryButton = newButton("View Rental History", "RENTAL_HISTORY", this, false);
		buttonPanel.add(addMovieButton);
		buttonPanel.add(addGameButton);
		buttonPanel.add(newButton("Add Member", "ADD_MEMBER", this, true));
		buttonPanel.add(recentRentalsButton);
		buttonPanel.add(popularTitlesButton);
		buttonPanel.add(rentButton);
		buttonPanel.add(viewSequelsButton);
		buttonPanel.add(rentalHistoryButton);
		add(buttonPanel, BorderLayout.SOUTH);
		
		resultTable = new JTable();
		resultTable.getSelectionModel().addListSelectionListener(this);
		outputScrollPane = new JScrollPane(resultTable);
		add(outputScrollPane, BorderLayout.CENTER);
		add(topPanel, BorderLayout.NORTH);
		
		setupMainFrame();
		makeConnection();
		new LoginDialog(this);
	}
	
	JButton newButton(String label, String actionCommand, ActionListener buttonListener, boolean startsEnabled)
	{
		JButton b;
		
		b = new JButton(label);
		b.setActionCommand(actionCommand);
		b.addActionListener(buttonListener);
		b.setEnabled(startsEnabled);
		
		return b;
	}
	
	void setupSearchOptions()
	{
		JPanel moviesPanel;
		JPanel gamesPanel;
		GroupLayout moviesLayout;
		GroupLayout gamesLayout;
		JLabel gapLabel = new JLabel("");
		
		JLabel actorsLabel = new JLabel("Actors:");
		JLabel directorsLabel = new JLabel("Director:");
		JLabel movieGenreLabel = new JLabel("Genre:");
		actorsField = new JTextField();
		directorsField = new JTextField();
		movieGenreBox = new JComboBox<String>(Movie.GENRES);
		awardWinningBox = new JCheckBox("Show only award-winning movies");
		newMoviesBox = new JCheckBox("Show only new movies for me");
		
		moviesPanel = new JPanel();
		moviesLayout = new GroupLayout(moviesPanel);
		moviesPanel.setLayout(moviesLayout);
		moviesLayout.setAutoCreateGaps(true);
		moviesLayout.setAutoCreateContainerGaps(true);
		
		moviesButton = new JRadioButton("Search movies");
		moviesButton.setSelected(true);
		gamesButton = new JRadioButton("Search games");
		titleTypes = new ButtonGroup();
		titleTypes.add(moviesButton);
		titleTypes.add(gamesButton);
		
		moviesLayout.setHorizontalGroup(
			moviesLayout.createSequentialGroup()
				.addGroup(moviesLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
					.addComponent(gapLabel)
					.addComponent(actorsLabel)
					.addComponent(directorsLabel)
					.addComponent(movieGenreLabel))
				.addGroup(moviesLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
					.addComponent(moviesButton)
					.addComponent(actorsField)
					.addComponent(directorsField)
					.addComponent(movieGenreBox)
					.addComponent(awardWinningBox)
					.addComponent(newMoviesBox))
		);
		
		moviesLayout.setVerticalGroup(
			moviesLayout.createSequentialGroup()
				.addGroup(moviesLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(gapLabel)
					.addComponent(moviesButton))
				.addGroup(moviesLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(actorsLabel)
					.addComponent(actorsField))
				.addGroup(moviesLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(directorsLabel)
					.addComponent(directorsField))
				.addGroup(moviesLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(movieGenreLabel)
					.addComponent(movieGenreBox))
				.addGroup(moviesLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(awardWinningBox))
				.addGroup(moviesLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(newMoviesBox))
		);
		
		JLabel platformLabel = new JLabel("Platform:");
		JLabel gameGenreLabel = new JLabel("Genre:");
		platformBox = new JComboBox<String>(Game.PLATFORMS);
		gameGenreBox = new JComboBox<String>(Game.GENRES);
		
		gamesPanel = new JPanel();
		gamesLayout = new GroupLayout(gamesPanel);
		gamesPanel.setLayout(gamesLayout);
		gamesLayout.setAutoCreateGaps(true);
		gamesLayout.setAutoCreateContainerGaps(true);
		
		gamesLayout.setHorizontalGroup(
			gamesLayout.createSequentialGroup()
				.addGroup(gamesLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
					.addComponent(gapLabel)
					.addComponent(platformLabel)
					.addComponent(gameGenreLabel))
				.addGroup(gamesLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
					.addComponent(gamesButton)
					.addComponent(platformBox)
					.addComponent(gameGenreBox))
		);
		
		gamesLayout.setVerticalGroup(
			gamesLayout.createSequentialGroup()
				.addGroup(gamesLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(gapLabel)
					.addComponent(gamesButton))
				.addGroup(gamesLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(platformLabel)
					.addComponent(platformBox))
				.addGroup(gamesLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(gameGenreLabel)
					.addComponent(gameGenreBox))
		);
		
		mainPanel = new JPanel(new GridLayout(1, 2));
		mainPanel.add(moviesPanel);
		mainPanel.add(gamesPanel);
		add(mainPanel, BorderLayout.SOUTH);
	}
	
	void setupMainFrame()
	{
		Toolkit tk;
		Dimension d;
		
		tk = Toolkit.getDefaultToolkit();
		d = tk.getScreenSize();
		
		setSize(d.width / 2, d.height / 2);
		setLocation(d.width / 4, d.height / 4);
		
		setTitle("Movies-R-Us");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	public void makeConnection()
	{
		try
		{
			Class.forName("com.mysql.cj.jdbc.Driver");
			connection = DriverManager.getConnection(URL, login, pass);
			statement = connection.createStatement();
		}
		
		catch(ClassNotFoundException e)
		{
			System.out.println("Unable to initialize JDBC driver. Ensure your CLASSPATH environment variable includes the Connector/J .jar file.");
			System.exit(0);
		}
		
		catch(SQLException e)
		{
			System.out.println("Error connecting to Movies-R-Us database.");
			e.printStackTrace();
		}
	}
	
//---------------------------------------------------------//
//---------------------ActionListener----------------------//
//---------------------------------------------------------//

	public void actionPerformed(ActionEvent e)
	{
		String command = e.getActionCommand();
		
		if(command.equals("ADD_MOVIE"))
		{
			new MovieDialog(this);
		}
		
		else if(command.equals("ADD_MEMBER"))
		{
			new MemberDialog(this);
		}
		
		else try
		{
			if(command.equals("SEARCH"))
				search();
		
			else if(command.equals("VIEW_SEQUELS"))
				viewSequels();
		
			else if(command.equals("RENTAL_HISTORY"))
				rentalHistory();
		
			else if(command.equals("RENT"))
				rent();
		
			else if(command.equals("RECENT_RENTALS"))
				recentRentals();
		
			else if(command.equals("POPULAR_TITLES"))
				popularTitles();
		}
		
		catch(SQLException s)
		{
			JOptionPane.showMessageDialog(this, s.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			s.printStackTrace();
		}
	}
	
	void search() throws SQLException
	{
		PreparedStatement pStatement;
		
		if(moviesButton.isSelected())
		{
			String awardString = "";
			String notRentedString = "";
			
			if(awardWinningBox.isSelected())
			{
				awardString = "AND T.tid IN "
					  + "(SELECT W.tid "
					  + "FROM Wins W, Titles T "
					  + "WHERE W.tid = T.tid) ";
			}
		
			if(newMoviesBox.isSelected())
			{
				notRentedString = "AND T.tid NOT IN "
						 + "(SELECT R.tid "
						 + "FROM Rentals R, Titles T "
						 + "WHERE R.tid = T.tid AND R.email = '" + email + "') ";
			}
			
			pStatement = connection.prepareStatement("SELECT DISTINCT T.tid AS 'Title ID', T.title AS 'Title', T.release_date AS 'Release Date', T.genre AS 'Genre', S1.name AS 'Director', T.num_copies AS '# Copies' "
					   + "FROM Titles T, Movies M, Directors D, Stars S1, Stars S2, Stars_In SI, Actors A "
					   + "WHERE M.movie_id = T.tid "
					   + "AND M.director_id = D.director_id "
					   + "AND D.director_id = S1.sid "
					   + "AND M.movie_id = SI.movie_id "
					   + "AND SI.actor_id = A.actor_id "
					   + "AND A.actor_id = S2.sid "
					   + "AND T.title LIKE ? "
					   + "AND S1.name LIKE ? "
					   + "AND S2.name LIKE ? "
					   + "AND T.genre LIKE ? "
					   + awardString
					   + notRentedString
					   + "ORDER BY TITLE ");
					   
			pStatement.setString(1, "%" + searchField.getText().trim() + "%");
			pStatement.setString(2, "%" + directorsField.getText().trim() + "%");
			pStatement.setString(3, "%" + actorsField.getText().trim() + "%");
			pStatement.setString(4, "%" + (String) movieGenreBox.getSelectedItem() + "%");
		}
		
		else
		{
			pStatement = connection.prepareStatement("SELECT T.tid AS 'Title ID', T.title AS 'Title', T.release_date AS 'Release Date', T.genre AS 'Genre', G.platform AS 'Platform', T.num_copies AS '# Copies' "
					   + "FROM Games G, Titles T "
					   + "WHERE G.game_id = T.tid "
					   + "AND T.title LIKE ? "
					   + "AND G.platform LIKE ? "
					   + "AND T.genre LIKE ? ");
			
			pStatement.setString(1, "%" + searchField.getText().trim() + "%");
			pStatement.setString(2, "%" + (String) platformBox.getSelectedItem() + "%");
			pStatement.setString(3, "%" + (String) gameGenreBox.getSelectedItem() + "%");
		}
		
		buildTableModel(pStatement);
	}
	
	void viewSequels() throws SQLException
	{
		PreparedStatement pStatement;
		pStatement = connection.prepareStatement("SELECT T2.tid AS 'Title ID', T2.title AS 'Title', T2.release_date AS 'Release Date', T2.genre AS 'Genre' "
										   + "FROM Titles T1, Titles T2, Sequel_To S "
										   + "WHERE T1.tid = S.original_id "
										   + "AND T2.tid = S.sequel_id "
										   + "AND T1.tid = ? ");
		
		pStatement.setInt(1, (Integer) (resultTable.getValueAt(resultTable.getSelectedRow(), 0)));
		buildTableModel(pStatement);
	}
	
	void rentalHistory() throws SQLException
	{
		PreparedStatement pStatement;
		pStatement = connection.prepareStatement("SELECT T.tid AS 'Title ID', T.title AS 'Title', T.release_date AS 'Release Date', T.genre AS 'Genre', R.rent_date AS 'Rental Date', R.return_date AS 'Return Date' "
										   + "FROM Titles T, Rentals R "
										   + "WHERE T.tid = R.tid "
										   + "AND R.email = ? ");
		
		pStatement.setString(1, email);
		buildTableModel(pStatement);
	}
	
	void recentRentals() throws SQLException
	{
		PreparedStatement pStatement;
		pStatement = connection.prepareStatement("SELECT T.tid AS 'Title ID', T.title AS 'Title', M.email AS 'Member e-mail', A.street AS 'Shipping Address', A.city AS 'City', A.state AS 'State', A.zip AS 'Zip' "
										   + "FROM Titles T, Members M, Addresses A, Rentals R "
										   + "WHERE T.tid = R.tid "
										   + "AND R.email = M.email "
										   + "AND M.street = A.street "
										   + "AND M.zip = A.zip "
										   + "AND R.timestamp > ?");
										   
		pStatement.setLong(1, System.currentTimeMillis() - 604800000L); //number of milliseconds in a week
		buildTableModel(pStatement);
	}
	
	void popularTitles() throws SQLException
	{
		PreparedStatement pStatement;
		pStatement = connection.prepareStatement("SELECT T.tid AS 'Title ID', T.title AS 'Title', T.release_date AS 'Release Date', T.genre AS 'Genre', COUNT(R.rid) AS 'Rental Count' "
										   + "FROM Titles T, Rentals R "
										   + "WHERE T.tid = R.tid "
										   + "GROUP BY T.tid "
										   + "ORDER BY 'Rental Count' DESC ");
										   
		buildTableModel(pStatement);
	}
	
	void rent() throws SQLException
	{
		PreparedStatement pStatement;
		int maxRid;
		long now;
		
		resultSet = statement.executeQuery("SELECT MAX(rid) "
									  + "FROM Rentals ");
		resultSet.next();
		
		if(resultSet.getObject(1) != null)
		{
			maxRid = Integer.parseInt(resultSet.getObject(1).toString());
			maxRid++;
		}
		
		else maxRid = 0;
		now = System.currentTimeMillis();
		
		pStatement = connection.prepareStatement("INSERT INTO Rentals "
										   + "VALUES(?,?,?,?,?,?) ");
		pStatement.setInt(1, maxRid);
		pStatement.setInt(2, (Integer) (resultTable.getValueAt(resultTable.getSelectedRow(), 0))); //the title ID
		pStatement.setString(3, email);
		pStatement.setString(4, sdf.format(new Date(now)));
		pStatement.setString(5, sdf.format(new Date(now + 604800000)));
		pStatement.setLong(6, now);
		
		pStatement.executeUpdate();
	}
	
	void buildTableModel(PreparedStatement pStatement) throws SQLException
	{
		resultSet = pStatement.executeQuery();
		metaData = resultSet.getMetaData();
		numberOfColumns = metaData.getColumnCount();
		
		Vector<String> columnNames = new Vector<String>();
		Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		Vector<Object> vector;
		
		for(int i = 1; i <= numberOfColumns; i++)
			columnNames.add(metaData.getColumnLabel(i));
		
		while(resultSet.next())
		{
			vector = new Vector<Object>();
			
			for(int i = 1; i <= numberOfColumns; i++)
				vector.add(resultSet.getObject(i));
			
			data.add(vector);
		}
		
		resultTable.setModel(new DefaultTableModel(data, columnNames));
	}

//---------------------------------------------------------//
//------------------ListSelectionListener------------------//
//---------------------------------------------------------//

	public void valueChanged(ListSelectionEvent e)
	{
		rentButton.setEnabled(resultTable.getSelectedRowCount() == 1);
		viewSequelsButton.setEnabled(resultTable.getSelectedRowCount() == 1);
	}

//---------------------------------------------------------//
//---------------------DatabaseManager---------------------//
//---------------------------------------------------------//
	
	public void login(String username, String pass) throws SQLException
	{
		PreparedStatement pStatement;
		
		pStatement = connection.prepareStatement("SELECT email, login, is_admin "
										   + "FROM Members "
										   + "WHERE email = ? AND login = ?");
										   
		pStatement.setString(1, username);
		pStatement.setString(2, pass);
		resultSet = pStatement.executeQuery();
		
		if(resultSet.next())
		{
			if(username.equals(resultSet.getObject(1)) && pass.equals(resultSet.getObject(2)))
			{
				email = username;
				
				if(resultSet.getObject(3).equals(1))
				{
					addMovieButton.setEnabled(true);
					addGameButton.setEnabled(true);
					recentRentalsButton.setEnabled(true);
					popularTitlesButton.setEnabled(true);
				}
				
				rentalHistoryButton.setEnabled(true);
			}
		}
		
		else
		{
			throw new SQLException("Username or password is incorrect.");
		}
	}
	
	public void addMovie(String title, String releaseDate, String director, String[] cast, String genre, int numCopies) throws SQLException
	{
		PreparedStatement pStatement;
		int maxTid;
		int directorId;
		int actorId;
		
		resultSet = statement.executeQuery("SELECT MAX(tid) FROM Titles");
		resultSet.next();
		
		if(resultSet.getObject(1) != null)
		{
			maxTid = Integer.parseInt(resultSet.getObject(1).toString());
			maxTid++;
		}
		
		else maxTid = 0; //got no movies in the database? no problem
		
		pStatement = connection.prepareStatement("SELECT D.director_id "
									+ "FROM Stars S, Directors D "
									+ "WHERE S.sid = D.director_id AND S.name = ? ");
									
		pStatement.setString(1, director);
		resultSet = pStatement.executeQuery();
		
		if(resultSet.next()) //if director already in database,
			directorId = Integer.parseInt(resultSet.getObject(1).toString()); //simply get their ID
			
		else //otherwise, they need to be added as director
		{
			pStatement = connection.prepareStatement("SELECT S.sid "
											   + "FROM Stars S "
											   + "WHERE S.name = ? ");
			pStatement.setString(1, director);
			resultSet = pStatement.executeQuery();
			
			if(resultSet.next())
			{
				directorId = Integer.parseInt(resultSet.getObject(1).toString());
				addDirector(directorId);
			}
			
			else //otherwise, they need to be added as both star and director
			{
				pStatement = connection.prepareStatement("SELECT MAX(sid) "
													+ "FROM Stars ");
				resultSet = pStatement.executeQuery();
				resultSet.next();
				
				if(resultSet.getObject(1) != null)
				{
					directorId = Integer.parseInt(resultSet.getObject(1).toString());
					directorId++;
				}
				
				else directorId = 0;
				
				addStar(directorId, director);
				addDirector(directorId);
			}
		}
		
		//Now, we can add the movie since we have the Director!
		pStatement = connection.prepareStatement("INSERT INTO Titles "
										    + "VALUES(?,?,?,?,?) ");
		pStatement.setInt(1, maxTid);
		pStatement.setString(2, title);
		pStatement.setString(3, releaseDate);
		pStatement.setString(4, genre);
		pStatement.setInt(5, numCopies);
		pStatement.executeUpdate();
		
		pStatement = connection.prepareStatement("INSERT INTO Movies "
											+ "VALUES(?,?) ");
		pStatement.setInt(1, maxTid);
		pStatement.setInt(2, directorId);
		pStatement.executeUpdate();
		
		for(String str : cast)
		{
			String actor = str.trim();
			pStatement = connection.prepareStatement("SELECT A.actor_id "
									+ "FROM Stars S, Actors A "
									+ "WHERE S.sid = A.actor_id AND S.name = ? ");
									
			pStatement.setString(1, actor);
			resultSet = pStatement.executeQuery();
		
			if(resultSet.next()) //if actor already in database
				actorId = Integer.parseInt(resultSet.getObject(1).toString());
			
			else
			{
				pStatement = connection.prepareStatement("SELECT S.sid "
											   + "FROM Stars S "
											   + "WHERE S.name = ? "); //maybe they're already listed under Stars?
				pStatement.setString(1, actor);
				resultSet = pStatement.executeQuery();
			
				if(resultSet.next())
				{
					actorId = Integer.parseInt(resultSet.getObject(1).toString());
					addActor(actorId);
				}
			
				else
				{
					pStatement = connection.prepareStatement("SELECT MAX(sid) "
														+ "FROM Stars ");
					resultSet = pStatement.executeQuery();
					resultSet.next();
				
					if(resultSet.getObject(1) != null)
					{
						actorId = Integer.parseInt(resultSet.getObject(1).toString());
						actorId++;
					}
					
					else actorId = 0;
					
					addStar(actorId, actor);
					addActor(actorId);
				}
			}
			
			pStatement = connection.prepareStatement("INSERT INTO Stars_In VALUES(?,?) ");
			pStatement.setInt(1, actorId);
			pStatement.setInt(2, maxTid);
			pStatement.executeUpdate();
		}
	} //this method is a goddamn mess. Come back later
	
	public void addGame(String title, String releaseDate, String platform, int version, String genre, int numCopies) throws SQLException
	{
	}
	
	public void addMember(String email, String name, String password, long phone, String street, String city, String state, int zip, String plan)
	{
	}
	
	public void updateMember(String email, String name, String password, long phone, String street, String city, String state, int zip, String plan)
	{
	}
	
	public String[] getMovies()
	{
		return new String[]{"HI"};
	}
	
	public String[] getPlans()
	{
		return new String[]{"None", "Standard", "Silver", "Gold", "Platinum"};
	}
	
	void addStar(int sid, String name) throws SQLException
	{
		PreparedStatement pStatement;
		
		pStatement = connection.prepareStatement("INSERT INTO Stars VALUES(?,?,NULL) ");
		pStatement.setInt(1, sid);
		pStatement.setString(2, name);
		pStatement.executeUpdate();
	}
	
	void addDirector(int directorId) throws SQLException
	{
		statement.executeUpdate("INSERT INTO Directors VALUES(" + directorId + ") ");
	}
	
	void addActor(int actorId) throws SQLException
	{
		statement.executeUpdate("INSERT INTO Actors VALUES(" + actorId + ") ");
	}
	
	
}