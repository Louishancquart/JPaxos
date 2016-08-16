package lsr.paxos.test.ka47;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

/**
 * This class Manage gather infos on hosted torrents
 */
public class KaTorrentManager {
    private ArrayList<String> addedDownloads;
    private ArrayList<String> deletedDownloads;


    private final File downloadsDir;
    private final File torrentsDir;
    private ArrayList<String> torrentsList;
    private ArrayList<String> downloadsList;

    public void setLastHostedDownloads(ArrayList<String> lastHostedDownloads) {
        this.lastHostedDownloads = lastHostedDownloads;
    }

    private ArrayList<String> lastHostedDownloads;

    public ArrayList<String> getLastHostedDownloads() {
        return lastHostedDownloads;
    }

    public ArrayList<String> getTorrentsList() {
        return torrentsList;
    }

    public ArrayList<String> getDownloadsList() {
        return downloadsList;
    }

    /**
     * update the list of Hosted torrents
     * this m
     */
    public void updateLastHostedDownloads(){
        setLastHostedDownloads(getHostedDownloads());
    }

    public KaTorrentManager(File torrentDir, File downloadDir) {
        this.torrentsDir = torrentDir;
        this.downloadsDir = downloadDir;
        this.lastHostedDownloads = new ArrayList<>();
        this.torrentsList = new ArrayList<>();
        this.downloadsList = new ArrayList<>();
    }

    public void updateChanges(){
        this.addedDownloads = addedDownloads();
        this.deletedDownloads = deletedDownloads();
        updateLastHostedDownloads();
    }

    /**
     * list Configured Download and .torrent Directories to identify matches
     * @return list of hosted Downloads copmpared with what contains the .torrent file folder configured
     */
    public ArrayList<String> getHostedDownloads() {
        // list torrentDir
        torrentsList = listDir(this.torrentsDir, ".torrent");

        //  list recursively downloadDir depth :2
        downloadsList = listDir(this.downloadsDir, "");

        //  match hosted torrents
        ArrayList<String> hostedTorrents = (ArrayList<String>) torrentsList.clone();
        hostedTorrents.retainAll(downloadsList);
        return hostedTorrents;
    }

    public ArrayList<String> addedDownloads(){
        // for 1 st launch
        if( this.lastHostedDownloads.isEmpty()){
            return getHostedDownloads();
        }

        //we get the lastHostedDownloads - deletedDownloads
        ArrayList<String> keptDownloads = (ArrayList<String>) this.lastHostedDownloads.clone();
        keptDownloads.removeAll(deletedDownloads());

        //we do downloadsList - keptDownloads
        ArrayList<String> addedDownloadsList = getHostedDownloads();
        addedDownloadsList.removeAll(keptDownloads);

        return addedDownloadsList;
        }

    public ArrayList<String> getAddedDownloads() {
        return addedDownloads;
    }

    public ArrayList<String> getDeletedDownloads() {
        return deletedDownloads;
    }

    public ArrayList<String> deletedDownloads(){
        ArrayList<String> deletedDownloads = new ArrayList<>();

        // for 1 st launch
        if( this.lastHostedDownloads.isEmpty()){
            return deletedDownloads;
        }

        deletedDownloads = getLastHostedDownloads();
        deletedDownloads.removeAll(getHostedDownloads());
        return deletedDownloads;
    }

    /**
     * List files in a directory according to an extenion file ( ".torrent"  or nothing for downloads)
     * @param dir
     * @param extension
     * @return liste of filenames ( without .torrent if necessary)
     */
    public ArrayList<String> listDir(File dir, final String extension) {
        File[] paths;

        // create new filename filter
        FilenameFilter fileNameFilter = new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {

                // for downloadsDir
                if (extension.equals("")) {
                    return true;
                }

                //for torrentsDir
                if (name.lastIndexOf('.') > 0) {
                    // get last index for '.' char
                    int lastIndex = name.lastIndexOf('.');

                    // get extension
                    String str = name.substring(lastIndex);

                    // match path name extension
                    if (str.equals(extension)) {
                        return true;
                    }
                }
                return false;
            }
        };

        // returns pathnames for files and directory
        paths = dir.listFiles(fileNameFilter);

        // Paths arrays to SimpleName String List
        ArrayList<String> fileNameList = new ArrayList<String>();


        if (extension.equals("")) {
            for (File path : paths) {
                fileNameList.add((path.getName()));
                if (path.isDirectory())
                    fileNameList.addAll(listDir(path, ""));
            }
        } else {
            for (File path : paths) {
                fileNameList.add((path.getName().split(extension))[0]);
            }
        }
            return fileNameList;
        }
    }
