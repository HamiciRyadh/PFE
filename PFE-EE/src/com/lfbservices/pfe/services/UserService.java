package com.lfbservices.pfe.services;

import java.net.HttpURLConnection;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.lfbservices.pfe.AuthenticationFilter;
import com.lfbservices.pfe.Encryption;
import com.lfbservices.pfe.dao.Access;
import com.lfbservices.pfe.model.User;

@Path("/User")
public class UserService {
	
	@POST
	@Path("/Connect")
	@PermitAll
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public boolean connect(
			@DefaultValue("") @FormParam("mailAddress") String mailAddress,
			@DefaultValue("") @FormParam("password") String password) throws Exception { 
		
		if (mailAddress == null || mailAddress.trim().equals("") ||
			 	password == null || password.trim().equals("")) {
		    throw new WebApplicationException(
		      Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
		        .entity("mailAddress and password parameters are mandatory.")
		        .build()
		    );
		}
		
		String encryptedPassword = "";
		
		try {
			encryptedPassword =  Encryption.sha1(password);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return false;
		}
		
		return Access.userExists(mailAddress, encryptedPassword);
	}
	
	
	
	@POST
	@Path("/Register")
	@PermitAll
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public boolean addUser(
			@DefaultValue("") @FormParam("mailAddress") String mailAddress,
			@DefaultValue("") @FormParam("password") String password) throws Exception { 		
		
		if (mailAddress == null || mailAddress.trim().equals("") ||
				password == null || password.trim().equals("")) {
		    throw new WebApplicationException(
		      Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
		        .entity("mailAddress and password parameters are mandatory.")
		        .build()
		    );
		}
		
		try {
			password = Encryption.sha1(password);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return false;
		}
		
		User user = new User(mailAddress, password);
		return Access.addUser(user);
	}
	
	
	
	@DELETE
	@Path("/Delete")
	@RolesAllowed({AuthenticationFilter.ADMIN})
	@Produces(MediaType.APPLICATION_JSON)
	public boolean deleteUser(@DefaultValue("") @QueryParam("mailAddress") String mailAddress) {
		if (mailAddress == null || mailAddress.trim().equals("")) {
		    throw new WebApplicationException(
		      Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
		        .entity("mailAddress parameter is mandatory.")
		        .build()
		    );
		}
		
		return Access.deleteUser(mailAddress);
	}
	
	
	
	@GET
	@Path("/Users")
	@RolesAllowed({AuthenticationFilter.ADMIN})
	@Produces(MediaType.APPLICATION_JSON)
	public List<User> getAllUsers() throws Exception { 		
		return Access.getAllUsers();
	}
}
