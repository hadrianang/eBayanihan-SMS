import java.util.*;
import java.io.*;
public class Tester
{
	public static void main(String[]args) throws Exception
	{
		TrieReader reader = new TrieReader();
		TrieAlt trie = reader.readTrie("brgycitymuni.out"); 
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in,"UTF8")); 
		PrintStream out = new PrintStream(System.out,true,"UTF-8"); 
		WordSegmentation ws = new WordSegmentation(); 
		long startTime = System.currentTimeMillis(); 
		while(true)
		{
			String temp = br.readLine();
			if(temp==null) break; 
			temp = temp.toLowerCase();
			String[] arr = ws.wordBreak(temp.trim(),3); 
			boolean not = false; 
			//System.out.println(Arrays.toString(arr));
			//System.out.println(arr[5] + " " + (int) arr[5].charAt(0)); 
			for(int i=0; i<arr.length; i++)
			{
				arr[i] = arr[i].trim(); 
				//if(arr[i].equals("") || arr[i].equals(" ")) continue;
				if(i!=0)
				{
					if(arr[i].equals("city") || not) 
					{
						out.print(" ");
						not = false; 
					}
					else out.print(",");
					
					if(arr[i].equals("not")) not = true; 
				}
				out.print(arr[i]); 
				/*System.out.print(" " + (int)arr[i].charAt(0)); */
			}
			
			//POST,Landyslideurgent,ECHAGUE,Road
			//System.out.println(Arrays.toString(arr));
			out.println();
			out.flush();
		}
		long endTime = System.currentTimeMillis(); 
		double exec = (endTime - startTime) / (double)1000;
		System.out.println("COMPUTATION TIME: " + exec + " seconds"); 
	}
}