package webService;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

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

import dao.Access;
import model.Product;
import model.ProductSalesPoint;
import model.Result;
import model.SalesPoint;

@Path("/Products")
public class Service {

	// http://localhost:8080/PFE/api/Products
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Product> getProducts() throws Exception {
		return Access.getProducts();
	}
	
	
	
	
	// http://localhost:8080/PFE/api/Products/product/1
	@Path("/product/{product_id}")
	@GET
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
	
	
	
	
	// http://localhost:8080/PFE/api/Products/Search?value=FH496ADP24
	@Path("/Search")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Product> search(@DefaultValue("") @QueryParam("value") String value) throws Exception {
		//TODO: Verify all the parameters
		if (value == null || value.trim().equals("")) {
		    throw new WebApplicationException(
		      Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
		        .entity("value parameter is mandatory.")
		        .build()
		    );
		}
		return Access.getProductsByName(value);
	}
	
	
	
	// http://localhost:8080/PFE/api/Products/Search/1
	@Path("/Search/{product_id}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Result getProductSalesPointsQte(@DefaultValue("-1") @PathParam("product_id") int productId) throws Exception {
		//TODO: Verify all the parameters
		if (productId == -1) {
		    throw new WebApplicationException(
		      Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
		        .entity("productId parameter is mandatory.")
		        .build()
		    );
		}
		List<SalesPoint> listSalesPoints = new ArrayList<SalesPoint>();

		List<ProductSalesPoint> listProductSalesPoints = Access.getSalesPointQte(productId);

		for (ProductSalesPoint productSalesPoint : listProductSalesPoints) {
			listSalesPoints.add(GoogleAPI.details(productSalesPoint.getSalesPointId()));
		}

		return new Result(listSalesPoints, listProductSalesPoints);
	}
	
	
	
	
	// http://localhost:8080/PFE/api/Products/category/1
	@Path("/category/{product_Category}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Product> getProductsByCategory(@DefaultValue("-1") @PathParam("product_Category") int categoryId) throws Exception {
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
	
	
	
	
	// http://localhost:8080/PFE/api/Products/add?id=5&name=probook&type=1&category=1&tradeMark=HP
	@Path("/add")
	@PUT
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
		if (productName == null || productName.trim().equals("")) missingParameters.add("productName");
		if (productType == -1) missingParameters.add("productType");
		if (productCategory == -1) missingParameters.add("productCategory");
		if (productTradeMark == null || productTradeMark.trim().equals("")) missingParameters.add("productTradeMark");
		
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
	
	
	
	
	// http://localhost:8080/PFE/api/Products/update/5?name=probook350&type=1&category=1&tradeMark=hp
	@Path("/update/{product_id}")
	@POST
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
		if (productName == null || productName.trim().equals("")) missingParameters.add("productName");
		if (productType == -1) missingParameters.add("productType");
		if (productCategory == -1) missingParameters.add("productCategory");
		if (productTradeMark == null || productTradeMark.trim().equals("")) missingParameters.add("productTradeMark");
		
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
	
	
	
	
	// http://localhost:8080/PFE/api/Products/delete/5
	@Path("/delete/{product_id}")
	@DELETE
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