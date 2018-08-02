package io;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * This module maintains a collection of files open, reading one line from all files at once. As we reach the EOF,
 * files will be discarded until there are none left.
 */
public class MultipleFilesReader implements AutoCloseable {
    /**
     * Collection of file scanners. We use a LinkedList since it has better performance for removing elements while iterating.
     */
    private LinkedList<Scanner> scanners;

    /**
     * @param files: list of files to read
     * @throws IOException if an IO error occurs while opening the files
     */
    public MultipleFilesReader(File[] files) throws IOException {
        scanners = new LinkedList<>();

        for (File file : files) {
            scanners.add(new Scanner(new FileReader(file)));
        }
    }

    /**
     * @return true if there is at least one s
     */
    public boolean hasNext(){
        return !scanners.isEmpty();
    }

    /**
     * Returns a collection containing one line from each file. Whenever we reach an EOF, the file is closed and elements
     * will no longer drawn from it. Thus, we DO NOT guarantee that lines will be drawn in the same order as the input files
     * (except if all files have the same number of lines).
     *
     * @return collection containing one line from each open file
     */
    public Collection<String> readlines() {
        ArrayList<String> lines = new ArrayList<>();

        ListIterator<Scanner> iterator = scanners.listIterator();
        while(iterator.hasNext()) {
            Scanner scanner = iterator.next();

            if (scanner.hasNextLine()){
                lines.add(scanner.nextLine());
            }

            // remove scanner if it reached EOF
            if (!scanner.hasNextLine()){
                scanner.close();
                iterator.remove();
            }
        }

        return lines;
    }

    /**
     * Closes all file resources
     */
    public void close() {
        for (Scanner reader : scanners){
            reader.close();
        }
    }
}
