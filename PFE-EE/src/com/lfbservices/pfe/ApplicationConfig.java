package com.lfbservices.pfe;

import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.server.ResourceConfig;


public class ApplicationConfig extends ResourceConfig
{
    public ApplicationConfig()
    {
        packages("com.lfbservices.pfe");
        register(LoggingFilter.class);
        register(GsonProvider.class);
 
        //Register Auth Filter here
        register(AuthenticationFilter.class);
    }
}