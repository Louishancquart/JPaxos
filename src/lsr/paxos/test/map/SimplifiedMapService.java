package lsr.paxos.test.map;

import lsr.service.SimplifiedService;

import java.io.*;
import java.util.HashMap;

public class SimplifiedMapService extends SimplifiedService {
    private HashMap<Long, String> map = new HashMap<>();

    protected byte[] execute(byte[] value) {
        MapServiceCommand command;
        try {
            command = new MapServiceCommand(value);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String lastString = map.get(command.getKey());
        if (lastString == null) {
            lastString = "";
        }

        map.put(command.getKey(), command.getValue());

        ByteArrayOutputStream byteArrayOutput = new ByteArrayOutputStream();
        DataOutputStream dataOutput = new DataOutputStream(byteArrayOutput);
        try {
            dataOutput.writeUTF(lastString);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return byteArrayOutput.toByteArray();
    }

    protected byte[] makeSnapshot() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(stream);
            objectOutputStream.writeObject(map);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return stream.toByteArray();
    }

    @SuppressWarnings("unchecked")
    protected void updateToSnapshot(byte[] snapshot) {
        ByteArrayInputStream stream = new ByteArrayInputStream(snapshot);
        ObjectInputStream objectInputStream;
        try {
            objectInputStream = new ObjectInputStream(stream);
            map = (HashMap<Long, String>) objectInputStream.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
