import java.io.*;
import java.util.*;

public class TrieAlt
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
	public TrieAlt(Node[] graph)
	{
		visCount = -1; 
		this.graph = graph;
		root = graph[0];
		words = null; 
		index = 0; 
		cutPoint = root;
	}
	
	private void resetViscount()
	{
		visCount = -1; 
	}	
	
	public Integer[] getMaxMin(){
		Integer[] ret = new Integer[2];
		int min = Integer.MAX_VALUE;
		int max = Integer.MIN_VALUE;
		ArrayDeque<Node> queue = new ArrayDeque<Node>();
		root.setDepth(0);
		queue.offer(root);
		while(queue.size()>0){
			Node cur = queue.poll();
			int depth = cur.getDepth();
			if(cur.getVis()==visCount) continue;
			if(cur.getName()=='$'){
				if(depth<min) min = depth;
				if(depth>min) max = depth;
			}
			Iterator<Node> iter = cur.getChildIterator();
			while(iter.hasNext()){
				Node temp = iter.next();
				temp.setDepth(depth+1);
				queue.offer(temp);
			}
		}
		visCount++;
		ret = new Integer[2];
		ret[0] = min;
		ret[1] = max;
		return ret;
	}

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
	
	public static ArrayList<Node> getWords()
	{
		return words; 
	}

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
					index = i-1; 
					cutPoint = child;
					//System.out.println(query + " " + backTrack(child.getId()) + " " + backTrack(cutPoint.getId()) + " " + sc + " " + maxScore + " " + (i-1));
				}
				
			}
		}
		ret[0] = cutPoint;
		ret[1] = index; 
		return ret;
	}
	
	public static double score(int edit, int lcs){
        double ret = (alpha*lcs)/(double)edit;
        return ret;
    }
}