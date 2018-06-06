package metrics;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ConfusionMatrixTest {
    private ConfusionMatrix confusionMatrix = new ConfusionMatrix();

    private void setUpACoupleLabelsWrongScenario() {
        int[] predictedLabels = new int[] {1, 1, 0, 0, 0, 0,  1,  1,  1,  1};
        int[] trueLabels      = new int[] {1, 1, 0,  1,  1,  1, 0, 0, 0, 0};
        confusionMatrix.fit(trueLabels, predictedLabels);
    }

    private void setUpAllLabelsWrongScenario(){
        int[] predictedLabels = new int[] {1, 1, 0, 0};
        int[] trueLabels      = new int[] {0, 0, 1, 1};
        confusionMatrix.fit(trueLabels, predictedLabels);
    }

    private void setUpAllLabelsCorrectScenario(){
        int[] predictedLabels = new int[] {1, 1, 0, 0};
        int[] trueLabels      = new int[] {1, 1, 0, 0};
        confusionMatrix.fit(trueLabels, predictedLabels);
    }

    @Test
    void compute_LabelDifferentFrom0or1_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> confusionMatrix.fit(new int[] {1,0}, new int[] {-1,1}));
    }

    @Test
    void compute_ZeroLengthLabels_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> confusionMatrix.fit(new int[0], new int[0]));
    }

    @Test
    void compute_IncompatibleSizesLabels_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> confusionMatrix.fit(new int[2], new int[4]));
    }

    //-------------------------------
    // Confusion Matrix Metrics
    //-------------------------------
    @Test
    void truePositives_ACoupleLabelsWrongScenario_CorrectValueComputed() {
        setUpACoupleLabelsWrongScenario();
        assertEquals(2, confusionMatrix.truePositives());
    }

    @Test
    void trueNegatives_ACoupleLabelsWrongScenario_CorrectValueComputed() {
        setUpACoupleLabelsWrongScenario();
        assertEquals(1, confusionMatrix.trueNegatives());
    }

    @Test
    void falsePositives_ACoupleLabelsWrongScenario_CorrectValueComputed() {
        setUpACoupleLabelsWrongScenario();
        assertEquals(4, confusionMatrix.falsePositives());
    }

    @Test
    void falseNegatives_ACoupleLabelsWrongScenario_CorrectValueComputed() {
        setUpACoupleLabelsWrongScenario();
        assertEquals(3, confusionMatrix.falseNegatives());
    }

    //-------------------------------
    // Accuracy
    //-------------------------------
    @Test
    void accuracy_ACoupleLabelsWrongScenario_CorrectValueComputed() {
        setUpACoupleLabelsWrongScenario();
        assertEquals(0.3, confusionMatrix.accuracy());
    }

    @Test
    void accuracy_AllLabelsWrongScenario_ReturnZero() {
        setUpAllLabelsWrongScenario();
        assertEquals(0, confusionMatrix.accuracy());
    }

    @Test
    void accuracy_AllLabelsCorrectScenario_ReturnOne() {
        setUpAllLabelsCorrectScenario();
        assertEquals(1.0, confusionMatrix.accuracy());
    }

    //-------------------------------
    // Precision
    //-------------------------------
    @Test
    void precision_ACoupleLabelsWrongScenario_CorrectValueComputed() {
        setUpACoupleLabelsWrongScenario();
        assertEquals(1.0/3, confusionMatrix.precision());
    }

    @Test
    void precision_AllLabelsWrongScenario_ReturnZero() {
        setUpAllLabelsWrongScenario();
        assertEquals(0, confusionMatrix.precision());
    }

    @Test
    void precision_AllLabelsCorrectScenario_ReturnOne() {
        setUpAllLabelsCorrectScenario();
        assertEquals(1.0, confusionMatrix.precision());
    }

    //-------------------------------
    // Recall
    //-------------------------------
    @Test
    void recall_ACoupleLabelsWrongScenario_CorrectValueComputed() {
        setUpACoupleLabelsWrongScenario();
        assertEquals(0.4, confusionMatrix.recall());
    }

    @Test
    void recall_AllLabelsWrongScenario_ReturnZero() {
        setUpAllLabelsWrongScenario();
        assertEquals(0, confusionMatrix.recall());
    }

    @Test
    void recall_AllLabelsCorrectScenario_ReturnOne() {
        setUpAllLabelsCorrectScenario();
        assertEquals(1.0, confusionMatrix.recall());
    }

    //-------------------------------
    // F-Score
    //-------------------------------
    @Test
    void fscore_ACoupleLabelsWrongScenario_CorrectValueComputed() {
        setUpACoupleLabelsWrongScenario();
        assertEquals(2/5.5, confusionMatrix.fscore());
    }

    @Test
    void fscore_AllLabelsWrongScenario_ReturnZero() {
        setUpAllLabelsWrongScenario();
        assertEquals(0, confusionMatrix.fscore());
    }

    @Test
    void fscore_AllLabelsCorrectScenario_ReturnOne() {
        setUpAllLabelsCorrectScenario();
        assertEquals(1.0, confusionMatrix.fscore());
    }
}