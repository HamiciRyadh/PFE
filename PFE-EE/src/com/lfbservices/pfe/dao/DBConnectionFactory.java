package com.lfbservices.pfe.dao;

import java.io.Reader;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

/**
 * This class handles the connection to the system's database.
 */
public class DBConnectionFactory {
	
	private static SqlSessionFactory sqlSessionFactory;
	
	/**
	 * This code initializes the sqlSessionFactory object for the system's database using
	 * the MyBatis configuration XML file.
	 */
	static {
		try {
			Reader reader=Resources.getResourceAsReader("Config.xml");
			sqlSessionFactory=new SqlSessionFactoryBuilder().build(reader);
			reader.close();
		} catch(Exception e) {	
			e.printStackTrace();
		}
	}
	
	/**
	 * This method opens a session for the system's database and returns it.
	 * @return A session for the system's database.
	 */
	public static SqlSession getNewSession(){
		return sqlSessionFactory.openSession();
	}
}