import java.net.*;
import java.io.*;

public class HTTPServer {

    /**
     * This parses the input received from the client.
     * 
     * @param request
     * @return
     */
    private String parseRequest(String request) {
        BufferedReader reader = new BufferedReader(request);
        String line = reader.readLine();
        String input;
        // Parse input
        while (!line.isEmpty()) {
            input += line;
            System.out.println(line);
            line = reader.readLine();
        }
        handleRequest(input);
    };

    /**
     * This function checks what kind of request was received (GET, POST, PUT,
     * DELETE), and redirects to those instructions.
     * 
     * @param String request
     * @return
     */
    private String handleRequest(String request) {
        if (request == "GET")
            ;
        {

        }
        elif(request == "POST");
        {

        }
        elif(request == "PUT");
        {

        }
        elif(request == "DELETE");
        {

        }
        else {
            String error = "This is not a valid request.";
            return error;
        }
    }

    public static void main(String[] args) {

        try {
            // Instantiate server object on port 8082
            final ServerSocket server = new ServerSocket(8082);
            System.out.println("Listening for connection on port 8082 ....");

            while (true) {
                // Get information from port 8082 and create client object
                final Socket client = server.accept();
                // Read HTTP request from the client socket
                InputStreamReader request = new InputStreamReader(clientSocket.getInputStream());
                String parsed_info = parseRequests(request);

                // 2. Prepare an HTTP response

                // 3. Send HTTP response to the client

                // 4. Close the socket

            }

        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }

    }
}