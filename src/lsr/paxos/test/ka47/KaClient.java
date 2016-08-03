package lsr.paxos.test.ka47;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.util.Arrays;

import lsr.paxos.client.Client;
import lsr.paxos.client.ReplicationException;
import lsr.paxos.test.map.MapServiceCommand;

public class KaClient {
    private Client client;
    /**
     * indicate the sate of the Client ( to the Server )
     */
    private enum State {
        UP, DOWN
    }

    private static void instructions() {
        System.out.println("Provide a pair of key-value <char> <String>");
    }

    public static void main(String[] args) throws IOException, ReplicationException {
        instructions();
        KaClient client = new KaClient();
        client.run();
    }

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

           String[] args = line.trim().split(" ",1);

            if (args.length <1) {
                System.exit(0);
            }

            if (args.length <2) {
                instructions();
            }

//            if (args[0].equals("bye")) {
//                System.exit(0);
//            }


            char key = args[0].charAt(0);
            String value = args[1];
            System.out.println(String.format("Commandline:\t\nkey :%c \t\nvalue: %s",key, value));


//            String value = line;
//            System.out.println(String.format("value: %s", value));

            KaCommand command = new KaCommand(key,value);
            byte[] response = client.execute(command.toByteArray());

            System.out.println(String.format("Previous value : %s", new String(response,"UTF-8")));

        }
    }

    /**
     * notify the server that the client is up
     */
    public void notifyServer(State state, String message) {

    }

    /**
     * scan torrent folder in order to send report to Server about content on this client
     */
    public void scanTorrentFolder() {

    }

    /**
     * report torrents hosted
     */
    public void reportTorrentsHosted() {

    }

    /**
     * things to do before shutting dowm the client App
     */
    public void tearDown() {

    }




}
