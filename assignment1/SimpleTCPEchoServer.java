import java.io.*;
import java.net.*;
import java.util.*;
class SimpleTCPEchoServer {
	public static void main(String[] args) throws IOException {
		int port = 5678; // server listens to this example port
		// server is waiting
		ServerSocket welcomeSocket = new ServerSocket(port);
		while (true) { // server is always alive
			Socket connectionSocket = welcomeSocket.accept();
			// to continue next page

			System.out.println("Connected to a client...");
			Scanner scanner = new Scanner(connectionSocket.getInputStream());
			// read data from the connection socket
			String fromClient = scanner.nextLine();

			PrintWriter toClient = new PrintWriter(connectionSocket.getOutputStream(), true);
			// write data to the connection socket
			toClient.println(fromClient);
		}
	}
}
