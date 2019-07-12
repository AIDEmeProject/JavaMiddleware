package machinelearning.active.learning.versionspace;

import data.LabeledDataset;
import explore.user.UserLabel;
import machinelearning.active.learning.versionspace.manifold.ConvexBody;
import machinelearning.active.learning.versionspace.manifold.HitAndRunSampler;
import machinelearning.active.learning.versionspace.manifold.euclidean.PolyhedralCone;
import machinelearning.active.learning.versionspace.manifold.euclidean.UnitBallPolyhedralCone;
import machinelearning.active.learning.versionspace.manifold.sphere.UnitSpherePolyhedralCone;
import machinelearning.classifier.LinearMajorityVote;
import machinelearning.classifier.margin.LinearClassifier;
import utils.Validator;
import utils.linalg.IncrementalCholesky;
import utils.linalg.Matrix;
import utils.linalg.Vector;
import utils.linprog.LinearProgramSolver;

import java.util.Arrays;
import java.util.Objects;

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
     * {@link HitAndRunSampler} instance for sampling from this version space
     */
    private final HitAndRunSampler hitAndRunSampler;

    /**
     * {@link LinearProgramSolver} factory
     */
    private final LinearProgramSolver.FACTORY solverFactory;

    /**
     * Whether to add intercept to data points
     */
    private boolean addIntercept = false;

    /**
     * Whether to decompose data matrix
     */
    private boolean decompose = false;

    private IncrementalCholesky decomposition;

    private double jitter = 0;

    /**
     * Whether to sample from sphere
     */
    private boolean useSphericalSampling = false;

    /**
     * By default, no intercept and no sample caching is performed.
     *
     * @param hitAndRunSampler: Hit-and-Run sampler instance
     * @param solverFactory: {@link LinearProgramSolver} factory object
     * @throws NullPointerException if sampler is null
     */
    public LinearVersionSpace(HitAndRunSampler hitAndRunSampler, LinearProgramSolver.FACTORY solverFactory) {
        this.hitAndRunSampler = Objects.requireNonNull(hitAndRunSampler);
        this.solverFactory = Objects.requireNonNull(solverFactory);
    }

    public void setJitter(double jitter) {
        Validator.assertNonNegative(jitter);
        this.jitter = jitter;
    }

    public void useSphericalSampling() {
        this.useSphericalSampling = true;
    }

    /**
     * Also sample intercept of Linear Classifiers
     */
    public void addIntercept() {
        this.addIntercept = true;
    }

    public void useDecomposition() {
        decompose = true;
        decomposition = new IncrementalCholesky();
    }

    /**
     * @param labeledPoints: training data
     * @param numSamples: number of hypothesis to sample
     * @return sample of Linear Classifiers obtained through the Hit-and-Run algorithm.
     */
    @Override
    public LinearMajorityVote sample(LabeledDataset labeledPoints, int numSamples) {
        Validator.assertPositive(numSamples);

        PolyhedralCone cone = buildPolyhedralCone(labeledPoints);
        ConvexBody body = useSphericalSampling ? new UnitSpherePolyhedralCone(cone) : new UnitBallPolyhedralCone(cone);

        Vector[] samples = hitAndRunSampler.sample(body, numSamples);

        return buildMajorityVoteClassifier(samples);
    }

    private PolyhedralCone buildPolyhedralCone(LabeledDataset labeledPoints) {
        Matrix X = labeledPoints.getData().copy();

        if (decompose) {
            X.iAddScalarToDiagonal(jitter);

            int lower = decomposition.getCurrentDim(), upper = X.rows();

            // reset Cholesky factorization if necessary
            if (lower >= upper) {
                lower = 0;
                decomposition = new IncrementalCholesky();
            }

            for (int i = lower; i < upper; i++) {
                decomposition.increment(X.getRow(i).resize(decomposition.getCurrentDim() + 1));
            }

            X = decomposition.getL();
        }

        X = addIntercept ? X.addBiasColumn() : X;

        Vector y = Vector.FACTORY.make(
                Arrays.stream(labeledPoints.getLabels())
                        .mapToDouble(UserLabel::asSign)
                        .toArray()
        );

        return new PolyhedralCone(X.iMultiplyColumn(y), solverFactory);
    }

    private LinearMajorityVote buildMajorityVoteClassifier(Vector[] samples) {
        Vector bias = Vector.FACTORY.zeros(samples.length);
        Matrix weights = Matrix.FACTORY.make(samples);

        if (addIntercept) {
            bias = weights.getCol(0);
            weights = weights.getColSlice(1, weights.cols());
        }

        if (decompose) {
            weights = weights.matrixMultiply(decomposition.getInverse());
        }

        return new LinearMajorityVote(bias, weights);
    }
}

