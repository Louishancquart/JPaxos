package lsr.paxos.test.ka47;

import lsr.service.SimplifiedService;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.logging.Level;
import java.util.logging.Logger;


public class KaService extends SimplifiedService {
    private static final Logger logger = Logger.getLogger(KaService.class.getCanonicalName());
    // Map to be replicated
    private HashMap<Character, String> message = new HashMap<>();
    private HashMap<String, Integer> allTorrents = new HashMap<>();
    private LinkedList<File> allTorrentsFiles = new LinkedList<>();

    private ArrayList<String> missingTorrents = new ArrayList<>();

    /**
     * Processes client request and returns the reply for client
     **/
    protected byte[] execute(byte[] value) {

        // Deserialise the client command
        KaCommand command = null;
        try {
            command = new KaCommand(value);
        } catch (IOException e) {
            logger.log(Level.WARNING, "Incorrect request", e);
            return null;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            logger.log(Level.WARNING, "ClassNotFoundException in request Object ", e);
        }


//        String lastString = command.getList().toString();
        ByteArrayOutputStream byteArrayOutput = new ByteArrayOutputStream();
        ArrayList<?> objectSent = new ArrayList<>();
//      We do the work
        if (command.getType().equals("String")) {
            getLists((ArrayList<String>) command.getList());
            objectSent = this.missingTorrents;

            //we serialise back the object
            try {
                ObjectOutputStream oos = new ObjectOutputStream(byteArrayOutput);
                oos.writeObject(objectSent);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

        } else // command type is File
        {
            //register all missing torrents into the torrent file list
            this.allTorrentsFiles.addAll((ArrayList<File>) command.getList());

            //make average rank
            double av = averageRank();

            //for each torrent that is not good rank
            ArrayList<String> underRankTorrents = getUnderRankTorrents(av);

            ArrayList<File> fileSent = new ArrayList<>();

            for (File f : this.allTorrentsFiles) {
                if (underRankTorrents.contains(f.getName()))
                    fileSent.add(f);
            }

            //we serialise back the object

            try {
                ObjectOutputStream oos = new ObjectOutputStream(byteArrayOutput);
                oos.writeObject(fileSent);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }//send the torrents files relates
        }


        // And return the reply to the client
        return byteArrayOutput.toByteArray();
    }

    /**
     * Compare each torrents with the average torrent Rank
     *
     * @param averageTorrentRank
     * @return a  list of torrents underranked
     */
    private ArrayList<String> getUnderRankTorrents(double averageTorrentRank) {
        ArrayList<String> underRankTorrents = new ArrayList<>();
        this.allTorrents.forEach(new BiConsumer<String, Integer>() {
            @Override
            public void accept(String s, Integer integer) {
                if (integer < averageTorrentRank) {
                    underRankTorrents.add(s);
                }
            }
        });
        return underRankTorrents;
    }

    /**
     * the rank represent the nu;ber of hosted downloads for 1 torrent
     *
     * @return the average rank for all torrents
     */
    private double averageRank() {
        int sum = 0;
        for (int i : this.allTorrents.values()) {
            sum += i;
        }
        return sum / this.allTorrents.size();
    }

    /**
     * separate the received list into 3 lists : added, deleted , hosted torrents on the client
     * ( the lists are sent in 1 list to optimize the number of reauests to the server )
     *
     * @param receivedList
     */
    private void getLists(ArrayList<String> receivedList) {
        int posDeletedList = receivedList.indexOf("deletedList");
        ArrayList<String> addedList = new ArrayList<>(receivedList.subList(1, posDeletedList));
        ArrayList<String> deletedList = new ArrayList<>(receivedList.subList(posDeletedList + 1, receivedList.size()));

        ranker(addedList, 1);
        ranker(deletedList, -1);
    }

    /**
     * Set a rank to each downloads received
     * the rank represent the number of copies*availability of each torrent
     *
     * @param list
     * @param increment
     */
    private void ranker(List<String> list, int increment) {
        for (String s : list) {
            int v = allTorrents.getOrDefault(s, 0);
            if (v == 0)
                missingTorrents.add(s);

            v = v + increment;
            allTorrents.put(s, v);
        }
    }

    /**
     * Makes snapshot used for recovery and replicas that have very old state
     **/
    protected byte[] makeSnapshot() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(stream);
            objectOutputStream.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return stream.toByteArray();
    }

    /**
     * Brings the system up-to-date from a snapshot
     **/
    @SuppressWarnings("unchecked")
    protected void updateToSnapshot(byte[] snapshot) {

        // For message service the "recovery" is just recreation of underlaying message
        ByteArrayInputStream stream = new ByteArrayInputStream(snapshot);
        ObjectInputStream objectInputStream;
        try {
            objectInputStream = new ObjectInputStream(stream);
            message = (HashMap<Character, String>) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
