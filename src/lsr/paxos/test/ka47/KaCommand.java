package lsr.paxos.test.ka47;


import java.io.*;
import java.nio.ByteBuffer;

public class KaCommand implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String requestType;
    private final String message;


    public KaCommand(String requestType, String message) {
        this.requestType = requestType;
        this.message = message;
    }

    public KaCommand(byte[] bytes) throws IOException {
//        DataInputStream dataInput = new DataInputStream(new ByteArrayInputStream(bytes));
//        String packet = new String(dataInput.readFully(,"UTF-8");
        String [] packet = new String(bytes, "UTF-8").split(" ",2);

//        String packet = dataInput.readChar();
        this.requestType = packet [0];
        this.message = packet [1];
    }

    public byte[] toByteArray() throws UnsupportedEncodingException {
//        byte[] packet = (requestType+" "+message).getBytes("UTF-8");
        ByteBuffer buffer = ByteBuffer.allocate(100); //to be adjusted
        System.out.println("allocate : ");
        buffer.put((requestType+" "+message).getBytes("UTF-8"));
        return buffer.array();
    }


    public String getRequestType() {
        return requestType;
    }

    public String getMessage() {
        return message;
    }
   public String toString() {
        return String.format("[requestType=%s, message=%s]", requestType, message);
    }
}
