package clientserver;

import java.io.IOException;
import java.util.concurrent.*;
import java.util.*;

public class Measurements {
    private static String command;
    private static ExecutorService pool;
    private static ArrayList<String> requests;

    public static void main(String[] args) {
        requests = getList();
        Random rand = new Random();

        int num = rand.nextInt(5);
        command = requests.get(num).toString();
        System.out.println("Commands: " + command);

        Runnable run = new Runnable() {
            @Override
            public void run() {
                try {
                    Client.runClient(command);
                } catch (IOException ex) {
                    System.out.println("Error: " + ex.getMessage());
                    pool.shutdown();
                }
            }
        };

        long startTime = System.nanoTime();
        pool = Executors.newFixedThreadPool(50);
        int count = 1;
        while (count != 50) {
            pool.execute(run);
            count++;
        }
        pool.shutdown();
        long endTime = System.nanoTime();
        long duration = (endTime - startTime);
        System.out.println("Timer: " + duration / 1000000000.0 + " seconds.");
    }

    public static ArrayList getList() {
        requests = new ArrayList<String>();
        requests.add("GET database");
        requests.add("GET Home.html");
        requests.add("GET database/10");
        requests.add("GET database/8/phoneNumbers");
        requests.add("GET database/1/firstName");
        requests.add("PUT database/1/gender/female");

        requests.add("GET database/212");
        requests.add("PUT database/20/email/gtongj@school.edu");
        requests.add("GET database/952");
        requests.add("PUT database/520/firstName/wabil");
        requests.add("HEAD database");

        requests.add("GET database/912");
        requests.add("PUT database/993/lastName/Geos");
        requests.add("GET database/753");
        requests.add("DELETE database/3");
        requests.add("DELETE database/999/lastName/deleting");

        requests.add("GET database/628");
        requests.add("DELETE database/999");
        requests.add("POST database/");
        requests.add("POST database/");
        requests.add("POST database/");

        requests.add("POST database/");
        requests.add("GET database/617");
        requests.add("DELETE database/82");
        requests.add("DELETE database/72");
        requests.add("PUT database/791/firstName/wabil");

        requests.add("POST database/");
        requests.add("GET database/1002");
        requests.add("DELETE database/327");
        requests.add("PUT database/759/firstName/annie");
        requests.add("HEAD database");

        requests.add("DELETE database/351");
        requests.add("PUT database/247/firstName/annie");
        requests.add("GET database/1999"); // Error check
        requests.add("GET database/459");
        requests.add("GET database/289");

        requests.add("PUT database/481/gender/Male");
        requests.add("PUT database/887/lastName/testing");
        requests.add("PUT database/853/gender/Female");
        requests.add("PUT database/422/gender/Female");
        requests.add("PUT database/333/gender/Female");

        requests.add("POST database/");
        requests.add("GET database/241");
        requests.add("PUT database/667/email/fdgjkdfhg@email.edu");
        requests.add("PUT database/622/firstName/testing");
        requests.add("POST database/");

        return requests;
    }
}

// Throughput - measure of how many units of information a system can process in
// a given amount of time
// How do the types of requests (GET,POST) made by clients affect throughput?
// How does multithreading affect throughput?
// Create multiple clients that perform different requests on a server
// Latency - the delay between a user's action and a web application's response
// to that action
// How does our server handle requests of varying sizes (i.e. simple HTML pages
// vs pages loaded with JS)
// Have a mix of clients that make big or small requests, and requests with low
// or high frequency.
// For example, there will be a client making many small requests, or a client
// making a very large request.
// Or a client making very small requests but more frequently.
// As the clients grow, does latency increase?
