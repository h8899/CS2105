/*
Name: LE TRUNG HIEU
Student number: A0161308M
Is this a group submission (yes/no)? no

If it is a group submission:
Name of 2nd group member: THE_OTHER_NAME_HERE_PLEASE
Student number of 2nd group member: THE_OTHER_NO

*/



import java.net.*;
import java.nio.*;
import java.util.zip.CRC32;
import java.util.*;

// This class is not absolutely necessary as you can mash everything
// directly into Alice and Bob classes. However, it might be nicer
// to have this class, which makes your code more organized and readable.
// Furthermore, I makes the assignment easier, as you might be able to
// reuse code

class Packet {
    private byte[] sendData;
    public final static int CHECKSUM_SIZE = 8;
    public final static int SEQNUM_SIZE = 4;
    public final static int ACK_SIZE = 4;

    Packet(byte[] sendData, String type, int seqNum) {
		this.sendData = sendData;
		if(type.equals("ack")) {
			addCheckSum();
		} else if(type.equals("data")) {
			addSequenceNumber(seqNum);
			addCheckSum();	
		}		
    }

    public void addSequenceNumber(int index) {
	   	byte[] sequenceNum = ByteConversionUtil.intToByteArray(index);
		sendData = concatenate(sequenceNum, sendData); 
    }

    public void addCheckSum() {
		CRC32 checksum = new CRC32();
		checksum.update(sendData);
		long checksumValue = checksum.getValue();
		byte[] checksumByteArray = ByteConversionUtil.longToByteArray(checksumValue);	
		sendData = concatenate(checksumByteArray, sendData);
    }

    public byte[] getData() {
		return sendData;
    }

    public static boolean isValidCheckSum(byte[] byteArr, int len) {
		CRC32 checksum = new CRC32();
		checksum.update(byteArr, 8, len);
		long checksumVal = checksum.getValue();
		long oldCheckSum = ByteConversionUtil.byteArrayToLong(Arrays.copyOfRange(byteArr, 0, 8)); 
		if(checksumVal == oldCheckSum) {
			return true;
		} else {
			return false;
		}   
    }

    // the 8 -> 12'th bytes are the sequence number of the packet
    public static int extractSequenceNum(byte[] byteArr) {
    	return ByteConversionUtil.byteArrayToInt(Arrays.copyOfRange(byteArr, 8, 12));
    }

    // this is to determine whether it is a signal to open file or signal to close file.
    public static int getSeqNumInSignal(byte[] byteArr) {
		String str0 = new String("0");
		String str1 = new String("1");
		if(byteArr[8] == str0.getBytes()[0]) {
			return 0;
		} else if(byteArr[8] == str1.getBytes()[0]) {
			return 1;	
		}
		return 1;
    }

    // concatenate two byte arrays
    private byte[] concatenate(byte[] buffer1, byte[] buffer2) {
        byte[] returnBuffer = new byte[buffer1.length + buffer2.length];
        System.arraycopy(buffer1, 0, returnBuffer, 0, buffer1.length);
        System.arraycopy(buffer2, 0, returnBuffer, buffer1.length, buffer2.length);
        return returnBuffer;
    }


     public static class ByteConversionUtil {
		private static ByteBuffer longBuffer = ByteBuffer.allocate(Long.BYTES);
		private static ByteBuffer intBuffer = ByteBuffer
				.allocate(Integer.BYTES);

		public static byte[] longToByteArray(long value) {
			longBuffer.putLong(0, value);
			return longBuffer.array();
		}

		public static long byteArrayToLong(byte[] array) {
			longBuffer = ByteBuffer.allocate(Long.BYTES);
			longBuffer.put(array, 0, array.length);
			longBuffer.flip();
			return longBuffer.getLong();
		}

		public static int byteArrayToInt(byte[] array) {
			intBuffer = ByteBuffer.allocate(Integer.BYTES);
			intBuffer.put(array, 0, array.length);
			intBuffer.flip();
			return intBuffer.getInt();
		}

		public static byte[] intToByteArray(int value) {
			intBuffer.putInt(0, value);
			return intBuffer.array();
		}
	}
}
