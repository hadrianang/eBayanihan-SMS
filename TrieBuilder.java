import java.util.*;
import java.io.*;
/**
* The <tt>TrieBuilder</tt> class allows the system to preprocess the dictionary into
* a {@link Trie} data structure. Since the list of cities, barangays, and municipalities
* along with the SMS keywords are not expected to change very often, we precompute
* the <tt>Trie</tt> structure and output it into a text file to minimize the overhead in
* case the server might need to restart.
*
*/
public class TrieBuilder{

	private TrieNode root;
	
	/**
	* The main method automates reading from stdin and writing output to console.
	* The user can redirect the input and output in the terminal.
	*/
	public static void main(String[] args) throws IOException{
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		TrieBuilder trie = new TrieBuilder();
		while(true){
			String g = in.readLine();
			if(g==null) break;
			trie.addString(g.toLowerCase());
		}
		System.out.println(TrieNode.count);
		System.out.print(trie.printTrie());
	}

	/**
	* Initializes the Trie with a root TrieNode containing a '#' character.
	*/
	public TrieBuilder(){
		root = new TrieNode('#');
	}

	/**
	*The method used to add new words into the Trie data structure.
	*
	*@param	word the new word to be inserted into the trie
	**/
	public void addString(String word){
		TrieNode current = root;
		for(int i=0; i<word.length(); i++){
			char letter = word.charAt(i);
			ArrayList<TrieNode> list = current.edges;
			boolean flag = false;
			for(TrieNode n:list){
				if(n.key==letter){
					flag = true;
					current = n;
					break;
				}
			}
			if(flag) continue;
			TrieNode newNode = new TrieNode(letter);
			current.edges.add(newNode);
			current = newNode;
		}
		boolean flag = false;
		for(TrieNode n: current.edges){
			if(n.key=='$'){
				flag = true;
				break;
			}
		}
		if(!flag){
			TrieNode lastNode = new TrieNode('$');
			current.edges.add(lastNode);
		}
	}

	/**
	*Prints the Trie into the format needed by the TrieReader program.
	*
	*@return Returns the String that contains the Trie in the format needed by the TrieReader.
	*/
	public String printTrie(){
		StringBuilder sb = new StringBuilder();
		StringBuilder sb2 = new StringBuilder();
		sb2.append("***\n");
		ArrayDeque<TrieNode> queue = new ArrayDeque<TrieNode>();
		queue.offer(root);
		while(!queue.isEmpty()){
			TrieNode cur = queue.poll();
			sb.append(cur.id+"|"+cur.key+"\n");
			ArrayList<TrieNode> list = cur.edges;
			for(TrieNode n:list){
				// sb2.append(cur.id+"|"+n.id+"|"+n.key+"\n");
				sb2.append(cur.id+"|"+n.id+"\n");
				queue.offer(n);
			}
		}
		sb.append(sb2);
		return sb.toString();
	}
}

class TrieNode{

	static int count = 0;
	int id;
	char key;
	ArrayList<TrieNode> edges = new ArrayList<TrieNode>();

	public TrieNode(char c){
		id = count++;
		key = c;
	}
}