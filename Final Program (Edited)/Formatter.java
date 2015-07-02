import java.util.*;

public class Formatter
{
	public String format(String[] arr)
	{
		StringBuilder sb = new StringBuilder(); 
		boolean not = false ;
		for(int i=0; i<arr.length; i++)
		{
			if(arr[i].equals(" ")) continue;
			if(i!=0)
			{
				if(arr[i].equals("city") || not)
				{
					not = false;
					sb.append(" ");
				} 
				else sb.append(",");

				if(arr[i].equalsIgnoreCase("not"))
					not = true; 
			}
			sb.append(arr[i]); 
		}
		sb.append("\n");
		return sb.toString();
	}
}