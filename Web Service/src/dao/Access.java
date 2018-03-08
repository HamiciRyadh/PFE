package dao;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import org.apache.ibatis.session.SqlSession;

import model.Product;
 
 
public class Access

{
    public List <Product> getProducts() throws SQLException, IOException
   {
 
   SqlSession session = DBConnectionFactory.getNewSession();
   
   List<Product> products = session.selectList("QueriesProduct.getAll");
       
   session.commit();   
   session.close();	
   
   return products;
     
   
    }
	

    
    public List<Product> getProductCat(int cat ) throws Exception
    {
    	  SqlSession session = DBConnectionFactory.getNewSession();
    	 
    	   
    	   List<Product> products =session.selectList("QueriesProduct.getByCategory",cat);
    	       
    	   session.commit();   
    	   session.close();	
    	   
    	   return products;
    }
    
    public Product getProductId(int id ) throws Exception
    {
    	  SqlSession session = DBConnectionFactory.getNewSession();
    	 
    	   
    	   Product prod = session.selectOne("QueriesProduct.getById",id);
    	       
    	   session.commit();   
    	   session.close();	
    	   
    	   return prod;
    }
    
    
    public void insertProduct(Product prod) throws Exception
    {
    	  SqlSession session = DBConnectionFactory.getNewSession();
    	   
    	  
    	   session.insert("QueriesProduct.insertProduct",prod);
    	       
    	   session.commit();   
    	   session.close();	
    	   
    	   
    }
    
    public void updateProduct(Product prod) throws Exception
    {
    	
    	  SqlSession session = DBConnectionFactory.getNewSession();
    	   
    	   session.update("QueriesProduct.updateById",prod);
    	       
    	   session.commit();   
    	   session.close();	
    	   
    	 
    }
    
    public void deleteProduct(int id) throws Exception
    {
    	  SqlSession session = DBConnectionFactory.getNewSession();
    	   
    	   session.delete("QueriesProduct.deleteById",id);
    	         
    	   session.commit();   
    	   session.close();	
    	  
    }
}
