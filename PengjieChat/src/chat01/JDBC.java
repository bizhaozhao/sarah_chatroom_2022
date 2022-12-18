package chat01;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class JDBC {
	
	
	String url = "jdbc:mysql://localhost:3306/clientServer_db";
	String databaseUser = "root";
	String databasePassword = "";
	Connection connection = null;
	
	public Connection getConnection() {

		try {
			// 1.注册驱动
			Class.forName("com.mysql.cj.jdbc.Driver");// jar
			//2.获取链接对象
			connection = DriverManager.getConnection(url, databaseUser, databasePassword);
			// 3.定义SQL
			// String sql = "select * from usertable";
			// 4.获取执行sql的对象
			// Statement statement = connection.createStatement();
			// 5.执行sql
			// int count = statement.executeUpdate(sql);
			// 6.处理结果
			// 7.释放资源
			// ResultSet rs = statement.executeQuery("select * from usertable");
			// rs.close();	
			// statement.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("LogIn Error: " + e.toString());
		}
		return connection;
	}
	
	public void close() {
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
