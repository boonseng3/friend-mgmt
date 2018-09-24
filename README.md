# Friend Management

 API server that does simple "Friend Management" based on the User Stories below.
 1. As a user, I need an API to create a friend connection between two email addresses.
 1. As a user, I need an API to retrieve the friends list for an email address.
 1. As a user, I need an API to retrieve the common friends list between two email addresses.
 1. As a user, I need an API to subscribe to updates from an email address.
 1. As a user, I need an API to block updates from an email address.
 1. As a user, I need an API to retrieve all email addresses that can receive updates from an email address.
 
 ## Requirements for running the application
 1. Docker 18.06.1-ce and above
 2. Docker Compose 1.22.0 and above
 
 ## Steps to deploy the application
 1. Run the docker/deploy.sh script. The script will pull the image from docker cloud and create the containers
 
 ## Steps to undeploy
 1. Run the docker/undeploy.sh script.
 
 ## TODO
 Due to time constraint, there are list of stuff that were not done.
 1. Explore the use of Graph database for representing relationships.
 1. Request validation e.g. email format, mandatory fields
 1. How to handle rollback manually or other solution when exception occurs updating across multiple documents.
 
 
 ## Doubts
 1. Http GET with request body. The API requirements seems to allow GET request with body. However, the body can be ignored by servers. Hence, in Spring implementation, it does support this. As a result, i had to use PUT to support idempotent request.
 1. @mentioned, what is this requirement?
   

 