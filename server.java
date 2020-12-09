import java.net.*;
import java.io.*;
import java.nio.file.*;
import java.nio.charset.*;
import java.util.*;
import java.text.SimpleDateFormat;
import org.json.simple.JSONObject;

public class HTTPServer implements Runnable {

    private Socket client;
    private InputStreamReader request;
    private PrintWriter os;
    private BufferedReader reader;
    private static int DEFAULT_PORT = 8082;
    private static int counter = 0;

    public HTTPServer(Socket c) {
        super();
        client = c;
    }

    /**
     * This parses the input received from the client.
     */
    private void parseRequest() {
        String input = "";
        try {
            String line = reader.readLine();
            // Parse input
            while (!line.isEmpty() || !line.equals("")) {
                input += line + "\n";
                System.out.println(line);
                line = reader.readLine();
            }
        } catch (IOException ex) {
            System.out.println("Exception on reading the line.");
        }

        // Get command from input using StringBuilder
        StringBuilder strBuilder = new StringBuilder();
        String linkRequested = input.split("\n")[0].split(" ")[1].split("/")[1];
        System.out.println(linkRequested);
        System.out.println(input.split("\n")[0]);
        if (input.split("\n")[0].contains("GET"))
            handleRequest("GET", linkRequested);
        // else if(input.split("\n")[0].contains("POST")) handleRequest("POST",
        // linkRequested, os);
        else if (input.split("\n")[0].contains("PUT"))
            handleRequest("PUT", linkRequested);
        else if (input.split("\n")[0].contains("DELETE"))
            handleRequest("DELETE", linkRequested);
    }

    /**
     * This function checks what kind of request was received (GET, POST, PUT,
     * DELETE), and redirects to those instructions.
     * 
     * @param String request
     * @return
     */
    private void handleRequest(String method, String linkRequested) {
        try {
            if (method.equals("GET")) {
                if (linkRequested.endsWith("/"))
                    linkRequested += "index.html";
                File file = new File(new File("."), linkRequested);
                String contentType = getContentType(linkRequested);
                int length = (int) file.length();
                if (!file.exists() || file.isDirectory()) {
                    response(404);
                    os.flush();
                } else {
                    byte[] fileBytes = Files.readAllBytes(Paths.get(linkRequested));
                    String fileString = new String(fileBytes, StandardCharsets.UTF_8);
                    response(200);
                    os.write("Content type: " + contentType + "\r\n");
                    os.write("Content length: " + length + "\r\n");
                    os.write(fileString);
                    os.flush();
                }
            } else if (method.equals("PUT")) {
                String input = "";
                String line = reader.readLine();
                while (line.contains("<html>")) {
                    while (!line.equals("</html>")) {
                        line = reader.readLine();
                        input += line + "\n";
                        System.out.println(line);
                    }
                    input += "</html>";
                }

                if (input != "") {
                    int responseCode = writeData(input, linkRequested);
                    response(responseCode);
                    os.flush();
                } else {
                    response(304);
                    os.flush();
                }
            } else if (method.equals("DELETE")) {
                String deleteMessage = "<html>\n<body>\n<h1>File deleted.</h1>\n</body>\n</html>";
                int responseCode = writeData(deleteMessage, linkRequested);
                response(responseCode);
                os.write(deleteMessage);
                os.flush();
            } else {
                String error = "This is not a valid request.";
                System.out.println(error);
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
        // elif(request == "POST");
        // {

        // }
    }

    private void response(int responseCode) {
        if (responseCode == 200) {
            os.write("HTTP/1.1 200 OK\r\n");
            os.write("Date: " + getTime() + "\r\n");
            os.write("Server: localhost\r\n");
        } else if (responseCode == 404) {
            // 404 page not found
            os.write("HTTP/1.1 404 Not Found\r\n");
            os.write("Date: " + getTime() + "\r\n");
            os.write("Server: localhost\r\n");
            os.write("\r\n");
        } else if (responseCode == 304) {
            os.write("HTTP/1.1 304 Not Modifies\r\n");
            os.write("Date: " + getTime() + "\r\n");
            os.write("Server: localhost\r\n");
            os.write("\r\n");
        }
    }

    /**
     * This function write data to the server.
     * 
     * @param data
     * @param linkRequested
     * @return
     */
    private int writeData(String data, String linkRequested) {
        File file = new File(new File("."), linkRequested);
        if (!file.exists() || file.isDirectory()) {
            return 404;
        }
        FileWriter filewriter;
        try {
            filewriter = new FileWriter(linkRequested);
            filewriter.write(data);
            filewriter.close();
            return 200; // Success
        } catch (IOException ex) {
            return 304;
        }
    }

    /**
     * This function get the content type of the input
     * 
     * @param String linkRequested
     * @return
     */
    private String getContentType(String linkRequested) {
        if (linkRequested.endsWith("htm") || linkRequested.endsWith("html"))
            return "text/html";
        else
            return "text/plain";
    }

    /**
     * This function get the TimeStamp
     * 
     * @return
     */
    private String getTime() {
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
                HTTPServer clientServer = new HTTPServer(server.accept());
                counter++;
                System.out.println("Client NO: " + counter);
                // Multi-threaded server
                Thread thread = new Thread(clientServer);
                thread.start();
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            // Read HTTP request from the client socket
            request = new InputStreamReader(client.getInputStream());
            reader = new BufferedReader(request);
            os = new PrintWriter(client.getOutputStream());

            // Parse Input
            parseRequest();

            // Close the socket
            System.out.println("Closing the connection.");
            if (reader != null) {
                reader.close();
                System.out.println("Socket input stream closed.");
            }
            if (os != null) {
                os.close();
                System.out.println("Socket output stream closed.");
            }
            if (client != null) {
                client.close();
                System.out.println("Socket closed.");
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
