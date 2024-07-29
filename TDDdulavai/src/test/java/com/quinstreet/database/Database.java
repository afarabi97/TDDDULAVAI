package com.quinstreet.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.jayway.restassured.RestAssured;

public class Database {
	private Connection getConnection() throws SQLException {
		String connectionUrl = null;
		String username = null;
		String pwd = null;
		if ("http://test.insurance.com".equalsIgnoreCase(RestAssured.baseURI)) {
			connectionUrl = "jdbc:sqlserver://ins-stage-db2.echo.quinstreet.net:1433;DatabaseName=Runtime_Test";
			username = "icom_automation";
			pwd = "C5tu$7MeTSh%u?!P6Qr3F8RA$r_w^$3L";
		} else {
			connectionUrl = "jdbc:sqlserver://ins-stage-db3.sf.quinstreet.net:1433;DatabaseName=Runtime_QA";
			username = "icom_automation";
			pwd = "vwRUHvvzd_C*yMh8#F_wmVA5WP-KS3Uy";
		}
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return DriverManager.getConnection(connectionUrl, username, pwd);
	}

	public String getRequestXML(String sessionId, String transportId) {
		try (Connection connection = getConnection(); Statement statement = connection.createStatement();) {
			String selectSql = "select debug_xml from application_debug_xml where transport_id ='" + transportId
					+ "' and event_type = 'rate1' and debug_xml_type = 'request' and content_type = 'XML' and "
					+ "session_guid = '" + sessionId + "' order by event_dtm desc";
			System.out.println(selectSql);
			ResultSet resultSet = statement.executeQuery(selectSql);
			while (resultSet.next()) {
				return resultSet.getString(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	
}


