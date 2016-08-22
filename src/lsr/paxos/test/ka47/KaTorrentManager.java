package lsr.paxos.test.ka47;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

/**
 * This class Manage gather infos on hosted torrents
 */
public class KaTorrentManager {
    private final File downloadsDir;

    public File getDownloadsDir() {
        return downloadsDir;
    }

    public File getTorrentsDir() {
        return torrentsDir;
    }

    private final File torrentsDir;
    private ArrayList<String> addedDownloads;
    private ArrayList<String> deletedDownloads;
    private ArrayList<String> torrentsList;
    private ArrayList<String> downloadsList;
    private ArrayList<String> lastHostedDownloads;

    public KaTorrentManager(File torrentDir, File downloadDir) {
        this.torrentsDir = torrentDir;
        this.downloadsDir = downloadDir;
        this.lastHostedDownloads = new ArrayList<>();
        this.torrentsList = new ArrayList<>();
        this.downloadsList = new ArrayList<>();
    }

    public ArrayList<String> getLastHostedDownloads() {
        return lastHostedDownloads;
    }

    public void setLastHostedDownloads(ArrayList<String> lastHostedDownloads) {
        this.lastHostedDownloads = lastHostedDownloads;
    }


    /**
     * update the list of Hosted torrents
     * this m
     */
    public void updateLastHostedDownloads() {
        setLastHostedDownloads(hostedDownloads());
    }

    /**
     * store  added / deleted Downloads
     * then update the current list of HostedDownloads
     */
    private void updateChanges() {
        this.addedDownloads = addedDownloads();
        this.deletedDownloads = deletedDownloads();
        updateLastHostedDownloads();
    }

    /**
     * @return added / deleted / hosted list in a single Array
     */
    public ArrayList<String> downloadsStateToArray() {
        updateChanges();

        ArrayList<String> returnedList = new ArrayList<>();

        returnedList.add("added");
        returnedList.addAll(this.addedDownloads);
        returnedList.add("deletedList");
        returnedList.addAll(deletedDownloads);

        return returnedList;
    }

    /**
     * list Configured Download and .torrent Directories to identify matches
     *
     * @return list of hosted Downloads copmpared with what contains the .torrent file folder configured
     */
    public ArrayList<String> hostedDownloads() {
        // list torrentDir
        torrentsList = filePathsToFileNamesArray(listDir(this.torrentsDir, ".torrent"),".torrent");

        //  list recursively downloadDir depth :2
        downloadsList = filePathsToFileNamesArray(listDir(this.downloadsDir, ""),"");

        //  match hosted torrents
        ArrayList<String> hostedTorrents = (ArrayList<String>) torrentsList.clone();
        hostedTorrents.retainAll(downloadsList);
        return hostedTorrents;
    }

    public ArrayList<String> addedDownloads() {
        // for 1 st launch
        if (this.lastHostedDownloads.isEmpty()) {
            return hostedDownloads();
        }

        //we get the lastHostedDownloads - deletedDownloads
        ArrayList<String> keptDownloads = (ArrayList<String>) this.lastHostedDownloads.clone();
        keptDownloads.removeAll(deletedDownloads());

        //we do downloadsList - keptDownloads
        ArrayList<String> addedDownloadsList = hostedDownloads();
        addedDownloadsList.removeAll(keptDownloads);

        return addedDownloadsList;
    }

    public ArrayList<String> getAddedDownloads() {
        return addedDownloads;
    }

    public ArrayList<String> getDeletedDownloads() {
        return deletedDownloads;
    }

    public ArrayList<String> deletedDownloads() {
        ArrayList<String> deletedDownloads = new ArrayList<>();

        // for 1 st launch
        if (this.lastHostedDownloads.isEmpty()) {
            return deletedDownloads;
        }

        deletedDownloads = getLastHostedDownloads();
        deletedDownloads.removeAll(hostedDownloads());
        return deletedDownloads;
    }

    /**
     * List files in a directory according to an extenion file ( ".torrent"  or nothing for downloads)
     *
     * @param dir
     * @param extension
     * @return liste of filenames ( without .torrent if necessary)
     */
    public File[] listDir(File dir, final String extension) {
        File[] paths;

        // create new filename filter
        FilenameFilter fileNameFilter = (dir1, name) -> {

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
        };

        // returns pathnames for files and directory
        paths = dir.listFiles(fileNameFilter);

        // Paths arrays to SimpleName String List
        return paths;
    }

    private ArrayList<String> filePathsToFileNamesArray(File[] paths, String extension) {
        ArrayList<String> fileNameList = new ArrayList<String>();

        if (extension.equals("")) {
            for (File path : paths) {
                fileNameList.add((path.getName()));
                if (path.isDirectory())
                    fileNameList.addAll(filePathsToFileNamesArray(listDir(path, ""),""));
            }
        } else {
            for (File path : paths)
                fileNameList.add((path.getName().split(extension))[0]);
        }
        return fileNameList;
    }


    public ArrayList<File> getMissingTorrentsFiles(ArrayList<String> list){
        File[] paths = listDir(torrentsDir,".torrent");
       ArrayList<File> missingTorrents = new ArrayList<>();
        for(File f : paths){
           if(list.contains(f.getName()))
               missingTorrents.add(f);
        }
        return missingTorrents;
    }
}
