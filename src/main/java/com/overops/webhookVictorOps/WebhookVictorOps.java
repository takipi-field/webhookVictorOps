package com.overops.webhookVictorOps;

import java.io.IOException;

import java.net.URISyntaxException;


import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;





@Path("/event")
public class WebhookVictorOps {
		
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String sayHtmlHello() {
	
	return ("Success! OverOps to VictorOps Get Response ");
	}
	
	@POST 
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response create(final OverOpsEvent input) throws URISyntaxException, IOException {
		//Do something cool with the events from OverOps
		//
		
		String AlertSummary = null;     
	    		

		Boolean WSlogging = JerseyConfig.properties.getProperty("WSlogging").contains("true");
		String AlertURl= JerseyConfig.properties.getProperty("AlertURl");

		//ToDo Set Proxy restApi.setProxy(proxy, userName, password);
		
		//ObjectMapper for json output from OverOps to newDefect
	    ObjectMapper mapper = new ObjectMapper();
	  	String AlertEventAsString=mapper.writerWithDefaultPrettyPrinter().writeValueAsString(input.data);
	    if(WSlogging) 
		System.out.println(AlertEventAsString);
	    
	    StringBuilder stringBuilder = new StringBuilder();
	    
		if (input.type.contains("ALERT"))
		{

			JsonObject newDefect = new JsonObject();
			JsonParser parser = new JsonParser();
			JsonObject OOEventObj = parser.parse(AlertEventAsString).getAsJsonObject();
			System.out.println("OverOpsAlert Type: " + OOEventObj.get("type").getAsString());
			if (OOEventObj.get("type").getAsString().contains("THRESHOLD"))
			{
			// Map OOEventObj data to newDefect for type: Threshold 
			String AlertType = OOEventObj.get("type").getAsString();
			String ViewName = OOEventObj.get("view_name").getAsString();
			newDefect.addProperty("entity_display_name", "OverOps Alert " + ViewName + " " + AlertType);
			
			
			AlertSummary = OOEventObj.get("summary").getAsString();
			stringBuilder.append(AlertSummary + "\n\n");
			
			JsonObject dataObj = (JsonObject) OOEventObj.get("data");		
			
			JsonArray top_events = dataObj.get("top_events").getAsJsonArray();
			for (int i = 0; i <top_events.size(); i++) {

				   JsonObject obj= (JsonObject) top_events.get(i);
				   String title=obj.get("title").getAsString();
				   JsonObject frame=(JsonObject) obj.get("frame");
				   String class_name=frame.get("class_name").getAsString();
				   String times=obj.get("times").getAsString();
				   String link=obj.get("link").getAsString();
		
				   stringBuilder.append(title + " " + class_name + " " +  "times: " + times + "\n\n" +  link + "\n\n\n\n");
				   
			}
			}
		
			if (OOEventObj.get("type").getAsString().contains("NEW_EVENT"))
			{
			// Map OOEventObj data to newDefect for type: NEW_EVENT 
		
			AlertSummary = OOEventObj.get("summary").getAsString();
			JsonObject dataObj = (JsonObject) OOEventObj.get("data");	
			String EventType = dataObj.get("type").getAsString();
			JsonObject EventLocation = (JsonObject) dataObj.get("location");
 			JsonObject EntryPoint = (JsonObject) dataObj.get("entry_point");
 			String message = dataObj.get("message").toString();
 			String link=dataObj.get("link").getAsString();
 			 			
 			stringBuilder.append(EventType + " "+  message + "<br>");
 			stringBuilder.append("<br><br>");
 			stringBuilder.append("Location: " + EventLocation.get("class_name").getAsString() + " " + EventLocation.get("method_name").getAsString() + "<br>");
 			stringBuilder.append("Entry Point: " + EntryPoint.get("class_name").getAsString() + " " + EntryPoint.get("method_name").getAsString() + "<br>");
 			stringBuilder.append("\n\n  " +  link + "\n\n\n\n");
 			newDefect.addProperty("entity_display_name", AlertSummary);
			

			
			}
			
			if (OOEventObj.get("type").getAsString().contains("RESURFACED"))
			{
			//OOEventObj data to newDefect for type: RESURFACED
				AlertSummary = OOEventObj.get("summary").getAsString();
				JsonObject dataObj = (JsonObject) OOEventObj.get("data");	
				String EventType = dataObj.get("type").getAsString();
				JsonObject EventLocation = (JsonObject) dataObj.get("location");
	 			JsonObject EntryPoint = (JsonObject) dataObj.get("entry_point");
	 			String link=dataObj.get("link").getAsString();
	 			 			
	 			stringBuilder.append(EventType + " ");
	 			stringBuilder.append("Location: " + EventLocation.get("class_name").getAsString() + " " + EventLocation.get("method_name").getAsString() + "<br>");
	 			stringBuilder.append("Entry Point: " + EntryPoint.get("class_name").getAsString() + " " + EntryPoint.get("method_name").getAsString() + "<br>");

	 			stringBuilder.append("\n" + link + "\n\n\n" );
	 			newDefect.addProperty("entity_display_name", AlertSummary);
			}
			
			String Description = stringBuilder.toString();
			//Setting the post method url to the client
			Client client = ClientBuilder.newClient();
			
			WebTarget webTarget = client.target(AlertURl);

			
			//Add key-value pair into the json object
			
					
			newDefect.addProperty("message_type", "critical");
			newDefect.addProperty("state_message", Description);

			//Send the form object along with the post call
			Response response = webTarget.request().post(Entity.json(newDefect.toString()));
			
			System.out.println("Respose code: " +  response.getStatus());
			System.out.println("Respose value: " + response.readEntity(String.class));

		}
	
		return Response.ok().build();
		
		
		
	}
}

