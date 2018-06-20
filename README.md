# webhookVictorOps
Webhook for creating incidents in VictorOps.   Maps incoming OverOps webhook Alert to VictorOps RESTful API. 
https://help.victorops.com/knowledge-base/victorops-restendpoint-integration/

Retrieve webhookRally.war from /bin.  Deploy to tomcat/webapps directory.  
Update WEB-INF/classes/victorops.properties

=\<Rally Project ID easiky obtained from Rally URL>

AlertURL=URL obtained from REST API settings page.  See link above.

WSlogging=true //Enable to see Web Service calls in catalina.out



Tested with Tomcat 8.x and Jetty 8.  
