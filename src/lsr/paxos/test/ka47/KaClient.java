package lsr.paxos.test.ka47;

import lsr.paxos.client.Client;
import lsr.paxos.client.ReplicationException;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class KaClient {

    private static void instructions() {
        System.out.println("The Download helper is running");
    }

    public static void main(String[] args) throws IOException, ReplicationException, InterruptedException, ClassNotFoundException, NoSuchAlgorithmException {
        instructions();
        KaClient client = new KaClient();
        client.run();
    }

    public void run() throws IOException, ReplicationException, InterruptedException, ClassNotFoundException, NoSuchAlgorithmException {
        //initialize the torrent manager
        KaTorrentManager tm = new KaTorrentManager(new File("/home/m/documents/put/s3/MTh/Paxos/JPaxos/src/lsr/testResources/torrents"),
                new File("/home/m/documents/put/s3/MTh/Paxos/JPaxos/src/lsr/testResources/downloads"));

        //connect to the client
        Client paxosClient = new Client();
        paxosClient.connect();

        while (true) {

            //send data downloads
            ArrayList<?> list = tm.downloadsStateToArray();

            System.out.println(String.format("DATA SENT \n"+list.toString()));
            KaCommand command = new KaCommand("String", list);

            // send     : added /deleted
            byte[] response = paxosClient.execute(command.toByteArray());

            // receive  : missing torrents
            ByteArrayInputStream bais = new ByteArrayInputStream(response);
            ObjectInputStream ois = new ObjectInputStream(bais);

            list = (ArrayList<?>) ois.readObject();
            System.out.println(String.format("Previous value :\n %s", list.toString()));

            // send     : the missing torrents files
            list = tm.getMissingTorrentsFiles((ArrayList<String>) list);

            System.out.println(String.format("DATA SENT \n"+list.toString()));
            command = new KaCommand("File", list);
            response = paxosClient.execute(command.toByteArray());

            // receive  : new torrents + pieces to dl
            ois = new ObjectInputStream(new ByteArrayInputStream(response));
            list = (ArrayList<?>) ois.readObject();

            System.out.println(String.format("Previous value :\n %s", list.toString()));

            //modify the torrent files


            //download them
            BittorrentClient bc = new BittorrentClient((ArrayList<File>) list, tm.getDownloadsDir(), 604800);

            //sleep
            Thread.sleep(5000);//would be set to 10 minutes after tests
        }
    }




}
