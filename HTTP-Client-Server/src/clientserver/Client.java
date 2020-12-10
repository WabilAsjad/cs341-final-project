package clientserver;

import java.net.*;
import java.io.*;
import java.util.Scanner;

public class Client {

    public static void receiveRequests(String command, String clientRequest) throws IOException{
        Socket clientSocket = new Socket("localhost", 8082);
        
        System.out.println("------------------");
        System.out.println("Connected");
        System.out.println("------------------");

        InputStreamReader request = new InputStreamReader(clientSocket.getInputStream());
        BufferedReader reader = new BufferedReader(request);
        PrintWriter os = new PrintWriter(clientSocket.getOutputStream());

        if(command.equals("GET")) getRequest(command, clientRequest, clientSocket, reader, os);
        else if(command.equals("DELETE")) deleteRequest(command, clientRequest, clientSocket, reader, os);
        else if(command.equals("PUT")) putRequest(command, clientRequest, clientSocket, reader, os);
    }

    public static void getRequest(String command, String clientRequest,
            Socket clientSocket, BufferedReader reader, PrintWriter os) throws IOException{
        os.write("GET /" + clientRequest + "/ HTTP/1.1\r\n");
        os.write("Host: localhost\r\n");
        os.write("Connection: close\r\n");
        os.write("\r\n");
        os.flush();
        System.out.println("GET Request Sent.");
        System.out.println("------------------");

        String response;
        while((response = reader.readLine()) != null){
            System.out.println(response);
        }

        os.close();
        reader.close();
        clientSocket.close();
    }

    public static void putRequest(String command, String clientRequest,
                Socket clientSocket, BufferedReader reader, PrintWriter os) throws IOException{
        os.write("PUT /" + clientRequest + "/ HTTP/1.1\r\n");
        os.write("Host: localhost\r\n");
        os.write("Languge-Accepted: en-us\r\n");
        os.write("Connection: Keep-Alive\r\n");
        os.write("Content type: text/html\r\n");
        os.write("Content length: 0\r\n");
        os.write("\r\n");

        System.out.println("PUT Request Header Sent.");
        System.out.println("------------------");

        String response;
        while((response = reader.readLine()) != null){
            System.out.println(response);
        }

        os.close();
        reader.close();
        clientSocket.close();
    }

    public static void deleteRequest(String command, String clientRequest,
        Socket clientSocket, BufferedReader reader, PrintWriter os) throws IOException{
        os.write("DELETE /" + clientRequest + "/ HTTP/1.1\r\n");
        os.write("Host: localhost\r\n");
        os.write("Connection: close\r\n");
        os.write("\r\n");
        os.flush();
        System.out.println("DELETE Request Sent.");
        System.out.println("------------------");

        String response;
        while((response = reader.readLine()) != null){
            System.out.println(response);
        }

        os.close();
        reader.close();
        clientSocket.close();
    }

    public static void main(String[] args) throws IOException{
        Scanner scan = new Scanner(System.in);
        System.out.println("Starting client server...");
        System.out.println("Enter your request:");
        String input = scan.nextLine();
        String[] argv = input.split(" ");
        String command = argv[0];
        String requests = argv[1];   
        receiveRequests(command, requests);
        scan.close();
    }
}
