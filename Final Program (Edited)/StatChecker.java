import java.util.*;
import java.io.*;
/**
* The <tt>StatChecker</tt> class is another one of the debugging tools
* developed for the system. It is used in conjunction with a Generator
* program that randomizes test cases. The StatChecker compares the 
* WordSegmentation output to the answer key and reports how accurate it is.
*/
public class StatChecker
{
	public static void main(String[]args) throws Exception
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in)); 
		BufferedReader bre = new BufferedReader(new FileReader("answer_key_generic.txt"));
		
		int correct = 0;
		int count = 0; 
		while(true) 
		{
			String input = br.readLine(); 
			String answer = bre.readLine();
			if(input == null || answer == null) break; 
			if(input.equalsIgnoreCase(answer)) correct++;
			else System.out.println("Case #" + (count+1) + " : " + input.toLowerCase() + " | " + answer);
			count++; 
		}
		double check = (double)correct/count; 
		System.out.println(check*100 + "% correct"); 
	}
}