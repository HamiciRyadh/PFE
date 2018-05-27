package com.lfbservices.pfe.services;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.security.PermitAll;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.lfbservices.pfe.dao.Access;
import com.lfbservices.pfe.model.SalesPoint;

@Path("/SalesPoint")
public class SalesPointService {

	
	// http://localhost:8080/PFE-EE/api/SalesPoint/Place/details/ChIJDXv5Z-ivjxIRiWBlVc6Eg_8
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
		
		
		
	// http://localhost:8080/PFE-EE/api/Products/UpdateSalesPoint/ChIJgbWj2jyxjxIRTEiyXgwGVqA
	@Path("/UpdateSalesPoint/{sales_point_id}")
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
}
