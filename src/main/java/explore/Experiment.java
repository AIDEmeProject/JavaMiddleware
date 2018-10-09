package explore;

import data.IndexedDataset;
import data.preprocessing.StandardScaler;
import explore.user.User;
import explore.user.UserStub;
import io.FolderManager;
import io.TaskReader;

import java.util.Set;

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
        IndexedDataset standardizedData = rawData.copyWithSameIndexes(StandardScaler.fitAndTransform(rawData.getData()));

        Set<Long> positiveKeys = reader.readTargetSetKeys();
        User user = new UserStub(positiveKeys);

        explore = new Explore(experimentFolder, standardizedData, user);
        evaluate = new Evaluate(experimentFolder, standardizedData, user);
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
