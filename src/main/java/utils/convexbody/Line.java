package utils.convexbody;

import utils.Validator;

import java.util.Random;

public class Line {
    private double[] center;
    private double[] direction;

    public Line(double[] center, double[] direction) {
        Validator.assertEqualLengths(center, direction);
        this.center = center;
        this.direction = direction;
    }

    public int getDim(){
        return center.length;
    }

    public double[] getCenter() {
        return center;
    }

    public double[] getDirection() {
        return direction;
    }

    public double[] getPoint(double t){
        double[] point = new double[center.length];

        for (int i = 0; i < center.length; i++) {
            point[i] = center[i] + t * direction[i];
        }

        return point;
    }

    public static Line getRandomLine(double[] center) {
        // TODO: how to set this seed
        Random rand = new Random();

        double[] direction = new double[center.length];

        for (int i = 0; i < center.length; i++) {
            direction[i] = rand.nextGaussian();
        }

        return new Line(center, direction);
    }
}
