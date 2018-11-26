import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;

public class LoginDialog extends JDialog implements ActionListener
{
	DatabaseManager dbm;
	//JTextField URLField;
	JTextField loginField;
	JPasswordField passField;
	
	LoginDialog(DatabaseManager dbm)
	{
		this.dbm = dbm;
		setupFields();
		setupMainDialog();
	}
	
	void setupFields()
	{
		JButton loginButton;
		JPanel mainPanel;
		GroupLayout layout;
		
		//URLField = new JTextField();
		loginField = new JTextField();
		passField = new JPasswordField();
		
		//JLabel URLLabel = new JLabel("URL: ");
		JLabel loginLabel = new JLabel("Login: ");
		JLabel passLabel = new JLabel ("Password: ");
		
		loginButton = new JButton("Login");
		loginButton.addActionListener(this);
		loginButton.setActionCommand("LOGIN");
		getRootPane().setDefaultButton(loginButton);
		
		mainPanel = new JPanel();
		layout = new GroupLayout(mainPanel);
		mainPanel.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		
		layout.setHorizontalGroup(
			layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
					.addComponent(loginLabel)
					.addComponent(passLabel))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
					.addComponent(loginField)
					.addComponent(passField))
		);
		
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(loginLabel)
					.addComponent(loginField))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(passLabel)
					.addComponent(passField))
		);
		
		add(mainPanel, BorderLayout.CENTER);
		add(loginButton, BorderLayout.SOUTH);
	}
	
	void setupMainDialog()
	{
		Toolkit tk;
		Dimension d;
		
		tk = Toolkit.getDefaultToolkit();
		d = tk.getScreenSize();
		
		setSize(d.width / 4, d.height / 4);
		setLocation(d.width / 4, d.height / 3);
		
		setTitle("Log in to Movies-R-Us");
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
		
		if(command.equals("LOGIN"))
		{	
			try
			{
				dbm.login(loginField.getText().trim(), new String(passField.getPassword()));
				dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
			}
			
			catch(SQLException s)
			{
				JOptionPane.showMessageDialog(this, s.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
}