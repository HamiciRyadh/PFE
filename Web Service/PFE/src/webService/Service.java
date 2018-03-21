package webService;

import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;


import dao.*;
import model.Product;
import model.ProductSalesPoint;
import model.Result;
import model.SalesPoint;

@Path("/Products")
public class Service {

	//http://localhost:8080/PFE/api/Products 
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Product> getProduits() throws Exception
	{
		ArrayList<Product> prodList = new ArrayList<Product>();
		Access access = new Access();
		prodList = (ArrayList<Product>) access.getProducts();
		return prodList;
	}
	



	//http://localhost:8080/PFE/api/Products/product/1
	@Path("/product/{product_id}")  
	@GET
	@Produces(MediaType.APPLICATION_JSON) 
	public Product getProduit(@PathParam("product_id") int id) throws Exception
	{
	  
		Product prod = new Product();
		Access access = new Access();
		prod = access.getProductId(id);
		return prod;
	}
	
	

	//http://localhost:8080/PFE/api/Products/Search/FH496ADP24
		@Path("/Search/{value}")  
		@GET
		@Produces(MediaType.APPLICATION_JSON) 
		public Result search (@PathParam("value") String value) throws Exception
		{
		    Access access = new Access();
		    ArrayList<Product> prodList = new ArrayList<Product>();
			prodList = (ArrayList<Product>) access.getProductByName(value);
			
			
	    	ArrayList<ProductSalesPoint> qteSalespointList = new ArrayList<ProductSalesPoint>();
	    	ArrayList<SalesPoint> salespointList = new ArrayList<SalesPoint>();
			
			for  (Product p : prodList)
			{
				 ArrayList<ProductSalesPoint> temp = new ArrayList<ProductSalesPoint>();
				 temp = (ArrayList<ProductSalesPoint>) access.getSalesPointQte(p.getProductId()); 
					
					qteSalespointList.addAll(temp);
			}
			
			for ( ProductSalesPoint p : qteSalespointList)
			{
				  
				SalesPoint temp = GoogleAPI.details(p.getSalespointId());
					
					if (! salespointList.contains(temp)) salespointList.add(temp);	 
			}
					
			
			return new Result(prodList,salespointList, qteSalespointList);
		}
		
		
	//http://localhost:8080/PFE/api/Products/category/1
	@Path("/category/{product_Category}")  
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<Product> getProduitCategory(@PathParam("product_Category") int cat) throws Exception
	{
	  
		ArrayList<Product> prod = new ArrayList<Product>();
		Access access = new Access();
		prod = (ArrayList<Product>) access.getProductCategory(cat);
		return prod;
	}
	
	
	//http://localhost:8080/PFE/api/Products/add?id=5&name=probook&type=1&category=1&mark=HP
	@Path("/add")
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	 public void insertProduct(
			    @QueryParam("id") int id,
				@QueryParam("name") String name,
				@QueryParam("type") int type ,
				@QueryParam("category") int category, 
				@QueryParam("mark") String mark ) throws Exception
    {
		Product prod = new Product(id,name,type,category,mark);
		Access access = new Access();
		access.insertProduct(prod);
    	   	   
    	   
    }

    //http://localhost:8080/PFE/api/Products/update/5?name=probook350&type=1&category=1&mark=hp	
	@Path("/update/{product_id}")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	 public void updateProduct(
			    @PathParam("product_id") int id,
				@QueryParam("name") String name,
				@QueryParam("type") int type ,
				@QueryParam("category") int category, 
				@QueryParam("mark") String marK
				) throws Exception
    {
		 Access access = new Access();
	     Product prod = this.getProduit(id); 
	     prod.setProductName(name);
	     prod.setProductCategory(category);
	     prod.setProductTradeMark(marK);
	     prod.setProductType(type);
		 access.updateProduct(prod);	
    	   	   
    	   
    }
	
	 //http://localhost:8080/PFE/api/Products/update?id=5&name=probook&type=1&category=1&mark=hp
	 @Path("/update")
		@POST
		@Consumes(MediaType.APPLICATION_JSON)
		 public void uppdateProduct(
				    @QueryParam("id") int id,
					@QueryParam("name") String name,
					@QueryParam("type") int type ,
					@QueryParam("category") int category, 
					@QueryParam("mark") String mark ) throws Exception
	 {
			Product prod = new Product(id,name,type,category,mark);
			Access access = new Access();
			access.updateProduct(prod);
	 	   	   
	 	   
	 }
	
	 //http://localhost:8080/PFE/api/Products/delete/5
	@Path("/delete/{product_id}")
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	 public void deleteProduct( @PathParam("product_id") int id) throws Exception
    {
		 
		Access access = new Access();
		access.deleteProduct(id);
    	   	   
    	   
    }

	

}