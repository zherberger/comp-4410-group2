import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.*;

public class GameDialog extends JDialog implements ActionListener
{
	DatabaseManager dbm;
	JTextField titleField;
	JTextField releaseDateField;
	JComboBox platformBox;
	
	GameDialog(DatabaseManager dbm)
	{
		this.dbm = dbm;
		setupFields();
		setupMainDialog();
	}
	
	void setupFields()
	{
	}
	
	void setupMainDialog()
	{
	}
	
//---------------------------------------------------------//
//---------------------ActionListener----------------------//
//---------------------------------------------------------//

	public void actionPerformed(ActionEvent e)
	{
	}
}