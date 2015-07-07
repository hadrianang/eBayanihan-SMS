import java.util.*; 
import java.io.*; 

/**
* The <tt>TrieReader</tt>'s <tt>readTrie</tt> method is called upon 
* initializing the system. It rebuilds the {@link Trie} according 
* to the output of the {@link TrieBuilder} class.
*/
public class TrieReader
{
	/**
	* Method to "rebuild" the Trie from a file that is the output of the 
	* TrieBuilder.
	*
	*@param fileName The name of the output file of the TrieBuilder program
	* to serve as input for the TrieReader.
	*@return Trie object that contains the Trie built according to the input
	* file.
	*/
	public Trie readTrie(String fileName) throws IOException
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "UTF8"));
		int size = Integer.parseInt(br.readLine()); 
		Node[] graph = new Node[size+1]; 
		for(int i=0; i<size; i++)
		{
			String input = br.readLine();
			StringTokenizer tk = new StringTokenizer(input,"|"); 
			
			int id = Integer.parseInt(tk.nextToken());
			char name = tk.nextToken().charAt(0); 
			graph[id] = new Node(id,name); 
		}
		br.readLine(); 
		while(true)
		{
			String input = br.readLine();
			if(input==null) break; 
			StringTokenizer tk = new StringTokenizer(input,"|"); 
			Node s = graph[Integer.parseInt(tk.nextToken())];
			Node d = graph[Integer.parseInt(tk.nextToken())];
			s.addChild(d);
			
			if(d.getName() == '$') s.setWordStatus(true); 
		}
		return new Trie(graph); 
	}
}