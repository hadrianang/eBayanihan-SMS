import java.sql.*;
import java.util.*;
import java.util.regex.*;
import java.net.*;
import java.io.*;

/**
* The <tt>SMSQuery</tt> class is responsible for the database access. It initializes two 
* threads. The first thread queries the database for the SMS messages and stores them in
* a queue every 15 secs. The second thread calls the <tt>wordBreak</tt> method on the 
* messages to correct them and then sends them to the server via HTTP POST. Depending on
* the reply of the server, it updates the row of the message as either "processed-correct"
* or "processed-incorrect". These two threads run indefinitely until the processes are
* terminated.
*/
public class SMSQuery {

	final static int THRESHOLD = 3;
	final static String JDBC_DRIVER = "org.postgresql.Driver";
	//final static String DB_URL = "jdbc:postgresql://localhost:5432/";   
	final static String DB_URL = "jdbc:postgresql://staging.ebayanihan.ateneo.edu:5432/";
	final static String db_name = DB_URL + "stg_ebayanihan";
	final static String user = "devuser0";
	final static String pass = "devuser0";
	static Connection conn1 = null;
	static Statement stmt1 = null;
	static String regextext = "post [a-z ]*,[a-z ]*,[-a-z0-9.\' ]*,[-a-z0-9.\' ]*(,[-a-z0-9 !@#$%^&*()_+`=;:\"\'<>/]*)?";
    static Pattern pat = Pattern.compile(regextext, Pattern.CASE_INSENSITIVE);

	static WordSegmentation ws;
	static Formatter form;
	static ArrayDeque<Integer> messageQueue;

	/**
	* The main method instantiates the QueryThread and acts as the ParserThread.
	*/
	public static void main(String[] args)throws Exception {
		try {
			Class.forName(JDBC_DRIVER);
		} catch(Exception e){
			e.printStackTrace();
		}
		ws = new WordSegmentation(); 
		form = new Formatter();
		messageQueue = new ArrayDeque<Integer>();
		
		Thread t = new Thread(new QueryThread());
		t.start();
		
		try{
			conn1 = DriverManager.getConnection(db_name,user,pass);
			stmt1 = conn1.createStatement();
			while(true){
				while(messageQueue.size()>0){
					int smsID = messageQueue.poll();
					if(httpPost(smsID))	stmt1.executeUpdate("UPDATE raw_sms_messages SET is_processed = \'processed-correct\' WHERE id = "+ smsID +";");
					else stmt1.executeUpdate("UPDATE raw_sms_messages SET is_processed = \'processed-incorrect\' WHERE id = "+ smsID +";");
				}
				Thread.sleep(1000);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try{
				stmt1.close();
				conn1.close();
			}
			catch(Exception e){}
		}
	}
	
	/**
	* This method calls the wordBreak method of the WordSegmentation class on the SMS message
	* referenced by its ID in the database. After the SMS is corrected, an HTTP POST request
	* is sent to the server. The method returns if the corrected message is OK or not.
	* 
	* @param id The row ID of the SMS message from the sms table in the database.
	* @return Returns true if the server accepted the "corrected" string; false if otherwise.
	*/
	public static boolean httpPost(int id) throws Exception{
		URL url = new URL("http://staging.ebayanihan.ateneo.edu/smsreport");
		ResultSet rs = stmt1.executeQuery("SELECT * FROM raw_sms_messages WHERE id = " + id +";");
        Map<String,Object> params = new LinkedHashMap<>();
        rs.next();
        String message = form.format(ws.wordBreak(rs.getString(3).trim().toLowerCase(), THRESHOLD));
        Matcher mat = pat.matcher(message);
        System.out.println(message);
        //System.out.println(mat.find());
        if(!mat.find()) 
    		return false;
        // System.out.println(Arrays.toString(ws.wordBreak(rs.getString(6).trim().toLowerCase(),THRESHOLD)));
        // System.out.println("LOOOOOOOOOK: " + ws.wordBreak(rs.getString(6).trim(),3).length);k
		
		String[] tokens = message.split(",");
		String[] par = {"keyword","urgency","location","remarks"};
		String[] timeStamp = rs.getString(4).split(" ");
		// String[] fields = new String[params.length];
		// fields[0] = timeStamp[0];
		// fields[0] = timeStamp[1];
		try{
			params.put("receiveddate", timeStamp[0]);
			params.put("receivedtime", timeStamp[1]);
			params.put("sendermobile", rs.getString(2));
			params.put("received_at","");
			params.put("source","");
			int count = 0;
			for(int i=0; i<4; i++)
			{
				String temp = "";

				if(i<tokens.length)
				{
					if(i==0)
					{
						String[]hold = tokens[i].split(" "); 
						tokens[i] = hold[1];
					}
					temp = tokens[i]; 
				} 
				// if(i==2)
				// {
				// 	i++;
				// 	if(i<tokens.length)
				// 		temp += " " + tokens[3];
				// }
				params.put(par[count],temp); 
				count++; 
			}
		}catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("Error creating POST map");
		}
		// 0 1 2 2 3
		// 1 2 3 4 5
		// params.put("keyword", tokens[1]);
		// params.put("urgency", tokens[2]);
		// params.put("location", tokens[3] + " " + tokens[4]);
		// params.put("remarks", tokens[5]);
		

        System.out.println(params.get("receiveddate") + " " + params.get("receivedtime") + " " + params.get("received_at"));
        System.out.println(params.get("keyword") + " " + params.get("urgency") + " "+ params.get("sendermobile") + " " + params.get("location")); 
        
        StringBuilder postData = new StringBuilder();
        for (Map.Entry<String,Object> param : params.entrySet()) {
            if (postData.length() != 0) postData.append('&');
            postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
            postData.append('=');
            postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
        }
        byte[] postDataBytes = postData.toString().getBytes("UTF-8");

        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
        conn.setDoOutput(true);
        conn.getOutputStream().write(postDataBytes);
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
        StringBuilder sb = new StringBuilder(); 
        for ( int c = in.read(); c != -1; c = in.read() ) sb.append((char)c);
        	System.out.println(sb);
        HashMap<String, String> parsed = parseJson(sb);
        //printMap(parsed);
        boolean ret = false;
        if(parsed.get("status").equals("OK")) ret = true;
        System.out.println(ret);
        return ret;
	}

	//debug function
	/**
	* This is a method used for debugging the intermediate steps in the HTTP POST.
	* It prints the contents of the map.
	* 
	* @param mp The map whose contents will be printed.
	*/
	public static void printMap(Map mp) {
	    Iterator it = mp.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry)it.next();
	        System.out.println(pair.getKey() + " = " + pair.getValue());
	        it.remove(); // avoids a ConcurrentModificationException
	    }
	}

	/**
	* This method was implemented to easil parse JSON strings into their field-value mappings.
	* 
	* @param sb The JSON string in a StringBuilder object.
	* @return Returns a HashMap containing the field-value mappings in the JSON text.
	*/
	static HashMap<String, String> parseJson(StringBuilder sb)
	{
		HashMap<String, String> ret = new HashMap<String, String>();
		int curr = 0; 
		while(true)
		{
			int start = sb.indexOf("\"",curr);
			if(start==-1) break;
			curr=start+1; 
			int end = sb.indexOf("\"",curr);
			String key = sb.substring(start+1,end);
			curr=end+1; 

			start = sb.indexOf("\"",curr);
			curr=start+1; 
			end = sb.indexOf("\"", curr);
			curr=end+1; 

			String val = sb.substring(start+1,end); 

			ret.put(key,val);
		}
		return ret; 
	}
	/**
	* The thread implementation to query SMS messages from the database.
	*/
	static class QueryThread implements Runnable{
		Connection conn;
		Statement stmt;

		public void run(){
			try{
				conn = DriverManager.getConnection(db_name,user,pass);
				stmt = conn.createStatement();
				while(true){
					String toProcess = "SELECT * FROM raw_sms_messages WHERE is_processed = \'unprocessed\'";
					ResultSet rs = stmt.executeQuery(toProcess);
					while(rs.next()) {
						String status = rs.getString(6);
						if(status == null||status.equals("unprocessed")){
							int smsID = Integer.parseInt(rs.getString(1));
							messageQueue.offer(smsID);
							conn.createStatement().executeUpdate("UPDATE raw_sms_messages SET is_processed = \'processing\' WHERE id = "+ smsID +";");
						}
						else if(status.contains("processed")) continue;
					}
					System.out.println("Waiting for new rows");
					Thread.sleep(15000);//15 secs sleep for now
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			finally {
				try{
					stmt.close();
					conn.close();
				}
				catch(Exception e){}
			}
		}
	}
}

class SMSobject{
	int id;
	String message;

	public SMSobject(int i, String m){
		id = i;
		message = m;
	}
}