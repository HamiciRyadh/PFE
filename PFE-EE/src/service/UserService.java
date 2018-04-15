package service;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import dao.Access;
import model.User;

@Path("/User")
public class UserService {
	
	@POST
	@Path("/Connect")
	@Produces(MediaType.APPLICATION_JSON)
	public boolean connect(
			@DefaultValue("") @QueryParam("mailAddress") String mailAddress,
			@DefaultValue("") @QueryParam("password") String password) throws Exception { 
		
		if (mailAddress == null || mailAddress.trim().equals("") ||
				password == null || password.trim().equals("")) {
		    throw new WebApplicationException(
		      Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
		        .entity("mailAddress and password parameters are mandatory.")
		        .build()
		    );
		}
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("mailAddress", mailAddress);
		parameters.put("password", password);
		
		return Access.connect(parameters);
	}
	
	
	
	@POST
	@Path("/Register")
	@Produces(MediaType.APPLICATION_JSON)
	public boolean addUser(
			@DefaultValue("") @QueryParam("mailAddress") String mailAddress,
			@DefaultValue("") @QueryParam("password") String password) throws Exception { 		
		
		if (mailAddress == null || mailAddress.trim().equals("") ||
				password == null || password.trim().equals("")) {
		    throw new WebApplicationException(
		      Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
		        .entity("mailAddress and password parameters are mandatory.")
		        .build()
		    );
		}
		
		User user = new User(mailAddress, password);
		return Access.addUser(user);
	}
	
	
	
	@GET
	@Path("/Users")
	@Produces(MediaType.APPLICATION_JSON)
	public List<User> getAllUsers() throws Exception { 		
		return Access.getAllUsers();
	}
}
