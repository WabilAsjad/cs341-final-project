import java.net.ServerSocket;

public class HTTPServer {
    public static void main(String[] args) throws Exception {
        final ServerSocket server = new ServerSocket(8082);
        System.out.println("Listening for connection on port 8082 ....");
        while (true) {
            final Socket client = server.accept();
            // do stuff
        }
    }
}
