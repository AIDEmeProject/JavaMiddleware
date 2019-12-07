package machinelearning.active;

import explore.ExperimentConfiguration;
import machinelearning.active.learning.SubspatialActiveLearner;
import machinelearning.active.learning.UncertaintySampler;
import machinelearning.classifier.Learner;
import machinelearning.classifier.svm.GaussianKernel;
import machinelearning.classifier.svm.Kernel;
import machinelearning.classifier.svm.SvmLearner;

public class LearnerFactory{

    public Learner buildLearner(String strLearner, ExperimentConfiguration configuration){


        if( strLearner.equals("simplemargin") || strLearner.equals("simplemargintsm")) {
            Learner learner;
            System.out.println("SVM");
            double C = 1000;
            Kernel kernel = new GaussianKernel();
            learner = new SvmLearner(C, kernel);
            return learner;
        }
        else if (strLearner.equals("versionspace")){
            Learner learner;
            learner = ((UncertaintySampler) configuration.getActiveLearner()).getLearner();
            return learner;
        }
        else {//factorizedversionspace
            Learner learner;
            learner = ((SubspatialActiveLearner) configuration.getActiveLearner()).getSubspatialLearner();
            return learner;
        }

    }
}


