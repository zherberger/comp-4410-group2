import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;

public class MemberDialog extends JDialog implements ActionListener
{
	DatabaseManager dbm;
	String email;
	String name;
	String password;
	long phone;
	String street;
	String city;
	String state;
	int zip;
	String plan;
	JTextField emailField;
	JTextField nameField;
	JPasswordField passwordField;
	JPasswordField confirmPasswordField;
	JTextField phoneField;
	JTextField streetField;
	JTextField cityField;
	JComboBox<String> stateBox;
	JTextField zipField;
	JComboBox<String> planBox;
	JTextField[] fields;
	static final String[] STATES = {"", "AL", "AK", "AZ", "AR", "CA", "CO", "CT", "DE", "FL", "GA",
									   "HI", "ID", "IL", "IN", "IA", "KS", "KY", "LA", "ME", "MD",
									   "MA", "MI", "MN", "MS", "MO", "MT", "NE", "NV", "NH", "NJ",
									   "NM", "NY", "NC", "ND", "OH", "OK", "OR", "PA", "RI", "SC",
									   "SD", "TN", "TX", "UT", "VT", "VA", "WA", "WV", "WI", "WY"};
	
	MemberDialog(DatabaseManager dbm)
	{
		this.dbm = dbm;
		setupFields();
		setupDialog();
	}
	
	MemberDialog(DatabaseManager dbm, String email, String name, String password, long phone, String street, String city, String state, int zip, String plan)
	{
		this.dbm = dbm;
		this.email = email;
		this.name = name;
		this.password = password;
		this.phone = phone;
		this.street = street;
		this.city = city;
		this.state = state;
		this.zip = zip;
		this.plan = plan;
		
		setupFields();
		populateFields();
		setupDialog();
	}
	
	void setupFields()
	{
		GroupLayout layout;
		JPanel mainPanel;
		JPanel buttonPanel;
		
		JLabel emailLabel = new JLabel("E-mail: ");
		JLabel nameLabel = new JLabel("Full Name: ");
		JLabel passwordLabel = new JLabel("Password: ");
		JLabel confirmPasswordLabel = new JLabel("Confirm Password: ");
		JLabel phoneLabel = new JLabel("Phone Number: ");
		JLabel streetLabel = new JLabel("Street Address: ");
		JLabel cityLabel = new JLabel("City: ");
		JLabel stateLabel = new JLabel("State: ");
		JLabel zipLabel = new JLabel("Zip: ");
		JLabel planLabel = new JLabel("Plan: ");
		
		emailField = new JTextField();
		nameField = new JTextField();
		passwordField = new JPasswordField();
		confirmPasswordField = new JPasswordField();
		phoneField = new JTextField();
		streetField = new JTextField();
		cityField = new JTextField();
		stateBox = new JComboBox<String>(STATES);
		zipField = new JTextField();
		planBox = new JComboBox<String>(dbm.getPlans());
		
		mainPanel = new JPanel();
		layout = new GroupLayout(mainPanel);
		mainPanel.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		
		layout.setHorizontalGroup(
			layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
					.addComponent(emailLabel)
					.addComponent(nameLabel)
					.addComponent(passwordLabel)
					.addComponent(confirmPasswordLabel)
					.addComponent(phoneLabel)
					.addComponent(streetLabel)
					.addComponent(cityLabel)
					.addComponent(stateLabel)
					.addComponent(zipLabel)
					.addComponent(planLabel))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
					.addComponent(emailField)
					.addComponent(nameField)
					.addComponent(passwordField)
					.addComponent(confirmPasswordField)
					.addComponent(phoneField)
					.addComponent(streetField)
					.addComponent(cityField)
					.addComponent(stateBox)
					.addComponent(zipField)
					.addComponent(planBox))
		);
		
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(emailLabel)
					.addComponent(emailField))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(nameLabel)
					.addComponent(nameField))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(passwordLabel)
					.addComponent(passwordField))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(confirmPasswordLabel)
					.addComponent(confirmPasswordField))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(phoneLabel)
					.addComponent(phoneField))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(streetLabel)
					.addComponent(streetField))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(cityLabel)
					.addComponent(cityField))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(stateLabel)
					.addComponent(stateBox))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(zipLabel)
					.addComponent(zipField))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(planLabel)
					.addComponent(planBox))
		);
		
		fields = new JTextField[]{emailField, nameField, phoneField, streetField, zipField};
		
		buttonPanel = new JPanel();
		
		if(email == null)
			buttonPanel.add(newButton("Add Member", "ADD", this, true));
		
		else buttonPanel.add(newButton("Save Member Info", "UPDATE", this, true));

		buttonPanel.add(newButton("Reset Values", "RESET", this, true));
		buttonPanel.add(newButton("Cancel", "CANCEL", this, true));
		
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
	
	void populateFields()
	{
		emailField.setText(email);
		nameField.setText(name);
		passwordField.setText(password);
		confirmPasswordField.setText(password);
		phoneField.setText(Long.toString(phone));
		streetField.setText(street);
		cityField.setText(city);
		stateBox.setSelectedItem(state);
		zipField.setText(Integer.toString(zip));
		planBox.setSelectedItem(plan);
	}
	
	void setupDialog()
	{
		pack();
		setTitle("Add Member");
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
			if(!fieldsLeftEmpty() && passwordsMatch())
			{
				try
				{
					dbm.addMember(emailField.getText().trim(),
								nameField.getText().trim(),
								new String(passwordField.getPassword()),
								Long.parseLong(phoneField.getText()),
								streetField.getText().trim(),
								cityField.getText().trim(),
								(String) stateBox.getSelectedItem(),
								Integer.parseInt(zipField.getText()),
								(String) planBox.getSelectedItem());
				}
				
				catch(SQLException s)
				{
					JOptionPane.showMessageDialog(this, s.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
					s.printStackTrace();
				}
			}
		}
		
		else if(command.equals("UPDATE"))
		{
			if(!fieldsLeftEmpty() && passwordsMatch())
			{
				try
				{
					dbm.updateMember(emailField.getText().trim(),
								nameField.getText().trim(),
								new String(passwordField.getPassword()),
								Long.parseLong(phoneField.getText()),
								streetField.getText().trim(),
								cityField.getText().trim(),
								(String) stateBox.getSelectedItem(),
								Integer.parseInt(zipField.getText()),
								(String) planBox.getSelectedItem());
				}
				
				catch(SQLException s)
				{
					JOptionPane.showMessageDialog(this, s.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
					s.printStackTrace();
				}
			}
		}
		
		else if(command.equals("RESET"))
		{
			populateFields();
		}
		
		else if(command.equals("CANCEL"))
		{
			dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
		}
	}
	
	boolean fieldsLeftEmpty()
	{
		boolean empty = false;
		
		for(JTextField field : fields)
		{
			if(field.getText().trim() == "")
			{
				empty = true;
			}
		}
		
		if(stateBox.getSelectedItem().toString() == "")
			empty = true;
		
		if(empty)
			JOptionPane.showMessageDialog(this, "One or more fields were left blank.", "Error", JOptionPane.ERROR_MESSAGE);
		
		return empty;
	}
	
	boolean passwordsMatch()
	{
		boolean passwordsMatch;
		
		passwordsMatch = new String(passwordField.getPassword()).equals(new String(confirmPasswordField.getPassword()));
		
		if(!passwordsMatch)
			JOptionPane.showMessageDialog(this, "Passwords do not match.", "Error", JOptionPane.ERROR_MESSAGE);
		
		return passwordsMatch;
	}
}