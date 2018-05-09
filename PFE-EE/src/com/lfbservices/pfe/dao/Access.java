package com.lfbservices.pfe.dao;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;

import com.lfbservices.pfe.model.KeyValue;
import com.lfbservices.pfe.model.Product;
import com.lfbservices.pfe.model.ProductSalesPoint;
import com.lfbservices.pfe.model.SalesPoint;
import com.lfbservices.pfe.model.User;

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

	public static Product getProductById(final String productBarcode) throws Exception {
		SqlSession session = DBConnectionFactory.getNewSession();

		Product product = session.selectOne("QueriesProduct.getProductById", productBarcode);

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

	public static void deleteProduct(final String productId) throws Exception {
		SqlSession session = DBConnectionFactory.getNewSession();

		session.delete("QueriesProduct.deleteProductById", productId);

		session.commit();
		session.close();

	}
	
	
	public static List<KeyValue> getProductCaracteristic(final String productBarcode) throws SQLException, IOException {

		SqlSession session = DBConnectionFactory.getNewSession();

		List<KeyValue> listProducts = session.selectList("QueriesProduct.getProductCaracteristic", productBarcode);

		session.commit();
		session.close();

		return listProducts;

	}
	

	/********************************	Queries for ProductSalesPoint	*************************************/
	
	
	public static List<ProductSalesPoint> getProductSalesPointList(final String productBarcode) throws Exception {
		SqlSession session = DBConnectionFactory.getNewSession();

		List<ProductSalesPoint> listProductSalesPoint = session.selectList("QueriesProductSalesPoint.getSalesPointsAndQte",
				productBarcode);

		session.commit();
		session.close();

		return listProductSalesPoint;
	}
	
	

	/********************************	Queries for SalesPoints		*************************************/


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
	
	public static boolean userExists(final User user) {
		SqlSession session = DBConnectionFactory.getNewSession();
		
		boolean exists = session.selectOne("QueriesUsers.userExists", user);

		session.commit();
		session.close();

		return exists;
	}
	
	
	public static boolean addUser(final User user) {
		if (!Access.isMailAddressAvailable(user.getMailAddress())) return false;
		
		SqlSession session = DBConnectionFactory.getNewSession();
		
		int rowsInserted = session.insert("QueriesUsers.addUser", user);

		session.commit();
		session.close();

		return rowsInserted == 1;
	}
	
	
	public static boolean isMailAddressAvailable(final String mailAddress) {
		SqlSession session = DBConnectionFactory.getNewSession();
		
		boolean isMailAddressAvailable = session.selectOne("QueriesUsers.isMailAddressAvailable", mailAddress);

		session.commit();
		session.close();

		return !isMailAddressAvailable;
	}
	
	
	public static boolean deleteUser(final String mailAddress) {
		SqlSession session = DBConnectionFactory.getNewSession();
		
		int rowsDeleted = session.delete("QueriesUsers.deleteUser", mailAddress);

		session.commit();
		session.close();

		return rowsDeleted == 1;
	}
	
	
	public static List<User> getAllUsers() {
		SqlSession session = DBConnectionFactory.getNewSession();
		
		List<User> users = session.selectList("QueriesUsers.getAllUsers");

		session.commit();
		session.close();

		return users;
	}
}
