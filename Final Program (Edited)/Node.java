import java.util.*;

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
	public boolean isWord()
	{
		return isWordEnd; 
	}
	
	public void setWordStatus(boolean val)
	{
		isWordEnd = val;
	}
	
	public void setVis(long val)
	{
		vis = val; 
	}
	
	public void setDepth(int val)
	{
		depth = val;
	}
	
	public int getDepth()
	{
		return depth; 
	}
	
	public void setRunningMax(int val)
	{
		runningMax = val; 
	}
	
	public void setLev(int ind, int val)
	{
		levRow[ind] = val;
	}
	
	public void setLcs(int ind, int val)
	{
		lcsRow[ind] = val; 
	}
	
	public int getLev(int ind)
	{
		return levRow[ind]; 
	}
	
	public int getLcs(int ind)
	{
		return lcsRow[ind]; 
	}
	
	public long getVis()
	{
		return vis; 
	}
	
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