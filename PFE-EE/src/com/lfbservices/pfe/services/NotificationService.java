package com.lfbservices.pfe.services;

import java.net.HttpURLConnection;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.lfbservices.pfe.AuthenticationFilter;
import com.lfbservices.pfe.FcmNotifications;
import com.lfbservices.pfe.dao.Access;

@Path("/Notification")
public class NotificationService {

	@GET
	@PermitAll
	@Produces(MediaType.TEXT_PLAIN)
	public String notification() {
		String string = "{}";
		try {
			string = FcmNotifications.pushFCMNotification(
					"fR92nDDOevQ:APA91bHPklTOxG0DbXkHsVDAoP6m5Qzqor2rmEfW_ya925tbSMsaWMpnsTUmnaa7cui5btqCd1uhWzZyA9HFIXk1Lm-UI0n8RGhpD4RR4XiPoOmE8neKRyHp3JVCL3m0rpf_FEDD67d-",
					"salesPointId", "productBarcode", 4, 50000.00, 4, 50000.00);
		} catch (Exception e) {
			throw new WebApplicationException(
				      Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
				        .entity("Exception : "+e)
				        .build());
		}
		return string;
	}
	
	
	@PUT
	@Path("/AddToNotificationsList")
	@RolesAllowed({AuthenticationFilter.USER})
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public boolean addToNotificationsList(
			@HeaderParam(AuthenticationFilter.AUTHORIZATION_PROPERTY) String authorization,
			@DefaultValue("") @FormParam("product_barcode") String productBarcode,
			@DefaultValue("") @FormParam("sales_point_id") String salesPointId) throws Exception { 	
		
		if (productBarcode == null || productBarcode.trim().equals("") ||
				salesPointId == null || salesPointId.trim().equals("")) {
		    throw new WebApplicationException(
		      Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
		        .entity("product_barcode and sales_point_id parameters are mandatory.")
		        .build()
		    );
		}
		
		final String[] authentication = AuthenticationFilter.extractMailAddressPassword(authorization);
		final String mailAddress = authentication[0];
		
		return Access.addToNotificationsList(mailAddress, productBarcode, salesPointId);
	}
	
	
	
	@DELETE
	@Path("/RemoveFromNotificationsList")
	@RolesAllowed({AuthenticationFilter.USER})
	@Produces(MediaType.APPLICATION_JSON)
	public boolean removeFromNotificationsList(
			@HeaderParam(AuthenticationFilter.AUTHORIZATION_PROPERTY) String authorization,
			@DefaultValue("") @QueryParam("product_barcode") String productBarcode,
			@DefaultValue("") @QueryParam("sales_point_id") String salesPointId) throws Exception { 	
		
		if (productBarcode == null || productBarcode.trim().equals("") ||
				salesPointId == null || salesPointId.trim().equals("")) {
		    throw new WebApplicationException(
		      Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
		        .entity("product_barcode and sales_point_id parameters are mandatory.")
		        .build()
		    );
		}
		
		final String[] authentication = AuthenticationFilter.extractMailAddressPassword(authorization);
		final String mailAddress = authentication[0];
		
		return Access.removeFromNotificationsList(mailAddress, productBarcode, salesPointId);
	}
}
