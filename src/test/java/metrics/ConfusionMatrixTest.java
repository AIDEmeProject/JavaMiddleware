package metrics;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ConfusionMatrixTest {
    private ConfusionMatrix confusionMatrix;

    private void setUpACoupleLabelsWrongScenario() {
        confusionMatrix = new ConfusionMatrix(2, 1, 4, 3);
    }

    private void setUpAllLabelsWrongScenario(){
        confusionMatrix = new ConfusionMatrix(0, 0, 2, 2);
    }

    private void setUpAllLabelsCorrectScenario(){
        confusionMatrix = new ConfusionMatrix(2, 2, 0, 0);
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