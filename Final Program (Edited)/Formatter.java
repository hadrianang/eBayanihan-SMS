import java.util.*;

public class Formatter
{
	public String format(String[] arr)
	{
		StringBuilder sb = new StringBuilder(); 
		for(int i=0; i<arr.length; i++)
		{
			if(i!=0 && i!=arr.length-1)
			{
				if(arr[i].equals("city") || arr[i].equals("not")) sb.append(" ");
				else sb.append(",");
			}
			sb.append(arr[i]); 
		}
		sb.append("\n");
		return sb.toString();
	}
}