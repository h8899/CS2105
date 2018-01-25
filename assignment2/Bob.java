/*
Name: LE TRUNG HIEU
Student number: A0161308M
Is this a group submission (yes/no)? NO

If it is a group submission:
Name of 2nd group member: THE_OTHER_NAME_HERE_PLEASE
Student number of 2nd group member: THE_OTHER_NO

*/


import java.net.*;
import java.nio.*;
import java.io.*;
import java.util.zip.*;
import java.util.*;

class Bob {
    private int expectedNum = 0;
    private final int CHECKSUM_LENGTH = 8;
    DatagramSocket socket;

    public static void main(String[] args) throws Exception {
        // Do not modify this method
        if (args.length != 1) {
            System.out.println("Usage: java Bob <port>");
            System.exit(1);
        }
        new Bob(Integer.parseInt(args[0]));
    }

    public Bob(int port) throws Exception {
        // Implement me
		socket = new DatagramSocket(port);
		byte[] byteBuf = new byte[1024];
		expectedNum = 0;
		byte[] ackNum = Packet.ByteConversionUtil.intToByteArray(expectedNum);
		boolean isFile = false;
		FileOutputStream fos = new FileOutputStream("output");
		BufferedOutputStream bos = new BufferedOutputStream(fos);

		while(true) {
			byteBuf = new byte[1024];
			DatagramPacket receivedPkt = new DatagramPacket(byteBuf, byteBuf.length);
			socket.receive(receivedPkt);
			InetAddress serverAddress = receivedPkt.getAddress();
			int serverPort = receivedPkt.getPort();
			
			if(!Packet.isValidCheckSum(byteBuf, receivedPkt.getLength() - CHECKSUM_LENGTH)) {
				sendAck(socket, serverAddress, serverPort);
			} else if (receivedPkt.getLength() == 9) {
				// this is to signal file open
				fos = new FileOutputStream("output");
				bos = new BufferedOutputStream(fos);
				isFile = true;
				int sequenceNum = Packet.getSeqNumInSignal(byteBuf);
				if(sequenceNum == expectedNum) {			
					expectedNum = (expectedNum + 1) % 2;
				}
				sendAck(socket, serverAddress, serverPort);
			} else if(receivedPkt.getLength() == 10) {
				// this is to signal file close
				bos.close();
				isFile = false;
				int sequenceNum = Packet.getSeqNumInSignal(byteBuf);
				if(sequenceNum == expectedNum) {
					expectedNum = (expectedNum + 1) % 2;
				}
				sendAck(socket, serverAddress, serverPort);
			} else {
				int sequenceNum = Packet.extractSequenceNum(byteBuf);
				String receivedData = new String(receivedPkt.getData(), 12, receivedPkt.getLength() - 12);
				if(sequenceNum == expectedNum) {
					expectedNum = (expectedNum + 1) % 2;
					if(isFile) {
						bos.write(receivedPkt.getData(), 12, receivedPkt.getLength() - 12);
					} else {
						printMessage(receivedData);
					}
				}
				
				sendAck(socket, serverAddress, serverPort);
			}
		}	
    }


    public void sendAck(DatagramSocket socket, InetAddress serverAddress, int serverPort) throws Exception {
	byte[] ackNum = Packet.ByteConversionUtil.intToByteArray(expectedNum);
	Packet packet = new Packet(ackNum, "ack", 0);
	DatagramPacket AckPkt = new DatagramPacket(packet.getData(), packet.getData().length, serverAddress, serverPort);
	socket.send(AckPkt);
    }
    public void printMessage(String message) {
        // Do not modify this method
        // Call me to print out the messages!
        System.out.println(message);
    }
}
