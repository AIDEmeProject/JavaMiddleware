package io;

import java.io.File;
import java.io.IOException;

/**
 * This class manages a folder in the local filesystem. It can create new files in the folder, and return a list of
 *
 */
public class FolderManager {
    /**
     * File object pointing to folder
     */
    private File folder;

    /**
     * @param path: path to folder
     * @throws RuntimeException if folder fails to be created or if path is not a directory
     */
    public FolderManager(String path) {
        folder = new File(path);

        if(!folder.exists() && !folder.mkdirs()){
           throw new RuntimeException("Failed to create output folder at: " + path);
        }

        if (!folder.isDirectory()){
            throw new RuntimeException("Path " + path + " does not represent an existing directory.");
        }
    }

    /**
     * @return a new file object
     * @throws RuntimeException if file already exists or an IO error occurs
     */
    private File createNewFile(String filename) {
        String path = folder.getAbsolutePath();
        File newFile = new File(path + File.separator + filename);
        try {
            if (!newFile.createNewFile()) {
                throw new RuntimeException("File already exists!");
            }
        } catch (IOException ex){
            throw new RuntimeException("Failed to create file: " + newFile.getName(), ex);
        }
        return newFile;
    }

    public File createNewRunFile() {
        return createNewFile(System.currentTimeMillis() + ".run");
    }

    public File createNewOutputFile(){
        return createNewFile(System.currentTimeMillis() + ".average");
    }

    /**
     * @return list of run files in folder
     */
    public File[] getRuns(){
        return folder.listFiles(file -> file.getName().endsWith(".run"));
    }
}
