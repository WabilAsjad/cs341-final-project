package clientserver;

import org.json.simple.parser.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import java.io.*;
import com.google.gson.*;

public class formatJSON {
    public static void main(String[] args){
        JSONParser jsonParser = new JSONParser();
        try{
            //Read JSON file
            Object obj = jsonParser.parse(new FileReader("database.json"));
            JSONArray database = (JSONArray) obj;

            JSONArray newDatabase = new JSONArray();
            int count = 1;
            JSONObject jsonObject;
            for(Object j: database){
                jsonObject = new JSONObject();
                jsonObject.put(Integer.toString(count), j);
                newDatabase.add(jsonObject);
                count++;
            }

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String jsonString = gson.toJson(newDatabase);
            FileWriter file = new FileWriter("database.json", false);
            file.write(jsonString);
            file.close();
        }catch (FileNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }catch (ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}