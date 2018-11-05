import javax.swing.*;

public class NumCopiesVerifier extends InputVerifier
{
	public boolean verify(JComponent input)
	{
		String inputString;
		int numCopies;
		boolean isValid = true;
		
		inputString = ((JTextField) input).getText().trim();
		
		if(!inputString.equals("")) //if input string is not blank (or spaces)
		{
			try
			{
				numCopies = Integer.parseInt(inputString);
				
				if(numCopies < 0 || numCopies >= Integer.MAX_VALUE)
					throw new Exception();
			}
			
			catch(Exception e)
			{
				isValid = false;
			}
		}
		
		return isValid;
	}
}