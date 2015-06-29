import java.sql.*;
import java.util.*;
import java.net.*;
public class SMSQuery {

	final static int THRESHOLD = 3;
	// final static long interval = 15000;

	public static void main(String[] args)throws Exception {

		DatabaseConnector dbc = new DatabaseConnector("stg_ebayanihan", "devuser0", "devuser0");
		WordSegmentation ws = new WordSegmentation(); 
		Formatter form = new Formatter();
		try{
			String toProcess = "SELECT * FROM rawsms";
			ResultSet rs = dbc.query();
			// long startTime = System.currentTimeMillis();
			while(rs.next()) {
				// long curTime = System.currentTimeMillis();
				// if(curTime-startTime<interval) continue;
				String status = rs.getString(4); 
				if(status.contains("processed")) continue;
				// dbc.update();
				toProcess = rs.getString(2); 
				String message = getMessage(toProcess).toLowerCase(); 
				String corrected = form.format(ws.wordBreak(message, THRESHOLD)); 
				System.out.println(getMobile(toProcess) + corrected);
				//System.out.println(getMessage(toProcess) + getMobile(toProcess));
				// Thread.sleep(15000);//not sure if this will suffice
			}
		}
		catch (SQLException ex) {}
		finally {
			dbc.exit();
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
}