package com.lfbservices.pfe.services;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

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
import com.lfbservices.pfe.dao.Access;
import com.lfbservices.pfe.model.KeyValue;
import com.lfbservices.pfe.model.Product;

@Path("/Products")
public class ProductService {

	// http://localhost:8080/PFE-EE/api/Products
	@GET
	@PermitAll
	@Produces(MediaType.APPLICATION_JSON)
	public List<Product> getProducts() throws Exception {
		return Access.getProducts(); 
	}
	
	

	// http://localhost:8080/PFE-EE/api/Products/1
	@Path("/{product_barcode}")
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
	
	
	
	// http://localhost:8080/PFE-EE/api/Products/ProductCaracteristic/4
	@Path("/ProductCaracteristic/{product_barcode}")
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
	
	

	// http://localhost:8080/PFE-EE/api/Products/Add?barcode=5&name=probook&type=1&category=1&tradeMark=HP
	@Path("/Add")
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
	
	
	
	
	// http://localhost:8080/PFE-EE/api/Products/Update/5?name=probook350&type=1&category=1&tradeMark=hp
	@Path("/Update/{product_barcode}")
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
	
	
	
	
	// http://localhost:8080/PFE-EE/api/Products/Delete/5
	@Path("/Delete/{product_barcode}")
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
	
}
