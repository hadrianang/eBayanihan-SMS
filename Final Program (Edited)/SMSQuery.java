import java.sql.*;
import java.util.*;
import java.net.*;
import java.io.*;

public class SMSQuery {

	final static int THRESHOLD = 3;
	final static String JDBC_DRIVER = "org.postgresql.Driver";
	final static String DB_URL = "jdbc:postgresql://staging.ebayanihan.ateneo.edu:5432/stg_ebayanihan";
	final static String db_name = DB_URL + "stg_ebayanihan";
	final static String user = "devuser0";
	final static String pass = "devuser0";
	static Connection conn1 = null;
	static Statement stmt1 = null;

	static WordSegmentation ws;
	static Formatter form;
	static ArrayDeque<Integer> messageQueue;

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
					if(httpPost(smsID))
						stmt1.executeUpdate("UPDATE receivedsms SET is_processed = \'processed\' WHERE id = "+ smsID +";");
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
	
	public static boolean httpPost(int id) throws Exception{
		URL url = new URL("http://staging.ebayanihan.ateneo.edu/smsreport");
		ResultSet rs = stmt1.executeQuery("SELECT * FROM raw_sms_messages WHERE id = " + id +";");
        Map<String,Object> params = new LinkedHashMap<>();
        rs.next();
        String message = form.format(ws.wordBreak(rs.getString(3).trim().toLowerCase(), THRESHOLD));
        // System.out.println(Arrays.toString(ws.wordBreak(rs.getString(6).trim().toLowerCase(),THRESHOLD)));
        // System.out.println("LOOOOOOOOOK: " + ws.wordBreak(rs.getString(6).trim(),3).length);
		System.out.println(message);
		String[] tokens = message.split(",");
		// params.put("receiveddate", rs.getString(2));
		// params.put("receivedtime", rs.getString(3));
		params.put("received_at","");
		params.put("keyword", tokens[1]);
		params.put("urgency", tokens[2]);
		params.put("sendermobile", rs.getString(2));
		params.put("location", tokens[3] + " " + tokens[4]);
		params.put("remarks", tokens[5]);
		params.put("source","");

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
        for ( int c = in.read(); c != -1; c = in.read() ) System.out.print((char)c);

        return true; 
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
					String toProcess = "SELECT * FROM receivedsms";
					ResultSet rs = stmt.executeQuery(toProcess);
					while(rs.next()) {
						String status = rs.getString(12);
						if(status == null||status.equals("unprocessed")){
							// System.out.println(rs.getString(1)+" "+rs.getString(2));
							// messageQueue.offer(new SMSobject(Integer.parseInt(rs.getString(1)),rs.getString(2)));
							messageQueue.offer(Integer.parseInt(rs.getString(1)));						
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