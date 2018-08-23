package machinelearning.active.learning.versionspace.convexbody.sampling;

import machinelearning.active.learning.versionspace.convexbody.ConvexBody;
import machinelearning.active.learning.versionspace.convexbody.Line;
import machinelearning.active.learning.versionspace.convexbody.LineSegment;
import utils.Validator;

import java.util.Random;

/**
 * The Hit-and-Run Sampler is an algorithm for sampling points uniformly at random from a bounded convex body K. This
 * algorithm computes a Markov Chain of points inside K, defined as:
 *
 *      1: \(X_0\) = any point inside the convex body
 *      2: for \(t\) from \(1\) to \(T\):
 *      3:    line = sample a random line going through \(X_t\)
 *      4:    segment = intersection between line and K
 *      5:    \(K_{t+1}\) = a random point in the segment
 *
 * This Markov Chain converges to the uniform distribution over K.
 *
 * Since samples in a markov chain are correlated and only reach a distribution in the limit, we provide two techniques
 * which help counter this effect:
 *
 *  - warm-up: we ignore the first "n" samples from the chain. The remaining samples will have a closer distribution to
 *             the limiting one.
 *  - thinning: we only consider every n-th sample from the chain. This reduces the correlation between samples.
 *
 * REFERENCES:
 *      Hit-and-run mixes fast, Laszlo Lovasz
 */
public class HitAndRunSampler {
    /**
     * Number of elements to skip during warm-up phase
     */
    private int warmup;

    /**
     * Select every "thin"-th element from the Hit-and-Run chain
     */
    private int thin;

    private final DirectionSamplingAlgorithm directionSamplingAlgorithm;

    private final Random rand = new Random();  // TODO: set this seed from exterior

    public HitAndRunSampler(int warmup, int thin) {
        this(warmup, thin, new RandomDirectionAlgorithm());
    }

    /**
     * @param warmup: the first "warmup" samples will be ignored. The higher this value, the closest to uniform samples will be.
     * @param thin: after the warmup phase, only retain every "thin" samples. The higher this value, the more independent to each other samples will be.
     * @throws IllegalArgumentException if "warmup" is negative or "thin" is not positive
     */
    public HitAndRunSampler(int warmup, int thin, DirectionSamplingAlgorithm directionSamplingAlgorithm) {
        Validator.assertNonNegative(warmup);
        Validator.assertPositive(thin);

        this.warmup = warmup;
        this.thin = thin;
        this.directionSamplingAlgorithm = directionSamplingAlgorithm;
    }

    /**
     * @param body: convex body to sample
     * @param numSamples: number of samples
     * @return matrix where each row is a hit-and-run sample from the convex body. Warm-up and thinning will be applied.
     * @throws IllegalArgumentException if numSamples is not positive
     */
    public double[][] sample(ConvexBody body, int numSamples){
        Validator.assertPositive(numSamples);

        DirectionSampler directionSampler = directionSamplingAlgorithm.fit(body);

        // warm-up phase
        double[][] samples = new double[numSamples][];
        samples[0] = advance(body, body.getInteriorPoint(), directionSampler, warmup);

        for (int i = 1; i < numSamples; i++) {
            // only keep every "thin" samples
            samples[i] = advance(body, samples[i-1], directionSampler, thin);
        }

        return samples;
    }

    /**
     * Advance "n" steps in the Markov Chain
     */
    private double[] advance(ConvexBody body, double[] point, DirectionSampler directionSampler, int n){
        for (int i = 0; i < n; i++) {
            point = advance(body, point, directionSampler);
        }
        return point;
    }

    /**
     * Compute the next element of the Markov Chain (steps 3 to 5 in the pseudo-code)
     */
    private double[] advance(ConvexBody body, double[] currentPoint, DirectionSampler directionSampler){
        Line line = new Line(currentPoint, directionSampler.sampleDirection(rand));
        LineSegment segment = body.computeLineIntersection(line);
        return segment.getPoint(rand.nextDouble());
    }
}
