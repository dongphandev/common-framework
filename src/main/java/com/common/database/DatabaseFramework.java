package com.common.database;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Calendar;

import javax.servlet.http.HttpServletResponse;

public class DatabaseFramework {

	public static void selectForUpdateShare(HttpServletResponse response) {

		try {
			// create a mysql database connection
			String myDriver = "com.mysql.jdbc.Driver";
			String myUrl = "jdbc:mysql://localhost/framework?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=Asia/Ho_Chi_Minh&characterEncoding=UTF-8&useSSL=false";
			Class.forName(myDriver);
			Connection conn = DriverManager.getConnection(myUrl, "dongphan", "123456");
			conn.setAutoCommit(false); // transaction block start

			// create a sql date object so we can use it in our INSERT statement
			Calendar calendar = Calendar.getInstance();
			java.sql.Date startDate = new java.sql.Date(calendar.getTime().getTime());

			// the mysql insert statement
			String query = " select srl, saledate,startdate from deal_daily_history where srl='11858' ";

			// create the mysql insert preparedstatement
			PreparedStatement preparedStmt = conn.prepareStatement(query);
			System.err.println("Begin execute in Share mode!");
			ResultSet result = preparedStmt.executeQuery();
			if (result.next()) {
				System.out.println("SRL: " + result.getInt(1));
				result.getInt(1);
			}
			Thread.sleep(60000);
			System.err.println("End execute!");
			conn.commit(); // transaction block end
			System.err.println("Transaction is commit!");
		} catch (Exception e) {
			System.err.println("Got an exception!");
			try {
				e.printStackTrace(response.getWriter());
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	public static void update(HttpServletResponse response) {

		try {
			// create a mysql database connection
			String myDriver = "com.mysql.jdbc.Driver";
			String myUrl = "jdbc:mysql://localhost/framework?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=Asia/Ho_Chi_Minh&characterEncoding=UTF-8&useSSL=false";
			Class.forName(myDriver);
			Connection conn = DriverManager.getConnection(myUrl, "dongphan", "123456");
			conn.setAutoCommit(false); // transaction block start

			// create a sql date object so we can use it in our INSERT statement
			Calendar calendar = Calendar.getInstance();
			java.sql.Date startDate = new java.sql.Date(calendar.getTime().getTime());

			// the mysql insert statement
			String query = " UPDATE  rule(rule_name, buy_srl) SET  ";

			// create the mysql insert preparedstatement
			PreparedStatement preparedStmt = conn.prepareStatement(query);
			System.err.println("Begin execute in Share mode!");
			ResultSet result = preparedStmt.executeQuery();
			if (result.next()) {
				System.out.println("SRL: " + result.getInt(1));
				result.getInt(1);
			}
			Thread.sleep(60000);
			System.err.println("End execute!");
			conn.commit(); // transaction block end
			System.err.println("Transaction is commit!");
		} catch (Exception e) {
			System.err.println("Got an exception!");
			try {
				e.printStackTrace(response.getWriter());
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	public static void insertBatch(HttpServletResponse response) {

		try {
			// create a mysql database connection
			String myDriver = "net.sf.log4jdbc.DriverSpy";
			String myUrl = "jdbc:log4jdbc:mysql://localhost/framework?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=Asia/Ho_Chi_Minh&characterEncoding=UTF-8&useSSL=false";
			Class.forName(myDriver);
			Connection conn = DriverManager.getConnection(myUrl, "dongphan", "123456");
			conn.setAutoCommit(false); // transaction block start

			// the mysql insert statement
			String query = " INSERT INTO  rule(rule_name, buy_srl) VALUES (?,?) ";
			PreparedStatement preparedStmt = conn.prepareStatement(query);
			Statement stmt = conn.createStatement();
			stmt.addBatch(query);
			for (int i = 1; i < 10; i++) {
				preparedStmt.setString(1, "Rule" + i);
				preparedStmt.setInt(2, i);
				preparedStmt.addBatch();
				if ((i % 2) == 0) {
					preparedStmt.setString(1, "loi " + 7);
					preparedStmt.setInt(2, 5);
					preparedStmt.addBatch();
					int[] x = preparedStmt.executeBatch();
					String s = "";
					for(int xx : x) {
						s = s + "" +x;
					}
					System.out.println("******Result: " +s );
					Thread.sleep(5000);
				}
			}
			conn.commit();
		} catch (Exception e) {
			System.err.println("Got an exception!");
			try {
				e.printStackTrace(response.getWriter());
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

}
