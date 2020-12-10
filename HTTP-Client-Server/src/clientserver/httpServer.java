package clientserver;

import java.net.*;
import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;
import org.json.simple.*;
import org.json.simple.parser.*;
import com.google.gson.*;

public class httpServer implements Runnable{

    private Socket client;
    private InputStreamReader request;
    private PrintWriter os;
    private BufferedReader reader;
    private static int counter = 0;

    public httpServer(Socket c) {
        super();
        client = c;
    }

    /**
     * This parses the input received from the client.
     */
    private void parseRequest() {
        String line = "";
        try{
            line = reader.readLine();
        }catch (IOException ex){
            response(404);
        }
        
        // Get command from input
        String[] str = line.split(" ")[1].split("/");
        String link = str[1] + ".json";
        if(line.contains("GET")){
            String key = str[2];
            getRequest(link, key);
        }else if(line.contains("DELETE")){
            String key = str[2];
            deleteRequest(link, key);
        }else if(line.contains("PUT")){
            String firstname = str[2];
            String lastname = str[3];
            String gender = str[4];
            String age = str[5];
            String phoneNum = str[6];
            putRequest(link, firstname, lastname, gender, age, phoneNum);
        }
        // else if(input.split("\n")[0].contains("POST")) handleRequest("POST", linkRequested, os);
    }

    /**
     * This function performs GET request: requests a representation of the specified resource
     * 
     * @param String link
     * @param String key
     */
    private void getRequest(String link, String key){
        String result = "";
        JSONParser jsonParser = new JSONParser();
        try (FileReader fileReader = new FileReader(link)){
            //Read JSON file
            Object obj = jsonParser.parse(fileReader);
            JSONArray database = (JSONArray) obj;

            for(Object temp: database){
                JSONObject tem = (JSONObject) temp;
                if(tem.keySet().contains(key)){
                    result = temp.toString();
                    response(200);
                    os.write("Content type: text/plain \r\n");
                    os.write(result);
                    os.flush();
                    break;
                }
            }
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
     * This function performs DELETE request: deletes the specified resource
     * 
     * @param String link
     * @param String key
     */
    private void deleteRequest(String link, String key){
        JSONParser jsonParser = new JSONParser();
        try (FileReader fileReader = new FileReader(link)){
            //Read JSON file
            Object obj = jsonParser.parse(fileReader);
            JSONArray database = (JSONArray) obj;
           
            for(Object temp: database){
                JSONObject tem = (JSONObject) temp;
                if(tem.keySet().contains(key)){
                    database.remove(tem);
                    break;
                }
            }

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String jsonString = gson.toJson(database);
            FileWriter file = new FileWriter(link, false);
            file.write(jsonString);
            file.close();
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
     * This function performs PUT request
     * 
     * @param String link
     * @param String information
     */
    private void putRequest(String link, String firstname, String lastname, String gender, String age, String phoneNum){
        JSONParser jsonParser = new JSONParser();
        try (FileReader fileReader = new FileReader(link)){
            //Read JSON file
            Object obj = jsonParser.parse(fileReader);
            JSONArray database = (JSONArray) obj;

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("firstName", firstname);
            jsonObject.put("lastName", lastname);
            jsonObject.put("gender", gender);
            jsonObject.put("age", age);
            jsonObject.put("phoneNumbers", phoneNum);

            database.add(jsonObject);

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String jsonString = gson.toJson(database);
            FileWriter file = new FileWriter(link, true);
            file.write(jsonString);
            file.close();
            response(200);
        } catch (IOException e) {
            response(304);
        } catch(ParseException e){
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    /**
     * This function get responseCode and write back to client
     * 
     * @param int responseCode
     */
    private void response(int responseCode){
        if(responseCode == 200){
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
            os.write("HTTP/1.1 304 Not Modifies\r\n");
            os.write("Date: " + getTime() + "\r\n");
            os.write("Server: localhost\r\n");
            os.write("\r\n");
        }else if(responseCode == 201){
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
            request = new InputStreamReader(client.getInputStream());
            reader = new BufferedReader(request);
            os = new PrintWriter(client.getOutputStream());
            
            // Parse Input
            parseRequest();
        
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