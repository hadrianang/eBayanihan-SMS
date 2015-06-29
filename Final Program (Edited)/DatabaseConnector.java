import java.sql.*;

public class DatabaseConnector {
	String db_name, user, pass;
	final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	Connection conn = null;
	Statement stmt = null;

	public DatabaseConnector(String dbn, String user, String pass){
		db_name = dbn;
		String DB_URL = "jdbc:mysql://localhost/" + db_name;
		
		try {
			Class.forName(JDBC_DRIVER);
			if(!user.equals("")) conn = DriverManager.getConnection(DB_URL,user,pass);
			stmt = conn.createStatement();
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	public ResultSet query(String q) throws Exception{
		return stmt.executeQuery(q);
	}

	public void update(String condition) throws Exception{
		stmt.executeUpdate(condition);
	} 

	public void exit(){
		try{
			stmt.close();
			conn.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

}