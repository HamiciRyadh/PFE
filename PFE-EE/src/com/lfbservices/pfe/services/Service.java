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
import com.lfbservices.pfe.FcmNotifications;
import com.lfbservices.pfe.dao.Access;
import com.lfbservices.pfe.model.KeyValue;
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
	public List<Product> getProducts() throws Exception {
		return Access.getProducts(); 
	}
	
	
	
	
	// http://localhost:8080/PFE-EE/api/Products/product/1
	@Path("/product/{product_barcode}")
	@GET
	@PermitAll
	@Produces(MediaType.APPLICATION_JSON)
	public Product getProductById(@DefaultValue("") @PathParam("product_barcode") String productBarcode) throws Exception {
		if (productBarcode .trim().equals("")) {
		    throw new WebApplicationException(
		      Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
		        .entity("productBarcode parameter is mandatory.")
		        .build()
		    );
		}
		
		return Access.getProductById(productBarcode);
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

		if (value.trim().equals("")) {
		    throw new WebApplicationException(
		      Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
		        .entity("value parameter is mandatory.")
		        .build()
		    );
		}
		final Map<String, Object> extraParameters = new HashMap<String, Object>();
		extraParameters.put("value", "%"+value.toUpperCase()+"%");
		
		if (!tradeMark.trim().equals("")) extraParameters.put("tradeMark", "%"+tradeMark.toUpperCase()+"%");
		if (type != -1) extraParameters.put("type", new Integer(type));
		
		return Access.getProductsByName(extraParameters);
	}
	
	
	
	// http://localhost:8080/PFE-EE/api/Products/Search/1
	// http://localhost:8080/PFE-EE/api/Products/Search/1?city=Ouled%20Fayet&latitudeSearchPosition=36.7332852&longitudeSearchPosition=2.955659199999999&searchDiametre=10
	// La même en modifiant un peut les coordonnées de recherche
	// http://localhost:8080/PFE-EE/api/Products/Search/1?city=Ouled%20Fayet&latitudeSearchPosition=33.7332852&longitudeSearchPosition=2.955659199999999&searchDiametre=10
	@Path("/Search/{product_barcode}")
	@GET
	@PermitAll
	@Produces(MediaType.APPLICATION_JSON)
	public Result getProductSalesPointsQte(
			@DefaultValue("") @PathParam("product_barcode") String productBarcode,
			@DefaultValue("") @QueryParam("wilaya") String wilaya,
			@DefaultValue("") @QueryParam("city") String city,
			//-3 -125 est une position au milieu de l'océan pour être
			@DefaultValue("-3") @QueryParam("latitudeSearchPosition") double latitudeSearchPosition,
			@DefaultValue("-125") @QueryParam("longitudeSearchPosition") double longitudeSearchPosition,
			@DefaultValue("0") @QueryParam("searchDiametre") int searchDiametre) throws Exception {
		
		if (productBarcode.trim().equals("")) {
		    throw new WebApplicationException(
		      Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
		        .entity("product_barcode parameter is mandatory.")
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
		final List<SalesPoint> listSalesPoints = new ArrayList<SalesPoint>();

		final List<ProductSalesPoint> listProductSalesPoints = Access.getProductSalesPointList(productBarcode);
		final List<ProductSalesPoint> listProductSalesPointsToRemove = new ArrayList<ProductSalesPoint>();

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

	
	
	
	@Path("/notification")
	@GET
	@PermitAll
	@Produces(MediaType.TEXT_PLAIN)
	public String notification() {
		String string = "{}";
		try {
			string = FcmNotifications.pushFCMNotification(
					"fR92nDDOevQ:APA91bHPklTOxG0DbXkHsVDAoP6m5Qzqor2rmEfW_ya925tbSMsaWMpnsTUmnaa7cui5btqCd1uhWzZyA9HFIXk1Lm-UI0n8RGhpD4RR4XiPoOmE8neKRyHp3JVCL3m0rpf_FEDD67d-",
					"salesPointId", "productBarcode", 10, 5000.00d);
		} catch (Exception e) {
			throw new WebApplicationException(
				      Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
				        .entity("Exception : "+e)
				        .build());
		}
		return string;
	}
	
	
	
	
	// http://localhost:8080/PFE-EE/api/Products/Place/details/ChIJDXv5Z-ivjxIRiWBlVc6Eg_8
	@Path("/Place/details/{sales_point_id}")
	@GET
	@PermitAll
	@Produces(MediaType.APPLICATION_JSON)
	public SalesPoint getPlaceDetails(@DefaultValue("") @PathParam("sales_point_id") String salesPointId) throws Exception {
		if (salesPointId == null || salesPointId.trim().equals("")) {
		    throw new WebApplicationException(
		      Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
		        .entity("sales_point_id parameter is mandatory.")
		        .build()
		    );
		}
		return GoogleAPI.moreDetails(salesPointId);
	}
	
	
	
	
	// http://localhost:8080/PFE-EE/api/Products/Category/1
	@Path("/Category/{category_id}")
	@GET
	@PermitAll
	@Produces(MediaType.APPLICATION_JSON)
	public List<Product> getProductsByCategory(@DefaultValue("-1") @PathParam("category_id") int categoryId) throws Exception {
		if (categoryId == -1) {
		    throw new WebApplicationException(
		      Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
		        .entity("category_id parameter is mandatory.")
		        .build()
		    );
		}
		return Access.getProductsByCategory(categoryId);
	}
	
	
		

	
	// http://localhost:8080/PFE-EE/api/Products/add?barcode=5&name=probook&type=1&category=1&tradeMark=HP
	@Path("/add")
	@PUT
	@RolesAllowed({AuthenticationFilter.MERCHANT})
	@Consumes(MediaType.APPLICATION_JSON)
	public void insertProduct(
			@DefaultValue("-1") @QueryParam("barcode") String productBarcode, 
			@DefaultValue("") @QueryParam("name") String productName, 
			@DefaultValue("-1") @QueryParam("type") int productType,
			@DefaultValue("") @QueryParam("tradeMark") String productTradeMark) throws Exception {
		
		final List<String> missingParameters = new ArrayList<String>();
		
		if (productBarcode.trim().equals("")) missingParameters.add("productBarcode");
		if (productName.trim().equals("")) missingParameters.add("productName");
		if (productType == -1) missingParameters.add("productType");
		if (productTradeMark.trim().equals("")) missingParameters.add("productTradeMark");
		
		if (missingParameters.size() != 0) {
		    throw new WebApplicationException(
		      Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
		        .entity("Missing parameters : " + missingParameters)
		        .build()
		    );
		}
		
		Product product = new Product(productBarcode, productName, productType, productTradeMark);
		Access.insertProduct(product);
	}
	
	
	
	
	// http://localhost:8080/PFE-EE/api/Products/update/5?name=probook350&type=1&category=1&tradeMark=hp
	@Path("/update/{product_barcode}")
	@POST
	@RolesAllowed({AuthenticationFilter.MERCHANT})
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public void updateProduct(
			@DefaultValue("") @PathParam("product_barcode") String productBarcode, 
			@DefaultValue("") @QueryParam("name") String productName,
			@DefaultValue("-1") @QueryParam("type") int productType, 
			@DefaultValue("") @QueryParam("tradeMark") String productTradeMark) throws Exception {
		
		final List<String> missingParameters = new ArrayList<String>();
		
		if (productBarcode.trim().equals("")) missingParameters.add("productBarcode");
		if (productName.trim().equals("")) missingParameters.add("productName");
		if (productType == -1) missingParameters.add("productType");
		if (productTradeMark.trim().equals("")) missingParameters.add("productTradeMark");
		
		if (missingParameters.size() != 0) {
		    throw new WebApplicationException(
		      Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
		        .entity("Missing parameters : " + missingParameters)
		        .build()
		    );
		}
		
		Product product = this.getProductById(productBarcode);
		product.setProductName(productName);
		product.setProductType(productType);
		product.setProductTradeMark(productTradeMark);
		Access.updateProduct(product);
	}
	
	
	
	
	// http://localhost:8080/PFE-EE/api/Products/delete/5
	@Path("/delete/{product_barcode}")
	@DELETE
	@RolesAllowed({AuthenticationFilter.MERCHANT})
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteProduct(@DefaultValue("") @PathParam("product_barcode") String productBarcode) throws Exception {
		if (productBarcode.trim().equals("")) {
		    throw new WebApplicationException(
		      Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
		        .entity("productBarcode parameter is mandatory.")
		        .build()
		    );
		}
		
		Access.deleteProduct(productBarcode);
	}
	
	// http://localhost:8080/PFE-EE/api/Products/Search/ProductSalesPoint/2
	@Path("/Search/ProductSalesPoint/{product_barcode}")
	@GET
	@PermitAll
	@Produces(MediaType.APPLICATION_JSON)
	public List<ProductSalesPoint>  getProductSalesPointsList(
			@DefaultValue("-1") @PathParam("product_barcode") String productBarcode) throws Exception {
		
		if (productBarcode.trim().equals("")) {
		    throw new WebApplicationException(
		      Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
		        .entity("product_barcode parameter is mandatory.")
		        .build()
		    );
		}
			
		return Access.getProductSalesPointList(productBarcode);
	
	}
	
	// http://localhost:8080/PFE-EE/api/Products/Search/ProductCaracteristic/4
	@Path("/Search/ProductCaracteristic/{product_barcode}")
	@GET
	@PermitAll
	@Produces(MediaType.APPLICATION_JSON)
	public List<KeyValue>  getProductCaracteristic(
			@DefaultValue("") @PathParam("product_barcode") String productBarcode) throws Exception {
		
		if (productBarcode.trim().equals("")) {
		    throw new WebApplicationException(
		  		Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
			        .entity("productBarcode parameter is mandatory.")
			        .build()
		    );
		}
				
		return Access.getProductCaracteristic(productBarcode);
	}

		
	// http://localhost:8080/PFE-EE/api/Products/updateSalespoint/ChIJgbWj2jyxjxIRTEiyXgwGVqA
	@Path("/updateSalespoint/{sales_point_id}")
	@GET
	@PermitAll
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public void updateSalesPoint(
			@DefaultValue("") @PathParam("sales_point_id") String salesPointId) throws Exception {
		
		final List<String> missingParameters = new ArrayList<String>();
		
		if (salesPointId.trim().equals("")) missingParameters.add("sales_point_id");
		if (missingParameters.size() != 0) {
		    throw new WebApplicationException(
		      Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
		        .entity("Missing parameters : " + missingParameters)
		        .build()
		    );
		}
		
		SalesPoint salespoint = Access.getSalesPointById(salesPointId);
		SalesPoint info = GoogleAPI.salespointInformations(salesPointId);
	
		salespoint.setSalesPointName(info.getSalesPointName());
		salespoint.setSalesPointLat(info.getSalesPointLat());
		salespoint.setSalesPointLong(info.getSalesPointLong());
		
		Access.updateSalesPoint(salespoint);
	}
	
	
	
	@Path("/UpdateProductSalesPoint")
	@GET
	@PermitAll
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public void updateProductSalesPoint(
			@DefaultValue("") @QueryParam("sales_point_id") String salesPointId,
			@DefaultValue("") @QueryParam("product_barcode") String productBarcode,
			@DefaultValue("-1") @QueryParam("product_quantity") int productQuantity,
			@DefaultValue("-1") @QueryParam("product_price") double productPrice) throws Exception {
		
		final List<String> missingParameters = new ArrayList<String>();
		
		if (salesPointId.trim().equals("")) missingParameters.add("sales_point_id");
		if (productBarcode.trim().equals("")) missingParameters.add("product_barcode");
		if (productQuantity == -1) missingParameters.add("product_quantity");
		if (productPrice == -1) missingParameters.add("product_price");
		if (missingParameters.size() != 0) {
		    throw new WebApplicationException(
		      Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
		        .entity("Missing parameters : " + missingParameters)
		        .build()
		    );
		}
		
		final ProductSalesPoint productSalesPoint = new ProductSalesPoint(productBarcode, salesPointId, productQuantity, productPrice);
		final boolean updated = Access.updateProductSalesPoint(productSalesPoint);
		if (updated) {
			final List<String> devicesIds = Access.getDevicesIdsForNotification(salesPointId, productBarcode, productQuantity, productPrice);
			if (devicesIds != null) {
				for (String deviceId : devicesIds) {
					FcmNotifications.pushFCMNotification(deviceId, salesPointId, productBarcode, productQuantity, productPrice);
				}
			}
		}
	}
}