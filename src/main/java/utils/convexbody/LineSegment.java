package utils.convexbody;

import java.util.Random;

public class LineSegment {
    private Line line;
    private double leftBound;
    private double rightBound;

    public LineSegment(Line line, double leftBound, double rightBound) {
        if (leftBound >= rightBound){
            throw new IllegalArgumentException("Left bound must be smaller than right bound.");
        }

        this.line = line;
        this.leftBound = leftBound;
        this.rightBound = rightBound;
    }

    public double[] sampleRandomPoint(){
        // TODO: how to set this seed
        Random rand = new Random();
        return line.getPoint(leftBound + rand.nextDouble() * (rightBound - leftBound));
    }
}
