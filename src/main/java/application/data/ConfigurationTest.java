package application.data;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import config.ExperimentConfiguration;
import data.DataPoint;
import data.LabeledPoint;
import io.json.JsonConverter;
import machinelearning.classifier.Label;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;

public class ConfigurationTest {


    public static void main(String[] args){



        String json = "{\"activeLearner\":{\"learner\":{\"name\":\"MajorityVote\",\"sampleSize\":8,\"versionSpace\":{\"addIntercept\":true,\"hitAndRunSampler\":{\"cache\":true,\"rounding\":true,\"selector\":{\"name\":\"WarmUpAndThin\",\"thin\":10,\"warmUp\":100}},\"kernel\":{\"name\":\"gaussian\"},\"solver\":\"ojalgo\"}},\"name\":\"UncertaintySampler\"},\"subsampleSize\":50000,\"task\":\"sdss_Q4_0.1%\",\"multiTSM\":{\"hasTSM\":true,\"searchUnknownRegionProbability\":0.5,\"featureGroups\":[[\"age\"],[\"sex\"]],\"columns\":[\"age\",\"sex\"],\"flags\":[[true,false],[true,false]]}}";


        ExperimentConfiguration configuration =  JsonConverter.deserialize(json, ExperimentConfiguration.class);


        System.out.println(configuration.getTsmConfiguration().getSearchUnknownRegionProbability());
        System.out.println(configuration.getTsmConfiguration().hasTsm());
        System.out.println(configuration.getTsmConfiguration().getFeatureGroups().get(0)[0]);
    }
    
}
