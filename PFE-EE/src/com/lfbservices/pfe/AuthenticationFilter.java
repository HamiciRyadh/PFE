package com.lfbservices.pfe;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import com.lfbservices.pfe.dao.Access;
import com.lfbservices.pfe.model.User;
 
/**
 * This filter verify the access permissions for a user
 * based on username and passowrd provided in request
 * */
@Provider
public class AuthenticationFilter implements javax.ws.rs.container.ContainerRequestFilter
{
     
	public static final String ADMIN = "ADMIN";
	public static final String MERCHANT = "MERCHANT";
	public static final String USER = "USER";
	
    @Context
    private ResourceInfo resourceInfo;
     
    public static final String AUTHORIZATION_PROPERTY = "Authorization";
    public static final String AUTHENTICATION_SCHEME = "Basic";
    
      
    
    @Override
    public void filter(ContainerRequestContext requestContext)
    {
        Method method = resourceInfo.getResourceMethod();
        //Access allowed for all
        if(!method.isAnnotationPresent(PermitAll.class))
        {
            //Access denied for all
            if(method.isAnnotationPresent(DenyAll.class))
            {
                requestContext.abortWith(Response.status(Response.Status.FORBIDDEN)
                        .entity("Access blocked for all users !!").build());
                return;
            }
              
            //Get request headers
            final MultivaluedMap<String, String> headers = requestContext.getHeaders();
              
            //Fetch authorization header
            final List<String> authorization = headers.get(AUTHORIZATION_PROPERTY);
              
            //If no authorization information present; block access
            if(authorization == null || authorization.isEmpty())
            {
                requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity("You cannot access this resource").build());
                return;
            }
              
            //Get encoded mailAddress and password
            final String[] mailAddressPassword = AuthenticationFilter.extractMailAddressPassword(authorization.get(0));
            final String mailAddress = mailAddressPassword[0];
            final String password = mailAddressPassword[1];
              
            //Verify user access
            if(method.isAnnotationPresent(RolesAllowed.class))
            {
                RolesAllowed rolesAnnotation = method.getAnnotation(RolesAllowed.class);
                Set<String> rolesSet = new HashSet<String>(Arrays.asList(rolesAnnotation.value()));
                  
                //Is user valid?
                if(!isUserAllowed(mailAddress, password, rolesSet))
                {
                    requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity("You cannot access this resource").build());
                    return;
                }
            }
        }
    }
    
    
    
    private boolean isUserAllowed(final String mailAddress, final String password, final Set<String> rolesSet) {
        boolean isAllowed = false;
          
        //Step 1. Fetch password from database and match with password in argument
        //If both match then get the defined role for user from database and continue; else return isAllowed [false]
        //Access the database and do this part yourself
        //String userRole = userMgr.getUserRole(mailAddress);
         
        //TODO: use Encryption with password for comparing data with the DB
        User user = new User(mailAddress, password);
        try {
        	if (mailAddress.equals("hamiciryadh@gmail.com") && password.equals("admin")) {
            	return true;
            }
            if (Access.userExists(user)) {
                String userRole = AuthenticationFilter.ADMIN;
                 
                //Step 2. Verify user role
                if(rolesSet.contains(userRole)) {
                    isAllowed = true;
                }
            }
        } catch (Exception e) {
        	e.printStackTrace();
        }
        
        return isAllowed;
    }
    
    
    public static String[] extractMailAddressPassword(String authorization) {
    	try {
    		//Get encoded mailAddress and password
            final String encodedMailAddressPassword = authorization.replaceFirst(AUTHENTICATION_SCHEME + " ", "");
              
            //Decode mailAddress and password
            //String usernameAndPassword = new String(Base64.decode(encodedUserPassword.getBytes()));
            String mailAddressAndPassword = new String(java.util.Base64.getMimeDecoder().decode(encodedMailAddressPassword.getBytes()));

            //Split mailAddress and password tokens
            final StringTokenizer tokenizer = new StringTokenizer(mailAddressAndPassword, ":");
            final String mailAddress = tokenizer.nextToken();
            final String password = tokenizer.nextToken();
            
            final String[] result = {mailAddress, password};
            return result;
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    		return new String[2];
    	}
    }
    
    
    /*
    @Override
    public void filter(ContainerRequestContext requestContext)
    {
        Method method = resourceInfo.getResourceMethod();
        
        //Access denied for all
        if(method.isAnnotationPresent(DenyAll.class))
        {
            requestContext.abortWith(Response.status(Response.Status.FORBIDDEN)
                    .entity("Access blocked for all users !!").build());
            return;
        }
        
        //Get request headers
        final MultivaluedMap<String, String> headers = requestContext.getHeaders();
          
        //Fetch authorization header
        final List<String> authorization = headers.get(AUTHORIZATION_PROPERTY);
          
        //If no authorization information present; block access
        if(authorization == null || authorization.isEmpty())
        {
        	if(!method.isAnnotationPresent(PermitAll.class)) {
        		requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity("You cannot access this resource").build());
                return;
        	}
        }
        
        //Get encoded mailAddress and password
        final String encodedMailAddressPassword = authorization.get(0).replaceFirst(AUTHENTICATION_SCHEME + " ", "");
          
        //Decode mailAddress and password
        //String usernameAndPassword = new String(Base64.decode(encodedUserPassword.getBytes()));
        String mailAddressAndPassword = new String(java.util.Base64.getMimeDecoder().decode(encodedMailAddressPassword.getBytes()));

        //Split mailAddress and password tokens
        final StringTokenizer tokenizer = new StringTokenizer(mailAddressAndPassword, ":");
        final String mailAddress = tokenizer.nextToken();
        final String password = tokenizer.nextToken();
        
        //Access allowed for all
        if(!method.isAnnotationPresent(PermitAll.class)) {   
        	
            //Verify user access
            if(method.isAnnotationPresent(RolesAllowed.class)) {
            	
                RolesAllowed rolesAnnotation = method.getAnnotation(RolesAllowed.class);
                Set<String> rolesSet = new HashSet<String>(Arrays.asList(rolesAnnotation.value()));
                  
                //Is user valid?
                if(!isUserAllowed(mailAddress, password, rolesSet)) {
                    requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity("You cannot access this resource").build());
                    return;
                }
            }
        }
    }
    */
}