import java.sql.*;
import java.util.*;
import java.net.*;

public class SMSQuery {

	final static int THRESHOLD = 3;
	final static String JDBC_DRIVER = "org.postgresql.Driver";
	final static String DB_URL = "jdbc:postgresql://localhost:5432/";
	final static String db_name = DB_URL + "stg_ebayanihan";
	final static String user = "devuser0";
	final static String pass = "devuser0";
	// final static long interval = 15000;

	static WordSegmentation ws;
	static Formatter form;
	static ArrayDeque<SMSobject> messageQueue;

	public static void main(String[] args)throws Exception {
		try {
			Class.forName(JDBC_DRIVER);
		} catch(Exception e){
			e.printStackTrace();
		}
		ws = new WordSegmentation(); 
		form = new Formatter();
		messageQueue = new ArrayDeque<SMSobject>();
		//start threads
		Thread t = new Thread(new QueryThread());
		t.start();
		// while(true){}
		Connection conn1 = null;
		Statement stmt1 = null;
		
		try{
			conn1 = DriverManager.getConnection(db_name,user,pass);
			stmt1 = conn1.createStatement();
			while(true){
				
				while(messageQueue.size()>0){
					System.out.println("INSIDE");
					SMSobject sms = messageQueue.poll();
					String toProcess = sms.message;
					String message = getMessage(toProcess).toLowerCase(); 
					String corrected = form.format(ws.wordBreak(message, THRESHOLD));
					String mobile =getMobile(toProcess);
					//if success
					System.out.println("LOLOLOL "+corrected);
					stmt1.executeUpdate("UPDATE rawsms SET is_processed = \'processed\' WHERE id = "+ sms.id +";");
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
	
	public static String getMessage(String query) throws Exception
	{
		StringBuilder sb = new StringBuilder();
		URLDecoder decoder = new URLDecoder();
		String g = query;
		int start = g.indexOf("&message=") + 9;
		int end = g.indexOf("&shortcode=");
		String result = g.substring(start,end);
		String result2 = decoder.decode(result,"UTF-8");
		sb.append(result2+"\n");
		return sb.toString();
	}
	
	public static String getMobile(String query) throws Exception
	{
		StringBuilder sb = new StringBuilder();
		URLDecoder decoder = new URLDecoder();
		String g = query; 
		int start = g.indexOf("&mobile_number=") + 15;
		int end = g.indexOf("&message=");
		String result = g.substring(start,end);
		String result2 = decoder.decode(result,"UTF-8");
		sb.append(result2+"\n");
		return sb.toString();
	}
	
	public static boolean httpPost(){
		URL url = new URL("http://example.net/new-message.php");
        Map<String,Object> params = new LinkedHashMap<>();
        params.put("receiveddate", "Freddie the Fish");
        params.put("receivedtime", "Freddie the Fish");
        params.put("received_at", "Freddie the Fish");
        params.put("keyword", "Freddie the Fish");
        params.put("urgency", "Freddie the Fish");
        params.put("sendermobile", "Freddie the Fish");
        params.put("location", "Freddie the Fish");
        params.put("remarks", "Freddie the Fish");
        params.put("source", "Freddie the Fish");

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

        Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
        for ( int c = in.read(); c != -1; c = in.read() ) System.out.print((char)c);
	}
	
	static class QueryThread implements Runnable{
		Connection conn;
		Statement stmt;
		
		public void run(){
			try{
				//System.out.println("HELLO1");
				conn = DriverManager.getConnection(db_name,user,pass);
				stmt = conn.createStatement();
				while(true){
					//System.out.println("HELLO");
					String toProcess = "SELECT * FROM rawsms";
					ResultSet rs = stmt.executeQuery(toProcess);
					while(rs.next()) {
						String status = rs.getString(4); 
						if(status == null||status.equals("unprocessed")){
							// System.out.println(rs.getString(1)+" "+rs.getString(2));
							messageQueue.offer(new SMSobject(Integer.parseInt(rs.getString(1)),rs.getString(2)));						
						}
						else if(status.contains("processed")) continue;
					}
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