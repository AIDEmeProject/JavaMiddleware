package machinelearning.active.learning.versionspace;

import data.LabeledPoint;
import machinelearning.active.learning.versionspace.convexbody.*;
import machinelearning.classifier.margin.LinearClassifier;
import utils.Validator;
import utils.linprog.LinearProgramSolver;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * The Version Space for the {@link LinearClassifier}. Mathematically, it can be defined by a set of linear inequalities:
 *
 *      \( y_i (b + \langle w, x_i \rangle) &gt; 0 \)
 *
 * As you can see, this region defines a PolyhedralCone in the euclidean space. In order to sample (b,w) pairs from this
 * region, we use the Hit-and-Run algorithm.
 *
 * @see HitAndRunSampler
 * @see PolyhedralCone
 */
public class LinearVersionSpace implements VersionSpace {
    /**
     * Whether to add intercept to data points
     */
    private boolean addIntercept;

    /**
     * {@link HitAndRunSampler} instance for sampling from this version space
     */
    private final HitAndRunSampler sampler;

    /**
     * {@link LinearProgramSolver} factory
     */
    private final LinearProgramSolver.FACTORY solverFactory;

    /**
     * {@link SampleCache} sample caching procedure
     */
    private SampleCache sampleCache;

    /**
     * By default, no intercept and no sample caching is performed.
     *
     * @param sampler: Hit-and-Run sampler instance
     * @param solverFactory: {@link LinearProgramSolver} factory object
     * @throws NullPointerException if sampler is null
     */
    public LinearVersionSpace(HitAndRunSampler sampler, LinearProgramSolver.FACTORY solverFactory) {
        this.sampler = sampler;
        this.addIntercept = false;
        this.solverFactory = solverFactory;
        this.sampleCache = new DummySampleCache();
    }

    /**
     * Also sample intercept of Linear Classifiers
     */
    public void addIntercept() {
        this.addIntercept = true;
    }

    /**
     * Do not sample the intercept of Linear Classifiers (default)
     */
    public void dropIntercept() {
        this.addIntercept = false;
    }

    /**
     * @param sampleCache: new sample caching strategy to use
     */
    public void setSampleCachingStrategy(SampleCache sampleCache) {
        this.sampleCache = sampleCache;
    }

    /**
     * @param labeledPoints: training data
     * @param numSamples: number of hypothesis to sample
     * @return sample of Linear Classifiers obtained through the Hit-and-Run algorithm.
     */
    @Override
    public LinearClassifier[] sample(Collection<LabeledPoint> labeledPoints, int numSamples) {
        Validator.assertPositive(numSamples);

        ConvexBody cone = new PolyhedralCone(addIntercept(labeledPoints), solverFactory);
        cone = sampleCache.attemptToSetDefaultInteriorPoint(cone);

        double[][] samples = sampler.sample(cone, numSamples);

        sampleCache.updateCache(samples);

        return getLinearClassifiers(samples);
    }

    private Collection<LabeledPoint> addIntercept(Collection<LabeledPoint> labeledPoints) {
        if (!addIntercept){
            return labeledPoints;
        }

        return labeledPoints.stream()
                .map(LabeledPoint::addBias)
                .collect(Collectors.toList());
    }

    private LinearClassifier[] getLinearClassifiers(double[][] samples) {
        LinearClassifier[] classifiers = new LinearClassifier[samples.length];

        for (int i = 0; i < samples.length; i++) {
            classifiers[i] = new LinearClassifier(samples[i], addIntercept);
        }

        return classifiers;
    }
}

