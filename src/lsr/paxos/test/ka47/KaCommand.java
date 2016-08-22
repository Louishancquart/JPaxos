package lsr.paxos.test.ka47;


import java.io.*;
import java.util.ArrayList;


public class KaCommand implements Serializable {
    private static final long serialVersionUID = 1L;

    public String getType() {
        return type;
    }

    private String type;
    private ArrayList<?> list;


    public KaCommand(String type, ArrayList<?> list) {
        this.type = type;
        this.list = list;
    }

    public KaCommand(byte[] bytes) throws IOException, ClassNotFoundException {
        // read from byte array
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStream ios = new ObjectInputStream(bais);
        type = ios.readUTF();
        list = (ArrayList<?>) ios.readObject();
    }


    public byte[] toByteArray() throws IOException {
        System.out.println("allocate  ");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeUTF(getType());
        oos.writeObject(getList());

        return baos.toByteArray();
    }


    public ArrayList<?> getList() {
        return list;
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
        return list.toString();
    }
}
