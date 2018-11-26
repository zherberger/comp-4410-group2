import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.text.*;

public class MovieDialog extends JDialog implements ActionListener
{
	DatabaseManager dbm;
	JTextField titleField;
	JTextField releaseDateField;
	JTextField directorField;
	JTextField castField;
	JComboBox<String> genreBox;
	JCheckBox sequelCheckBox;
	JComboBox<String> sequelComboBox;
	JTextField numCopiesField;
	JTextField[] fields;
	final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
	static final String[] GENRES = {"", "Action", "Adventure", "Animation", "Biopic",
								   "Childrens", "Comedy", "Crime", "Documentary",
								   "Drama", "Experimental", "Family", "Fantasy",
								   "Historical", "Horror", "Musical", "Romance",
								   "Sports", "Science Fiction", "Thriller", "War",
								   "Western", "Other"};
	
	MovieDialog(DatabaseManager dbm)
	{
		this.dbm = dbm;
		setupFields();
		setupMainDialog();
	}
	
	void setupFields()
	{
		GroupLayout layout;
		JPanel mainPanel;
		JPanel buttonPanel;
		
		JLabel titleLabel = new JLabel("Title: ");
		JLabel releaseDateLabel = new JLabel("Release Date: ");
		JLabel directorLabel = new JLabel("Director: ");
		JLabel castLabel = new JLabel("Cast: ");
		JLabel genreLabel = new JLabel("Genre: ");
		JLabel toLabel = new JLabel("To: ");
		JLabel numCopiesLabel = new JLabel("Number of copies: ");
		
		titleField = new JTextField();
		releaseDateField = new JTextField();
		releaseDateField.setInputVerifier(new DateVerifier());
		directorField = new JTextField();
		castField = new JTextField();
		genreBox = new JComboBox<String>(GENRES);
		sequelCheckBox = new JCheckBox("Sequel ");
		sequelComboBox = new JComboBox<String>();
		numCopiesField = new JTextField();
		numCopiesField.setInputVerifier(new NumCopiesVerifier());
		
		mainPanel = new JPanel();
		layout = new GroupLayout(mainPanel);
		mainPanel.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		
		layout.setHorizontalGroup(
			layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
					.addComponent(titleLabel)
					.addComponent(releaseDateLabel)
					.addComponent(directorLabel)
					.addComponent(castLabel)
					.addComponent(genreLabel)
					.addComponent(sequelCheckBox)
					.addComponent(numCopiesLabel))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
					.addComponent(titleField)
					.addComponent(releaseDateField)
					.addComponent(directorField)
					.addComponent(castField)
					.addComponent(genreBox)
					.addGroup(layout.createSequentialGroup()
						.addComponent(toLabel)
						.addComponent(sequelComboBox))
					.addComponent(numCopiesField))
		);
		
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(titleLabel)
					.addComponent(titleField))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(releaseDateLabel)
					.addComponent(releaseDateField))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(directorLabel)
					.addComponent(directorField))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(castLabel)
					.addComponent(castField))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(genreLabel)
					.addComponent(genreBox))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(sequelCheckBox)
					.addComponent(toLabel)
					.addComponent(sequelComboBox))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(numCopiesLabel)
					.addComponent(numCopiesField))
		);
		
		buttonPanel = new JPanel(new GridLayout(1, 3));
		buttonPanel.add(newButton("Add Movie", "ADD", this, true));
		buttonPanel.add(newButton("Reset Values", "RESET", this, true));
		buttonPanel.add(newButton("Cancel", "CANCEL", this, true));
		
		fields = new JTextField[]{titleField, releaseDateField, directorField, castField, numCopiesField};
		
		add(mainPanel, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);
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
	
	void setupMainDialog()
	{
		Toolkit tk;
		Dimension d;
		
		tk = Toolkit.getDefaultToolkit();
		d = tk.getScreenSize();
		
		setSize(d.width / 4, d.height / 3);
		setLocation(d.width / 4, d.height / 3);
		
		setTitle("Add Movie");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
		setVisible(true);
	}
	
//---------------------------------------------------------//
//---------------------ActionListener----------------------//
//---------------------------------------------------------//
	
	public void actionPerformed(ActionEvent e)
	{
		String command = e.getActionCommand();
		
		if(command.equals("ADD"))
		{
			String[] cast;
			boolean isValid = true;
		
			for(JTextField field : fields)
			{
				if(field.getText().trim().equals(""))
					isValid = false;
			}
			
			if(genreBox.getSelectedItem() == "")
				isValid = false;
		
			if(!isValid)
				JOptionPane.showMessageDialog(this, "One or more fields were left blank.", "Could not add/edit assignment", JOptionPane.ERROR_MESSAGE);
		
			else
			{
				try
				{
					dbm.addMovie(titleField.getText().trim(),
								releaseDateField.getText().trim(),
								directorField.getText().trim(),
								castField.getText().trim().split(","),
								(String) genreBox.getSelectedItem(),
								Integer.parseInt(numCopiesField.getText().trim()));
				}
				
				catch(SQLException s)
				{
					JOptionPane.showMessageDialog(this, s.getMessage(), "Could not add movie", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
		
		else if(command.equals("RESET"))
		{
		}
		
		else if(command.equals("CANCEL"))
		{
			dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
		}
	}
}