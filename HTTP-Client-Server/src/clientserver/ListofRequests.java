 package clientserver;

 import java.util.*;

 public class ListofRequests{
    private static ArrayList<String> requests;

     public static ArrayList getList(){
        requests = new ArrayList<String>();
        requests.add("GET database");
        requests.add("PUT database/1/gender/ajkd");
        return requests;
     }
 }