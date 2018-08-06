package sampling;

import utils.Validator;
import utils.convexbody.ConvexBody;
import utils.convexbody.Line;

public class HitAndRunSampler {
    private int warmup;
    private int thin;

    public HitAndRunSampler(int warmup, int thin) {
        Validator.assertNonNegative(warmup);
        Validator.assertPositive(thin);

        this.warmup = warmup;
        this.thin = thin;
    }

    public double[] sample(ConvexBody body){
        return sample(body, 1)[0];
    }

    //TODO: should we return a Stream here?
    public double[][] sample(ConvexBody body, int numSamples){
        double[] point = body.getInteriorPoint();
        double[][] samples = new double[numSamples][point.length];

        // warm-up phase
        for (int i = 0; i < warmup; i++) {
            point = advance(body, point);
        }

        for (int i = 0; i < numSamples; i++) {
            // only keep every "thin" samples
            for (int j = 0; j < thin; j++) {
                point = advance(body, point);
            }

            samples[i] = point;
        }

        return samples;
    }

    private double[] advance(ConvexBody body, double[] point){
        return body.computeLineIntersection(Line.getRandomLine(point)).sampleRandomPoint();
    }

}
