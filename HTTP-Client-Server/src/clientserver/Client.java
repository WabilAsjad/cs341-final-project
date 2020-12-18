package clientserver;

import java.net.*;
import java.io.*;
import java.util.Scanner;

public class Client {

    private static BufferedReader reader;
    private static PrintWriter os;

    public Client(){
        super();
    }

    /**
     * This function receives requests
     * Detemine whether the request is GET, HEAD, DELETE, POST, PUT
     * 
     * @param String command
     * @param String clientRequest
     */
    public static void receiveRequests(String command, String clientRequest) throws IOException{
        Socket clientSocket = new Socket("localhost", 8082);
        
        System.out.println("------------------");
        System.out.println("Connected");
        System.out.println("------------------");

        // Initialize BufferedReader and PrintWriter
        reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        os = new PrintWriter(clientSocket.getOutputStream());

        // Determine whether the request is GET, HEAD, DELETE, PUT, or POST
        if(command.equals("GET")) getRequest(command, clientRequest, clientSocket, reader, os);
        else if(command.equals("HEAD")) headRequest(command, clientRequest, clientSocket, reader, os);
        else if(command.equals("DELETE")) deleteRequest(command, clientRequest, clientSocket, reader, os);
        else if(command.equals("PUT")) putRequest(command, clientRequest, clientSocket, reader, os);
        else if(command.equals("POST")) postRequest(command, clientRequest, clientSocket, reader, os);
    }

    /**
     * This function sends GET request 
     * 
     * @param String command
     * @param String clientRequest
     * @param Socket clientSocket
     * @param BufferedReader reader
     * @param PrintWriter os
     */
    public static void getRequest(String command, String clientRequest,
            Socket clientSocket, BufferedReader reader, PrintWriter os) throws IOException{
        // Send request to the server
        os.write("GET /" + clientRequest + "/ HTTP/1.1\r\n");
        os.write("Host: localhost\r\n");
        os.write("Connection: close\r\n");
        os.write("\r\n");
        os.flush();
        System.out.println("GET Request Sent.");
        System.out.println("------------------");

        // Receives and print response from the server
        String response;
        while((response = reader.readLine()) != null){
            System.out.println(response);
        }

        os.close();
        reader.close();
        clientSocket.close();
    }

    /**
     * This function sends PUT request 
     * 
     * @param String command
     * @param String clientRequest
     * @param Socket clientSocket
     * @param BufferedReader reader
     * @param PrintWriter os
     */
    public static void putRequest(String command, String clientRequest,
                Socket clientSocket, BufferedReader reader, PrintWriter os) throws IOException{
        // Send request to the server
        os.write("PUT /" + clientRequest + "/ HTTP/1.1\r\n");
        os.write("Host: localhost\r\n");
        os.write("Languge-Accepted: en-us\r\n");
        os.write("Connection: Keep-Alive\r\n");
        os.write("Content type: text/plain\r\n");
        os.write("Content length: 0\r\n");
        os.write("\r\n");
        os.flush();

        System.out.println("PUT Request Sent.");
        System.out.println("------------------");

        // Receives and print response from the server
        String response;
        while((response = reader.readLine()) != null){
            System.out.println(response);
        }

        os.close();
        reader.close();
        clientSocket.close();
    }

    /**
     * This function sends DELETE request 
     * 
     * @param String command
     * @param String clientRequest
     * @param Socket clientSocket
     * @param BufferedReader reader
     * @param PrintWriter os
     */
    public static void deleteRequest(String command, String clientRequest,
        Socket clientSocket, BufferedReader reader, PrintWriter os) throws IOException{
        // Send request to the server
        os.write("DELETE /" + clientRequest + "/ HTTP/1.1\r\n");
        os.write("Host: localhost\r\n");
        os.write("Connection: close\r\n");
        os.write("\r\n");
        os.flush();
        System.out.println("DELETE Request Sent.");
        System.out.println("------------------");

        // Receives and print response from the server
        String response;
        while((response = reader.readLine()) != null){
            System.out.println(response);
        }

        os.close();
        reader.close();
        clientSocket.close();
    }

    /**
     * This function sends POST request 
     * 
     * @param String command
     * @param String clientRequest
     * @param Socket clientSocket
     * @param BufferedReader reader
     * @param PrintWriter os
     */
    public static void postRequest(String command, String clientRequest,
                Socket clientSocket, BufferedReader reader, PrintWriter os) throws IOException{
        // Send request to the server
        os.write("POST /" + clientRequest + "/ HTTP/1.1\r\n");
        os.write("Host: localhost\r\n");
        os.write("Languge-Accepted: en-us\r\n");
        os.write("Connection: Keep-Alive\r\n");
        os.write("Content type: text/plain\r\n");
        os.write("Content length: 0\r\n");
        os.write("\r\n");
        os.flush();

        System.out.println("POST Request Sent.");
        System.out.println("------------------");

        // Receives and print response from the server
        String response;
        while((response = reader.readLine()) != null){
            System.out.println(response);
        }

        os.close();
        reader.close();
        clientSocket.close();
    }

    /**
     * This function sends HEAD request 
     * 
     * @param String command
     * @param String clientRequest
     * @param Socket clientSocket
     * @param BufferedReader reader
     * @param PrintWriter os
     */
    public static void headRequest(String command, String clientRequest,
    Socket clientSocket, BufferedReader reader, PrintWriter os) throws IOException{
        // Send request to the server
        os.write("HEAD /" + clientRequest + "/ HTTP/1.1\r\n");
        os.write("Host: localhost\r\n");
        os.write("Connection: close\r\n");
        os.write("\r\n");
        os.flush();
        System.out.println("HEAD Request Sent.");
        System.out.println("------------------");

        // Receives and print response from the server
        String response;
        while((response = reader.readLine()) != null){
            System.out.println(response);
        }

        os.close();
        reader.close();
        clientSocket.close();
    }

    public static void main(String[] args) throws IOException{
        // Get input from command line
        Scanner scan = new Scanner(System.in);
        System.out.println("Starting client server...");
        System.out.println("Enter your request:");
        String input = scan.nextLine();
        scan.close();
        // Parse the input
        String[] argv = input.split(" ");
        if(argv.length < 2){
            System.out.println("Please enter at least one argument.");
            return;
        }
        String command = argv[0];
        String requests = argv[1];
        // Pass the input to receiveRequests
        receiveRequests(command, requests);
    }

    /**
     * This function is called when doing measurements
     * 
     * @param String INPUT
     */
    public static void runClient(String input) throws IOException{
        String[] argv = input.split(" ");
        String command = argv[0];
        String requests = argv[1];   
        receiveRequests(command, requests);
    }
}