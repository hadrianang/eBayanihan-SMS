import java.util.*;
import java.io.*;

public class TrieBuilder{

	private Node root;

	public static void main(String[] args) throws Exception{
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		TrieBuilder trie = new TrieBuilder();
		while(true){
			String g = in.readLine();
			if(g==null) break;
			trie.addString(g.toLowerCase());
		}
		System.out.println(Node.count);
		System.out.print(trie.printTrie());
	}

	public TrieBuilder(){
		root = new Node('#');
	}

	public void addString(String s){
		Node current = root;
		for(int i=0; i<s.length(); i++){
			char letter = s.charAt(i);
			ArrayList<Node> list = current.edges;
			boolean flag = false;
			for(Node n:list){
				if(n.key==letter){
					flag = true;
					current = n;
					break;
				}
			}
			if(flag) continue;
			Node newNode = new Node(letter);
			current.edges.add(newNode);
			current = newNode;
		}
		boolean flag = false;
		for(Node n: current.edges){
			if(n.key=='$'){
				flag = true;
				break;
			}
		}
		if(!flag){
			Node lastNode = new Node('$');
			current.edges.add(lastNode);
		}
	}

	public String printTrie(){
		StringBuilder sb = new StringBuilder();
		StringBuilder sb2 = new StringBuilder();
		sb2.append("***\n");
		ArrayDeque<Node> queue = new ArrayDeque<Node>();
		queue.offer(root);
		while(!queue.isEmpty()){
			Node cur = queue.poll();
			sb.append(cur.id+"|"+cur.key+"\n");
			ArrayList<Node> list = cur.edges;
			for(Node n:list){
				// sb2.append(cur.id+"|"+n.id+"|"+n.key+"\n");
				sb2.append(cur.id+"|"+n.id+"\n");
				queue.offer(n);
			}
		}
		sb.append(sb2);
		return sb.toString();
	}
}

class Node{
	// int[] dp;
	static int count = 0;
	int id;
	char key;
	ArrayList<Node> edges = new ArrayList<Node>();

	public Node(char c){
		id = count++;
		key = c;
	}
}