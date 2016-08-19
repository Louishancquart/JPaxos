package lsr.paxos.test.ka47;

import com.turn.ttorrent.client.Client;
import lsr.paxos.client.ReplicationException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class KaClient {
    private static KaTorrentManager tm;
    private PaxosClient paxosClient;

    private static void instructions() {
        System.out.println("Provide a pair of key-value <char> <String>");
    }

    public static void main(String[] args) throws IOException, ReplicationException, InterruptedException {
        instructions();
        KaClient client = new KaClient();
        client.run();
    }

    public void run() throws IOException, ReplicationException, InterruptedException {
        //initialize the torrent manager
        tm = new KaTorrentManager(new File("/home/m/documents/put/s3/MTh/Paxos/JPaxos/src/lsr/testResources/torrents"),
                new File("/home/m/documents/put/s3/MTh/Paxos/JPaxos/src/lsr/testResources/downloads"));

        //connect to the client
        paxosClient = new PaxosClient();
        paxosClient.connect();

        while (true) {

            //send data downloads
            ArrayList<String> clientList = tm.downloadsStateToArrray();

            System.out.println(String.format("DATA SENT \n"+clientList.toString()));
            KaCommand command = new KaCommand(clientList);
            byte[] response = paxosClient.execute(command.toByteArray());

            System.out.println(String.format("Previous value :\n %s", new String(response, "UTF-8")));

            //sleep
            Thread.sleep(5000);
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

    /**
     * indicate the sate of the Client ( to the Server )
     */
    private enum State {
        UP, DOWN
    }

    /**
     * private class to avoid className conflict with class Client :
     *  lsr.paxos.client.Client
     */
    private class PaxosClient extends lsr.paxos.client.Client {
        public PaxosClient() throws IOException {
        }
    }


}
