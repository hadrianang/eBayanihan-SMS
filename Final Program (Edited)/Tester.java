import java.util.*;
import java.io.*;
public class Tester
{
	public static void main(String[]args) throws Exception
	{
		TrieReader reader = new TrieReader();
		TrieAlt trie = reader.readTrie("brgycitymuni.out"); 
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in)); 
		WordSegmentation ws = new WordSegmentation(); 
		long startTime = System.currentTimeMillis(); 
		while(true)
		{
			String temp = br.readLine();
			if(temp==null) break; 
			temp = temp.toLowerCase();
			String[] arr = ws.wordBreak(temp.trim(),3); 
			for(int i=0; i<arr.length; i++)
			{
				if(i!=0)
				{
					if(arr[i].equals("city") || arr[i].equals("not")) System.out.print(" ");
					else System.out.print(",");
				}
				System.out.print(arr[i]); 
			}
			System.out.println();
		}
		long endTime = System.currentTimeMillis(); 
		double exec = (endTime - startTime) / (double)1000;
		System.out.println("COMPUTATION TIME: " + exec + " seconds"); 
	}
}