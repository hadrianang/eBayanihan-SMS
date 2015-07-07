import java.util.*;
import java.io.*;

/**
* The <tt>WordSegmentation</tt> class is responsible for the parsing and
* correcting of the SMS messages given a {@link Trie} object. The approach
* used by the <tt>WordSegmentation</tt> class is a greedy method that maximizes
* the string length that maximizes the score assigned to it based on its LCS and
* Edit Distance values. 
*/
public class WordSegmentation{

	private static TrieReader reader;
	private static Trie trie;
	private static String EMPTY = "";
    private static String SPACE = " ";
    private static int minLength, maxLength;
    private static double alpha = 1.0;
    private static HashSet<String> noCount; 
	
	/**
	* Initializes the variables needed by the class. 
	*/
	public WordSegmentation()
	{
		reader = new TrieReader();
		try{
			trie = reader.readTrie("brgycitymuni.out");
		}catch(Exception e) {}

		noCount = new HashSet<String>();
		noCount.add("city");
		noCount.add("island");
		noCount.add("town");
		noCount.add("not");
	}
	
	/**
	* The wordBreak method performs the greedy algorithm to split the SMS into
	* tokens and correct them at the same time. It calls the <tt>computeDP</tt>
	* method of the <tt>Trie</tt> class.
	* 
	* @param query The query string or the SMS text.
	* @param threshold The threshold value that limits the search space.
	* @return The String array containing the corrected tokens.
	*/
	public static String[] wordBreak(String query, int threshold){
        ArrayList<String> list = new ArrayList<String>();
       	
		int i;
		int tokCount=0; 
		//int prevCut = 0; 
        for(i=0; i<query.length(); i++){
			if(tokCount==5)
			{
				if(query.charAt(i)==',') i++;
				query = query.substring(i); 
				list.add(query.trim());
				break; 
			}
            //double maxScore = -1;
            int index = -1;
			
            String edit = "";
            int temp = i; 
            if(query.charAt(temp)==',') temp++;
            String quer = query.substring(temp).trim(); 
            //if(quer.charAt(0)==',') quer = quer.substring(1);
			//System.out.println("QUERY LOOK: " + quer + " " + tokCount);

            Object[] res = trie.computeDP(quer.trim(),threshold);
			edit = trie.backTrack(((Node)res[0]).getId()); 
			index = (int)res[1]-1;
            if(index!=-1){
                i = index;
				//prevCut = index;
				//System.out.println(edit);
				if(edit.equals(" ") || edit.equals("")) continue;
                list.add(edit);
				if(!noCount.contains(edit.toLowerCase())) 
					tokCount++; 
            }
            query = quer;
        }
        //System.out.println(Arrays.toString(list.toArray()));
        return list.toArray(new String[list.size()]);
    }
	
}