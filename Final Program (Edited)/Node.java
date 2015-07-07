import java.util.*;

/**
* The <tt>Node</tt> object is the basic component of the {@link Trie} data structure.
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
	* Returns the value of the ID for the Node.
	*
	* @return The value of the ID of the current Node object.
	*/	
	public int getId()
	{
		return id; 
	}
	
	/**
	* Returns the array of the Edit Distance computation. 
	*
	* @return The array of the Edit Distance computation for the current Node.
	*/
	public int[] getLevRow()
	{
		return levRow; 
	}
	
	/**
	* Returns the array of the LCS computation. 
	*
	* @return The array fo the LCS computation for the current Node.
	*/
	public int[] getLcsRow()
	{
		return lcsRow; 
	}
	
	/**
	* Instantiates a new Edit Distance array with a new size. This is called
	* when a new query string is used or when a prefix of the query string has
	* found a match.
	*
	* @param size The size of the new Edit Distance array.
	*/
	public void createLevRow(int size)
	{
		levRow = new int[size];
	}
	
	/**
	* Instantiates a new LCS array with a new size. This is called
	* when a new query string is used or when a prefix of the query string has
	* found a match.
	*
	* @param size The size of the new LCs array.
	*/
	public void createLcsRow(int size)
	{
		lcsRow = new int[size]; 
	}
	
	/**
	* Returns the character value that is associated with the current Node. 
	*
	* @return The character value that is stored in the Node.
	*/
	public char getName()
	{
		return name; 
	}
	
	/**
	* Returns an ArrayList of the child Nodes of the current Node. 
	*
	* @return The set of the children of the current Node.
	*/
	public ArrayList<Node> getChildren()
	{
		return conns; 
	}
	
	/**
	* This method sets a Node as a child Node of the current Node. This is called
	* by the TrieReader when building the Trie from the file.
	*
	* @param child The Node that will be included in the set of children of the
	*  current Node.
	*/
	public void addChild(Node child)
	{
		conns.add(child); 
	}
	
	/**
	* Returns the parent Node of the current Node.
	*
	* @return The parent of the current Node.
	*/
	public Node getParent()
	{
		return parent;
	}
	
	/**
	* This method sets a Node to be the parent Node of the current Node. This
	* is called by the TrieReader when building the Trie from the file.
	*
	* @param par The Node that will be set as the parent of the current Node.
	*/
	public void setParent(Node par)
	{
		parent = par; 
	}
	
	/**
	* Returns an Iterator object that is used to iterate through the set of 
	* children of the current Node.
	*
	* @return The Iterator that contains the children of the current Node. 
	*/
	public Iterator<Node> getChildIterator()
	{
		return conns.iterator(); 
	}
}