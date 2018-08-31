package machinelearning.active.learning;

import machinelearning.active.ActiveLearner;
import machinelearning.active.Ranker;
import machinelearning.active.ranker.RandomRanker;

/**
 * The RandomSampler is the most used baseline active. Basically, at every iteration it randomly samples one point from
 * the unlabeled set at random. It should only be used for comparison with new algorithms, never in any serious scenario.
 *
 * @author luciano
 */
public class RandomSampler extends ActiveLearner {
    @Override
    protected Ranker computeRanker() {
        return new RandomRanker();
    }
}
