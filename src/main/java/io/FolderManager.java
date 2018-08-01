package io;

import java.io.File;

public class FolderManager {
    private File folder;

    public FolderManager(String path) {
        folder = new File(path);

        if(!folder.exists() && !folder.mkdirs()){
           throw new RuntimeException("Failed to create output folder at: " + path);
        }

        if (!folder.isDirectory()){
            throw new RuntimeException("Path " + path + " does not represent an existing directory.");
        }
    }

    public File createNewFile() {
        String path = folder.getAbsolutePath();
        return new File(path + File.separator + "run_" + System.currentTimeMillis() + ".jsonl");
    }

    public File[] getRuns(){
        return folder.listFiles(file -> file.getName().startsWith("run"));
    }
}
