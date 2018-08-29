package machinelearning.classifier.TSM;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class OneDimTSMTest {
    private OneDimTSM oneDimTSM;
    private ArrayList<double[]> samplesWithLabel;

    public static void RandomizeArray(ArrayList<double[]> array){
        HashMap<Double, Integer> result = new HashMap<>();
        Random rgen = new Random(0);  // Random number generator

        for (int i=0; i<array.size(); i++) {
            int randomPosition = rgen.nextInt(array.size());
            double[] temp = array.get(i);
            array.set(i, array.get(randomPosition));
            array.set(randomPosition, temp);

        }
    }

    public static void PrintArrayList(ArrayList<double[]> array){
        for(double[] item : array){
            System.out.println(Arrays.toString(item));
        }
    }

    @BeforeEach
    void setUp() {
        // -Infinity < a < b < +Infinity
        // Successful test for [-Infinity, a] is pos, [a, +Infinity] is neg
        // Successful test for [a, b] is pos, [-Infinity, a] and [b, +Infinity] are neg
        // Successful test for [-Infinity, a] is neg, [a, +Infinity] is pos
        samplesWithLabel = new ArrayList<>();
        for(int i=-10; i<11; i++){
            if(i<-3.2){
                samplesWithLabel.add(i+10, new double[]{i, 1});
            }else {
                samplesWithLabel.add(i+10, new double[]{i, -1} );
            }
        }

        System.out.println("--------------------------------------");
        RandomizeArray(samplesWithLabel);
        samplesWithLabel.add(0, new double[]{-3.5, 1});
        samplesWithLabel.add(1, new double[]{1.5, -1});
//        PrintArrayList(samplesWithLabel);
        System.out.println("--------------------------------------");

        oneDimTSM = new OneDimTSM();

        //test the final result of all updates
        for(int i=0; i<samplesWithLabel.size(); i++){
            oneDimTSM.updatePos(samplesWithLabel.get(i)[0], samplesWithLabel.get(i)[1]);
            System.out.println("number of samples: " + (i+1) + " new sample: " + Arrays.toString(samplesWithLabel.get(i)));
            System.out.println("Convex line segment: " + Arrays.toString(oneDimTSM.getConvexLineSeg().toArray()));
            System.out.println("Concave rays: " + Arrays.toString(oneDimTSM.getConcaveRay().toArray()));
            System.out.println("--------------------------------------");
        }

    }


    @Test
    void isInConvexSeg() {
        assertEquals(true, oneDimTSM.isInConvexSeg(-5));
    }

    @Test
    void isConvexSegExtremePoint(){
        assertEquals(true, oneDimTSM.isInConvexSeg(-10));
    }

    @Test
    void notInConvexSeg(){
        assertEquals(false, oneDimTSM.isInConvexSeg(-20));
    }

    @Test
    void notInConvexSeg1(){
        assertEquals(false, oneDimTSM.isInConvexSeg(20));
    }

    @Test
    void isInConcaveRay() {
        assertEquals(true, oneDimTSM.isInConcaveRay(20));
    }

    @Test
    void isConcaveRayExtremePoint(){
        assertEquals(true, oneDimTSM.isInConcaveRay(-3));
    }

    @Test
    void noInConcaveRay(){
        assertEquals(false, oneDimTSM.isInConcaveRay(-5));
    }


    @Test
    void getConcaveRay() {
        ArrayList<Double> expected = new ArrayList<>();
        expected.add(Double.NEGATIVE_INFINITY);
        expected.add(-3.0);
        assertArrayEquals(expected.toArray(), oneDimTSM.getConcaveRay().toArray());

    }

    @Test
    void getConvexLineSeg() {
        ArrayList<Double> expected = new ArrayList<>();
        expected.add(-10.0);
        expected.add(-3.5);
        assertArrayEquals(expected.toArray(), oneDimTSM.getConvexLineSeg().toArray());
    }

    @Test
    void findTopK() {
        HashSet<Double> set = new HashSet<>();
        for(int i = 0; i < 11; i++){
            set.add((double) i);
        }
        assertArrayEquals(new double[]{5, 6}, OneDimTSM.findTopK(set, 2, 5.3));
    }

    @Test
    void findTopK_externalRef() {
        HashSet<Double> set = new HashSet<>();
        for(int i = 0; i < 11; i++){
            set.add((double) i);
        }
        assertArrayEquals(new double[]{8, 9, 10}, OneDimTSM.findTopK(set, 3, 11));
    }


}