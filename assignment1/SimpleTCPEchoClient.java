import java.io.*;
import java.net.*;
import java.util.*;
class SimpleTCPEchoClient {
public static void main(String[] args) throws IOException {
	String serverIP = "127.0.0.1";
	int serverPort = 8000;
	// local host, example
	// just an example
	// create a client socket and connect to the server
	Socket clientSocket = new Socket(serverIP, serverPort);
	while(true) {
	// read user input from keyboard
	Scanner scanner = new Scanner(System.in);
	String fromKeyboard = scanner.nextLine();
	PrintWriter toServer = new PrintWriter(clientSocket.getOutputStream(), true);
	// write user input to the socket
	toServer.println(fromKeyboard);
	// to continue next page
	byte[] buffer = new byte[50];

	// create output stream to ser
//	InputStream is = clientSocket.getInputStream();
//	is.read(buffer);
//	for (byte b:buffer) {
//		char c = (char)b;
//		System.out.println(c);
  //      }	
	// write user input to the socket
	//toServer.println(fromKeyboard);
	// create input stream from server
//	Scanner sc = new Scanner(clientSocket.getInputStream());
	// read server reply from the socket
//	String fromServer = sc.nextLine();
	// show on screen
//	System.out.println("Echo from server: " + fromServer);
//	clientSocket.close();
        	}
	}
}
