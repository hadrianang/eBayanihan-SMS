import java.util.*;
import java.io.*;
public class WordSegmentation{

	private static TrieReader reader;
	private static TrieAlt trie;
	private static String EMPTY = "";
    private static String SPACE = " ";
    private static int minLength, maxLength;
    private static double alpha = 1.0;
	
	public WordSegmentation()
	{
		reader = new TrieReader();
		try{
			trie = reader.readTrie("brgycitymuni.out");
		}catch(Exception e) {}
	}
	
	public static String[] wordBreak(String query, int threshold){
        ArrayList<String> list = new ArrayList<String>();
		int i;
		int tokCount=0; 
		int prevCut = 0; 
        for(i=0; i<query.length(); i++){
			if(tokCount==5)
			{
				list.add(query.substring(i));
				break; 
			}
            double maxScore = -1;
            int index = -1;
			
            String edit = "";
            Object[] res = trie.computeDP(query.substring(i),threshold);
			
			edit = trie.backTrack(((Node)res[0]).getId()); 
			index = prevCut + 1 + (int)res[1];
            if(index!=-1){
                i = index-1;
				prevCut = index;
                list.add(edit);
				if(!edit.equals("city") && !edit.equals("not")) 
					tokCount++; 
            }
        }
        return list.toArray(new String[list.size()]);
    }
	
}