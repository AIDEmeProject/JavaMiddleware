package explore;

import data.DataPoint;
import data.preprocessing.StandardScaler;
import explore.user.User;
import explore.user.UserStub;
import io.FolderManager;
import io.TaskReader;

import java.util.Collections;
import java.util.List;
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

        List<DataPoint> dataPoints = Collections.unmodifiableList(StandardScaler.fitAndTransform(reader.readData()));

        Set<Long> positiveKeys = reader.readTargetSetKeys();  // TODO: pass feature groups
        User user = new UserStub(positiveKeys);

        explore = new Explore(experimentFolder, dataPoints, user);
        evaluate = new Evaluate(experimentFolder, dataPoints, user);
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
