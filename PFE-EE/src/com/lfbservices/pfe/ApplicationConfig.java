package com.lfbservices.pfe;

import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.server.ResourceConfig;

/**
 * This class handles the configuration of the web service.
 */
public class ApplicationConfig extends ResourceConfig {
	
	/**
	 * Registers the {@link LoggingFilter} to log the queries received by the web service,
	 * the {@link GsonProvider} to automatically convert POJOs to their JSON representation 
	 * and the {@link AuthenticationFilter} to restrict the access to some functionalities.
	 */
    public ApplicationConfig() {
        packages("com.lfbservices.pfe");
        register(LoggingFilter.class);
        register(GsonProvider.class); 
        register(AuthenticationFilter.class);
    }
}