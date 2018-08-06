package utils.convexbody;

public interface ConvexBody {
    boolean isInside(double[] x);

    double[] getInteriorPoint();

    LineSegment computeLineIntersection(Line line);
}
