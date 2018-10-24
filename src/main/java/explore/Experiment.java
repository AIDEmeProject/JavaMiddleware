package explore;

import data.DataPoint;
import data.preprocessing.StandardScaler;
import explore.user.FactoredUser;
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
        User user = getUser(configuration, reader);

        explore = new Explore(experimentFolder, dataPoints, user);
        evaluate = new Evaluate(experimentFolder, dataPoints, user);
    }

    private User getUser(ExperimentConfiguration configuration, TaskReader reader) {
        if (configuration.hasMultiTSM()) {
            ExperimentConfiguration.TsmConfiguration tsmConfiguration = configuration.getTsmConfiguration();
            return new FactoredUser(reader.readFactorizedTargetSetKeys(tsmConfiguration));
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
