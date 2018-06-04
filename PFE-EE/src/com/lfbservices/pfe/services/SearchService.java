package com.lfbservices.pfe.services;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.security.PermitAll;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.lfbservices.pfe.dao.Access;
import com.lfbservices.pfe.model.KeyValue;
import com.lfbservices.pfe.model.Product;
import com.lfbservices.pfe.model.ProductSalesPoint;
import com.lfbservices.pfe.model.Result;
import com.lfbservices.pfe.model.SalesPoint;

@Path("/Search")
public class SearchService {

	// http://localhost:8080/PFE-EE/api/Search?value=FH496ADP24
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
		
		
		
	// http://localhost:8080/PFE-EE/api/Search/1
	// http://localhost:8080/PFE-EE/api/Search/1?city=Ouled%20Fayet&latitudeSearchPosition=36.7332852&longitudeSearchPosition=2.955659199999999&searchDiametre=10
	// La même en modifiant un peut les coordonnées de recherche
	// http://localhost:8080/PFE-EE/api/Search/1?city=Ouled%20Fayet&latitudeSearchPosition=33.7332852&longitudeSearchPosition=2.955659199999999&searchDiametre=10
	@Path("/{product_barcode}")
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
		
		

	// http://localhost:8080/PFE-EE/api/Search/Category/1
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
	
	
	
	// http://localhost:8080/PFE-EE/api/Search/propositions?query=pro
	@Path("/propositions")
	@GET
	@PermitAll
	@Produces(MediaType.APPLICATION_JSON)
	public List<String> getPropositions(@DefaultValue("") @QueryParam("query") String query) throws Exception {
		if (query == null || query.trim().length() <= 2) {
		    throw new WebApplicationException(
		      Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
		        .entity("category_id parameter is mandatory.")
		        .build()
		    );
		}
		return Access.getPropositions(query);
	}
		
	
	
	//http://localhost:8080/PFE-EE/api/Search/TypeCaracteristic/4
	@Path("/TypeCaracteristic/{product_barcode}")
	@GET
	@PermitAll
	@Produces(MediaType.APPLICATION_JSON)
	public List<KeyValue> getTypeCaracteristic(
			@DefaultValue("") @PathParam("product_barcode") String productId) throws Exception {
		
		if (productId.trim().equals("")) {
		    throw new WebApplicationException(
		      Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
		        .entity("product_barcode parameter is mandatory.")
		        .build()
		    );
		}
		
		return Access.getProductCharacteristicID(productId);
	}
		
		

	//TODO: ????
	// http://localhost:8080/PFE-EE/api/Search/ProductSalesPoint/2
	@Path("/ProductSalesPoint/{product_barcode}")
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
}
