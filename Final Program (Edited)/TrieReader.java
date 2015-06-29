import java.util.*; 
import java.io.*; 

public class TrieReader
{
	
	public TrieAlt readTrie(String fileName) throws Exception
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
		return new TrieAlt(graph); 
	}
}