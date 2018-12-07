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
	JButton newMemberButton;
	JButton editMemberButton;
	JButton currentRentalsButton;
	JButton viewMembersButton;
	JButton markReturnedButton;
	JButton deleteButton;
	JButton logoutButton;
	JTable resultTable;
	JRadioButton moviesButton;
	JRadioButton gamesButton;
	ButtonGroup titleTypes;
	boolean titlesDisplayed;
	boolean membersDisplayed;
	boolean rentalsDisplayed;
	
	JPanel mainPanel;
	JPanel buttonPanel;
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
		
		addMovieButton = newButton("Add Movie", "ADD_MOVIE", this, true);
		addGameButton = newButton("Add Game", "ADD_GAME", this, true);
		recentRentalsButton = newButton("View Recent Rentals", "RECENT_RENTALS", this, true);
		popularTitlesButton = newButton("View Popular Titles", "POPULAR_TITLES", this, true);
		rentButton = newButton("Rent Title", "RENT", this, false);
		viewSequelsButton = newButton("View Sequels", "VIEW_SEQUELS", this, false);
		rentalHistoryButton = newButton("View Rental History", "RENTAL_HISTORY", this, true);
		newMemberButton = newButton("Add Member", "ADD_MEMBER", this, true);
		editMemberButton = newButton("Edit Member Info", "EDIT_MEMBER", this, true);
		currentRentalsButton = newButton("Currently Rented Items", "CURRENT_RENTALS", this, true);
		viewMembersButton = newButton("View Members", "VIEW_MEMBERS", this, true);
		markReturnedButton = newButton("Mark Returned", "RETURN", this, false);
		deleteButton = newButton("Delete", "DELETE", this, false);
		logoutButton = newButton("Log Out", "LOGOUT", this, true);
		
		resultTable = new JTable();
		resultTable.getSelectionModel().addListSelectionListener(this);
		outputScrollPane = new JScrollPane(resultTable);
		add(outputScrollPane, BorderLayout.CENTER);
		add(topPanel, BorderLayout.NORTH);
		
		makeConnection();
		new LoginDialog(this);
		setupMainFrame();
		searchButton.doClick();
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
		
		else if(command.equals("ADD_GAME"))
		{
			new GameDialog(this);
		}
		
		else if(command.equals("ADD_MEMBER"))
		{
			new MemberDialog(this);
		}
		
		else if(command.equals("EDIT_MEMBER"))
		{
			PreparedStatement pStatement;
			ResultSet addressResultSet;
			
			try
			{
				pStatement = connection.prepareStatement("SELECT * FROM Members "
												   + "WHERE email = ? ");
				pStatement.setString(1, email);
				resultSet = pStatement.executeQuery();
				resultSet.next();
				
				pStatement = connection.prepareStatement("SELECT A.city, A.state "
												   + "FROM Addresses A, Members M "
												   + "WHERE M.email = ? "
												   + "AND M.street = A.street "
												   + "AND M.zip = A.zip ");
				pStatement.setString(1, email);
				addressResultSet = pStatement.executeQuery();
				addressResultSet.next();
				
				new MemberDialog(this,
								email,
								resultSet.getObject(2).toString(),
								resultSet.getObject(3).toString(),
								Long.parseLong(resultSet.getObject(4).toString()),
								resultSet.getObject(6).toString(),
								addressResultSet.getObject(1).toString(),
								addressResultSet.getObject(2).toString(),
								Integer.parseInt(resultSet.getObject(7).toString()),
								resultSet.getObject(8).toString());
			}
			
			catch(SQLException s)
			{
				JOptionPane.showMessageDialog(this, s.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
		
		else try
		{
			if(command.equals("SEARCH"))
			{
				search();
			}
		
			if(command.equals("VIEW_SEQUELS"))
				viewSequels();
		
			else if(command.equals("RENTAL_HISTORY"))
				rentalHistory();
		
			else if(command.equals("RENT"))
				rent();
		
			else if(command.equals("RECENT_RENTALS"))
				recentRentals();
		
			else if(command.equals("POPULAR_TITLES"))
				popularTitles();
			
			else if(command.equals("LOGOUT"))
			{
				connection.close();
				resultSet.close();
				System.exit(0);
			}
			
			else if(command.equals("DELETE"))
				delete();
			
			else if(command.equals("CURRENT_RENTALS"))
			{
				currentRentals();
			}
			
			else if(command.equals("RETURN"))
			{
				markReturned();
			}
				
			else if(command.equals("VIEW_MEMBERS"))
			{
				viewMembers();
			}
			
			rentalsDisplayed = (rentalsDisplayed && command.equals("DELETE")) || command.equals("CURRENT_RENTALS");
			membersDisplayed = (membersDisplayed && command.equals("DELETE")) || command.equals("VIEW_MEMBERS");
			titlesDisplayed = (titlesDisplayed && command.equals("DELETE")) || command.equals("SEARCH");
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
										   + "VALUES(?,?,?,?,?,?,0) ");
		pStatement.setInt(1, maxRid);
		pStatement.setInt(2, (Integer) (resultTable.getValueAt(resultTable.getSelectedRow(), 0))); //the title ID
		pStatement.setString(3, email);
		pStatement.setString(4, sdf.format(new Date(now)));
		pStatement.setString(5, sdf.format(new Date(now + 604800000)));
		pStatement.setLong(6, now);
		
		pStatement.executeUpdate();
	}
	
	void currentRentals() throws SQLException
	{
		PreparedStatement pStatement;
		pStatement = connection.prepareStatement("SELECT R.rid as 'Rental ID', T.tid AS 'Title ID', T.title AS 'Title', M.email AS 'Member e-mail', A.street AS 'Shipping Address', A.city AS 'City', A.state AS 'State', A.zip AS 'Zip' "
									+ "FROM Titles T, Members M, Addresses A, Rentals R "
									+ "WHERE T.tid = R.tid "
									+ "AND R.email = M.email "
									+ "AND M.street = A.street "
									+ "AND M.zip = A.zip "
									+ "AND R.returned = 0 ");
									
		buildTableModel(pStatement);
	}
	
	void viewMembers() throws SQLException
	{
		PreparedStatement pStatement;
		pStatement = connection.prepareStatement("SELECT email AS 'E-mail', name AS 'Name', login AS 'Password', phone AS 'Phone #', street AS 'Street Address', zip AS 'Zip', pname AS 'Plan' "
										   + "FROM Members ");
										   
		buildTableModel(pStatement);
	}
	
	void delete() throws SQLException
	{
		PreparedStatement pStatement;
		
		if(titlesDisplayed)
		{
			pStatement = connection.prepareStatement("DELETE FROM Titles "
											   + "WHERE tid = ? ");
			pStatement.setInt(1, (Integer) (resultTable.getValueAt(resultTable.getSelectedRow(), 0)));
			pStatement.executeUpdate();
			search();
		}
		
		else if(membersDisplayed)
		{
			pStatement = connection.prepareStatement("DELETE FROM Members "
											   + "WHERE email = ? ");
			pStatement.setString(1, resultTable.getValueAt(resultTable.getSelectedRow(), 0).toString());
			pStatement.executeUpdate();
			viewMembers();
		}
	}
	
	void markReturned() throws SQLException
	{
		PreparedStatement pStatement;
		
		pStatement = connection.prepareStatement("UPDATE Rentals "
											+ "SET returned = 1 "
											+ "WHERE rid = ? ");
		pStatement.setInt(1, (Integer) (resultTable.getValueAt(resultTable.getSelectedRow(), 0)));
		pStatement.executeUpdate();
		currentRentals();
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
		int selectedRows = resultTable.getSelectedRowCount();
		
		rentButton.setEnabled(selectedRows == 1);
		viewSequelsButton.setEnabled(selectedRows == 1);
		markReturnedButton.setEnabled(rentalsDisplayed && selectedRows == 1);
		deleteButton.setEnabled((membersDisplayed || titlesDisplayed) && selectedRows == 1);
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
				
				if(resultSet.getObject(3).equals(1)) //if user is admin
				{
					buttonPanel = new JPanel(new GridLayout(2, 5));
					buttonPanel.add(addMovieButton);
					buttonPanel.add(addGameButton);
					buttonPanel.add(newMemberButton);
					buttonPanel.add(recentRentalsButton);
					buttonPanel.add(popularTitlesButton);
					buttonPanel.add(currentRentalsButton);
					buttonPanel.add(viewMembersButton);
					buttonPanel.add(markReturnedButton);
					buttonPanel.add(deleteButton);
					buttonPanel.add(logoutButton);
				}
				
				else
				{
					buttonPanel = new JPanel(new GridLayout(1, 5));
					buttonPanel.add(rentButton);
					buttonPanel.add(viewSequelsButton);
					buttonPanel.add(rentalHistoryButton);
					buttonPanel.add(editMemberButton);
					buttonPanel.add(logoutButton);
				}
				
				add(buttonPanel, BorderLayout.SOUTH);
			}
		}
		
		else
		{
			throw new SQLException("Username or password is incorrect.");
		}
		
		if(email == null)
			System.exit(0);
	}
	
	public void addMovie(String title, String releaseDate, String director, String[] cast, String genre, int numCopies, int sequelID) throws SQLException
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
			
			if(sequelID != -1)
			{
				pStatement = connection.prepareStatement("INSERT INTO Sequel_To VALUES(?,?) ");
				pStatement.setInt(1, maxTid);
				pStatement.setInt(2, sequelID);
				pStatement.executeUpdate();
			}
		}
	} //this method is a goddamn mess. Come back later
	
	public void addGame(String title, String releaseDate, String platform, String version, String genre, int numCopies) throws SQLException
	{
		PreparedStatement pStatement;
		int maxTid;
		
		resultSet = statement.executeQuery("SELECT MAX(tid) FROM Titles");
		resultSet.next();
		
		if(resultSet.getObject(1) != null)
		{
			maxTid = Integer.parseInt(resultSet.getObject(1).toString());
			maxTid++;
		}
		
		else maxTid = 0; //got no movies in the database? no problem
		pStatement = connection.prepareStatement("INSERT INTO Titles "
										   + "VALUES(?,?,?,?,?) ");
		pStatement.setInt(1, maxTid);
		pStatement.setString(2, title);
		pStatement.setString(3, releaseDate);
		pStatement.setString(4, genre);
		pStatement.setInt(5, numCopies);
		pStatement.executeUpdate();

		pStatement = connection.prepareStatement("INSERT INTO Games "
										   + "VALUES(?,?,?) ");
		pStatement.setInt(1, maxTid);
		pStatement.setString(2, platform);
		pStatement.setString(3, version);
		pStatement.executeUpdate();
	}
	
	public void addMember(String email, String name, String password, long phone, String street, String city, String state, int zip, String plan) throws SQLException
	{
		PreparedStatement pStatement;
		
		pStatement = connection.prepareStatement("SELECT M.email "
										   + "FROM Members M "
										   + "WHERE M.email = ? ");
		pStatement.setString(1, email);
		resultSet = pStatement.executeQuery();
		
		if(resultSet.next())
			throw new SQLException("User already exists with this email.");
		
		checkAddress(street, city, state, zip);
		
		pStatement = connection.prepareStatement("INSERT INTO Members "
										   + "VALUES(?,?,?,?,0,?,?,?) "); //can an admin add another admin? For now, I'm saying NO
										   
		pStatement.setString(1, email);
		pStatement.setString(2, name);
		pStatement.setString(3, password);
		pStatement.setLong(4, phone);
		pStatement.setString(5, street);
		pStatement.setInt(6, zip);
		pStatement.setString(7, plan);
		
		pStatement.executeUpdate();
	}
	
	public void updateMember(String email, String name, String password, long phone, String street, String city, String state, int zip, String plan) throws SQLException
	{
		PreparedStatement pStatement;
		
		checkAddress(street, city, state, zip);
		
		pStatement = connection.prepareStatement("UPDATE Members "
										   + "SET name = ?, "
										   + "login = ?, "
										   + "phone = ?, "
										   + "street = ?, "
										   + "zip = ?, "
										   + "pname = ? "
										   + "WHERE email = ? ");
										   
		pStatement.setString(1, name);
		pStatement.setString(2, password);
		pStatement.setLong(3, phone);
		pStatement.setString(4, street);
		pStatement.setInt(5, zip);
		pStatement.setString(6, plan);
		pStatement.setString(7, email);
		
		pStatement.executeUpdate();
	}
	
	public void checkAddress(String street, String city, String state, int zip) throws SQLException
	{
		PreparedStatement pStatement;
		
		pStatement = connection.prepareStatement("SELECT A.street, A.zip "
										   + "FROM Addresses A "
										   + "WHERE A.street = ? "
										   + "AND A.zip = ? ");
										   
		pStatement.setString(1, street);
		pStatement.setInt(2, zip);
		resultSet = pStatement.executeQuery();
		
		if(!resultSet.next()) //if address not already in database
		{
			pStatement = connection.prepareStatement("INSERT INTO Addresses "
											    + "VALUES(?,?,?,?) ");
												
			pStatement.setString(1, street);
			pStatement.setString(2, city);
			pStatement.setString(3, state);
			pStatement.setInt(4, zip);
			
			pStatement.executeUpdate();
		}
	}
	
	public Vector<String> getMovies()
	{
		PreparedStatement pStatement;
		Vector<String> movies = new Vector<String>();
		
		try
		{
			pStatement = connection.prepareStatement("SELECT T.tid, T.title "
										   + "FROM Titles T, Movies M "
										   + "WHERE T.tid = M.movie_id ");
			resultSet = pStatement.executeQuery();
			
			while(resultSet.next())
			movies.add(String.format("%-5s%-20s", resultSet.getObject(1).toString(), resultSet.getObject(2).toString()));
		}
		
		catch(SQLException e)
		{
			System.out.println("Could not GET MOVIES.");
		}
		
		return movies;
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