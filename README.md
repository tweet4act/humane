humane
======

humane is a java based code base which helps one to collect tweets from the Twitter using the streaming api. It also helps to extracts content features from the collected tweets and save them in the persistent database for further processing such as building classifiers etc. 



humane is written using technology core java & JSP. The persistent database is written in cassandra (http://cassandra.apache.org/) version 1.1.7. For crawling the tweet streams, we have written a wrapper over Twitter's streaming api by extending twitter4j functions.



To run--- 

1) To run the humane's modules one must have a web server (apache tomcat), cassandra version 1.1.7 installed and running. 

2) deploy the humane.war file inside the webapps folder in your tomcat server (refer to http://tomcat.apache.org/tomcat-7.0-doc/deployer-howto.html).

3) start the tomcat server.

4) once started go to a web browser and type http://localhost:8080/Humane/ 

5) this will take you to the login page: use the credential as username : admin and password :admin (at this moment we support only one admin account)

6) this will redirect to another page http://localhost:8080/Humane/landingpage.jsp. 

7) Pressing on Create New Collection button will redirect to another page (http://localhost:8080/Humane/collection.jsp), where one can define a collection name (e.g., Hurricane) and provide credentials required for the streaming api to crawl Tweeter data stream (mandatory). 

7) after this page the user will be redirected to another page (http://localhost:8080/Humane/CollectorServlet), where he/she defines the criteria ( e.g., hashtags, keywords based upon which the collection must be done or the geo-location coordinate which determines the collection filtering condition) for the collection task.

8) once these information is correctly provided the user's will be redirected to the dashboard page where he can view all the collection history along with the details for each collection tasks and last 5 tweets for each of those listed collections.

--

NB: this is an active project, the future version of the release will have more features. Interested collaborator are also requested to extend the released code with more functionalities. 

