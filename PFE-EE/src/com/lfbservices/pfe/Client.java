package com.lfbservices.pfe;

import java.io.IOException;


public class Client 
{
	public static void main(String[] args) throws IOException, ClassNotFoundException 
	{
		
		/*
		httpGETCollectionExample();
		httpGETEntityExample();
		httpPOSTMethodExample();
		httpPUTMethodExample();
		httpDELETEMethodExample();
		*/
	
	}
	
	/*
	private static void httpGETCollectionExample() 
	{

		
		System.out.println("Go");
		ClientConfig clientConfig = new ClientConfig();

		javax.ws.rs.client.Client client = ClientBuilder.newClient( clientConfig );
		WebTarget webTarget = client.target("http://localhost:8080/PFE-EE/api").path("Products/UpdateProductSalesPoint?sales_point_id=ChIJgbWj2jyxjxIRTEiyXgwGVqA&product_barcode=4&product_quantity=13&product_price=58000");
		
		Invocation.Builder invocationBuilder =	webTarget.request(MediaType.APPLICATION_XML);
		Response response = invocationBuilder.header("Authorization", "Basic aGFtaWNpcnlhZGhAZ21haWwuY29tOmFkbWlu").get();
		
		System.out.println(response.getStatus());
	}
	
	private static void httpGETEntityExample() 
	{
		Client client = ClientBuilder.newClient( new ClientConfig().register( LoggingFilter.class ) );
		WebTarget webTarget = client.target("http://localhost:8080/JerseyDemos/rest").path("employees").path("1");
		
		Invocation.Builder invocationBuilder =	webTarget.request(MediaType.APPLICATION_XML);
		Response response = invocationBuilder.get();
		
		Employee employee = response.readEntity(Employee.class);
			
		System.out.println(response.getStatus());
		System.out.println(employee);
	}

	private static void httpPOSTMethodExample() 
	{
		Client client = ClientBuilder.newClient( new ClientConfig().register( LoggingFilter.class ) );
		WebTarget webTarget = client.target("http://localhost:8080/JerseyDemos/rest").path("employees");
		
		Employee emp = new Employee();
		emp.setId(1);
		emp.setName("David Feezor");
		
		Invocation.Builder invocationBuilder =	webTarget.request(MediaType.APPLICATION_XML);
		Response response = invocationBuilder.post(Entity.entity(emp, MediaType.APPLICATION_XML));
		
		System.out.println(response.getStatus());
		System.out.println(response.readEntity(String.class));
	}
	
	private static void httpPUTMethodExample() 
	{
		Client client = ClientBuilder.newClient( new ClientConfig().register( LoggingFilter.class ) );
		WebTarget webTarget = client.target("http://localhost:8080/JerseyDemos/rest").path("employees").path("1");
		
		Employee emp = new Employee();
		emp.setId(1);
		emp.setName("David Feezor");
		
		Invocation.Builder invocationBuilder =	webTarget.request(MediaType.APPLICATION_XML);
		Response response = invocationBuilder.put(Entity.entity(emp, MediaType.APPLICATION_XML));
		
		Employee employee = response.readEntity(Employee.class);
			
		System.out.println(response.getStatus());
		System.out.println(employee);
	}
	
	private static void httpDELETEMethodExample() 
	{
		Client client = ClientBuilder.newClient( new ClientConfig().register( LoggingFilter.class ) );
		WebTarget webTarget = client.target("http://localhost:8080/JerseyDemos/rest").path("employees").path("1");
		
		Invocation.Builder invocationBuilder =	webTarget.request();
		Response response = invocationBuilder.delete();
		
		System.out.println(response.getStatus());
		System.out.println(response.readEntity(String.class));
	}
	*/
}