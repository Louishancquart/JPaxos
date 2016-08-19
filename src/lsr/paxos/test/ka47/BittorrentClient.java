//package lsr.paxos.test.ka47;
//
//import com.turn.ttorrent.client.Client;
//import com.turn.ttorrent.client.SharedTorrent;
//
//import java.io.File;
//import java.io.IOException;
//import java.net.InetAddress;
//import java.net.UnknownHostException;
//import java.security.NoSuchAlgorithmException;
//
///**
// * Created by m on 16.08.16.
// */
//public class BittorrentClient {
//    public BittorrentClient() {
//    }
//
//    public void initiateClient() throws IOException, NoSuchAlgorithmException {
//        // First, instantiate the Client object.
//        Client client = new Client(
//                // This is the interface the client will listen on (you might need something
//                InetAddress.getLocalHost(),
//
//                // Load the torrent from the torrent file and use the given
//                // output directory. Partials downloads are automatically recovered.
//                SharedTorrent.fromFile(
//                        new File("/home/m/documents/put/s3/MTh/Paxos/JPaxos/src/lsr/testResources/helper"),
//                        new File("/home/m/documents/put/s3/MTh/Paxos/JPaxos/src/lsr/testResources/helper")));
//
//        // You can optionally set download/upload rate limits
//        // in kB/second. Setting a limit to 0.0 disables rate
//        // limits.
//        client.setMaxDownloadRate(50.0);
//        client.setMaxUploadRate(50.0);
//
//// At this point, can you either call download() to download the torrent and
//// stop immediately after...
//        client.download();
//
//// Or call client.share(...) with a seed time in seconds:
//// client.share(3600);
//// Which would seed the torrent for an hour after the download is complete.
//
//// Downloading and seeding is done in background threads.
//// To wait for this process to finish, call:
//        client.waitForCompletion();
//
//// At any time you can call client.stop() to interrupt the download.
//    }
//}
