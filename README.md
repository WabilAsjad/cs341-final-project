## Multithreaded HTTP Server 
- Wellesley College CS341 Operating Systems - Final Project
- Authors: Wabil Asjad & Yaxin Liu

## Description
This is a multithreaded HTTP server that uses socket programming and TCP, written in Java. The locally hosted server is a database server that provides http/rest interface. Implemented requests include GET, HEAD, POST, PUT, and DELETE.

## Folder Structure

The workspace contains two folders by default, where:

- `src`: the folder to maintain sources
    - `clientserver`: the folder to maintain files for measurement and to run the client server
- `lib`: the folder to maintain dependencies

## Description of Files

- `Client.java`: a client that sends HTTP requests to the server
- `httpServer.java`: a locally hosted server that listens for requests, handles requests, and responses to the client
- `formatJSON.java`: consists of code to format databases we downloaded from online
- `Measurements.java`: consists of code to measure the effectiveness of the server
- `database.json`: a database with approximately 1000 data points
- `mini_database.json`: a database with approximately 20 data points
- `Home.html`: a simple HTML test file for GET and DELETE requests

## Features
Our multithreaded HTTP server provides HTTP/REST interface. We implemented the following requests:
- `GET`: read the data, retrieve it, and return that to the client
    - `GET` data from database:
        - `GET` whole database: `GET database`
        - `GET` a JSONObject from database: `GET database/1`
        - `GET` a key from a JSONObject: `GET database/1/email`
    - `GET` data from HTML: `GET Home.html`
- `HEAD`: read the data
    - `HEAD` data from database:
        - `HEAD` whole database: `HEAD database`
        - `HEAD` a JSONObject from database: `HEAD database/1`
        - `HEAD` a key from a JSONObject: `HEAD database/1/email`
    - `HEAD` data from HTML: `HEAD Home.html`
- `DELETE`: delete all the data from the target location requested by the client
    - `DELETE` data from database: 
        - `DELETE` whole database: `DELETE database`
        - `DELETE` a JSONObject from database: `DELETE database/1`
        - `DELETE` a key from a JSONObject: `DELETE database/1/email`
    - `DELETE` data from HTML: `DELETE Home.html`
- `POST`: create or add a new item to the database
    - ` POST` data to the database: `POST database/annie/liu/Female/12345@gmail.com/123456789`
- `PUT`: modify or replace the current data with the requested data
    - `PUT` data to the database: `PUT database/1/firstName/annie`


## Dependency Management

We imported two jar files in `lib` folder: 
- `gson-2.7.jar`: provides helper functions and common JSON structures using GSON.
- `json-simple-1.1.1.jar`: deserialize and serialize JSON


## Setting Up the Envinronment
- In order to run Java in Visual Studio Code, please follow the link and install the coding pack and the Java extension pack: https://code.visualstudio.com/docs/languages/java#_install-visual-studio-code-for-java
- After opening Visual Studio Code, click `File` on the top left, then click `Open` to open the entire `HTTP-Client-Server` folder. 

![](https://i.imgur.com/jjaeEd7.gif)

- Once opened, go to `Java Project` and in `Referecence`, check the existence of these two jar files. If not, click `+`, browse to `HTTP-Client-Server/lib/`, and manually add the jar files.

![](https://i.imgur.com/3Al9qJ1.gif)

- Then open `setting.json` file in `.vscode`, check if you see the below information

![](https://i.imgur.com/wFWavY4.png)

- Now, you are ready to run the server. 

## Running HTTP Client-Server
- Right click `httpSever.java`, then click `Run`. You should see 
`Listening for connection on port 8082 ....` 
in the terminal if the server runs successfully. 
- Right click `Client.java`, then click `Run`. You should see
`Starting client server...`
`Enter your request:` 
in the terminal if the client runs successfully.
- Then enter your request. Check Features on how to enter the requests.

![](https://i.imgur.com/uuurh88.gif)


## Assumptions and Limitations

## Learn More
Check out the slides: https://docs.google.com/presentation/d/195akDiXrJWZK4GMb9WR8_6hf7aldFTZeucLE-SQr5JI/edit?usp=sharing
>>>>>>> 9c3097062c9c371ed48b98803e1e2a1a0cdb3f20
