import java.util.*;

/**
* The Node object is the basic component of the {@link Trie} data structure.
* It contains several getter/setter methods that are needed for the
* operation of the <tt>Trie</tt> structure to implement the basic dictionary methods
* and for the computation of LCS and Edit Distance.
*/
public class Node
{
	private int id; 
	private char name; 
	private int[]levRow;
	private int[]lcsRow; 
	private int[]subRow; 
	private int depth; 
	private ArrayList<Node> conns; 
	private Node parent; 
	private long vis; 
	private int runningMax; 
	private boolean isWordEnd; 

	/**
	* Creates a Node object that is used for the Trie data structure. The 
	* constructor is called only by the {@link TrieReader} when building according to
	* the output of the {@link TrieBuilder} class.
	*
	* @param id The ID of a Node object is given by the TrieBuilder.
	* @param name The name of a Node object is the character value that is
	*  assigned to it.
	*/
	public Node(int id, char name)
	{
		this.name=name;
		this.id=id;
		levRow = new int[1];
		lcsRow = new int[1];
		subRow = new int[1]; 
		parent = null; 
		depth = 0; 
		conns = new ArrayList<Node>(); 
		runningMax = 0; 
		vis = Integer.MIN_VALUE;
		isWordEnd = false; 
	}
	/**
	* Checks whether the the current Node makes a complete word. 
	* Can also mean that the Node has a child that contains a '$' 
	* or end of word character.
	*
	* @return True if the Node marks the end of a word.
	*/
	public boolean isWord()
	{
		return isWordEnd; 
	}

	/**
	* Changes the value of the word status of a Node.
	*
	* @param val The new value that the wordStatus should be set to.
	*/
	public void setWordStatus(boolean val)
	{
		isWordEnd = val;
	}
	
	/**
	* The visited state is implemented using a counter rather than a boolean
	* to be able to reset all Node visited states by simply updating a global
	* counter.
	*
	* @param val The long value that will replace the current vis value.
	*/
	public void setVis(long val)
	{
		vis = val; 
	}
	
	/**
	* This method is usually called only by the TrieReader program to set
	* the current Node's depth while building the Trie.
	*
	* @param val The new depth value to be set for the current Node.
	*/
	public void setDepth(int val)
	{
		depth = val;
	}
	
	/**
	* Returns the value of a Node's depth. This makes it easier to know the
	* length of the current substring that ends in the current Node.
	*
	* @return The depth of the current Node in the Trie.
	*/
	public int getDepth()
	{
		return depth; 
	}
	
	/**
	* This method is used when computing the Edit Distance between a dictionary 
	* entry to a certain query string. 
	*
	* @param ind The index of the Levenshtein array to be set.
	* @param val The new value that will be set in the index of the array.
	*/
	public void setLev(int ind, int val)
	{
		levRow[ind] = val;
	}
	
	/**
	* This method is used when computing the Longest Common Subsequence
	* between a dictionary entry to a certain query string. 
	*
	* @param ind The index of the LCS array to be set.
	* @param val The new value that will be set in the index of the array.
	*/
	public void setLcs(int ind, int val)
	{
		lcsRow[ind] = val; 
	}
	
	/**
	* Returns the value of the Edit Distance array at a certain index for 
	* the current node.
	*
	* @param ind The index of the array that is to be accessed.
	* @return The value of the Edit Distance array in the index provided.
	*/
	public int getLev(int ind)
	{
		return levRow[ind]; 
	}
	
	/**
	* Returns the value of the LCS array at a certain index for 
	* the current node.
	*
	* @param ind The index of the array that is to be accessed.
	* @return The value of the LCS array in the index provided.
	*/
	public int getLcs(int ind)
	{
		return lcsRow[ind]; 
	}
	
	/**
	* Returns the value of the visited count for the Node.
	*
	* @return The value of the visited counter of the current Node object.
	*/
	public long getVis()
	{
		return vis; 
	}
	
	/**
	* Returns the value of the visited count for the Node.
	*
	* @return The value of the visited counter of the current Node object.
	*/	
	public int getId()
	{
		return id; 
	}
	
	public int[] getLevRow()
	{
		return levRow; 
	}
	
	public int[] getLcsRow()
	{
		return lcsRow; 
	}
	
	public void createLevRow(int size)
	{
		levRow = new int[size];
	}
	
	public void createLcsRow(int size)
	{
		lcsRow = new int[size]; 
	}
	
	public char getName()
	{
		return name; 
	}
	
	public ArrayList<Node> getChildren()
	{
		return conns; 
	}
	
	public void addChild(Node child)
	{
		conns.add(child); 
	}
	
	public Node getParent()
	{
		return parent;
	}
	
	public void setParent(Node par)
	{
		parent = par; 
	}
	
	public Iterator<Node> getChildIterator()
	{
		return conns.iterator(); 
	}
}