package explore;

import config.ExperimentConfiguration;
import config.TsmConfiguration;
import data.IndexedDataset;
import data.preprocessing.StandardScaler;
import explore.user.FactoredUser;
import explore.user.User;
import explore.user.UserStub;
import io.FolderManager;
import io.TaskReader;

public final class Experiment {
    private final FolderManager experimentFolder;
    private Explore explore;
    private Evaluate evaluate;

    public Experiment(FolderManager experimentFolder) {
        this.experimentFolder = experimentFolder;
        this.explore = null;
        this.evaluate = null;
    }

    private void initialize() {
        ExperimentConfiguration configuration = experimentFolder.getExperimentConfig();

        TaskReader reader = new TaskReader(configuration.getTask());

        IndexedDataset rawData = reader.readData();
        IndexedDataset scaledData = rawData.copyWithSameIndexes(StandardScaler.fitAndTransform(rawData.getData()));
        User user = getUser(configuration, reader);

        explore = new Explore(experimentFolder, scaledData, user);
        evaluate = new Evaluate(experimentFolder, scaledData, user);
    }

    private User getUser(ExperimentConfiguration configuration, TaskReader reader) {
        if (configuration.getTsmConfiguration().hasTsm()) {
            return new FactoredUser(reader.readFactorizedTargetSetKeys(configuration.getTsmConfiguration()));
        } else {
            return new UserStub(reader.readTargetSetKeys());
        }
    }

    public Explore getExplore() {
        if (explore == null) {
            initialize();
        }
        return explore;
    }

    public Evaluate getEvaluate() {
        if (evaluate == null) {
            initialize();
        }
        return evaluate;
    }
}
