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

package application;

import data.*;

import explore.ExploreIteration;
import explore.InitialIteration;
import config.ExperimentConfiguration;

import machinelearning.active.Ranker;
import machinelearning.classifier.Classifier;

import java.util.ArrayList;

public class UserExperimentManager implements FacadeInterface {

    private final ExperimentConfiguration configuration;

    private final ExploreIteration exploreIteration;

    private final RandomSampler initialSampler;

    private final PartitionedDataset partitionedDataset;

    private final InitialIteration intialIteration;

    private Ranker ranker;

    private boolean isFirstStep;

    /**
     *
     */
    public UserExperimentManager(ExperimentConfiguration configuration, IndexedDataset dataset) {

        this.configuration = configuration;

        this.intialIteration = new InitialIteration(configuration);

        this.exploreIteration = new ExploreIteration(configuration);

        this.partitionedDataset = new PartitionedDataset(dataset);

        this.ranker = null;

        this.isFirstStep = true;

        this.initialSampler = new RandomSampler(this.partitionedDataset);
    }

    public LabeledDataset getLabeledDataset(Classifier classifier){

        IndexedDataset unlabeledPoints = this.partitionedDataset.getUnlabeledPoints();

        //LabeledDataset labeledDataset = new LabeledDataset(unlabeledPoints, classifier.predict(unlabeledPoints));

        //return labeledDataset;
        return null;
    }

    @Override
    public ArrayList<DataPoint> nextIteration(ArrayList<LabeledPoint> userLabeledPoints) {


        return new ArrayList<DataPoint>();
        /*
        Iteration.Result result;
        if (userLabeledPoints.isEmpty()){
            return this.initialSampler.getPoints(3);
        }
        if (this.isFirstStep){

            //HERE I HAVE THE FIVE FIRST LABEL WITH ONE YES and ONE NO
            //this.intialIteration.run(this.partitionedDataset);
            for (LabeledPoint point: userLabeledPoints){
                this.partitionedDataset.update(point);
            }

            this.ranker = this.configuration.getActiveLearner().fit(this.partitionedDataset.getLabeledPoints());
            this.isFirstStep = false;
            return new ArrayList<DataPoint>();
        }
        else{
            for (LabeledPoint point: userLabeledPoints){

                GuiUser user = new GuiUser();
                user.setLabel(point);

                result = this.exploreIteration.run(this.partitionedDataset, user, ranker);

                this.ranker = result.getRanker();
            }
            GuiUser user = new GuiUser();
            return (ArrayList<DataPoint>) this.exploreIteration.getNextPointsToLabel(this.partitionedDataset, user, this.ranker);

            return new ArrayList<DataPoint>();


        }
           */
    }
}
