/*
 * Copyright (c) 2019 École Polytechnique
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, you can obtain one at http://mozilla.org/MPL/2.0
 *
 * Authors:
 *       Luciano Di Palma <luciano.di-palma@polytechnique.edu>
 *       Enhui Huang <enhui.huang@polytechnique.edu>
 *       Laurent Cetinsoy <laurent.cetinsoy@gmail.com>
 *
 * Description:
 * AIDEme is a large-scale interactive data exploration system that is cast in a principled active learning (AL) framework: in this context,
 * we consider the data content as a large set of records in a data source, and the user is interested in some of them but not all.
 * In the data exploration process, the system allows the user to label a record as “interesting” or “not interesting” in each iteration,
 * so that it can construct an increasingly-more-accurate model of the user interest. Active learning techniques are employed to select
 * a new record from the unlabeled data source in each iteration for the user to label next in order to improve the model accuracy.
 * Upon convergence, the model is run through the entire data source to retrieve all relevant records.
 */

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
