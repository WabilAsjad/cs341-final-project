package clientserver;

import java.net.*;
import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;
import org.json.simple.*;
import org.json.simple.parser.*;
import com.google.gson.*;
import java.nio.file.*;
import java.nio.charset.*;

public class httpServer implements Runnable{

    private Socket client;
    private BufferedReader reader;
    private PrintWriter os;
    private static int counter = 0;
    private String link = "";
    private String key = "";
    private String key2 = "";
    private String value = "";

    public httpServer(Socket c) {
        super();
        client = c;
    }

    /**
     * This function parses the input received from the client.
     */
    private void parseRequest() {
        try{
            // Read the first line from client's input
            String line = reader.readLine();
            while(line.equals("")){
                line = reader.readLine();
            }
            String[] str = line.split(" ")[1].split("/");

            // Get command from input and determines whether it's GET/DELETE/PUT/POST
            if(line.contains("GET")){
                getAndHeadRequests(str, "GET");
            }else if(line.contains("HEAD")){
                getAndHeadRequests(str, "HEAD");
            }else if(line.contains("DELETE")){
                deleteRequest(str);
            }else if(line.contains("PUT")){
                putRequest(str);
            }else if(line.contains("POST")){
                postRequest(str);
            }
        }catch (IOException ex){
            response(404);
            os.flush();
        }
    }

    /**
     * This function performs GET request: requests a representation of the specified resource
     * 
     * @param String[] str
     * @param String command
     */
    private void getAndHeadRequests(String[] str, String command){
        // check number of arguments in client's input
        // no argument.
        if(str.length < 2){
            response(404);
            os.flush();
        // only one argument. ex: database or Home.html
        }else if(str.length == 2){
            if(str[1].endsWith("html") || str[1].endsWith("htm")){
                readHTMLFile(str[1], command);
            }else{
                link = str[1] + ".json";
                readWholeJSONFile(link, command);
            }
        }else{
            // two arguments. ex: database/1
            if(str.length >= 3){
                link = str[1] + ".json";
                key = str[2];
            // three arguments. ex: database/1/gender
            }
            if(str.length >= 4){
                key2 = str[3];
            // four arguments (PUT) ex: database/1/gender/man
            }
            if(str.length >= 5){
                value = str[4];
            }
            getJSONFile(link, command);
        }
    }

    /**
     * This function reads the whole HTML file
     * 
     * @param String link
     * @param String command
     */
    private void readHTMLFile(String link, String command){
        try{
            // Open the file
            File file = new File(link);
            int length = (int)file.length();

            // Check if the file exists and is not a directory
            if(!file.exists() || file.isDirectory()){
                response(404);
                os.flush();
            }else{
                // Send response to the client
                response(200);
                os.write("Content type: text/html \r\n");
                os.write("Content length: " + length + "\r\n");
                if(command.equals("GET")){
                    // Read all contents in the file
                    byte[] fileBytes = Files.readAllBytes(Paths.get(link));
                    String fileString = new String(fileBytes, StandardCharsets.UTF_8);
                    os.write(fileString);
                }
                os.flush();
            }
        }catch(IOException e){
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * This function reads the JSON file with key
     * 
     * @param String link
     * @param String command
     */
    private void getJSONFile(String link, String command){
        // Initialize variable
        String jsonString = "";
        JSONParser jsonParser = new JSONParser();
        try (FileReader fileReader = new FileReader(link)){
            //Read JSON file
            Object obj = jsonParser.parse(fileReader);
            JSONArray database = (JSONArray) obj;

            // Iterate through JSONArray
            for(Object temp: database){
                JSONObject tem = (JSONObject) temp;
                // If we find the JSONObject which client requested
                if(tem.keySet().contains(key)){
                    if(!key2.equals("") && ((JSONObject)tem.get(key)).keySet().contains(key2)){
                        jsonString = ((JSONObject)tem.get(key)).get(key2).toString();
                    }else{
                        Gson gson = new GsonBuilder().setPrettyPrinting().create();
                        jsonString = gson.toJson(tem);
                    }
                    // Send response to the client
                    response(200);
                    os.write("Content type: text/plain \r\n");
                    if(command.equals("GET")) os.write(jsonString);
                    os.flush();
                    return;
                }
            }
            // If we do not find the JSONObject which client requested
            response(404);
            os.write("Contents not in the file. \r\n");
            os.flush();
        } catch (FileNotFoundException e) {
            response(404);
            os.flush();
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        } catch(ParseException e){
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    /**
     * This function reads the whole JSON file
     * 
     * @param String link
     * @param String command
     */
    private void readWholeJSONFile(String link, String command){
        // Initialize parser
        JSONParser jsonParser = new JSONParser();
        try (FileReader fileReader = new FileReader(link)){
            // Send response to the client
            response(200);
            os.write("Content type: text/plain \r\n");
            if(command.equals("GET")){
                //Read JSON file
                Object obj = jsonParser.parse(fileReader);
                JSONArray database = (JSONArray) obj;

                // write jsonString in JSON format using Gson
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                String jsonString = gson.toJson(database);
                os.write(jsonString);
            }
            os.flush();
        }catch (FileNotFoundException e) {
            response(404);
            os.flush();
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }catch(ParseException e){
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    /**
     * This function performs DELETE request: deletes the specified resource
     * 
     * @param String[] str
     */
    private void deleteRequest(String[] str){
        // check number of arguments in client's input
        // no argument.
        if(str.length < 2){
            response(404);
            os.flush();
        // only one argument. ex: database or Home.html
        }else if(str.length == 2){
            if(str[1].endsWith("html") || str[1].endsWith("htm")){
                link = str[1];
            }else{
                link = str[1] + ".json";
            }
            deleteWholeFile(link);
        }else{
            // two arguments. ex: database/1
            if(str.length >= 3){
                link = str[1] + ".json";
                key = str[2];
            // three arguments. ex: database/1/gender
            }
            if(str.length >= 4){
                key2 = str[3];
            // four arguments (PUT) ex: database/1/gender/man
            }
            if(str.length >= 5){
                value = str[4];
            }
            deleteJSON(link);
        }
    }

    /**
     * This function performs DELETE request on whole file
     * 
     * @param String link
     */
    private void deleteWholeFile(String link){
        String deleteMessage = "";
        // Initialize deleteMessage for database and HTML
        if(link.endsWith("json")){
            deleteMessage = "File deleted.";
        }else{
            deleteMessage = "<html>\n<body>\n<h1>File deleted.</h1>\n</body>\n</html>";
        }

        // Open the file
        File file = new File(link);
        // Check if the file exists and is not a directory
        if(!file.exists() || file.isDirectory()){
            response(404);
            os.flush();
        }else{
            // Write delete message to file using FileWriter
            FileWriter filewriter;
            try{
                filewriter = new FileWriter(link);
                filewriter.write(deleteMessage);
                filewriter.close();
                // Send reponse to client
                response(200);
                os.write(deleteMessage);
                os.flush();
            }catch(IOException ex){
                response(404);
                os.flush();
            }
        }
    }
    
    /**
     * This function performs DELETE request on JSON database
     * 
     * @param String link
     */
    private void deleteJSON(String link){
        // Initialize parser
        JSONParser jsonParser = new JSONParser();
        try (FileReader fileReader = new FileReader(link)){
            //Read JSON file
            Object obj = jsonParser.parse(fileReader);
            JSONArray database = (JSONArray) obj;
           
            // Iterate through JSONArray and delete the target
            for(int i = 0; i < database.size(); i++){
                JSONObject tem = (JSONObject) database.get(i);
                if(!key.equals("") && tem.keySet().contains(key)){
                    database.remove(tem);
                    if(!key2.equals("") && ((JSONObject)tem.get(key)).keySet().contains(key2)){ 
                        ((JSONObject)tem.get(key)).remove(key2);
                        database.add(i, tem);
                    }
                    break;
                }
            }
            // Write back to JSON file
            writeJSONFile(database, link);
            // Send response to client
            response(200);
            os.write("Successfully delete: " + key + " from " + link);
            os.flush();
        } catch (FileNotFoundException e) {
            response(404);
            os.flush();
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        } catch(ParseException e){
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    /**
     * This function performs PUT request: modify or replace the current data with the requested data
     * 
     * @param String[] str
     */
    private void putRequest(String[] str){
        // Check if there are enough arguments
        if(str.length < 5){
            response(404);
            os.flush();
        }

        // Parse the arguments
        link = str[1] + ".json";
        key = str[2];
        key2 = str[3];
        value = str[4];

        JSONParser jsonParser = new JSONParser();
        try (FileReader fileReader = new FileReader(link)){
            //Read JSON file
            Object obj = jsonParser.parse(fileReader);
            JSONArray database = (JSONArray) obj;
           
            // Update the data
            for(int i = 0; i < database.size(); i++){
                JSONObject tem = (JSONObject) database.get(i);
                if(!key.equals("") && tem.keySet().contains(key)){
                    if(!key2.equals("") && ((JSONObject)tem.get(key)).keySet().contains(key2)){ 
                        if(!value.equals("")){
                            database.remove(tem);
                            ((JSONObject)tem.get(key)).replace(key2, value);
                            database.add(i, tem);
                        }
                    }
                    break;
                }           
            }

            // Write back to JSON file
            writeJSONFile(database, link);
            response(200);
            os.write("Successfully update: " + key + " from " + link);
            os.flush();
        } catch (FileNotFoundException e) {
            response(404);
            os.flush();
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        } catch(ParseException e){
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    /**
     * This function writes/updates JSON file
     * 
     * @param JSONArray database
     * @param String link
     */
    private void writeJSONFile(JSONArray database, String link){
        try{
            // Writing jsonString in JSON format using Gson
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String jsonString = gson.toJson(database);
            FileWriter file = new FileWriter(link, false);
            file.write(jsonString);
            file.close();
        }catch (FileNotFoundException e) {
            response(404);
            os.flush();
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * This function performs POST request: create or add a new item JSON database
     * 
     * @param String link
     */
    private void postRequest(String[] str){
        // Check if there is at least one argument
        if(str.length < 2){
            response(404);
            os.flush();
        }
        String link = str[1] + ".json";
        postJSONRequest(link, str);
    }
  
    /**
     * This function performs POST request on JSON database
     * 
     * @param String link
     * @param String[] str
     */
    private void postJSONRequest(String link, String[] str){
        try{
            // Open the file
            File myObj = new File(link);
            // If the file does not exist, create the file
            if(myObj.createNewFile()){
                JSONArray database = new JSONArray();
                writeJSONFile(database, link);
                response(201);
                os.write("Content-Location: /" + myObj.getPath());
                os.flush();
            }else{
                // Check if there are enough arguments
                if(str.length != 7){
                    response(404);
                    os.write("Not enough information.");
                    os.flush();
                }else{
                    addJSONObject(link, str);
                    response(200);
                    os.flush();
                }
            }
        } catch (IOException e) {
            response(404);
            os.flush();
        }
    }
    
    /**
     * This function adds a JSON Object to the database
     * 
     * @param String link
     * @param String[] str
     */
    private void addJSONObject(String link, String[] str){
        JSONParser jsonParser = new JSONParser();
        try{
            //Read JSON file
            Object obj = jsonParser.parse(new FileReader(link));
            JSONArray database = (JSONArray) obj;

            // Add data to JSONObject
            JSONObject jsonObject = new JSONObject();
            JSONObject json = new JSONObject();
            jsonObject.put("firstName", str[2]);
            jsonObject.put("lastName", str[3]);
            jsonObject.put("gender", str[4]);
            jsonObject.put("email", str[5]);
            jsonObject.put("phoneNumbers", str[6]);
            json.put(database.size()+1, jsonObject);

            // Add JSONObject to database
            database.add(database.size(), json);

            // Write back to the file
            writeJSONFile(database, link);
        }catch(ParseException e){
            System.out.println("Error: " + e.getMessage());
        }catch (IOException e) {
            response(404);
            os.flush();
        }
    }
    
    /**
     * This function gets responseCode and write back to client
     * 
     * @param int responseCode
     */
    private void response(int responseCode){
        if(responseCode == 200){
            // 200 Successfull
            os.write("HTTP/1.1 200 OK\r\n");
            os.write("Date: " + getTime() + "\r\n");
            os.write("Server: localhost\r\n");
        }else if(responseCode == 404){
            // 404 page not found
            os.write("HTTP/1.1 404 Not Found\r\n");
            os.write("Date: " + getTime() + "\r\n");
            os.write("Server: localhost\r\n");
            os.write("\r\n");
        }else if(responseCode == 201){
            // 201 Created
            os.write("HTTP/1.1 201 Created\r\n");
            os.write("Date: " + getTime() + "\r\n");
            os.write("Server: localhost\r\n");
        }
    }

    /**
     * This function gets the TimeStamp
     * 
     * @return 
     */
    private String getTime(){
        Date date = new Date();
        SimpleDateFormat form = new SimpleDateFormat("MM/dd/yyyy h:mm:ss a, zzzz");
        return form.format(date);
    }

    public static void main(String[] args) {
        try {
            // Instantiate server object on port
            final ServerSocket server = new ServerSocket(8082);
            System.out.println("Listening for connection on port 8082 ....");

            while (true) {
                // Get information from port 8082 and create client object
                httpServer clientServer = new httpServer(server.accept());
                counter++;
                System.out.println("Client NO: " + counter);
                // Multi-threaded server
                Thread thread = new Thread(clientServer);
                thread.start();
            }
        }catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    @Override
    public void run(){
        try {
            // Read HTTP request from the client socket
            reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
            os = new PrintWriter(client.getOutputStream());
            
            // Parse Input
            parseRequest();
                    
            // Close the socket
            System.out.println("Closing the connection.");
            if(reader != null){
                reader.close();
                System.out.println("Socket input stream closed.");
            }
            if(os != null){
                os.close();
                System.out.println("Socket output stream closed.");
            }
            if(client != null){
                client.close();
                System.out.println("Socket closed.");
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}