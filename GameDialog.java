	import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.*;

public class GameDialog extends JDialog implements ActionListener{
DatabaseManager dbm;
JTextField titleField;
JTextField releaseDateField;
JComboBox<String> platformBox;
JTextField versionField;
JComboBox<String> genreBox;
JTextField numCopiesField;
JTextField[] fields;
final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

GameDialog(DatabaseManager dbm)
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
	JLabel platformLabel = new JLabel("Platform: ");
	JLabel versionLabel = new JLabel("Version: ");
	JLabel genreLabel = new JLabel("Genre: ");
	JLabel numCopiesLabel = new JLabel("Number of copies: ");
	
	titleField = new JTextField();
	releaseDateField = new JTextField();
	releaseDateField.setInputVerifier(new DateVerifier());
	platformBox = new JComboBox<String>(Game.PLATFORMS);
	versionField = new JTextField();
	genreBox = new JComboBox<String>(Game.GENRES);
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
				.addComponent(platformLabel)
				.addComponent(versionLabel)
				.addComponent(genreLabel)
				.addComponent(numCopiesLabel))
			.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(titleField)
				.addComponent(releaseDateField)
				.addComponent(platformBox)
				.addComponent(versionField)
				.addComponent(genreBox)
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
				.addComponent(platformLabel)
				.addComponent(platformBox))
			.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(versionLabel)
				.addComponent(versionField))
			.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(genreLabel)
				.addComponent(genreBox))
			.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(numCopiesLabel)
				.addComponent(numCopiesField))
	);
	
	buttonPanel = new JPanel(new GridLayout(1, 3));
	buttonPanel.add(newButton("Add Game", "ADD", this, true));
	buttonPanel.add(newButton("Reset Values", "RESET", this, true));
	buttonPanel.add(newButton("Cancel", "CANCEL", this, true));
	
	fields = new JTextField[]{titleField, releaseDateField, versionField, numCopiesField};
	
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
	
	setTitle("Add Game");
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
		String[] version;
		boolean isValid = true;
	
		for(JTextField field : fields)
		{
			if(field.getText().trim().equals(""))
				isValid = false;
		}
	
		if(!isValid)
			JOptionPane.showMessageDialog(this, "One or more fields were left blank.", "Could not add/edit assignment", JOptionPane.ERROR_MESSAGE);
	
		else
		{
			try
			{
				dbm.addGame(titleField.getText().trim(),
							releaseDateField.getText().trim(),
							(String) platformBox.getSelectedItem(),
							versionField.getText().trim(),
							(String) genreBox.getSelectedItem(),
							Integer.parseInt(numCopiesField.getText().trim()));
			}

			catch(SQLException s)
			{
				JOptionPane.showMessageDialog(this, s.getMessage(), "Could not add game", JOptionPane.ERROR_MESSAGE);
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