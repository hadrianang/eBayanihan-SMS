import java.util.*;
import java.io.*;

public class StatChecker
{
	public static void main(String[]args) throws Exception
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in)); 
		BufferedReader bre = new BufferedReader(new FileReader("answer_key_large.txt"));
		
		int correct = 0;
		int count = 0; 
		while(true) 
		{
			String input = br.readLine(); 
			String answer = bre.readLine();
			if(input == null || answer == null) break; 
			if(input.equalsIgnoreCase(answer)) correct++;
			count++; 
		}
		double check = (double)correct/count; 
		System.out.println(check*100 + "% correct"); 
	}
}