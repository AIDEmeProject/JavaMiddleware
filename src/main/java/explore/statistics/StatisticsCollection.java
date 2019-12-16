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

package explore.statistics;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringJoiner;

/**
 * This class maintains a collection of metric statistics, allowing to add new statistics and update their values.
 */
public class StatisticsCollection implements Iterable<Statistics> {
    /**
     * mapping metric_name -> statistics
     */
    private Map<String, Statistics> statistics;

    /**
     * Creates an empty collection
     */
    public StatisticsCollection() {
        statistics = new HashMap<>();
    }

    /**
     * @param name: statistic's name
     * @return requested statistic
     * @throws IllegalArgumentException if name is not in collection
     */
    public Statistics get(String name){
        Statistics stat = statistics.get(name);

        if (stat == null){
            throw new IllegalArgumentException("Statistic " + name + " not in collection.");
        }

        return stat;
    }

    /**
     * Update's a particular metric statistic with a new value. If this metric is not in the object, it will be inserted instead.
     *
     * @param name: name of metric to be updated
     * @param value: new value observed for this metric
     */
    public void update(String name, Double value){
        if (this.statistics.containsKey(name)){
            this.statistics.get(name).update(value);
        }
        else {
            statistics.put(name, new Statistics(name, value));
        }
    }

    /**
     * Update statistics from values in Metrics object. Metrics not in this collection will be simply appended.
     * @param metrics: Metrics object
     */
    public void update(Map<String, Double> metrics){
        metrics.forEach(this::update);
    }

    @Override
    public Iterator<Statistics> iterator() {
        return statistics.values().iterator();
    }

    /**
     * @return JSON array of all statistics in this collection
     */
    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(", ","[", "]");
        for (Statistics metric : statistics.values()){
            joiner.add(metric.toString());
        }
        return joiner.toString();
    }
}
