package com.lfbservices.pfe;

import java.lang.reflect.Method;
import java.security.NoSuchAlgorithmException;
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
 
/**
 * This filter verifies the access permissions for a user based on the mail address and the
 *  password provided in the request.
 * */
@Provider
public class AuthenticationFilter implements javax.ws.rs.container.ContainerRequestFilter {
    
	/**
	 * The different roles available.
	 */
	public static final String ADMIN = "ADMIN";
	public static final String MERCHANT = "MERCHANT";
	public static final String USER = "USER";
	
    @Context
    private ResourceInfo resourceInfo;
    
    /**
     * The HTTP header used for the authentication.
     */
    public static final String AUTHORIZATION_PROPERTY = "Authorization";
    /**
     * The beginning of the string representing the mail address and password.
     */
    public static final String AUTHENTICATION_SCHEME = "Basic";
    
      
    /**
     * This method is executed for every request received by the web service and determines whether or
     * not the client has access to the method he is requesting.
     */
    @Override
    public void filter(ContainerRequestContext requestContext) {
        Method method = resourceInfo.getResourceMethod();
        //Access not allowed for all
        if(!method.isAnnotationPresent(PermitAll.class)) {
            //Access denied for all
            if(method.isAnnotationPresent(DenyAll.class)) {
                requestContext.abortWith(Response.status(Response.Status.FORBIDDEN)
                        .entity("Access blocked for all users !!").build());
                return;
            }
              
            //Get request headers
            final MultivaluedMap<String, String> headers = requestContext.getHeaders();
              
            //Fetch authorization header
            final List<String> authorization = headers.get(AUTHORIZATION_PROPERTY);
              
            //If no authorization information present; block access
            if(authorization == null || authorization.isEmpty()) {
                requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity("You cannot access this resource").build());
                return;
            }
              
            //Get encoded mailAddress and password
            final String[] mailAddressPassword = AuthenticationFilter.extractMailAddressPassword(authorization.get(0));
            final String mailAddress = mailAddressPassword[0];
            final String password = mailAddressPassword[1];
              
            //Verify user access
            if(method.isAnnotationPresent(RolesAllowed.class)) {
                RolesAllowed rolesAnnotation = method.getAnnotation(RolesAllowed.class);
                Set<String> rolesSet = new HashSet<String>(Arrays.asList(rolesAnnotation.value()));
                  
                //Is user allowed to have access?
                if(!isUserAllowed(mailAddress, password, rolesSet)) {
                    requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity("You cannot access this resource").build());
                    return;
                }
            }
        }
    }
    
    
    /**
     * This method verifies if the given mail address and passwords corresponds to any of the given roles.
     * @param mailAddress The mail address of the user.
     * @param password The password of the user.
     * @param rolesSet A {@link List} of roles authorised to have access to the requested method.
     * @return true if the user exists and have access to any one of the given roles, false otherwise.
     */
    private boolean isUserAllowed(final String mailAddress, final String password, final Set<String> rolesSet) {
        boolean isAllowed = false;
                 
        String encryptedPassword = null;
        try {
			encryptedPassword = Encryption.sha1(password);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return false;
		}
        if (encryptedPassword == null) return false;
        String userRole = null;
        try {
        	if (Access.adminExists(mailAddress, encryptedPassword)) {
                userRole = AuthenticationFilter.ADMIN;
            }
        	else if (Access.merchantExists(mailAddress, encryptedPassword)) {
                userRole = AuthenticationFilter.MERCHANT;
            }
        	else if (Access.userExists(mailAddress, encryptedPassword)) {
                userRole = AuthenticationFilter.USER;
        	}
            if (userRole != null) {
            	if(rolesSet.contains(userRole)) {
                    isAllowed = true;
                }
            }
        } catch (Exception e) {
        	e.printStackTrace();
        }
        
        return isAllowed;
    }
    
    
    /**
     * This method extracts the mail address and the password from the given authorization string using the 
     * authentication scheme defined in this class.
     * @param authorization The string contained in the Authorization HTTP header.
     * @return An array of strings of 2 elements that will be empty if an error occurred or will contain at the
     * position 0 the extracted mail address and at the position 1 the password.
     */
    public static String[] extractMailAddressPassword(String authorization) {
    	try {
    		//Get encoded mailAddress and password
            final String encodedMailAddressPassword = authorization.replaceFirst(AUTHENTICATION_SCHEME + " ", "");
              
            //Decode mailAddress and password
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
}