
package com.lfbservices.pfe.dao;

import java.sql.Connection;
import java.sql.DriverManager;

public class Database

{

	public Database() {
	}

	public Connection getConnection() throws Exception {
		try {
			String connectionURL = "jdbc:postgresql://localhost:5432/LFB";
			Connection connection = null;
			Class.forName("org.postgresql.Driver").newInstance();
			connection = DriverManager.getConnection(connectionURL, "postgres", "postgres");
			return connection;
		} catch (Exception e) {
			throw e;
		}

	}

}
