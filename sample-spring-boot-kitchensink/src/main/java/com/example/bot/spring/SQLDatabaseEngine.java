package com.example.bot.spring;

import lombok.extern.slf4j.Slf4j;
import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.*;
import java.net.URISyntaxException;
import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class SQLDatabaseEngine extends DatabaseEngine {
	private String databaseName = "chatbot";
	private String [] col = {"keyword", "response", "hit"};

	@Override
	String search(String text) throws Exception {
		//Write your code here
		// NON partial
		/*
		try {
			Connection connection = this.getConnection();
			PreparedStatement stmt = connection.prepareStatement("SELECT response FROM chatbot WHERE keyword LIKE concat('%', ?, '%')");
			stmt.setString(1,  text);
			ResultSet rs = stmt.executeQuery();
			if(!rs.next()) 
				throw new Exception("NOT FOUND");
			else {
				String result = rs.getString(1);
				rs.close();
				stmt.close();
				connection.close();
				return result;
			}
		} catch (Exception e) {
			System.out.print(e);
		}
		*/
		// partial version
		String result = null;
		try {
			Connection connection = this.getConnection();
			//PreparedStatement stmt = connection.prepareStatement("SELECT " + col[0] + " FROM " + databaseName);
			PreparedStatement stmt = connection.prepareStatement("SELECT * FROM chatbot");
			ResultSet rs = stmt.executeQuery();
			if(!rs.next()) 
				throw new Exception("NOT FOUND");
			else do {
				String keyword = rs.getString("keyword");
				Matcher m = Pattern.compile(keyword.toLowerCase()).matcher(text.toLowerCase());
				if (m.find()) {
					
					//try {rs.updateInt(3, rs.getInt(3) + 1);} catch (SQLException e) {throw e;}
					int time = rs.getInt("hit") + 1;
					//rs.updateInt("hit", time);
					//rs.updateRow();
					//result = rs.getString("response") + ". You have hit this keyword for " + time + " time(s)!";
					//result = "." + rs.getInt("hit");
					result = rs.getString("response");
					rs.close(); stmt.close(); connection.close();
					return result;
				}
			} while(rs.next());
			rs.close(); stmt.close(); connection.close();

		} catch (Exception e) {
			System.out.println(e);
		}
		//return null;
		if (result != null)
			return result;
		throw new Exception("NOT FOUND");
	}
	
	
	private Connection getConnection() throws URISyntaxException, SQLException {
		Connection connection;
		URI dbUri = new URI(System.getenv("DATABASE_URL"));

		String username = dbUri.getUserInfo().split(":")[0];
		String password = dbUri.getUserInfo().split(":")[1];
		String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath() +  "?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory";

		log.info("Username: {} Password: {}", username, password);
		log.info ("dbUrl: {}", dbUrl);
		
		connection = DriverManager.getConnection(dbUrl, username, password);

		return connection;
	}

}
