package com.overops.webhookVictorOps;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("webhookVictorOps")
public class JerseyConfig extends Application {

public static Properties properties = new Properties();

private Properties readProperties() {
	
    InputStream inputStream = getClass().getClassLoader().getResourceAsStream("victorops.properties");
 //   if (inputStream != null) {
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            // TODO Add your custom fail-over code here
            e.printStackTrace();
        }
 //   }
    return properties;
}

@Override
public Set<Class<?>> getClasses() {     
    // Read the properties file
    readProperties();

    // Set up Jersey resources
    Set<Class<?>> rootResources = new HashSet<Class<?>>();
    rootResources.add(WebhookVictorOps.class);
    return rootResources;
}

}