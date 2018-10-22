import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Vector;

public class Movies_R_Us
{
	public static void main(String[] args)
	{
		new Movies_R_Us_Frame();
	}
}

class Movies_R_Us_Frame extends JFrame implements ActionListener, DatabaseManager
{
	JTextField queryField;
	JButton queryButton;
	JTable resultTable;
	Connection connection;
	Statement statement;
	ResultSet resultSet;
	ResultSetMetaData metaData;
	int numberOfColumns;
	final String URL = "jdbc:mysql://localhost:3306/sys?autoReconnect=true&useSSL=false";
	
	Movies_R_Us_Frame()
	{
		JPanel queryPanel;
		JScrollPane outputScrollPane;
		LoginDialog loginDialog;
		
		queryField = new JTextField(60);
		queryButton = new JButton("Send query");
		queryButton.setActionCommand("QUERY");
		queryButton.addActionListener(this);
		queryPanel = new JPanel();
		queryPanel.add(queryField);
		queryPanel.add(queryButton);
		
		resultTable = new JTable();
		outputScrollPane = new JScrollPane(resultTable);
		add(outputScrollPane, BorderLayout.CENTER);
		add(queryPanel, BorderLayout.NORTH);
		
		setupMainFrame();
		loginDialog = new LoginDialog(this);
	}
	
	void setupMainFrame()
	{
		Toolkit tk;
		Dimension d;
		
		tk = Toolkit.getDefaultToolkit();
		d = tk.getScreenSize();
		
		setSize(d.width / 2, d.height / 4);
		setLocation(d.width / 4, d.height / 2);
		
		setTitle("Movies-R-Us");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}
	
//---------------------------------------------------------//
//---------------------ActionListener----------------------//
//---------------------------------------------------------//

	public void actionPerformed(ActionEvent e)
	{
		String command = e.getActionCommand();
		
		if(command.equals("QUERY"))
		{
			executeQuery();
		}
	}
	
	void executeQuery()
	{
		try
		{
			resultSet = statement.executeQuery(queryField.getText().trim());
			metaData = resultSet.getMetaData();
			numberOfColumns = metaData.getColumnCount();
			
			resultTable.setModel(buildTableModel());
		}
		
		catch(SQLException s)
		{
			s.printStackTrace();
		}
	}
	
	DefaultTableModel buildTableModel() throws SQLException
	{
		Vector<String> columnNames = new Vector<String>();
		Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		Vector<Object> vector;
		
		for(int i = 1; i < numberOfColumns; i++)
			columnNames.add(metaData.getColumnName(i));
		
		while(resultSet.next())
		{
			vector = new Vector<Object>();
			
			for(int i = 1; i <= numberOfColumns; i++)
			{
				vector.add(resultSet.getObject(i));
			}
			
			data.add(vector);
		}
		
		return new DefaultTableModel(data, columnNames);
	}
	
//---------------------------------------------------------//
//---------------------DatabaseManager---------------------//
//---------------------------------------------------------//
	
	public void makeConnection(String URL, String login, String pass) throws SQLException
	{
		try
		{
			Class.forName("com.mysql.cj.jdbc.Driver");
			connection = DriverManager.getConnection(URL, login, pass);
			statement = connection.createStatement();
		}
		
		catch(ClassNotFoundException e)
		{
			System.out.println("Unable to initialize JDBC driver. Exiting...");
			System.exit(0);
		}
	}
}