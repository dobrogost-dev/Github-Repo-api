Github Repo API

Task:


As an api consumer, given username and header “Accept: application/json”, I would like to list all his github repositories, which are not forks. Information, which I require in the response, is:

Repository Name

Owner Login

For each branch it’s name and last commit sha

As an api consumer, given not existing github user, I would like to receive 404 response in such a format:

{



    “status”: ${responseCode}

 

    “Message”: ${whyHasItHappened}



}



As an api consumer, given header “Accept: application/xml”, I would like to receive 406 response in such a format:

    {

 

    “status”: ${responseCode}

 

    “Message”: ${whyHasItHappened}



}

How to run the project:

1. Open the project with IntelliJ or other IDE
2. Run the application
3. Send the get request with username and accept header, you can use Postman to do this

Used https://developer.github.com/v3 as a backing API
