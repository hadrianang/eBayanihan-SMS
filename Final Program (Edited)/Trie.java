import java.io.*;
import java.util.*;
/**
* The <tt>Trie</tt> class is the underlying data structure for the
* dictionary of the system. It is composed of {@link Node} objects
* in a special tree structure that allows quicker dictionary search
* and retrieval.
* <p> The <tt>Trie</tt> structure executes fuzzy searching in the 
* dictionary by using Dynamic Programming (DP) algorithms and
* memoization tables to reuse computation results from previous
* substrings. The tree structure further optimizes this DP approach
* by allowing the memoization table to be split up into rows. We store
* each of these rows in each <tt>Node</tt> object.
*/
public class Trie
{
	private Node[] graph; 
	private Node root; 
	private static long visCount; 
	private static ArrayList<Node> words;
	private static double maxScore; 
	private static int index;
	private static Node cutPoint; 
	private static double alpha = 1.0;
	private static int maxLCS; 
	private static int minEdit;

	/**
	* Creates a Trie object using a Node array.
	* 
	* @param graph The Node array that contains the graph of the 
	*  Node objects that make up the Trie
	*/
	public Trie(Node[] graph)
	{
		visCount = -1; 
		this.graph = graph;
		root = graph[0];
		words = null; 
		index = 0; 
		cutPoint = root;
	}
	/**
	* Resets the visited counter to its default value.
	*/
	private void resetViscount()
	{
		visCount = -1; 
	}

	/**
	* The Trie's implmentation of exact string matching in a dictionary.
	* 
	* @param query The search string that will be searched in the Trie.
	* @return True if the query string was found in the Trie; False if
	*  otherwise.
	*/
	public boolean contains(String query)
	{
		Stack<Node> stack = new Stack<Node>(); 
		Stack<Integer> ind = new Stack<Integer>();
		stack.push(root); 
		ind.push(0); 
		
		while(!stack.isEmpty())
		{
			Node u = stack.pop();
			int curr = ind.pop(); 
			if(curr>=query.length()) continue;
			Iterator<Node> iter = u.getChildIterator(); 
			while(iter.hasNext())
			{
				Node v = iter.next(); 
				if(v.getName()==query.charAt(curr))
				{
					if(curr==query.length()-1)
					{
						Iterator<Node> it = v.getChildIterator(); 
						while(it.hasNext())
						{
							Node child = it.next();
							if(child.getName()=='$')
								return true; 
						}
					}
					
					stack.push(v);
					ind.push(curr+1); 
				}
			}
		}
		
		return false; 
	}
	
	/**
	* This method is used to compute the values of the DP memoization
	* tables in the Node objects given a query string. The algorithm
	* is optimized by pruning the search and computation space by
	* providing a threshold value so that the program does not compute
	* the values for substrings that are "too far" from the query string.
	* The output of this method is the cut point Node and string index of
	* the word in the dictionary that contains the highest score.
	* 
	* @param query The query string to be used.
	* @param threshold The threshold value which will limit the computation.
	* @return An object array containing the cut point Node and string index.
	*/
	public Object[] computeDP(String query, int threshold)
	{
		maxScore = -1; 
		maxLCS = Integer.MIN_VALUE;
		minEdit = Integer.MAX_VALUE;
		cutPoint = root; 
		Object[] ret = new Object[2]; 
		boolean collect = false; 
		if(words==null)
		{
			collect = true;
			words = new ArrayList<Node>(); 
		}
		double currMax = Integer.MIN_VALUE;
		int len = query.length()+1; 
		
		Stack<Node> stack = new Stack<Node>(); 
		root.setVis(visCount); 
		root.createLevRow(len); 
		root.createLcsRow(len); 
		for(int i=0; i<len; i++)
			root.setLev(i,i); 
		stack.push(root); 
		
		while(!stack.isEmpty())
		{
			Node u = stack.pop(); 
			
			Iterator<Node> iter = u.getChildIterator(); 
			while(iter.hasNext())
			{
				Node v = iter.next();
				v.setDepth(u.getDepth()+1); 
				v.setParent(u); 
				if(v.getDepth()>query.length()+threshold) continue;
				if(v.getVis()!=visCount && v.getName()!='$')
				{
					v.createLevRow(len);
					v.createLcsRow(len); 
					
					ret = computeArray(u,v,query); 
					int lev = v.getLev(query.length()); 
					stack.push(v); 
				}
				if(v.getName()=='$' && collect)
					words.add(v.getParent()); 
			}
		}
		visCount++; 
		return ret; 
	}

	/**
	* This method "backtracks" up the Trie to rebuild the dictionary
	* word that is represented by the Node sequence.
	* 
	* @param id The ID of the Node object at the end of the word.
	* @return The rebuilt String that the Node sequence represents.
	*/
	public String backTrack(int id)
	{
		StringBuilder sb =  new StringBuilder(); 
		Node start = graph[id]; 
		Node curr = start; 
		while(curr.getParent() != null)
		{
			sb.append(curr.getName()); 
			curr = curr.getParent(); 
		}
		return sb.reverse().toString(); 
	}
	
	/**
	* This method computes the individual DP memoization arrays of a
	* certain Node object given its parent Node and the query string.
	* The computeArray method returns the cut point Node and string 
	* index with the highest score.
	* 
	* @param par The parent Node object.
	* @param child The child Node whose DP tables will be computed.
	* @param query The query string needed for the computation.
	* @return An Object array containing the cut point Node and string
	*  index.
	*/
	private Object[] computeArray(Node par, Node child, String query)
	{
		int[] parLcs = par.getLcsRow(); 
		int[] parLev = par.getLevRow(); 
		Object[] ret = new Object[2]; 
		int parInd = parLev[0]+1; 
		child.setLev(0,parInd);
		//int[] childLcs = child.getLcsRow(); 
		
		//Compute LCS
		for(int i=1; i<parLcs.length; i++)
		{
			int querChar = i-1;
			if(query.charAt(querChar) == child.getName()) child.setLcs(i,parLcs[i-1]+1); 
			else child.setLcs(i,Math.max(parLcs[i],child.getLcs(i-1)));
			
			int cost = 0;
			
			if(query.charAt(querChar) != child.getName()) cost=1; 
			
			int b1 = parLev[i-1] + cost; 
			int b2 = parLev[i] + 1; 
			int b3 = child.getLev(i-1) + 1; 
			child.setLev(i, Math.min(Math.min(b1,b2),b3));
			
			
			if((parInd>1) && (i>1) && (child.getName()==query.charAt(querChar-1) && 
			par.getName()==query.charAt(querChar)))
				child.setLev(i,Math.min(child.getLev(i), par.getParent().getLev(querChar-1)+cost)); 
				
			if(child.isWord())
			{
				int lcs = child.getLcs(i); 
				int lev = child.getLev(i); 
				double sc = score(lev,lcs); 
				//boolean in = false; 
				if(sc>=maxScore)
				{
					//System.out.println(query + " " + backTrack(child.getId()) + " " + backTrack(cutPoint.getId()) + " " + sc + " " + maxScore + " " + (i-1));
					if(sc==maxScore && lcs==maxLCS && lev==minEdit && i<=index)
						continue;
					
					maxLCS = lcs;
					minEdit = lev;
					maxScore = sc; 
					index = i; 
					cutPoint = child;
					System.out.println(query + " " + backTrack(child.getId()) + " " + backTrack(cutPoint.getId()) + " " + sc + " " + maxScore + " " + index);
				}
				
			}
		}
		ret[0] = cutPoint;
		ret[1] = index; 
		return ret;
	}
	
	/**
	* This method defines the scoring of each substring based on its
	* Edit Distance and LCS value with a query string.
	* 
	* @param edit The computed Edit Distance of the substring.
	* @param lcs The computed LCS value of the substring.
	* @return The score that will be assigned to the substring.
	*/
	public static double score(int edit, int lcs){
        double ret = (alpha*lcs)/(double)edit;
        return ret;
    }
}