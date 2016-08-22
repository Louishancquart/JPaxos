package lsr.paxos.test.ka47;

import com.turn.ttorrent.client.Client;
import com.turn.ttorrent.client.SharedTorrent;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

/**
 * Created by m on 16.08.16.
 */
public class BittorrentClient {

    public BittorrentClient(ArrayList<File> list, File downloadDir, int sharingTime) throws IOException, NoSuchAlgorithmException {

        for( File f : list){
            initiateClient(f,downloadDir, sharingTime);
        }

    }



    public void initiateClient(File torrentFile, File downloadDir,int sharingTime) throws IOException, NoSuchAlgorithmException {
        // First, instantiate the Client object.
        Client client = new Client(
                // This is the interface the client will listen on (you might need something
                InetAddress.getLocalHost(),

                // Load the torrent from the torrent file and use the given
                // output directory. Partials downloads are automatically recovered.
                SharedTorrent.fromFile(torrentFile,downloadDir));

        // You can optionally set download/upload rate limits
        // in kB/second. Setting a limit to 0.0 disables rate
        // limits.
        client.setMaxDownloadRate(50.0);
        client.setMaxUploadRate(50.0);

        // At this point, can you either call download() to download the torrent and
        // stop immediately after...
        client.download();

        // Or call client.share(...) with a seed time in seconds:
         client.share(sharingTime);
        // Which would seed the torrent for an hour after the download is complete.
        // Downloading and seeding is done in background threads.
        // To wait for this process to finish, call:
//        client.waitForCompletion();

        // At any time you can call client.stop() to interrupt the download.
    }
}
