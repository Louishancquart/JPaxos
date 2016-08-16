package lsr.paxos.test.ka47;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;


public class KaTorrentManagerTest {
    private static KaTorrentManager tm;
    private static ArrayList<String> verifyedList;

    @Before
    public void setUp() throws Exception {
        tm = new KaTorrentManager(new File("/home/m/documents/put/s3/MTh/Paxos/JPaxos/src/lsr/testResources/torrents"),
                new File("/home/m/documents/put/s3/MTh/Paxos/JPaxos/src/lsr/testResources/downloads"));

        tm.setLastHostedDownloads(tm.getHostedDownloads());

        verifyedList = new ArrayList<String>(Arrays.asList("Pirate Informatique N°30 - Aout-Octobre 2016.pdf",
                "Votre santé par les jus frais de légumes et de fruits - Norman Walker.pdf",
                "(pdf+epub) Comment décoder les gestes de vos interlocuteurs  David Cohen",
                "Le Temps est assassin - Michel Bussi (2016) [ePub].epub"));
    }

    @Test
    public void setHostedDownloadsTest() throws Exception {
        assertEquals(verifyedList, this.tm.getLastHostedDownloads());

        }

    @Test
    public void getDeletedDownloadsTest() throws Exception {
        //emptytest
        tm.setLastHostedDownloads(new ArrayList<String>());
        assertEquals(new ArrayList<>(),tm.deletedDownloads());

        //normal test
        tm.setLastHostedDownloads(tm.getHostedDownloads());
        ArrayList<String> tmpList = tm.getHostedDownloads();
        tmpList.add(0,"new DL");
        tm.setLastHostedDownloads(tmpList);

        assertEquals(tmpList.get(0), tm.deletedDownloads().get(0));

    }

    @Test
    public void getAddedDownloadsTest() throws Exception {
         //emptytest
        tm.setLastHostedDownloads(tm.getHostedDownloads());
        ArrayList<String> tmpList = tm.getHostedDownloads();
        tm.setLastHostedDownloads(new ArrayList<String>());

        assertEquals(tmpList,tm.addedDownloads());

        //normal test
        tm.updateLastHostedDownloads();
        String verify = tmpList.remove(0);
        tm.setLastHostedDownloads(tmpList);

        assertEquals(verify, tm.addedDownloads().get(0));
    }
}