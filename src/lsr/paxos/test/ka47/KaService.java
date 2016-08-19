package lsr.paxos.test.ka47;

import lsr.service.SimplifiedService;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class KaService extends SimplifiedService {
    // Map to be replicated
    private HashMap<Character, String> message = new HashMap<>();
    private HashMap<String,Integer> allTorrents = new HashMap<>();
    private LinkedList<File> allTorrentsFiles = new LinkedList<>();
    private LinkedList<String> askedList = new LinkedList<>();



//    private String com = "";

    /** Processes client request and returns the reply for client **/
    protected byte[] execute(byte[] value) {

        // Deserialise the client command
        KaCommand command = null;
        try {
            command = new KaCommand(value);
        } catch (IOException e) {
            logger.log(Level.WARNING, "Incorrect request", e);
            return null;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            logger.log(Level.WARNING, "ClassNotFoundException in request Object ", e);
        }

        String lastString = command.getValue().toString();

//      We do the work
        decoupeLists(command.getValue());


//        Long x = message.get(command.getKey());
//        if (x == null) {
//            x = Long.valueOf(0);
//        }
//        message.put(command.getKey(), command.getValue());
//        String lastString = message.get(command.getKey()).toString();
//        if (lastString == null) {
//            lastString = "Key not found\n";
//        }

//        message.put(command.getKey(), command.getValue().toString());


//        message.put(command.getKey(), command.getValue());
//        message = command.getValue();

        // We serialise the message back
        ByteArrayOutputStream byteArrayOutput = new ByteArrayOutputStream();
        DataOutputStream dataOutput = new DataOutputStream(byteArrayOutput);
        try {
            dataOutput.writeUTF(lastString);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        // And return the reply to the client
        return byteArrayOutput.toByteArray();
    }

    private void decoupeLists(ArrayList<String> receivedList) {
        int posDeletedList = receivedList.indexOf("deletedList");
        int posHostedList = receivedList.indexOf("hostedList");
        ArrayList<String> addedList = new ArrayList<>(receivedList.subList(1,posDeletedList));
        ArrayList<String> deletedList = new ArrayList<>(receivedList.subList(posDeletedList+1,posHostedList));

        ranker(addedList, 1);
        ranker(deletedList,-1);
    }

    /**
     * Set a rank to each downloads received
     * @param list
     * @param incr
     */
    private void ranker(List<String> list, int incr) {
       for(String s:list) {
           int v = allTorrents.getOrDefault(s, 0);
           if (v == 0)
                askedList.add(s);

           v = v + incr;
           allTorrents.put(s, v);
       }
    }

    /** Makes snapshot used for recovery and replicas that have very old state **/
    protected byte[] makeSnapshot() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(stream);
            objectOutputStream.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return stream.toByteArray();
    }

    /** Brings the system up-to-date from a snapshot **/
    @SuppressWarnings("unchecked")
    protected void updateToSnapshot(byte[] snapshot) {

        // For message service the "recovery" is just recreation of underlaying message
        ByteArrayInputStream stream = new ByteArrayInputStream(snapshot);
        ObjectInputStream objectInputStream;
        try {
            objectInputStream = new ObjectInputStream(stream);
            message = (HashMap<Character, String>) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static final Logger logger = Logger.getLogger(KaService.class.getCanonicalName());

}
