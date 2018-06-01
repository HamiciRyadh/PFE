package com.lfbservices.pfe.dao;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;

import com.lfbservices.pfe.model.KeyValueID;
import com.lfbservices.pfe.model.Product;
import com.lfbservices.pfe.model.ProductSalesPoint;
import com.lfbservices.pfe.model.SalesPoint;
import com.lfbservices.pfe.model.User;

public class Access {
	
	/********************************	Queries for Products		*************************************/

	public static List<Product> getProducts() throws SQLException, IOException {

		final SqlSession session = DBConnectionFactory.getNewSession();

		final List<Product> listProducts = session.selectList("QueriesProduct.getAllProducts");

		session.commit();
		session.close();

		return listProducts;

	}

	public static List<Product> getProductsByCategory(final int categoryId) throws Exception {
		final SqlSession session = DBConnectionFactory.getNewSession();

		final List<Product> listProducts = session.selectList("QueriesProduct.getProductsByCategory", categoryId);

		session.commit();
		session.close();

		return listProducts;
	}

	public static Product getProductById(final String productBarcode) throws Exception {
		final SqlSession session = DBConnectionFactory.getNewSession();

		final Product product = session.selectOne("QueriesProduct.getProductById", productBarcode);

		session.commit();
		session.close();

		return product;
	}

	public static List<Product> getProductsByName(final Map<String,Object> value) throws Exception {
		final SqlSession session = DBConnectionFactory.getNewSession();
		
		final List<Product> listProducts = session.selectList("QueriesProduct.getProductsByName", value);

		session.commit();
		session.close();

		return listProducts;
	}
	
	public static void insertProduct(final Product product) throws Exception {
		final SqlSession session = DBConnectionFactory.getNewSession();

		session.insert("QueriesProduct.insertProduct", product);

		session.commit();
		session.close();

	}

	public static void updateProduct(final Product product) throws Exception {

		final SqlSession session = DBConnectionFactory.getNewSession();

		session.update("QueriesProduct.updateProductById", product);

		session.commit();
		session.close();

	}

	public static void deleteProduct(final String productId) throws Exception {
		final SqlSession session = DBConnectionFactory.getNewSession();

		session.delete("QueriesProduct.deleteProductById", productId);

		session.commit();
		session.close();

	}
	
	
	public static List<KeyValueID> getProductCharacteristicID(String value) throws SQLException, IOException {

		SqlSession session = DBConnectionFactory.getNewSession();

		List<KeyValueID> listProducts = session.selectList("QueriesProduct.getProductCharacteristicID",value);

		session.commit();
		session.close();

		return listProducts;

	}
	

	/********************************	Queries for ProductSalesPoint	*************************************/
	
	
	public static List<ProductSalesPoint> getProductSalesPointList(final String productBarcode) throws Exception {
		final SqlSession session = DBConnectionFactory.getNewSession();

		final List<ProductSalesPoint> listProductSalesPoint = session.selectList("QueriesProductSalesPoint.getSalesPointsAndQte",
				productBarcode);

		session.commit();
		session.close();

		return listProductSalesPoint;
	}
	
	public static boolean updateProductSalesPoint(final ProductSalesPoint productSalesPoint) throws Exception {
		final SqlSession session = DBConnectionFactory.getNewSession();
		
		int rowsUpdated = session.update("QueriesProductSalesPoint.updateProductSalesPoint", productSalesPoint);

		session.commit();
		session.close();

		return rowsUpdated == 1;
	}
	
	public static List<String> getPropositions(final String query) {
		final int NUMBER_OF_PROPOSITIONS = 5;
		final SqlSession session = DBConnectionFactory.getNewSession();
		
		final List<String> propositions = session.selectList("QueriesProduct.getPropositions", query+"%");

		session.close();

		List<String> sortedPropositions = new ArrayList<String>();
		for (String proposition : propositions) {
			sortedPropositions.add(proposition.toLowerCase());
		}
		
		sortedPropositions.sort(new Comparator<String>() {

			@Override
			public int compare(String str1, String str2) {
				return str1.compareTo(str2);
			}
			
		});
		
		List<String> returnedPropositions = new ArrayList<String>();
		for (int i = 0; (i < NUMBER_OF_PROPOSITIONS) && (i < sortedPropositions.size()); i++) {
			returnedPropositions.add(sortedPropositions.get(i));
		}
		
		return sortedPropositions;
	}
	
	

	/********************************	Queries for SalesPoints		*************************************/


	public static SalesPoint getSalesPointById(final String salesPointId) throws Exception {
		final SqlSession session = DBConnectionFactory.getNewSession();

		final SalesPoint salesPoint = session.selectOne("QueriesSalesPoint.getSalesPointById", salesPointId);

		session.commit();
		session.close();

		return salesPoint;
	}


	public static void updateSalesPoint(final SalesPoint salesPoint) throws Exception {

		final SqlSession session = DBConnectionFactory.getNewSession();

		session.update("QueriesSalesPoint.updateSalesPointById", salesPoint);

		session.commit();
		session.close();

	}
	
	/********************************	Queries for Users	*************************************/
	
	public static boolean adminExists(final String mailAddress, final String encryptedPassword) {
		final SqlSession session = DBConnectionFactory.getNewSession();
		
		final Map<String,Object> parameters = new HashMap<String,Object>();
		parameters.put("mailAddress", mailAddress);
		parameters.put("password", encryptedPassword);
		
		final boolean exists = session.selectOne("QueriesUsers.adminExists", parameters);

		session.commit();
		session.close();

		return exists;
	}
	
	public static boolean merchantExists(final String mailAddress, final String encryptedPassword) {
		final SqlSession session = DBConnectionFactory.getNewSession();
		
		final Map<String,Object> parameters = new HashMap<String,Object>();
		parameters.put("mailAddress", mailAddress);
		parameters.put("password", encryptedPassword);
		
		final boolean exists = session.selectOne("QueriesUsers.merchantExists", parameters);

		session.commit();
		session.close();

		return exists;
	}
	
	public static boolean userExists(final String mailAddress, final String encryptedPassword) {
		final SqlSession session = DBConnectionFactory.getNewSession();
		
		final Map<String,Object> parameters = new HashMap<String,Object>();
		parameters.put("mailAddress", mailAddress);
		parameters.put("password", encryptedPassword);
		
		final boolean exists = session.selectOne("QueriesUsers.userExists", parameters);

		session.commit();
		session.close();

		return exists;
	}
	
	
	public static boolean addUser(final User user) {
		if (!Access.isMailAddressAvailable(user.getMailAddress())) return false;
		
		final SqlSession session = DBConnectionFactory.getNewSession();
		
		int rowsInserted = session.insert("QueriesUsers.addUser", user);

		session.commit();
		session.close();

		return rowsInserted == 1;
	}
	
	
	public static boolean isMailAddressAvailable(final String mailAddress) {
		final SqlSession session = DBConnectionFactory.getNewSession();
		
		boolean isMailAddressAvailable = session.selectOne("QueriesUsers.isMailAddressAvailable", mailAddress);

		session.commit();
		session.close();

		return !isMailAddressAvailable;
	}
	
	
	public static boolean deleteUser(final String mailAddress) {
		final SqlSession session = DBConnectionFactory.getNewSession();
		
		int rowsDeleted = session.delete("QueriesUsers.deleteUser", mailAddress);

		session.commit();
		session.close();

		return rowsDeleted == 1;
	}
	
	
	public static int getUserDeviceId(final String userMailAddress) {
		final SqlSession session = DBConnectionFactory.getNewSession();
		
		final Integer result = session.selectOne("QueriesUsers.getUserId", userMailAddress);

		session.commit();
		session.close();
		
		int userId = -1;
		if (result != null) userId = result.intValue();

		return userId;
	}
	
	
	public static List<User> getAllUsers() {
		final SqlSession session = DBConnectionFactory.getNewSession();
		
		final List<User> users = session.selectList("QueriesUsers.getAllUsers");

		session.commit();
		session.close();

		return users;
	}
	
	public static boolean userDeviceExists(final Map<String,Object> parameters) {
		final SqlSession session = DBConnectionFactory.getNewSession();
		
		boolean exists = session.selectOne("QueriesUsers.userDeviceExists", parameters);

		session.commit();
		session.close();

		return exists;
	}
	
	public static boolean addUserDevice(final String mailAddress, final String deviceId) {
		
		final int userId = Access.getUserDeviceId(mailAddress);
		if (userId == -1) return false;
		
		final SqlSession session = DBConnectionFactory.getNewSession();
				
		final Map<String,Object> parameters = new HashMap<String,Object>();
		parameters.put("userId", userId);
		parameters.put("deviceId", deviceId);
		
		if (Access.userDeviceExists(parameters)) return false;
		int rowsInserted = session.insert("QueriesUsers.addUserDevice", parameters);

		session.commit();
		session.close();

		return rowsInserted == 1;
	}
	
	public static boolean updateUserDeviceId(final String mailAddress, final String previousDeviceId, final String newDeviceId) {
		
		final int userId = Access.getUserDeviceId(mailAddress);
		if (userId == -1) return false;
		
		final SqlSession session = DBConnectionFactory.getNewSession();
				
		final Map<String,Object> parameters = new HashMap<String,Object>();
		parameters.put("userId", userId);
		parameters.put("previousDeviceId", previousDeviceId);
		parameters.put("newDeviceId", newDeviceId);
		
		int rowsUpdated = session.update("QueriesUsers.updateUserDeviceId", parameters);

		session.commit();
		session.close();

		return rowsUpdated == 1;
	}
	
	public static boolean removeUserDeviceId(final String mailAddress, final String deviceId) {
		
		final int userId = Access.getUserDeviceId(mailAddress);
		if (userId == -1) return false;
		
		final SqlSession session = DBConnectionFactory.getNewSession();
				
		final Map<String,Object> parameters = new HashMap<String,Object>();
		parameters.put("userId", userId);
		parameters.put("deviceId", deviceId);
		
		int rowsDeleted = session.delete("QueriesUsers.removeUserDeviceId", parameters);

		session.commit();
		session.close();

		return rowsDeleted == 1;
	}
	
	public static boolean userNotificationExists(final Map<String,Object> parameters) {
		final SqlSession session = DBConnectionFactory.getNewSession();
		
		boolean exists = session.selectOne("QueriesUsers.userNotificationExists", parameters);

		session.commit();
		session.close();

		return exists;
	}
	
	public static boolean addToNotificationsList(final String mailAddress, final String productBarcode, final String salesPointId) {
		
		final int userId = Access.getUserDeviceId(mailAddress);
		if (userId == -1) return false;
		
		final SqlSession session = DBConnectionFactory.getNewSession();
				
		final Map<String,Object> parameters = new HashMap<String,Object>();
		parameters.put("userId", userId);
		parameters.put("productBarcode", productBarcode);
		parameters.put("salesPointId", salesPointId);
		
		if (Access.userNotificationExists(parameters)) return false;
		int rowsInserted = session.insert("QueriesUsers.addToNotificationsList", parameters);

		session.commit();
		session.close();

		return rowsInserted == 1;
	}
	
	public static boolean removeFromNotificationsList(final String mailAddress, final String productBarcode, final String salesPointId) {
		
		final int userId = Access.getUserDeviceId(mailAddress);
		if (userId == -1) return false;
		
		final SqlSession session = DBConnectionFactory.getNewSession();
				
		final Map<String,Object> parameters = new HashMap<String,Object>();
		parameters.put("userId", userId);
		parameters.put("productBarcode", productBarcode);
		parameters.put("salesPointId", salesPointId);
		
		int rowsDeleted = session.delete("QueriesUsers.removeFromNotificationsList", parameters);

		session.commit();
		session.close();

		return rowsDeleted == 1;
	}
	
	public static List<String> getDevicesIdsForNotification(final String salesPointId, final String productBarcode) {
		final SqlSession session = DBConnectionFactory.getNewSession();
		
		final Map<String,Object> parameters = new HashMap<String,Object>();
		parameters.put("salesPointId", salesPointId);
		parameters.put("productBarcode", productBarcode);
		
		final List<String> devicesIds = session.selectList("QueriesUsers.getDevicesIdsForNotification", parameters);
		 
		devicesIds.add(salesPointId);
		devicesIds.add(productBarcode);
		 
		session.commit();
		session.close();
		
		return devicesIds;
	}
}
