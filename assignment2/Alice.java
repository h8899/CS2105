/*
Name: LE TRUNG HIEU
Student number: A0161308M
Is this a group submission (yes/no)? no

If it is a group submission:
Name of 2nd group member: THE_OTHER_NAME_HERE_PLEASE
Student number of 2nd group member: THE_OTHER_NO

*/


// Please DO NOT copy from the Internet (or anywhere else)
// Instead, if you see nice code somewhere try to understand it.
//
// After understanding the code, put it away, do not look at it,
// and write your own code.
// Subsequent exercises will build on the knowledge that
// you gain during this exercise. Possibly also the exam.
//
// We will check for plagiarism. Please be extra careful and
// do not share solutions with your friends.
//
// Good practices include
// (1) Discussion of general approaches to solve the problem
//     excluding detailed design discussions and code reviews.
// (2) Hints about which classes to use
// (3) High level UML diagrams
//
// Bad practices include (but are not limited to)
// (1) Passing your solution to your friends
// (2) Uploading your solution to the Internet including
//     public repositories
// (3) Passing almost complete skeleton codes to your friends
// (4) Coding the solution for your friend
// (5) Sharing the screen with a friend during coding
// (6) Sharing notes
//
// If you want to solve this assignment in a group,
// you are free to do so, but declare it as group work above.




import java.net.*;
import java.nio.*;
import java.util.Arrays;
import java.util.Scanner;
import java.io.*;
import java.nio.file.*;
import java.util.zip.*;

class Alice {
    private int seqNum = 0;
    private DatagramSocket socket;
    private static final int MAX_SIZE = 500; 

    public static void main(String[] args) throws Exception {
        // Do not modify this method
        if (args.length != 2) {
            System.out.println("Usage: java Alice <host> <unreliNetPort>");
            System.exit(1);
        }
        InetAddress address = InetAddress.getByName(args[0]);
        new Alice(address, Integer.parseInt(args[1]));
    }

    public Alice(InetAddress address, int port) throws Exception {
        // Do not modify this method
        socket = new DatagramSocket();
        socket.setSoTimeout(100);

        Scanner sc = new Scanner(System.in);
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            handleLine(line, socket, address, port);
            // Sleep a bit. Otherwise (if we type very very fast)
            // sunfire might get so busy that it actually drops UDP packets.
            Thread.sleep(20);
        }
    }

    public void handleLine(String line, DatagramSocket socket, InetAddress address, int port) throws Exception {
        // Do not modify this method
        if (line.startsWith("/send ")) {
            String path = line.substring("/send ".length());
            System.err.println("Sending file: " + path);
            try {
                File file = new File(path);
                if (!(file.isFile() && file.canRead())) {
                    System.out.println("Path is not a file or not readable: " + path);
                    return;
                }
            } catch (Exception e) {
                System.out.println("Could not read " + path);
                return;
            }
            sendFile(path, socket, address, port);
            System.err.println("Sent file.");
        } else {
            if (line.length() > 450) {
                System.out.println("Your message is too long to be sent in a single packet. Rejected.");
                return;
            }
            sendMessage(line, socket, address, port);
        }
	
    }

    public void sendFile(String path, DatagramSocket socket, InetAddress address, int port) {
			BufferedInputStream bis = null;
        	Path filePath = Paths.get(path);
			if(Files.exists(filePath)) {
				try {
					// byte[] fileData = Files.readAllBytes(filePath);
					FileInputStream fis = new FileInputStream(path);
					bis = new BufferedInputStream(fis);

					int len = bis.available();
					int offset = 0;
					// int len = fileData.length;
					byte[] sendData = new byte[MAX_SIZE];

					//signal open file
					String signal = new String(Integer.toString(seqNum));
					byte[] signalData = signal.getBytes();
					Packet p = new Packet(signalData, "ack", 0);
					DatagramPacket signalPkt = new DatagramPacket(p.getData(), 0, p.getData().length, address, port);
					sendPkt(socket, signalPkt, address, port);

					while(len > 0) {
						DatagramPacket sendPkt;
						if(len >= MAX_SIZE) {
							len -= MAX_SIZE;
							sendData = new byte[MAX_SIZE];
							bis.read(sendData, 0, MAX_SIZE);
							Packet packet = new Packet(sendData, "data", seqNum);
							sendPkt = new DatagramPacket(packet.getData(), 0, 512, address, port);
						} else {
							sendData = new byte[len];
							bis.read(sendData, 0, len);
							Packet packet = new Packet(sendData, "data", seqNum);
							sendPkt = new DatagramPacket(packet.getData(), 0, len + 12, address, port);
							len = 0;
						}	
						sendPkt(socket, sendPkt, address, port);
					}


					// signal close file
					signal = new String(Integer.toString(seqNum) + "1");
					signalData = signal.getBytes();
					p = new Packet(signalData, "ack", 0);
					signalPkt = new DatagramPacket(p.getData(), 0, p.getData().length, address, port);
					sendPkt(socket, signalPkt, address, port);

				} catch(IOException e) {	
				} finally {
					try {
						bis.close();
					} catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
    }

    public void sendMessage(String message, DatagramSocket socket, InetAddress address, int port) throws Exception {
        	byte[] sendData = message.getBytes();
		Packet packet = new Packet(sendData, "data", seqNum);
		DatagramPacket sendPkt = new DatagramPacket(packet.getData(), packet.getData().length, address, port);
		sendPkt(socket, sendPkt, address, port);
    }
 
    public void sendPkt(DatagramSocket socket, DatagramPacket pkt, InetAddress address, int port) {
	try {	
		socket.send(pkt);			
		while(true) {
			int ackNum = waitAck(socket);
			if(ackNum == seqNum) {
				socket.send(pkt);
			} else {
				seqNum = ackNum;
				break;
			}
		}
	} catch(Exception e) {
		//e.printStackTrace();
		sendPkt(socket, pkt, address, port);
	}
    }

    public int waitAck(DatagramSocket socket) throws Exception {
    		while(true) {
			byte[] buffer = new byte[Packet.SEQNUM_SIZE + Packet.CHECKSUM_SIZE];
			DatagramPacket receivedPkt = new DatagramPacket(buffer, buffer.length);
			socket.setSoTimeout(100);
       	 		socket.receive(receivedPkt);
			if(receivedPkt != null) {
				if(Packet.isValidCheckSum(buffer, Packet.ACK_SIZE)) {
					return Packet.ByteConversionUtil.byteArrayToInt(Arrays.copyOfRange(buffer, 8, 12));
				}
			}
		}
	
    }
}
