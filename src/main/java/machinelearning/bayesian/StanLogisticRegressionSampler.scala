package machinelearning.bayesian

import com.cibo.scalastan.{RunMethod, ScalaStan}

/**
  * Logistic regression posterior sampler. Uses scalastan library, which is basically a wrapper over the Bayesian sampling
  * library STAN in R.
  *
  * Let (Xi, yi) be a collection of labeled data. Under the Logistic Regression model, we assume that the response variable
  * Y can be predicted from the data X through a linear model:
  *
  *     P(Y = 1 | X = x, beta) = sigmoid(beta * X)  (we do not take into account the bias)
  *
  * From this model, the parameter beta posterior distribution can be written as:
  *
  *    P(beta | Xi, yi) ~ P(Y = yn | X = Xn, beta) * ... * P(Y = y0 | X = X0, beta) * p0(beta)
  *
  * Where p0(beta) is a prior distribution. In our model, we assume a centered gaussian distribution for the prior p0.
  *
  * @param warmup: number of initial samples to skip in the beginning
  * @param thin: retain every "thin" samples after warm-up
  * @param sigma: standard deviation of gaussian prior
  */
class StanLogisticRegressionSampler(warmup: Int, thin: Int, sigma: Double) {

  /**
    * @param numSamples: number of samples to retrieve
    * @param xs: array of data points
    * @param ys: labels array (0 and 1)
    * @return a collection of samples obtained from the posterior distribution fitted over (xs, ys)
    */
  def run(numSamples: Int, xs: Array[Array[Double]], ys: Array[Int]): Array[Array[Double]] = {
    val iters = warmup + thin * numSamples
    val results = StanLogisticRegressionSampler.model
        .withData(StanLogisticRegressionSampler.x, xs.toSeq.map(t => t.toSeq))
        .withData(StanLogisticRegressionSampler.y, ys.toSeq)
        .withData(StanLogisticRegressionSampler.sigma, sigma)
        .run(chains = 1, method = RunMethod.Sample(samples = iters))

    val sample = results.samples(StanLogisticRegressionSampler.beta)
      .head  // there is only a single chain
      .drop(warmup)  // skip initial "warmup" samples
      .zipWithIndex.collect {case (e,i) if ((i+1) % thin) == 0 => e}  // get every "thin" sample

    sample.map(t => t.toArray).toArray
  }
}

/**
  * Companion class. Contains the STAN model definition and its variables.
  */
object StanLogisticRegressionSampler extends ScalaStan {
  // dimensions
  private val D = data(int(lower = 1))
  private val N = data(int(lower = 1))

  // logistic regression parameter
  private val beta = parameter(vector(D))

  // gaussian prior standard deviation
  private val sigma = data(real(lower = 0D))

  // data
  private val x = data(matrix(N, D))
  private val y = data(int(lower=0, upper=1)(N))

  private val model = new Model {
    beta ~ stan.normal(0, sigma)
    y ~ stan.bernoulli_logit(x * beta)
  }
}