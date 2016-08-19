package lsr.paxos.test.ka47;


import com.turn.ttorrent.bcodec.BDecoder;
import com.turn.ttorrent.bcodec.BEValue;
import com.turn.ttorrent.bcodec.BEncoder;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import static java.lang.System.out;

public class KaCommand implements Serializable {
    private static final long serialVersionUID = 1L;
    private final ArrayList<String> value;
//    private final char key;


    public KaCommand(ArrayList<String> value) {
//        this.key = key;
        this.value = value;
    }

    public KaCommand(byte[] bytes) throws IOException, ClassNotFoundException {ArrayList<String> value1;
//        String msg = new String(bytes,Charset.forName("UTF-8"));
//        value = msg.substring(2,msg.length()-1);// to avoid [] of list
//        value = listDecoder(bytes);
//        key = value.remove(0).toCharArray()[0]; // msg.charAt(0);
//        System.out.println(String.format("< key, value > : \n %c %s: ", key, value.toString()));

        // read from byte array
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStream ios = new ObjectInputStream(bais);
        value = (ArrayList<String>) ios.readObject();


//        DataInputStream in = new DataInputStream(bais);
//        while (in.available() > 0) {
//            String element = in.readUTF();
//            value.add(element);
//            out.println(element);
//        }
//        in.close();
    }


    public byte[] toByteArray() throws IOException {
        out.println("allocate  ");
//        ByteBuffer buffer = null;

//        String val = getValue();
//
//        if(val.contains(",,,")){
//            val.replaceAll(",,,","#,separator,#");
//        }
//        val.replaceAll(", ",",,,");
//
//        String msg = String.format("%c%s",getKey(),getValue());


        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(getValue());

        // write to byte array
//        for (String element : getValue()) {V
//            out.writeUTF(element);
//        }


        //        buffer = ByteBuffer.wrap(baos.toByteArray());

//        buffer = buffer.wrap(listBencoder(getKey(), getValue()));
//        buffer = buffer.wrap(msg.getBytes(Charset.forName("UTF-8")));
//        return buffer.array();
        return baos.toByteArray();
    }

//    public char getKey() {
//        return key;
//    }

    public ArrayList<String> getValue() {
        return value;
    }

//    public byte[] listBencoder(char key, ArrayList<String> list) throws IOException {
//        ArrayList<BEValue> bvList = new ArrayList<>();
//        bvList.add(new BEValue(key));
//
//        for (String s : list) {
//            bvList.add(new BEValue(s));
//        }
//        ByteArrayOutputStream out = new ByteArrayOutputStream();
//
//        new BEncoder().bencode(bvList, out);
//        return out.toByteArray();
//    }
//
//    public ArrayList<String> listDecoder(byte[] bytes) throws IOException {
//        ByteArrayInputStream input = new ByteArrayInputStream(bytes);
//
//        BDecoder dec = new BDecoder(input);
//        BEValue bvList = dec.bdecodeList();
//
//        ArrayList<String> list = new ArrayList<String>();
//
//        for (BEValue v : bvList.getList()) {
//            list.add(v.getString());
//        }
//
//        return list;
//    }

    public String toString() {
        return value.toString();
//        return String.format("key=%c, value=%s]", key, value);
    }
}
