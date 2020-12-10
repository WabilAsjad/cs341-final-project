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
    private void parseRequest() {
        String line = "";
        try{
            line = reader.readLine();
            String[] str = line.split(" ")[1].split("/");

            // Get command from input
            if(line.contains("GET")){
                getRequest(str);
            }else if(line.contains("DELETE")){
                deleteRequest(str);
            }else if(line.contains("PUT")){
                putRequest(str);
            }
        }catch (IOException ex){
            response(404);
            os.flush();
        }
        // }else if(line.contains("POST")){
        //     postRequest(str);
        // }
    }

    /**
     * This function performs GET request: requests a representation of the specified resource
     * 
     * @param String[] str
     */
    private void getRequest(String[] str){
        // check number of arguments
        checkRequests(str);

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
     * This function checks the number of arguments which client requested
     * 
     * @param String[] str
     */
    private void checkRequests(String[] str){
        // no argument.
        if(str.length < 2){
            response(404);
            os.flush();
        // only one argument. ex: database
        }else if(str.length == 2){
            link = str[1] + ".json";
            readWholeJSONFile(link);
        // two arguments. ex: database/1
        }else if(str.length == 3){
            link = str[1] + ".json";
            key = str[2];
        // three arguments. ex: database/1/gender
        }else if(str.length == 4){
            link = str[1] + ".json";
            key = str[2];
            key2 = str[3];
        // four arguments (PUT) ex: database/1/gender/man
        }else if(str.length == 5){
            link = str[1] + ".json";
            key = str[2];
            key2 = str[3];
            value = str[4];
        }
    }

    /**
     * This function read the whole JSON file
     * 
     * @param String link
     */
    private void readWholeJSONFile(String link){
        JSONParser jsonParser = new JSONParser();
        try (FileReader fileReader = new FileReader(link)){
            //Read JSON file
            Object obj = jsonParser.parse(fileReader);
            JSONArray database = (JSONArray) obj;

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String jsonString = gson.toJson(database);
            response(200);
            os.write(jsonString);
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
        // check number of arguments
        checkRequests(str);

        JSONParser jsonParser = new JSONParser();
        try (FileReader fileReader = new FileReader(link)){
            //Read JSON file
            Object obj = jsonParser.parse(fileReader);
            JSONArray database = (JSONArray) obj;
           
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
            writeJSONFile(database, link);
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
     * This function performs PUT request: 
     * 
     * @param String[] str
     */
    private void putRequest(String[] str){
        // check number of arguments
        checkRequests(str);

        System.out.println("here");
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
                            database.remove(i);
                            ((JSONObject)tem.get(key)).put(key2, value);
                            database.add(i, tem);
                        }
                    }
                    break;
                }           
            }

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

    private void writeJSONFile(JSONArray database, String link){
        try{
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