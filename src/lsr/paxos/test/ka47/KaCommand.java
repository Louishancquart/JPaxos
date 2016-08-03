package lsr.paxos.test.ka47;


import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class KaCommand implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String value;
    private final char key;


//    public KaCommand(String value) {
//        this.value = value;
//    }

    public KaCommand(char key, String value) {
        this.key = key;
        this.value = value;
    }

    public KaCommand(byte[] bytes) throws IOException {
        String msg = new BASE64Encoder().encode(bytes);
        key = msg.charAt(0);
        value = msg.substring(1,msg.length());
        System.out.println("value :"+value);
        System.out.println("mesg :"+msg);
    }

    public byte[] toByteArray() throws UnsupportedEncodingException {
        System.out.println("allocate  ");
        ByteBuffer buffer = null;
        try {
            buffer = new BASE64Decoder().decodeBufferToByteBuffer(String.format("%c%s",key,value));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer.array();
    }

    public char getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }


   public String toString() {
        return String.format("key=%c, value=%s]",key, value);
    }
}
