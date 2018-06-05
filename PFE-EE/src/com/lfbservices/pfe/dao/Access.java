package com.lfbservices.pfe.dao;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
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

	/**
	 * This method returns a list of all the {@link Product} of the system's database.
	 * @return A {@link List} of {@link Product}.
	 * @throws SQLException If an error occurred while querying the database.
	 * @throws IOException If the resource to use to query the database is not found.
	 */
	public static List<Product> getProducts() throws SQLException, IOException {

		final SqlSession session = DBConnectionFactory.getNewSession();

		final List<Product> listProducts = session.selectList("QueriesProduct.getAllProducts");

		session.close();

		return listProducts;
	}

	/**
	 * This method returns a {@link List} of {@link Product} corresponding to the given category.
	 * @param categoryId The id of the category of the products.
	 * @return A {@link List} of {@link Product}.
	 * @throws SQLException If an error occurred while querying the database.
	 * @throws IOException If the resource to use to query the database is not found.
	 */
	public static List<Product> getProductsByCategory(final int categoryId) throws SQLException, IOException {
		final SqlSession session = DBConnectionFactory.getNewSession();

		final List<Product> listProducts = session.selectList("QueriesProduct.getProductsByCategory", categoryId);

		session.close();

		return listProducts;
	}

	/**
	 * This method returns the {@link Product} corresponding to the given barcode.
	 * @param productBarcode The identifier of the {@link Product}.
	 * @return The corresponding {@link Product}.
	 * @throws SQLException If an error occurred while querying the database.
	 * @throws IOException If the resource to use to query the database is not found.
	 */
	public static Product getProductById(final String productBarcode) throws SQLException, IOException {
		final SqlSession session = DBConnectionFactory.getNewSession();

		final Product product = session.selectOne("QueriesProduct.getProductById", productBarcode);

		session.close();

		return product;
	}

	/**
	 * This method returns a {@link List} of {@link Product} corresponding to the parameters.
	 * @param parameters a {@link Map} representing the possible parameters for this query, it has to contain a
	 * search query named value and may also contain a {@link String} representing the trade mark with the name tradeMark 
	 * and an {@link Integer} representing the product's type named type.
	 * @returnA A {@link List} of {@link Product}.
	 * @throws SQLException If an error occurred while querying the database.
	 * @throws IOException If the resource to use to query the database is not found.
	 */
	public static List<Product> getProductsByName(final Map<String,Object> parameters) throws SQLException, IOException {
		final SqlSession session = DBConnectionFactory.getNewSession();
		
		final List<Product> listProducts = session.selectList("QueriesProduct.getProductsByName", parameters);

		session.close();

		return listProducts;
	}
	
	/**
	 * This method inserts {@link Product} into the system's database.
	 * @param product The {@link Product} to insert.
	 * @throws SQLException If an error occurred while querying the database.
	 * @throws IOException If the resource to use to query the database is not found.
	 */
	public static void insertProduct(final Product product) throws SQLException, IOException {
		final SqlSession session = DBConnectionFactory.getNewSession();

		session.insert("QueriesProduct.insertProduct", product);

		session.commit();
		session.close();
	}

	/**
	 * This method updates a {@link Product} of the system's database.
	 * @param product The {@link Product} to update.
	 * @throws SQLException If an error occurred while querying the database.
	 * @throws IOException If the resource to use to query the database is not found.
	 */
	public static void updateProduct(final Product product) throws SQLException, IOException {

		final SqlSession session = DBConnectionFactory.getNewSession();

		session.update("QueriesProduct.updateProductById", product);

		session.commit();
		session.close();
	}

	/**
	 * This method deletes a {@link Product} from the system's database.
	 * @param productBarcode The unique identifier of the {@link Product} to delete.
	 * @throws SQLException If an error occurred while querying the database.
	 * @throws IOException If the resource to use to query the database is not found.
	 */
	public static void deleteProduct(final String productBarcode) throws SQLException, IOException {
		final SqlSession session = DBConnectionFactory.getNewSession();

		session.delete("QueriesProduct.deleteProductById", productBarcode);

		session.commit();
		session.close();
	}
	
	/**
	 * This method returns a {@link List} of {@link KeyValue} that represents the characteristics of a {@link Product}.
	 * @param productBarcode The unique identifier of a {@link Product}.
	 * @return A {@link List} of {@link KeyValue}.
	 * @throws SQLException If an error occurred while querying the database.
	 * @throws IOException If the resource to use to query the database is not found.
	 */
	public static List<KeyValue> getProductCharacteristicID(final String productBarcode) throws SQLException, IOException {

		SqlSession session = DBConnectionFactory.getNewSession();

		List<KeyValue> listProducts = session.selectList("QueriesProduct.getProductCharacteristicID",productBarcode);

		session.close();

		return listProducts;
	}
	
	/**
	 * This method extracts from the system's database a {@link List} of {@link String} representing
	 * the propositions corresponding to the given search query, it then filters it and returns the 
	 * first NUMBER_OF_PROPOSITIONS elements.
	 * @param query The user's search query.
	 * @return A {@link List} of {@link String} representing the propositions.
	 * @throws SQLException If an error occurred while querying the database.
	 * @throws IOException If the resource to use to query the database is not found.
	 */
	public static List<String> getPropositions(final String query) throws SQLException, IOException {
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
	

	/********************************	Queries for ProductSalesPoint	*************************************/
	
	/**
	 * This method returns a {@link List} of {@link ProductSalesPoint} for a given {@link Product} represented by its barcode.
	 * @param productBarcode The unique identifier of a {@link Product}.
	 * @return A {@link List} of {@link ProductSalesPoint}.
	 * @throws SQLException If an error occurred while querying the database.
	 * @throws IOException If the resource to use to query the database is not found.
	 */
	public static List<ProductSalesPoint> getProductSalesPointList(final String productBarcode) throws SQLException, IOException {
		final SqlSession session = DBConnectionFactory.getNewSession();

		final List<ProductSalesPoint> listProductSalesPoint = session.selectList("QueriesProductSalesPoint.getSalesPointsAndQte", productBarcode);

		session.close();

		return listProductSalesPoint;
	}
	
	/**
	 * This method updates a {@link ProductSalesPoint}.
	 * @param productSalesPoint The new {@link ProductSalesPoint}.
	 * @return true if the update succeeded, false otherwise.
	 * @throws SQLException If an error occurred while querying the database.
	 * @throws IOException If the resource to use to query the database is not found.
	 */
	public static boolean updateProductSalesPoint(final ProductSalesPoint productSalesPoint) throws SQLException, IOException {
		final SqlSession session = DBConnectionFactory.getNewSession();
		
		int rowsUpdated = session.update("QueriesProductSalesPoint.updateProductSalesPoint", productSalesPoint);

		session.commit();
		session.close();

		return rowsUpdated == 1;
	}
	
	public static List<ProductSalesPoint> getProductSalesPointsForPlaceAndProduct(final String productBarcode, final List<String> salesPointsIds) {
		final SqlSession session = DBConnectionFactory.getNewSession();
		final List<ProductSalesPoint> productSalesPointsList = new ArrayList<ProductSalesPoint>();
		final Map<String,Object> parameters = new HashMap<String,Object>();
		ProductSalesPoint productSalesPoint;
		
		parameters.put("productBarcode", productBarcode);
		for (String salesPointId : salesPointsIds) {
			parameters.remove("salesPointId");
			parameters.put("salesPointId", salesPointId);
			productSalesPoint = session.selectOne("QueriesProductSalesPoint.getProductSalesPointForPlaceAndProduct", parameters);
			productSalesPointsList.add(productSalesPoint);
		}
		
		session.close();
		return productSalesPointsList;
	}
	
	public static boolean removeProductSalesPoint(final String salesPointId, final String productBarcode) {
		final SqlSession session = DBConnectionFactory.getNewSession();
		final Map<String,Object> parameters = new HashMap<String,Object>();
		
		parameters.put("productBarcode", productBarcode);
		parameters.put("salesPointId", salesPointId);
		
		final int rowsDeleted = session.delete("QueriesProductSalesPoint.removeProductSalesPoint", parameters);
		
		session.commit();
		session.close();
		
		return rowsDeleted > 0;
	}
	
	
	/********************************	Queries for SalesPoints		*************************************/

	/**
	 * This method extracts from the system's database the characteristics of a {@link SalesPoint} identified 
	 * by its id.
	 * @param salesPointId The id of the {@link SalesPoint}.
	 * @return A {@link SalesPoint}.
	 * @throws SQLException If an error occurred while querying the database.
	 * @throws IOException If the resource to use to query the database is not found.
	 */
	public static SalesPoint getSalesPointById(final String salesPointId) throws SQLException, IOException {
		final SqlSession session = DBConnectionFactory.getNewSession();

		final SalesPoint salesPoint = session.selectOne("QueriesSalesPoint.getSalesPointById", salesPointId);

		session.close();

		return salesPoint;
	}

	/**
	 * This method updates a {@link SalesPoint} in the system's database.
	 * @param salesPoint The {@link SalesPoint} to update with the newest informations.
	 * @throws SQLException If an error occurred while querying the database.
	 * @throws IOException If the resource to use to query the database is not found.
	 */
	public static void updateSalesPoint(final SalesPoint salesPoint) throws SQLException, IOException {

		final SqlSession session = DBConnectionFactory.getNewSession();

		session.update("QueriesSalesPoint.updateSalesPointById", salesPoint);

		session.commit();
		session.close();
	}
	
	/********************************	Queries for Users	*************************************/
	
	/**
	 * This method verifies if the given parameters can be used to identify an administrator.
	 * @param mailAddress The mail address of the administrator.
	 * @param encryptedPassword the password encrypted using a Sha1 algorithm and a salt.
	 * @return true if the administrator exists, false otherwise.
	 * @throws SQLException If an error occurred while querying the database.
	 * @throws IOException If the resource to use to query the database is not found.
	 */
	public static boolean adminExists(final String mailAddress, final String encryptedPassword) throws SQLException, IOException {
		final SqlSession session = DBConnectionFactory.getNewSession();
		
		final Map<String,Object> parameters = new HashMap<String,Object>();
		parameters.put("mailAddress", mailAddress);
		parameters.put("password", encryptedPassword);
		
		final boolean exists = session.selectOne("QueriesUsers.adminExists", parameters);

		session.close();

		return exists;
	}
	
	/**
	 * This method verifies if the given parameters can be used to identify a merchant.
	 * @param mailAddress The mail address of the merchant.
	 * @param encryptedPassword the password encrypted using a Sha1 algorithm and a salt.
	 * @return true if the merchant exists, false otherwise.
	 * @throws SQLException If an error occurred while querying the database.
	 * @throws IOException If the resource to use to query the database is not found.
	 */
	public static boolean merchantExists(final String mailAddress, final String encryptedPassword) throws SQLException, IOException {
		final SqlSession session = DBConnectionFactory.getNewSession();
		
		final Map<String,Object> parameters = new HashMap<String,Object>();
		parameters.put("mailAddress", mailAddress);
		parameters.put("password", encryptedPassword);
		
		final boolean exists = session.selectOne("QueriesUsers.merchantExists", parameters);

		session.close();

		return exists;
	}
	
	/**
	 * This method verifies if the given parameters can be used to identify a user.
	 * @param mailAddress The mail address of the user.
	 * @param encryptedPassword the password encrypted using a Sha1 algorithm and a salt.
	 * @return true if the user exists, false otherwise.
	 * @throws SQLException If an error occurred while querying the database.
	 * @throws IOException If the resource to use to query the database is not found.
	 */
	public static boolean userExists(final String mailAddress, final String encryptedPassword) throws SQLException, IOException {
		final SqlSession session = DBConnectionFactory.getNewSession();
		
		final Map<String,Object> parameters = new HashMap<String,Object>();
		parameters.put("mailAddress", mailAddress);
		parameters.put("password", encryptedPassword);
		
		final boolean exists = session.selectOne("QueriesUsers.userExists", parameters);

		session.close();

		return exists;
	}
	
	/**
	 * This method adds a user to the system's database.
	 * @param user The user to add to the system's database.
	 * @return true if the user was added, false otherwise.
	 * @throws SQLException If an error occurred while querying the database.
	 * @throws IOException If the resource to use to query the database is not found.
	 */
	public static boolean addUser(final User user) throws SQLException, IOException {
		if (!Access.isMailAddressAvailable(user.getMailAddress())) return false;
		
		final SqlSession session = DBConnectionFactory.getNewSession();
		
		int rowsInserted = session.insert("QueriesUsers.addUser", user);

		session.commit();
		session.close();

		return rowsInserted == 1;
	}
	
	/**
	 * This method checks if the given mail address is available or not.
	 * @param mailAddress The mail address for which to check the availability.
	 * @return true if the mail address is currently available, false otherwise.
	 * @throws SQLException If an error occurred while querying the database.
	 * @throws IOException If the resource to use to query the database is not found.
	 */
	public static boolean isMailAddressAvailable(final String mailAddress) throws SQLException, IOException {
		final SqlSession session = DBConnectionFactory.getNewSession();
		
		boolean isMailAddressAvailable = session.selectOne("QueriesUsers.isMailAddressAvailable", mailAddress);

		session.close();

		return !isMailAddressAvailable;
	}
	
	/**
	 * This method deletes a user from the system's database.
	 * @param mailAddress The mail address identifying the user to delete.
	 * @return true if the deletion succeeded, false otherwise.
	 * @throws SQLException If an error occurred while querying the database.
	 * @throws IOException If the resource to use to query the database is not found.
	 */
	public static boolean deleteUser(final String mailAddress) throws SQLException, IOException {
		final SqlSession session = DBConnectionFactory.getNewSession();
		
		int rowsDeleted = session.delete("QueriesUsers.deleteUser", mailAddress);

		session.commit();
		session.close();

		return rowsDeleted == 1;
	}
	
	/**
	 * This method gets the user id associated with the given mail address.
	 * @param userMailAddress The user's mail address.
	 * @return An int representing the user's id.
	 * @throws SQLException If an error occurred while querying the database.
	 * @throws IOException If the resource to use to query the database is not found.
	 */
	public static int getUserId(final String userMailAddress) throws SQLException, IOException {
		final SqlSession session = DBConnectionFactory.getNewSession();
		
		final Integer result = session.selectOne("QueriesUsers.getUserId", userMailAddress);

		session.close();
		
		int userId = -1;
		if (result != null) userId = result.intValue();

		return userId;
	}
	
	/**
	 * This method extracts from the database a {@link List} representing all the existing users.
	 * @return A {@link List} of users.
	 * @throws SQLException If an error occurred while querying the database.
	 * @throws IOException If the resource to use to query the database is not found.
	 */
	public static List<User> getAllUsers() throws SQLException, IOException {
		final SqlSession session = DBConnectionFactory.getNewSession();
		
		final List<User> users = session.selectList("QueriesUsers.getAllUsers");

		session.close();

		return users;
	}
	
	/**
	 * This method verifies if the given device id is already associated with the given user.
	 * @param parameters A {@link Map} containing the parameters for the query, it has to contain the device id
	 * with the name deviceId and the user's id with the name userId.
	 * @return true if the device is already associated with the user, false otherwise.
	 * @throws SQLException If an error occurred while querying the database.
	 * @throws IOException If the resource to use to query the database is not found.
	 */
	public static boolean userDeviceExists(final Map<String,Object> parameters) throws SQLException, IOException {
		final SqlSession session = DBConnectionFactory.getNewSession();
		
		boolean exists = session.selectOne("QueriesUsers.userDeviceExists", parameters);

		session.close();

		return exists;
	}
	
	/**
	 * This method adds a user device to a user's devices.
	 * @param mailAddress The mail address of the user.
	 * @param deviceId The Firebase token id representing the device's id.
	 * @return true if the device was successfully added, false otherwise.
	 * @throws SQLException If an error occurred while querying the database.
	 * @throws IOException If the resource to use to query the database is not found.
	 */
	public static boolean addUserDevice(final String mailAddress, final String deviceId) throws SQLException, IOException {
		
		final int userId = Access.getUserId(mailAddress);
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
	
	/**
	 * This method updates a user's device id in the system's database.
	 * @param mailAddress The mail address of the user.
	 * @param previousDeviceId The previous value of the device's Firebase token id.
	 * @param newDeviceId The new value of the device's Firebase token id.
	 * @return true if the update succeeded, false otherwise.
	 * @throws SQLException If an error occurred while querying the database.
	 * @throws IOException If the resource to use to query the database is not found.
	 */
	public static boolean updateUserDeviceId(final String mailAddress, final String previousDeviceId, final String newDeviceId) throws SQLException, IOException {
		
		final int userId = Access.getUserId(mailAddress);
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
	
	/**
	 * This method deletes a user's device id from the system's database.
	 * @param mailAddress The mail address of the user.
	 * @param deviceId The Firebase token id to remove.
	 * @return true if the deletion succeeded, false otherwise.
	 * @throws SQLException If an error occurred while querying the database.
	 * @throws IOException If the resource to use to query the database is not found.
	 */
	public static boolean removeUserDeviceId(final String mailAddress, final String deviceId) throws SQLException, IOException {
		
		final int userId = Access.getUserId(mailAddress);
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
	
	/**
	 * This method verifies if the user already subscribed to the notification's feeds for the given product and sales point.
	 * @param parameters A {@link Map} containing the parameters for the query, it has to contain the user's id with
	 * the name userId, the product's barcode with the name productBarcode and the sales point's id with the name
	 * salesPointId.
	 * @return true if the user already subscribed for this notification's feeds, false otherwise.
	 * @throws SQLException If an error occurred while querying the database.
	 * @throws IOException If the resource to use to query the database is not found.
	 */
	public static boolean userNotificationExists(final Map<String,Object> parameters) throws SQLException, IOException {
		final SqlSession session = DBConnectionFactory.getNewSession();
		
		boolean exists = session.selectOne("QueriesUsers.userNotificationExists", parameters);

		session.close();

		return exists;
	}
	
	/**
	 * This method adds a user to the notification's feeds for a given product and sales point, it starts by 
	 * verifying if the user is not already subscribed, if he is not, it adds it to the corresponding table 
	 * in the system's database.
	 * @param mailAddress The user's mail address.
	 * @param productBarcode The barcode of the product for which the user wants to be notified.
	 * @param salesPointId The id of the sales point for which the user wants to be notified.
	 * @return true if the user was successfully addeed, false otherwise.
	 * @throws SQLException If an error occurred while querying the database.
	 * @throws IOException If the resource to use to query the database is not found.
	 */
	public static boolean addToNotificationsList(final String mailAddress, final String productBarcode, final String salesPointId) throws SQLException, IOException {
		
		final int userId = Access.getUserId(mailAddress);
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
	
	/**
	 * This method removes a user from the notifications feeds for a given product and sales point.
	 * @param mailAddress The user's mail address.
	 * @param productBarcode The barcode of the product for which the user wants to disable the notifications.
	 * @param salesPointId The id of the sales point where the product is proposed and for which the user 
	 * wants to disable the notifications.
	 * @return true if the deletion succeeded, false otherwise.
	 * @throws SQLException If an error occurred while querying the database.
	 * @throws IOException If the resource to use to query the database is not found.
	 */
	public static boolean removeFromNotificationsList(final String mailAddress, final String productBarcode, final String salesPointId) 
			throws SQLException, IOException{
		
		final int userId = Access.getUserId(mailAddress);
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
	
	/**
	 * This method extracts from the system's database a {@link List} representing all the device's ids associated
	 * with a given user that wants to be notified for a modification in the given product identified by its barcode
	 * and a given sales point identified by its sales point id.
	 * @param salesPointId The id of the sales point for which the notification is made.
	 * @param productBarcode The barcode of the product for which the notification is made.
	 * @return A {@link List} of {@link String} representig all the user's devices ids.
	 * @throws SQLException If an error occurred while querying the database.
	 * @throws IOException If the resource to use to query the database is not found.
	 */
	public static List<String> getDevicesIdsForNotification(final String salesPointId, final String productBarcode) throws SQLException, IOException {
		final SqlSession session = DBConnectionFactory.getNewSession();
		
		final Map<String,Object> parameters = new HashMap<String,Object>();
		parameters.put("salesPointId", salesPointId);
		parameters.put("productBarcode", productBarcode);
		
		final List<String> devicesIds = session.selectList("QueriesUsers.getDevicesIdsForNotification", parameters);
		 
		devicesIds.add(salesPointId);
		devicesIds.add(productBarcode);
		 
		session.close();
		
		return devicesIds;
	}
}