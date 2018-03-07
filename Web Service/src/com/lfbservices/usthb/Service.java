package com.lfbservices.usthb;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/Products")
public class Service {
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Product> getProduits()
	{
		Product result = new Product(), r2 = new Product(0,"a","a","b","c"), r3 = new Product(0,"z","z","b","c");
		
		//Appel à la méthode de Dao
		ArrayList<Product> l = new ArrayList<Product>();
		
		l.add(result);
		l.add(r2);
		l.add(r3);
		
		return l;
	}
	

}
