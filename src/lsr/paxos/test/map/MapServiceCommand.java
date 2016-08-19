package lsr.paxos.test.map;


import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;

public class MapServiceCommand implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long key = Long.valueOf(1);
    private final  String value;

    public MapServiceCommand(Long key, String value) {
        this.key = key;
        this.value = value;
    }

    public MapServiceCommand(byte[] bytes) throws IOException {
//        DataInputStream dataInput = new InputStream(new ByteArrayInputStream(bytes));
//        key = dataInput.readLong();
//        key =(Long) 0
//        b1;
//        value = dataInput.readLine();
//        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
//        value = new String( .encode( bais ));
        value ="";// new BASE64Encoder().encode(bytes);
//        value = new String(bytes,"UTF-8");
        System.out.println("value :"+value);
    }

    public Long getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public byte[] toByteArray() {
        System.out.println("allocate  ");
        ByteBuffer buffer = null;
        try {
            buffer = new BASE64Decoder().decodeBufferToByteBuffer(value);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        buffer.put(bytes.getBytes());
        return buffer.array();
    }

    public String toString() {
        return String.format("[key=%d, value=%s]", key, value);
    }
}
