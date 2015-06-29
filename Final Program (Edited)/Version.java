import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.net.URLDecoder;
public class Version {
	final static int THRESHOLD = 3; 
    public static void main(String[] args)throws Exception {

		Class.forName("org.postgresql.Driver");
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
		

		//real
        //String url = "jdbc:postgresql://staging.ebayanihan.ateneo.edu:5432/stg_ebayanihan";
		//local
        String url = "jdbc:postgresql://localhost:5432/stg_ebayanihan";
        String user = "devuser0";
        String password = "devuser0";
		WordSegmentation ws = new WordSegmentation(); 
		Formatter form = new Formatter(); 
        try {
            con = DriverManager.getConnection(url, user, password);
            st = con.createStatement();
            rs = st.executeQuery("SELECT * FROM rawsms");
			
			String toProcess = ""; 
            while(rs.next()) {
                toProcess = rs.getString(2); 
				String message = getMessage(toProcess).toLowerCase(); 
				String corrected = form.format(ws.wordBreak(message, THRESHOLD)); 
				System.out.println(getMobile(toProcess) + corrected);
				//System.out.println(getMessage(toProcess) + getMobile(toProcess));
				
				String status = rs.getString(4); 
            }
			

        } catch (SQLException ex) {
            Logger lgr = Logger.getLogger(Version.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);

        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (st != null) {
                    st.close();
                }
                if (con != null) {
                    con.close();
                }

            } catch (SQLException ex) {
                Logger lgr = Logger.getLogger(Version.class.getName());
                lgr.log(Level.WARNING, ex.getMessage(), ex);
            }
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