package lsr.paxos.test.ka47;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;

import lsr.paxos.client.Client;
import lsr.paxos.client.ReplicationException;

public class KaClient {
    private Client client;
    /**
     * indicate the sate of the Client ( to the Server )
     */
    private enum State {
        UP, DOWN
    }

    private static void instructions() {
        System.out.println("Provide key-value pair: integer String");
        System.out.println("<key> <value>");
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

            String[] args = line.trim().split(" ");

            if (args[0].equals("bye")) {
                System.exit(0);
            }

            if (args.length != 2) {
                instructions();
                continue;
            }

//            Long key = Long.parseLong(args[0]);
//            Long value = Long.parseLong(args[1]);
//
//            KaCommand command = new KaCommand(key, value);
//            byte[] response = client.execute(command.toByteArray());
//            ByteBuffer buffer = ByteBuffer.wrap(response);
//            Long previousValue = buffer.getLong();
//            System.out.println(String.format("Previous value for %d was %d", key, previousValue));

            String requestType = args[0];
            String message = args[1];
            System.out.println(" requestType: "+requestType + " message: "+message);


            /** Prepairing request **/
            KaCommand command = new KaCommand(requestType, message);

            /** Executing the request **/
            byte[] response = client.execute(command.toByteArray());
            ByteBuffer buffer = ByteBuffer.wrap(response);
            String answer = buffer.toString();
            System.out.println("answer: "+answer);
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
