package machinelearning.threesetmetric.TSM;

import utils.ObjectWithDistance;

import java.util.*;

/**
 * This class stores the three-set partition on 1-dim space: if the pos region is convex, create an interval(or a single point) for pos region and two rays for neg regions
 *                                                           if the neg region is convex, create an interval(or a single point) for neg region and two rays for pos regions
 *
 * @author enhui
 */

public class OneDimTSM {
    /**
     * An interval or a single point
     */
    private ArrayList<Double> convexLineSeg;

    /**
     * Two Rays
     */
    private ArrayList<Double> concaveRay;

    /**
     * True if the interval(the single point)  has existed, false otherwise
     */
    private boolean convexInitialized = false;

    /**
     * True if a ray has existed, false otherwise
     */
    private boolean concaveInitialized = false;

    /**
     * Reservoir of concave points before the initialization of convex region
     */
    private HashSet<Double> concavePoints;

    /**
     * True if the pos region is convex, false otherwise
     */
    private boolean flag;

    public OneDimTSM(){

        convexLineSeg = new ArrayList<>();
        concaveRay = new ArrayList<>();

        concavePoints = new HashSet<>();
    }

    /**
     * Update three-set partition on 1-dim space when the pos region is convex
     * @param point value of selected feature
     * @param label label of the point on the 1-dim space
     * @throws IllegalArgumentException if a positive point is in negative rays or a negative point is in positive interval
     */
    public void updatePos(double point, double label) {
        if (label > 0) {
            updateConvexLineSeg(point);
        } else {
            updateConcaveRay(point);
        }
    }

    /**
     * Update three-set partition on 1-dim space when the neg region is convex
     * @param point value of selected feature
     * @param label label of the point on the 1-dim space
     * @throws IllegalArgumentException if a positive point is in negative rays or a negative point is in positive interval
     */
    public void updateNeg(double point, double label) {
        if (label < 0) {
            updateConvexLineSeg(point);
        } else {
            updateConcaveRay(point);
        }
    }


    /**
     * Update the convex region(interval or single point) on 1-dim space
     * @param point value of selected feature
     * @throws IllegalArgumentException if a positive point is in negative rays or a negative point is in positive interval
     */
    public void updateConvexLineSeg(double point){
        // check whether the sample is in concave regions
        if (concaveRay.size() == 2 && isInConcaveRay(point)) {
            throw new IllegalArgumentException("A positive point is in negative rays");
        }
        if (!convexInitialized) {
            convexLineSeg.add(0, point);
            convexInitialized = true;
        } else if (convexLineSeg.size() == 1) {
            if (point > convexLineSeg.get(0)) {
                convexLineSeg.add(1, point);
            } else if (point < convexLineSeg.get(0)) {
                convexLineSeg.add(1, convexLineSeg.get(0));
                convexLineSeg.set(0, point);
            }
        } else if (convexLineSeg.size() == 2) {
            if (point > convexLineSeg.get(1)) {
                convexLineSeg.set(1, point);
            } else if (point < convexLineSeg.get(0)) {
                convexLineSeg.set(0, point);
            }
        } else {
            throw new IllegalArgumentException("The extreme points of convex region should be 2 instead of " + convexLineSeg.size() + ": " + Arrays.toString(convexLineSeg.toArray()));
        }
    }

    /**
     * Update the concave rays on 1-dim space
     * @param point value of selected feature
     * @throws IllegalArgumentException if a positive point is in negative rays or a negative point is in positive interval
     */
    public void updateConcaveRay(double point){
        if ((convexLineSeg.size() == 2 && isInConvexSeg(point)) || (convexLineSeg.size() == 1 && point == convexLineSeg.get(0))) {
            throw new IllegalArgumentException("A negative point is in positive convex interval");
            //if the interval has not been defined, instead of defining the rays, only negative points will be recorded
        } else if (!convexInitialized) {
            concavePoints.add(point);
        } else {
            if (!concaveInitialized) {
                // a convex extreme point must exist before adding concave extreme points
                if (concavePoints.size() > 0) {
                    concavePoints.add(point);
                    // find two points in concave region which are closest to the convex region
                    double[] subConcaveRays = findTopK(concavePoints, 2, convexLineSeg.get(0));
                    if (subConcaveRays[0] > convexLineSeg.get(0)) {
                        concaveRay.add(0, Double.NEGATIVE_INFINITY);
                        concaveRay.add(1, subConcaveRays[0]);
                    } else if (subConcaveRays[1] < convexLineSeg.get(0)) {
                        concaveRay.add(0, subConcaveRays[1]);
                        concaveRay.add(1, Double.POSITIVE_INFINITY);
                    } else {
                        concaveRay.add(0, subConcaveRays[0]);
                        concaveRay.add(1, subConcaveRays[1]);
                    }
                } else if (point > convexLineSeg.get(0)) {
                    concaveRay.add(0, Double.NEGATIVE_INFINITY);
                    concaveRay.add(1, point);
                } else if (point < convexLineSeg.get(0)) {
                    concaveRay.add(0, point);
                    concaveRay.add(1, Double.POSITIVE_INFINITY);
                } else {
                    throw new IllegalArgumentException("A point " + point + " in the convex line segment also lies in concave rays.");
                }
                concaveInitialized = true;
            } else {
                if (convexLineSeg.size() == 1) {
                    if (point < concaveRay.get(1) && point > convexLineSeg.get(0)) {
                        concaveRay.set(1, point);
                    } else if (point > concaveRay.get(0) && point < convexLineSeg.get(0)) {
                        concaveRay.set(0, point);
                    }
                } else if (convexLineSeg.size() == 2) {
                    if (point < concaveRay.get(1) && point > convexLineSeg.get(1)) {
                        concaveRay.set(1, point);
                    } else if (point > concaveRay.get(0) && point < convexLineSeg.get(0)) {
                        concaveRay.set(0, point);
                    }
                } else {
                    throw new IllegalArgumentException("The extreme points should be 2 instead of " + convexLineSeg.size() + ": " + Arrays.toString(convexLineSeg.toArray()));
                }
            }
        }
    }


    /**
     * Check whether a point is in the interval
     * @param sample point to be checked
     * @return true if the point is in the interval, false otherwise
     */
    public boolean isInConvexSeg(double sample){
        return (convexLineSeg.size() == 2 && sample >= convexLineSeg.get(0) && sample <= convexLineSeg.get(1)) || (convexLineSeg.size() > 0 && sample == convexLineSeg.get(0));
    }

    /**
     * Check whether a point is in any of the rays
     * @param sample point to be checked
     * @return true if the point is in the rays, false otherwise
     */
    public boolean isInConcaveRay(double sample){
        return concaveRay.size() == 2 && (sample <= concaveRay.get(0) || sample >= concaveRay.get(1));
    }

    /**
     * @return extreme points of rays
     */
    public ArrayList<Double> getConcaveRay() {
        return concaveRay;
    }

    /**
     * @return extreme points of the interval
     */
    public ArrayList<Double> getConvexLineSeg() {
        return convexLineSeg;
    }


    /**
     * Find top-k points with largest(or smallest) distance to the reference
     * @param points a set of points
     * @param k number of points returned
     * @param convexPoint the reference for the computation of distance
     * @return top-k points with largest(or smallest) distance to the reference
     */
    public static double[] findTopK(HashSet<Double> points, int k, double convexPoint){
        Double[] pointsArray = points.toArray(new Double[points.size()]);
        //System.out.println("transformed Array is: " + Arrays.toString(pointsArray));
        Comparator<ObjectWithDistance<Double>> comparator = (o1, o2) -> Double.compare(o2.getDistance(), o1.getDistance());

        PriorityQueue<ObjectWithDistance<Double>> topK = new PriorityQueue<>(1, comparator);
        for(int i=0;i<points.size();i++){
            topK.add( new ObjectWithDistance<Double>(Math.abs(pointsArray[i]-convexPoint), pointsArray[i]));
            if(topK.size() > k){
                topK.remove();
            }
        }

        double[] res = new double[k];
        int i = 0;
        for (ObjectWithDistance<Double> aTopk : topK) {
            res[i] = aTopk.getObject();
            i++;
        }
        Arrays.sort(res);

        return res;
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("It's 1-dim tsm, the convex line segment is: ");
        sb.append(Arrays.deepToString(convexLineSeg.toArray()));
        sb.append(" and the concave ray end points are: ");
        sb.append(Arrays.deepToString(concaveRay.toArray()));
        return sb.toString();
    }
}
