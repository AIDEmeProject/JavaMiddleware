package utils.linprog;

import org.ojalgo.optimisation.Expression;
import org.ojalgo.optimisation.ExpressionsBasedModel;
import org.ojalgo.optimisation.Optimisation;
import org.ojalgo.optimisation.Variable;
import utils.Validator;

/**
 * Wrapper for Linear Programming solver in the OjAlgo library.
 *
 * @see <a href="https://github.com/optimatika/ojAlgo/wiki/The-Diet-Problem">OjAlgo wiki on Linear Programming</a>
 * @author lucianodp
 */
public class OjalgoLinearProgramSolver implements LinearProgramSolver {
    private int numConstrains;
    private final int dim;
    private final ExpressionsBasedModel tmpModel;

    public OjalgoLinearProgramSolver(int dim){
        Validator.assertPositive(dim);
        this.dim = dim;
        this.numConstrains = 0;
        this.tmpModel = new ExpressionsBasedModel();
        for (int i=0; i < dim; i++){
            Variable var = Variable.make(String.format("X%d", i+1));
            this.tmpModel.addVariable(var);
        }
    }

    @Override
    public void setObjectiveFunction(double[] vector) {
        Validator.assertEquals(vector.length, dim);
        for(int i=0; i < dim; i++){
            tmpModel.getVariable(i).weight(vector[i]);
        }
    }

    @Override
    public void setLower(double[] vector) {
        Validator.assertEquals(vector.length, dim);
        for(int i=0; i < dim; i++){
            tmpModel.getVariable(i).lower(vector[i]);
        }
    }

    @Override
    public void setUpper(double[] vector) {
        Validator.assertEquals(vector.length, dim);
        for(int i=0; i < dim; i++){
            tmpModel.getVariable(i).upper(vector[i]);
        }
    }

    @Override
    public void addLinearConstrain(double[] vector, InequalitySign inequalitySign, double val) {
        Validator.assertEquals(vector.length, dim);

        numConstrains++;

        Expression exp = tmpModel.addExpression(String.format("constrain_%d", numConstrains));

        if (inequalitySign == InequalitySign.LEQ) {
            exp.upper(val);
        }
        else {
            exp.lower(val);
        }

        for(int i=0; i < dim; i++) {
            exp.set(tmpModel.getVariable(i), vector[i]);
        }
    }

    @Override
    public double[] findMinimizer() {
        Optimisation.Result minimizer = tmpModel.minimise();
        double[] res = new double[dim];
        for(int i=0; i < dim; i++)
            res[i] = minimizer.get(i).doubleValue();
        return res;
    }

}
