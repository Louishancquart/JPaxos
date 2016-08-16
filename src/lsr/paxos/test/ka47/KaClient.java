package lsr.paxos.test.ka47;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import lsr.paxos.client.Client;
import lsr.paxos.client.ReplicationException;

public class KaClient {
    private static KaTorrentManager tm;
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
        //initialize the torrent manager
        tm = new KaTorrentManager(new File("/home/m/documents/put/s3/MTh/Paxos/JPaxos/src/lsr/testResources/torrents"),
                new File("/home/m/documents/put/s3/MTh/Paxos/JPaxos/src/lsr/testResources/downloads"));


        //connect to the client
        client = new Client();
        client.connect();

//        while (true) {
//            tm.updateChanges();
//
//            //send deleted downloads
//            char key = 'a';
//
//            String value = args[1];
//            System.out.println(String.format("Commandline:\t\nkey :%c \t\nvalue: %s",key, value));
//
//
////            String value = line;
////            System.out.println(String.format("value: %s", value));
//
//            KaCommand command = new KaCommand(key,value);
//            byte[] response = client.execute(command.toByteArray());
//
//            System.out.println(String.format("Previous value : %s", new String(response,"UTF-8")));
//
//            //send added downloads
//            //send helping downloads
//
//
//        }
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
