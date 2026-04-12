# digital-system
Group CourseWork

To test this API you need to make sure you have Maven installed into your computer, if you do not have you can find it on this link https://maven.apache.org/download.cgi

steps to follow :
-install and set the environment for maven,
-Download the digital-system from this repository and try to unzip it,
-enter into the folder and in the directory bar, delete the address and  type cmd to access cmd from that repository
-install maven in that folder, by simply typing in cmd this command : mvn install
-After doing so, ensure your server is running, whether you are using Xampp or wampserver, make sure it is running.

#Database setting
-in your browser, type http://localhost//phpmyadmin, in that interface that appears, simply click go
-create a database named payment_db, then click go

#Running the Api
-in that same cmd, simply type :mvn spring-boot:run 
-and just like that your api will be running

#how to see and test the endpoints:
-in the internet brwoser, go to the search bar and type http://localhost:9090/swagger-ui/index.html
- and just like that you will be able to see the endpoints and test them

#Errors that may occur during the process
=if you run mvn spring-boot:run and the output is build success, it means another server is using the same port. the solution would be to go to digital-system folder you downloaded and search for application.poperties file, open it and change the port to the one that is available, you may try 8080 or 8081 or any other 
