package com.lfbservices.pfe.services;

import java.net.HttpURLConnection;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.lfbservices.pfe.AuthenticationFilter;
import com.lfbservices.pfe.dao.Access;

@Path("/Device")
public class DeviceService {
	
	@PUT
	@Path("/Add")
	@RolesAllowed({AuthenticationFilter.USER})
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public boolean addDevice(
			@HeaderParam(AuthenticationFilter.AUTHORIZATION_PROPERTY) String authorization,
			@DefaultValue("") @FormParam("deviceId") String deviceId) throws Exception { 	
		
		if (deviceId == null || deviceId.trim().equals("")) {
		    throw new WebApplicationException(
		      Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
		        .entity("deviceId parameter is mandatory.")
		        .build()
		    );
		}
		
		final String[] authentication = AuthenticationFilter.extractMailAddressPassword(authorization);
		final String mailAddress = authentication[0];
		
		return Access.addUserDevice(mailAddress, deviceId);
	}
	
	
	
	@POST
	@Path("/Update")
	@RolesAllowed({AuthenticationFilter.USER})
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public boolean updateDeviceId(
			@HeaderParam(AuthenticationFilter.AUTHORIZATION_PROPERTY) String authorization,
			@DefaultValue("") @FormParam("previousDeviceId") String previousDeviceId,
			@DefaultValue("") @FormParam("newDeviceId") String newDeviceId) throws Exception { 		
		
		if (previousDeviceId == null || previousDeviceId.trim().equals("") ||
				newDeviceId == null || newDeviceId.trim().equals("")) {
		    throw new WebApplicationException(
		      Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
		        .entity("previousDeviceId and NewDeviceId parameters are mandatory.")
		        .build()
		    );
		}
		
		final String[] authentication = AuthenticationFilter.extractMailAddressPassword(authorization);
		final String mailAddress = authentication[0];
		
		return Access.updateUserDeviceId(mailAddress, previousDeviceId, newDeviceId);
	}
	
	
	
	@DELETE
	@Path("/Remove")
	@RolesAllowed({AuthenticationFilter.USER})
	@Produces(MediaType.APPLICATION_JSON)
	public boolean removeDeviceId(
			@HeaderParam(AuthenticationFilter.AUTHORIZATION_PROPERTY) String authorization,
			@DefaultValue("") @QueryParam("deviceId") String deviceId) throws Exception { 		
		
		if (deviceId == null || deviceId.trim().equals("")) {
		    throw new WebApplicationException(
		      Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
		        .entity("devideId parameter is mandatory.")
		        .build()
		    );
		}
		
		final String[] authentication = AuthenticationFilter.extractMailAddressPassword(authorization);
		final String mailAddress = authentication[0];
		
		return Access.removeUserDeviceId(mailAddress, deviceId);
	}
}