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
			boolean not = false; 
			//System.out.println(Arrays.toString(arr));
			//System.out.println(arr[5] + " " + (int) arr[5].charAt(0)); 
			Formatter form = new Formatter();
			
			//POST,Landyslideurgent,ECHAGUE,Road
			//System.out.println(Arrays.toString(arr));
			String[] arr = ws.wordBreak(temp.trim(),3);
			// System.out.println(arr.length);
			// for(int i=0; i<arr.length; i++)
			// 	System.out.print(arr[i] + "|");
			// System.out.println();
			out.print(form.format(arr));
			out.flush();
		}
		long endTime = System.currentTimeMillis(); 
		double exec = (endTime - startTime) / (double)1000;
		System.out.println("COMPUTATION TIME: " + exec + " seconds"); 
	}
}