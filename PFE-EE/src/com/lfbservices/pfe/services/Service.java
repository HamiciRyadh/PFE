package com.lfbservices.pfe.services;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.lfbservices.pfe.AuthenticationFilter;
import com.lfbservices.pfe.dao.Access;
import com.lfbservices.pfe.model.Product;
import com.lfbservices.pfe.model.ProductSalesPoint;
import com.lfbservices.pfe.model.Result;
import com.lfbservices.pfe.model.SalesPoint;

@Path("/Products")
public class Service {

	// http://localhost:8080/PFE-EE/api/Products
	@GET
	@PermitAll
	@Produces(MediaType.APPLICATION_JSON)
	public List<Product> getProducts(@HeaderParam(AuthenticationFilter.AUTHORIZATION_PROPERTY) String authorization) throws Exception {
		final String[] authentication = AuthenticationFilter.extractMailAddressPassword(authorization);
		final String mailAddress = authentication[0];
		//TODO: Do the same thing EVERYWHERE we need to query using user data to update the DB
		
		return Access.getProducts(); 
	}
	
	
	
	
	// http://localhost:8080/PFE-EE/api/Products/product/1
	@Path("/product/{product_id}")
	@GET
	@PermitAll
	@Produces(MediaType.APPLICATION_JSON)
	public Product getProductById(@DefaultValue("-1") @PathParam("product_id") int productId) throws Exception {
		//TODO: Verify all the parameters
		if (productId == -1) {
		    throw new WebApplicationException(
		      Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
		        .entity("productId parameter is mandatory.")
		        .build()
		    );
		}
		
		return Access.getProductById(productId);
	}
	
	
	
	
	// http://localhost:8080/PFE-EE/api/Products/Search?value=FH496ADP24
	@Path("/Search")
	@GET
	@PermitAll
	@Produces(MediaType.APPLICATION_JSON)
	public List<Product> search(
			@DefaultValue("") @QueryParam("value") String value,
			@DefaultValue("") @QueryParam("tradeMark") String tradeMark,
			@DefaultValue("-1") @QueryParam("type") int type) throws Exception {
		//TODO: Verify all the parameters
		if (value.trim().equals("")) {
		    throw new WebApplicationException(
		      Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
		        .entity("value parameter is mandatory.")
		        .build()
		    );
		}
		Map<String, Object> extraParameters = new HashMap<String, Object>();
		extraParameters.put("value", "%"+value.toUpperCase()+"%");
		
		if (!tradeMark.trim().equals("")) extraParameters.put("tradeMark", "%"+tradeMark.toUpperCase()+"%");
		if (type != -1) extraParameters.put("type", new Integer(type));
		
		return Access.getProductsByName(extraParameters);
	}
	
	
	
	// http://localhost:8080/PFE-EE/api/Products/Search/1
	// http://localhost:8080/PFE-EE/api/Products/Search/1?city=Ouled%20Fayet&latitudeSearchPosition=36.7332852&longitudeSearchPosition=2.955659199999999&searchDiametre=10
	// La même en modifiant un peut les coordonnées de recherche
	// http://localhost:8080/PFE-EE/api/Products/Search/1?city=Ouled%20Fayet&latitudeSearchPosition=33.7332852&longitudeSearchPosition=2.955659199999999&searchDiametre=10
	@Path("/Search/{productId}")
	@GET
	@PermitAll
	@Produces(MediaType.APPLICATION_JSON)
	public Result getProductSalesPointsQte(
			@DefaultValue("-1") @PathParam("productId") int productId,
			@DefaultValue("") @QueryParam("wilaya") String wilaya,
			@DefaultValue("") @QueryParam("city") String city,
			//-3 -125 est une position au milieu de l'océan pour être
			@DefaultValue("-3") @QueryParam("latitudeSearchPosition") double latitudeSearchPosition,
			@DefaultValue("-125") @QueryParam("longitudeSearchPosition") double longitudeSearchPosition,
			@DefaultValue("0") @QueryParam("searchDiametre") int searchDiametre) throws Exception {
		
		if (productId == -1) {
		    throw new WebApplicationException(
		      Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
		        .entity("productId parameter is mandatory.")
		        .build()
		    );
		}
		
		double minLatitude = latitudeSearchPosition;
		double maxLatitude = latitudeSearchPosition;
		double minLongitude = longitudeSearchPosition;
		double maxLongitude = longitudeSearchPosition;
		
		boolean positionSpecified = false;
		
		if (latitudeSearchPosition != -3.0 && longitudeSearchPosition != -125.0 && searchDiametre > 0) 
			{
				double offsetLatitude = searchDiametre/111110;
				double oneLongitudeDegree = 111110 * Math.cos(latitudeSearchPosition * Math.PI/180);
				double offsetLongitude = searchDiametre/oneLongitudeDegree;
				
				minLatitude = latitudeSearchPosition - offsetLatitude;
				maxLatitude = latitudeSearchPosition + offsetLatitude;
				minLongitude = longitudeSearchPosition - offsetLongitude;
				maxLongitude = longitudeSearchPosition + offsetLongitude;
				
				positionSpecified = true;
			}		
		
		SalesPoint salesPoint;
		List<SalesPoint> listSalesPoints = new ArrayList<SalesPoint>();

		List<ProductSalesPoint> listProductSalesPoints = Access.getSalesPointQte(productId);
		List<ProductSalesPoint> listProductSalesPointsToRemove = new ArrayList<ProductSalesPoint>();

		for (ProductSalesPoint productSalesPoint : listProductSalesPoints) {
			salesPoint = GoogleAPI.details(productSalesPoint.getSalesPointId());
			
			if (salesPoint.getSalesPointAddress().toUpperCase().contains(wilaya.toUpperCase()) && 
					salesPoint.getSalesPointAddress().toUpperCase().contains(city.toUpperCase())) {
				//Si wilaya et city correspondent
						if (positionSpecified) {
							//Si la position a été spécifiée
							if (salesPoint.getSalesPointLat() >= minLatitude && salesPoint.getSalesPointLat() <= maxLatitude && 
					salesPoint.getSalesPointLong() >= minLongitude && salesPoint.getSalesPointLong() <= maxLongitude) {
								//Si la position spécifiée est dans le rayon
								listSalesPoints.add(salesPoint);
							}
							else {
								//Si la position spécifiée n'est pas dans le rayon
								listProductSalesPointsToRemove.add(productSalesPoint);
							}
						}
						else {
							// Si wilaya et city correspondent mais que position n'a pas été spécifiée
							listSalesPoints.add(salesPoint);
						}
					}
			else {
				//Si rien ne correspond
				listProductSalesPointsToRemove.add(productSalesPoint);
			}
		}
		
		listProductSalesPoints.removeAll(listProductSalesPointsToRemove);

		return new Result(listSalesPoints, listProductSalesPoints);
	}

	
	
	
	// http://localhost:8080/PFE-EE/api/Products/Place/details/ChIJDXv5Z-ivjxIRiWBlVc6Eg_8
	@Path("/Place/details/{salesPointId}")
	@GET
	@PermitAll
	@Produces(MediaType.APPLICATION_JSON)
	public SalesPoint getPlaceDetails(@DefaultValue("") @PathParam("salesPointId") String salesPointId) throws Exception {
		//TODO: Verify all the parameters
		if (salesPointId == null || salesPointId.trim().equals("")) {
		    throw new WebApplicationException(
		      Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
		        .entity("salesPointId parameter is mandatory.")
		        .build()
		    );
		}
		return GoogleAPI.moreDetails(salesPointId);
	}
	
	
	
	
	// http://localhost:8080/PFE-EE/api/Products/Category/1
	@Path("/Category/{categoryId}")
	@GET
	@PermitAll
	@Produces(MediaType.APPLICATION_JSON)
	public List<Product> getProductsByCategory(@DefaultValue("-1") @PathParam("categoryId") int categoryId) throws Exception {
		//TODO: Verify all the parameters
		if (categoryId == -1) {
		    throw new WebApplicationException(
		      Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
		        .entity("categoryId parameter is mandatory.")
		        .build()
		    );
		}
		return Access.getProductsByCategory(categoryId);
	}
	
	
	
	
	// http://localhost:8080/PFE-EE/api/Products/add?id=5&name=probook&type=1&category=1&tradeMark=HP
	@Path("/add")
	@PUT
	@RolesAllowed({AuthenticationFilter.ADMIN,AuthenticationFilter.MERCHANT})
	@Consumes(MediaType.APPLICATION_JSON)
	public void insertProduct(
			@DefaultValue("-1") @QueryParam("id") int productId, 
			@DefaultValue("") @QueryParam("name") String productName, 
			@DefaultValue("-1") @QueryParam("type") int productType,
			@DefaultValue("-1") @QueryParam("category") int productCategory, 
			@DefaultValue("") @QueryParam("tradeMark") String productTradeMark) throws Exception {
		
		//TODO: Verify all the parameters
		List<String> missingParameters = new ArrayList<String>();
		
		if (productId == -1) missingParameters.add("productId");
		if (productName.trim().equals("")) missingParameters.add("productName");
		if (productType == -1) missingParameters.add("productType");
		if (productCategory == -1) missingParameters.add("productCategory");
		if (productTradeMark.trim().equals("")) missingParameters.add("productTradeMark");
		
		if (missingParameters.size() != 0) {
		    throw new WebApplicationException(
		      Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
		        .entity("Missing parameters : " + missingParameters)
		        .build()
		    );
		}
		
		Product product = new Product(productId, productName, productType, productCategory, productTradeMark);
		Access.insertProduct(product);
	}
	
	
	
	
	// http://localhost:8080/PFE-EE/api/Products/update/5?name=probook350&type=1&category=1&tradeMark=hp
	@Path("/update/{product_id}")
	@POST
	@RolesAllowed({AuthenticationFilter.ADMIN,AuthenticationFilter.MERCHANT})
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public void updateProduct(
			@DefaultValue("-1") @PathParam("product_id") int productId, 
			@DefaultValue("") @QueryParam("name") String productName,
			@DefaultValue("-1") @QueryParam("type") int productType, 
			@DefaultValue("-1") @QueryParam("category") int productCategory, 
			@DefaultValue("") @QueryParam("tradeMark") String productTradeMark) throws Exception {
		
		//TODO: Verify all the parameters
		List<String> missingParameters = new ArrayList<String>();
		
		if (productId == -1) missingParameters.add("productId");
		if (productName.trim().equals("")) missingParameters.add("productName");
		if (productType == -1) missingParameters.add("productType");
		if (productCategory == -1) missingParameters.add("productCategory");
		if (productTradeMark.trim().equals("")) missingParameters.add("productTradeMark");
		
		if (missingParameters.size() != 0) {
		    throw new WebApplicationException(
		      Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
		        .entity("Missing parameters : " + missingParameters)
		        .build()
		    );
		}
		
		Product product = this.getProductById(productId);
		product.setProductName(productName);
		product.setProductType(productType);
		product.setProductCategory(productCategory);
		product.setProductTradeMark(productTradeMark);
		Access.updateProduct(product);
	}
	
	
	
	
	// http://localhost:8080/PFE-EE/api/Products/delete/5
	@Path("/delete/{product_id}")
	@DELETE
	@RolesAllowed({AuthenticationFilter.ADMIN,AuthenticationFilter.MERCHANT})
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteProduct(@DefaultValue("-1") @PathParam("product_id") int productId) throws Exception {
		//TODO: Verify all the parameters
		if (productId == -1) {
		    throw new WebApplicationException(
		      Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
		        .entity("productId parameter is mandatory.")
		        .build()
		    );
		}
		
		Access.deleteProduct(productId);
	}
}