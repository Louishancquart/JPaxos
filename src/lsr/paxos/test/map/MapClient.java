package lsr.paxos.test.map;

import lsr.paxos.client.Client;
import lsr.paxos.client.ReplicationException;
import sun.misc.BASE64Encoder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class MapClient {
    private Client client;

    public void run() throws IOException, ReplicationException {
        client = new Client();
        client.connect();

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            String line;

            line = reader.readLine();

            if (line == null) {
                break;
            }

            String[] args = line.trim().split(" ");
            if (args[0].equals("bye")) {
                System.exit(0);
            }

            if (args.length != 2) {
                instructions();
                continue;
            }

            Long key = Long.parseLong(args[0]);
            String value = args[1];
            System.out.println(String.format("Commandline:\t\nkey :%d \t\nvalue: %s",key, value));

            MapServiceCommand command = new MapServiceCommand(key, value);
            byte[] response = client.execute(command.toByteArray());
//            ByteBuffer buffer = ByteBuffer.wrap(response);
//            key = buffer.getLong();

            System.out.println(String.format("Previous value for %d was %s", key, new String(Arrays.copyOfRange(response,2,response.length),"UTF-8")));// previousValue.toString()));
        }
    }

    private static void instructions() {
        System.out.println("Provide key-value pair of integers to insert to hash map");
        System.out.println("<key> <value>");
    }

    public static void main(String[] args) throws IOException, ReplicationException {
        instructions();
        MapClient client = new MapClient();
        client.run();
    }
}
