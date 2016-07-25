package lsr.paxos.test.ka47;

import lsr.service.SimplifiedService;

import java.io.*;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;


public class KaService extends SimplifiedService {
    // Map to be replicated
//    private HashMap<Long, Long> map = new HashMap<Long, Long>();
    String transmission = new String("");

    /** Processes client request and returns the reply for client **/
    protected byte[] execute(byte[] value) {

        // Deserialise the client command
        KaCommand command;
        try {
            command = new KaCommand(value);
        } catch (IOException e) {
            logger.log(Level.WARNING, "Incorrect request", e);
            return null;
        }

        // We do the work
//        Long x = map.get(command.getKey());
//        if (x == null) {
//            x = Long.valueOf(0);
//        }
//        map.put(command.getKey(), command.getValue());


        // We serialise the message back
        ByteArrayOutputStream byteArrayOutput = new ByteArrayOutputStream();
        DataOutputStream dataOutput = new DataOutputStream(byteArrayOutput);
        try {
            dataOutput.writeUTF("ok");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        // And return the reply to the client
        return byteArrayOutput.toByteArray();
    }
    /** Makes snapshot used for recovery and replicas that have very old state **/
    protected byte[] makeSnapshot() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(stream);
            objectOutputStream.writeObject(transmission);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return stream.toByteArray();
    }

    /** Brings the system up-to-date from a snapshot **/
    @SuppressWarnings("unchecked")
    protected void updateToSnapshot(byte[] snapshot) {

        // For map service the "recovery" is just recreation of underlaying map
        ByteArrayInputStream stream = new ByteArrayInputStream(snapshot);
        ObjectInputStream objectInputStream;
        try {
            objectInputStream = new ObjectInputStream(stream);
            transmission = (String) objectInputStream.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static final Logger logger = Logger.getLogger(KaService.class.getCanonicalName());

}