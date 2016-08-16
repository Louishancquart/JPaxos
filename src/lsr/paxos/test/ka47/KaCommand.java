package lsr.paxos.test.ka47;


import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public class KaCommand implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String value;
    private final char key;


    public KaCommand(char key, String value) {
        this.key = key;
        this.value = value;
    }

    public KaCommand(byte[] bytes) throws IOException {
        String msg = new String(bytes,Charset.forName("UTF-8"));
        key = msg.charAt(0);
        value = msg.substring(1,msg.length());
        System.out.println("value :"+value);
        System.out.println("mesg :"+msg);
    }



    public byte[] toByteArray() throws UnsupportedEncodingException {
        System.out.println("allocate  ");
        ByteBuffer buffer = null;
        String msg = String.format("%c%s",getKey(),getValue());
        buffer = buffer.wrap(msg.getBytes(Charset.forName("UTF-8")));
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
