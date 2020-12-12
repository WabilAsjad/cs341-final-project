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
     * This parses the input received from the client.
     */
    private void parseRequest(BufferedReader reader, PrintWriter os) {
        try{
            // Read the first line from client's input
            System.out.println("hi");
            String line = reader.readLine();
            while(line.equals("")){
                line = reader.readLine();
            }
            System.out.println(line);
            String[] str = line.split(" ")[1].split("/");

            // Get command from input and determines whether it's GET/DELETE/PUT/POST
            if(line.contains("GET")){
                getRequest(str, os);
            }else if(line.contains("DELETE")){
                deleteRequest(str, os);
            }else if(line.contains("PUT")){
                putRequest(str, os);
            }
             // }else if(line.contains("POST")){
        //     postRequest(str);
        // }
        }catch (IOException ex){
            response(404, os);
            os.flush();
        }
    }

    /**
     * This function performs GET request: requests a representation of the specified resource
     * 
     * @param String[] str
     */
    private void getRequest(String[] str, PrintWriter os){
        // check number of arguments in client's input
        // no argument.
        if(str.length < 2){
            response(404, os);
            os.flush();
        // only one argument. ex: database or Home.html
        }else if(str.length == 2){
            if(str[1].endsWith("html") || str[1].endsWith("htm")){
                readHTMLFile(str[1], os);
            }else{
                link = str[1] + ".json";
                readWholeJSONFile(link, os);
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
            getJSONFile(link, os);
        }
    }

    /**
     * This function read the whole HTML file
     * 
     * @param String link
     */
    private void readHTMLFile(String link, PrintWriter os){
        try{
            // Open the file
            File file = new File(link);
            int length = (int)file.length();

            // Check if the file exists and is not a directory
            if(!file.exists() || file.isDirectory()){
                response(404, os);
                os.flush();
            }else{
                // Read all contents in the file
                byte[] fileBytes = Files.readAllBytes(Paths.get(link));
                String fileString = new String(fileBytes, StandardCharsets.UTF_8);
                // Send response to the client
                response(200, os);
                os.write("Content type: text/html \r\n");
                os.write("Content length: " + length + "\r\n");
                os.write(fileString);
                os.flush();
            }
        }catch(IOException e){
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * This function read the JSON file with key
     * 
     * @param String link
     */
    private void getJSONFile(String link, PrintWriter os){
        String result = "";
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
                        result = ((JSONObject)tem.get(key)).get(key2).toString();
                    }else{
                        result = tem.toJSONString();
                    }
                    // Send response to the client
                    response(200, os);
                    os.write("Content type: text/plain \r\n");
                    os.write(result);
                    os.flush();
                    break;
                }
            }
        } catch (FileNotFoundException e) {
            response(404, os);
            os.flush();
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        } catch(ParseException e){
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    /**
     * This function read the whole JSON file
     * 
     * @param String link
     */
    private void readWholeJSONFile(String link, PrintWriter os){
        JSONParser jsonParser = new JSONParser();
        try (FileReader fileReader = new FileReader(link)){
            //Read JSON file
            Object obj = jsonParser.parse(fileReader);
            JSONArray database = (JSONArray) obj;

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String jsonString = gson.toJson(database);
            // Send response to the client
            response(200, os);
            os.write("Content type: text/plain \r\n");
            os.write(jsonString);
            os.flush();
        }catch (FileNotFoundException e) {
            response(404, os);
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
    private void deleteRequest(String[] str, PrintWriter os){
        // check number of arguments in client's input
        // no argument.
        if(str.length < 2){
            response(404, os);
            os.flush();
        // only one argument. ex: database or Home.html
        }else if(str.length == 2){
            if(str[1].endsWith("html") || str[1].endsWith("htm")){
                link = str[1];
            }else{
                link = str[1] + ".json";
            }
            deleteWholeFile(link, os);
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
            deleteJSON(link, os);
        }
    }

    /**
     * This function performs DELETE request on whole file
     * 
     * @param String link
     */
    private void deleteWholeFile(String link, PrintWriter os){
        String deleteMessage = "";
        if(link.endsWith("json")){
            deleteMessage = "File deleted.";
        }else{
            deleteMessage = "<html>\n<body>\n<h1>File deleted.</h1>\n</body>\n</html>";
        }

        // Open the file
        File file = new File(link);
        // Check if the file exists and is not a directory
        if(!file.exists() || file.isDirectory()){
            response(404, os);
            os.flush();
        }else{
            // Write delete message to file using FileWriter
            FileWriter filewriter;
            try{
                filewriter = new FileWriter(link);
                filewriter.write(deleteMessage);
                filewriter.close();
                // Send reponse to client
                response(200, os);
                os.write(deleteMessage);
                os.flush();
            }catch(IOException ex){
                response(304, os);
                os.flush();
            }
        }
    }
    
    /**
     * This function performs DELETE request on JSON database
     * 
     * @param String link
     */
    private void deleteJSON(String link, PrintWriter os){
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
            writeJSONFile(database, link, os);
            // Send response to client
            response(200, os);
            os.write("Successfully delete: " + key + " from " + link);
            os.flush();
        } catch (FileNotFoundException e) {
            response(404, os);
            os.flush();
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        } catch(ParseException e){
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    /**
     * This function performs PUT request: 
     * 
     * @param String[] str
     */
    private void putRequest(String[] str, PrintWriter os){
        if(str.length < 5){
            response(404, os);
            os.flush();
        }

        link = str[1] + ".json";
        key = str[2];
        key2 = str[3];
        value = str[4];

        JSONParser jsonParser = new JSONParser();
        try (FileReader fileReader = new FileReader(link)){
            //Read JSON file
            Object obj = jsonParser.parse(fileReader);
            JSONArray database = (JSONArray) obj;
           
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

            writeJSONFile(database, link, os);
            response(200, os);
            os.write("Successfully update: " + key + " from " + link);
            os.flush();
        } catch (FileNotFoundException e) {
            response(404, os);
            os.flush();
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        } catch(ParseException e){
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    /**
     * This function write/update JSON file
     * 
     * @param String link
     */
    private void writeJSONFile(JSONArray database, String link, PrintWriter os){
        try{
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String jsonString = gson.toJson(database);
            FileWriter file = new FileWriter(link, false);
            file.write(jsonString);
            file.close();
        }catch (FileNotFoundException e) {
            response(404, os);
            os.flush();
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * This function performs POST request
     * 
     * @param String link
     * @param String information
     */
    // private void postRequest(String[] str){
    //     if(str.length < 2){
    //         response(404);
    //         os.close();
    //     }
    //     String link = str[1];
        
    //     try{
    //         File myObj = new File(link);
    //         if(myObj.createNewFile()){
    //             addJSONObject(link, str);
    //             response(201);
    //             os.write("Content-Location: /" + myObj.getPath());
    //             os.flush();
    //         }else{
    //             addJSONObject(link, str);
    //             response(200);
    //             os.flush();
    //         }
    //         os.close();
    //     } catch (IOException e) {
    //         response(404);
    //         os.close();
    //     }
    // }
    
    // private void addJSONObject(String link, String[] str){
    //     JSONParser jsonParser = new JSONParser();
    //     try{
    //         //Read JSON file
    //         Object obj = jsonParser.parse(new FileReader(link));
    //         JSONArray database = (JSONArray) obj;

    //         JSONObject jsonObject = new JSONObject();
    //         JSONObject ob = new JSONObject();
    //         jsonObject.put(database.size(), ob);
    //         ob.put("firstName", str[2]);
    //         ob.put("lastName", str[3]);
    //         ob.put("gender", str[4]);
    //         ob.put("age", str[5]);
    //         ob.put("phoneNumbers", str[6]);

    //         database.add(-1, jsonObject);

    //         writeJSONFile(database, link);
    //     }catch(ParseException e){
    //         System.out.println("Error: " + e.getMessage());
    //     }catch (IOException e) {
    //         response(404);
    //         os.close();
    //     }
    // }
    
    /**
     * This function get responseCode and write back to client
     * 
     * @param int responseCode
     */
    private void response(int responseCode, PrintWriter os){
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
        }else if(responseCode == 304){
            // 304 Not Modifies
            os.write("HTTP/1.1 304 Not Modifies\r\n");
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
     * This function get the TimeStamp
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
            BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
            PrintWriter os = new PrintWriter(client.getOutputStream());
            
            // Parse Input
            parseRequest(reader, os);
        
            // To do: wait for 10s and if there's no more requests, close the connection; otherwise open.
            
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