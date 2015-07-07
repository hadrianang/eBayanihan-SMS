import java.util.*;

/**
* The <tt>Formatter</tt> class is used to "standardize" the format of the
* tokens that are the result of the <tt>wordBreak</tt> method of the 
* {@link WordSegmentation} class.
*/
public class Formatter
{
	/**
	* This method converts an array to a String that should be in the 
	* correct SMS format.
	*
	* @param arr The array of corrected String tokens. 
	* @return The standardized String output of the tokens supplied.
	*/
	public String format(String[] arr)
	{
		HashSet<String> space = new HashSet<String>();
		space.add("city");
		space.add("island");
		space.add("town"); 

		StringBuilder sb = new StringBuilder(); 
		boolean not = false ;
		int tokens = 0; 
		for(int i=0; i<arr.length; i++)
		{
			if(arr[i].equals(" ")) continue;
			if(i!=0)
			{
				if(space.contains(arr[i].toLowerCase())|| not || i==1||tokens == 4)
				{
					not = false;
					sb.append(" ");
				} 
				else sb.append(",");

				if(arr[i].equalsIgnoreCase("not"))
					not = true; 
			}
			if(!arr[i].equalsIgnoreCase("not") && !space.contains(arr[i].toLowerCase()))
				tokens++; 
			sb.append(arr[i]); 
		}
		sb.append("\n");
		return sb.toString();
	}
}