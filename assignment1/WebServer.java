/*
Name: Le Trung Hieu
Student number: A0161308M
Is this a group submission (yes/no)? no

If it is a group submission:
Name of 2nd group member: THE_OTHER_NAME_HERE_PLEASE
Student number of 2nd group member: THE_OTHER_NO

*/
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.file.*;
import java.io.*;
import java.util.*;

public class WebServer {
    public static void main(String[] args) {
        // dummy value that is overwritten below
        int port = 8080;
        try {
          port = Integer.parseInt(args[0]);
        } catch (Exception e) {
          System.out.println("Usage: java webserver <port> ");
          System.exit(0);
        }

        WebServer serverInstance = new WebServer();
        serverInstance.start(port);
    }

    private void start(int port) {
      try {
      	System.out.println("Starting server on port " + port);
      	ServerSocket welcomeSocket = new ServerSocket(port);
      
      	while(true) {
      	  Socket connectionSocket = welcomeSocket.accept();
	  System.out.println("A new client connected");
          handleClientSocket(connectionSocket);
      	}
      } catch(IOException e) {
      	  e.printStackTrace();
      }
    }

    /**
     * Handles requests sent by a client
     * @param  client Socket that handles the client connection
     */
    private void handleClientSocket(Socket client) {
      try {
	while(client.getInetAddress().isReachable(20000)) {
      	    InputStreamReader isr = new InputStreamReader(client.getInputStream());
      	    BufferedReader br = new BufferedReader(isr);
     	    // read data from the connection socket     
      	    HttpRequest request = new HttpRequest(br);
      	    request.parseRequest();
      	    byte[] response = formHttpResponse(request);
	    System.out.println("String response");
	    System.out.println(new String(response, "UTF-8"));

      	    sendHttpResponse(client, response);
		
	    String HttpVersion = request.HttpVersion;
	    if(HttpVersion.equals("HTTP/1.0")) {
	        break;
	     } else {
		client.setSoTimeout(2000);
	    }
	}

      } catch(Exception e) {
      	  e.printStackTrace();
      } finally {
	  try {
	     System.out.println("Connection is closing");
	     client.close();
	  } catch(Exception e) {
	     e.printStackTrace();
	  }
      }	
    }

    /**
     * Sends a response back to the client
     * @param  client Socket that handles the client connection
     * @param  response the response that should be send to the client
     */
    private void sendHttpResponse(Socket client, byte[] response) {
      try {
      	DataOutputStream dos = new DataOutputStream(client.getOutputStream());
      	dos.write(response, 0, response.length);
      } catch(IOException e) {
      	e.printStackTrace();
      }
    }

    /**
     * Form a response to an HttpRequest
     * @param  request the HTTP request	
     * @return a byte[] that contains the data that should be send to the client
     */
    private byte[] formHttpResponse(HttpRequest request) {
      Path path = Paths.get(request.path);
     
      if(Files.exists(path)) {
	  try {
          	byte[] fileData = Files.readAllBytes(path);
          	StringBuilder str = new StringBuilder();
          	str.append(request.HttpVersion + " 200 OK \r\n");
	  	str.append("Content-Length: " + fileData.length + "\r\n");
          	str.append("\r\n");
          	return concatenate((str.toString()).getBytes(), fileData);
          } catch(IOException e) {
          	e.printStackTrace();
          }
      } 
      return form404Response(request);
    }


    /**
     * Form a 404 response for a HttpRequest
     * @param  request a HTTP request
     * @return a byte[] that contains the data that should be send to the client
     */
    private byte[] form404Response(HttpRequest request) {
	StringBuilder str = new StringBuilder();
        str.append(request.HttpVersion + " 404 Not Found \r\n");
        str.append(get404Content(request.path));	
        str.append("\r\n");
        return (str.toString()).getBytes();
    }
   

    /**
     * Concatenates 2 byte[] into a single byte[]
     * This is a function provided for your convenience.
     * @param  buffer1 a byte array
     * @param  buffer2 another byte array
     * @return concatenation of the 2 buffers
     */
    private byte[] concatenate(byte[] buffer1, byte[] buffer2) {
        byte[] returnBuffer = new byte[buffer1.length + buffer2.length];
        System.arraycopy(buffer1, 0, returnBuffer, 0, buffer1.length);
        System.arraycopy(buffer2, 0, returnBuffer, buffer1.length, buffer2.length);
        return returnBuffer;
    }

    /**
     * Returns a string that represents a 404 error
     * You should use this string as the return website
     * for 404 errors.
     * @param  filePath path of the file that caused the 404
     * @return a String that represents a 404 error website
     */
    private String get404Content(String filePath) {
      // You should not change this function. Use it as it is.
        StringBuilder sb = new StringBuilder();
        sb.append("<html>");
        sb.append("<head>");
        sb.append("<title>");
        sb.append("404 Not Found");
        sb.append("</title>");
        sb.append("</head>");
        sb.append("<body>");
        sb.append("<h1>404 Not Found</h1> ");
        sb.append("<p>The requested URL <i>" + filePath + "</i> was not found on this server</p>");
        sb.append("</body>");
        sb.append("</html>");

        return sb.toString();
    }
}



class HttpRequest {
    // NEEDS IMPLEMENTATION
    // This class should represent a HTTP request.
    // Feel free to add more attributes if needed.
    public BufferedReader br;
    public String path;
    public String method;
    public String HttpVersion;

    HttpRequest(BufferedReader br) {
    	this.br = br;
    }
    
    public void parseRequest() {
	try {
		String specification = br.readLine();
        	System.out.println("The request is: " + specification);
		String[] contents = specification.split(" ");
		if(contents.length > 0) {
        		method = contents[0];
			StringBuffer pathHelper = new StringBuffer(contents[1]);
        		path = (pathHelper.delete(0, 1)).toString();

        		HttpVersion = contents[2];
			while(true) {
				String nextData = br.readLine();
				System.out.println(nextData);
				if (nextData.trim().isEmpty()) {
					break;
				}
			}
		} else {
		}
	} catch(IOException e) {
		e.printStackTrace();
	}
    }
}
