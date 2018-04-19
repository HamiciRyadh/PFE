package com.lfbservices.pfe.dao;

import java.io.Reader;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

public class DBConnectionFactory {
	
	private static SqlSessionFactory sqlSessionFactory;
	
	static
	
	{
		try {
			Reader reader=Resources.getResourceAsReader("Config.xml");
			
			sqlSessionFactory=new SqlSessionFactoryBuilder().build(reader);
			reader.close();
			
			
		} catch(Exception e) {
			
			e.printStackTrace();
		}
		
		
	}
	
	
	public static SqlSession getNewSession(){
		return sqlSessionFactory.openSession();
	}
	
	
	

}