package dao;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;

import model.Product;
import model.ProductSalesPoint;
import model.SalesPoint;
import model.User;

public class Access {
	
	/********************************	Queries for Products		*************************************/

	public static List<Product> getProducts() throws SQLException, IOException {

		SqlSession session = DBConnectionFactory.getNewSession();

		List<Product> listProducts = session.selectList("QueriesProduct.getAllProducts");

		session.commit();
		session.close();

		return listProducts;

	}

	public static List<Product> getProductsByCategory(final int categoryId) throws Exception {
		SqlSession session = DBConnectionFactory.getNewSession();

		List<Product> listProducts = session.selectList("QueriesProduct.getProductsByCategory", categoryId);

		session.commit();
		session.close();

		return listProducts;
	}

	public static Product getProductById(final int id) throws Exception {
		SqlSession session = DBConnectionFactory.getNewSession();

		Product product = session.selectOne("QueriesProduct.getProductById", id);

		session.commit();
		session.close();

		return product;
	}

	public static List<Product> getProductsByName(final Map<String,Object> value) throws Exception {
		SqlSession session = DBConnectionFactory.getNewSession();

		
		List<Product> listProducts = session.selectList("QueriesProduct.getProductsByName", value);

		session.commit();
		session.close();

		return listProducts;
	}
	
	public static void insertProduct(final Product product) throws Exception {
		SqlSession session = DBConnectionFactory.getNewSession();

		session.insert("QueriesProduct.insertProduct", product);

		session.commit();
		session.close();

	}

	public static void updateProduct(final Product product) throws Exception {

		SqlSession session = DBConnectionFactory.getNewSession();

		session.update("QueriesProduct.updateProductById", product);

		session.commit();
		session.close();

	}

	public static void deleteProduct(final int productId) throws Exception {
		SqlSession session = DBConnectionFactory.getNewSession();

		session.delete("QueriesProduct.deleteProductById", productId);

		session.commit();
		session.close();

	}

	/********************************	Queries for SalesPoints		*************************************/

	
	public static List<ProductSalesPoint> getSalesPointQte(final int productId) throws Exception {
		SqlSession session = DBConnectionFactory.getNewSession();

		List<ProductSalesPoint> listProductSalesPoint = session.selectList("QueriesProductSalesPoint.getSalesPointsAndQte",
				productId);

		session.commit();
		session.close();

		return listProductSalesPoint;
	}

	public static SalesPoint getSalesPointById(final String salesPointId) throws Exception {
		SqlSession session = DBConnectionFactory.getNewSession();

		SalesPoint salesPoint = session.selectOne("QueriesSalesPoint.getSalesPointById", salesPointId);

		session.commit();
		session.close();

		return salesPoint;
	}


	public static void updateSalesPoint(final SalesPoint salesPoint) throws Exception {

		SqlSession session = DBConnectionFactory.getNewSession();

		session.update("QueriesSalesPoint.updateSalesPointById", salesPoint);

		session.commit();
		session.close();

	}
	
	/********************************	Queries for Users	*************************************/
	
	public static boolean connect(final Map<String,Object> parameters) {
		SqlSession session = DBConnectionFactory.getNewSession();
		
		boolean exists = session.selectOne("QueriesUsers.userExists", parameters);

		session.commit();
		session.close();

		return exists;
	}
	
	
	public static boolean addUser(User user) {
		SqlSession session = DBConnectionFactory.getNewSession();
		
		int result = session.insert("QueriesUsers.addUser");

		session.commit();
		session.close();

		return result == 1;
	}
	
	
	public static List<User> getAllUsers() {
		SqlSession session = DBConnectionFactory.getNewSession();
		
		List<User> users = session.selectList("QueriesUsers.getAllUsers");

		session.commit();
		session.close();

		return users;
	}
}
