/*
 * Copyright (c) 2019 École Polytechnique
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, you can obtain one at http://mozilla.org/MPL/2.0
 *
 * Authors:
 *       Luciano Di Palma <luciano.di-palma@polytechnique.edu>
 *       Enhui Huang <enhui.huang@polytechnique.edu>
 *       Laurent Cetinsoy <laurent.cetinsoy@gmail.com>
 *
 * Description:
 * AIDEme is a large-scale interactive data exploration system that is cast in a principled active learning (AL) framework: in this context,
 * we consider the data content as a large set of records in a data source, and the user is interested in some of them but not all.
 * In the data exploration process, the system allows the user to label a record as “interesting” or “not interesting” in each iteration,
 * so that it can construct an increasingly-more-accurate model of the user interest. Active learning techniques are employed to select
 * a new record from the unlabeled data source in each iteration for the user to label next in order to improve the model accuracy.
 * Upon convergence, the model is run through the entire data source to retrieve all relevant records.
 */

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
  def run(numSamples: Int, xs: Array[Array[Double]], ys: Array[Int], seed: Int): Array[Array[Double]] = {
    val iters = thin * numSamples
    val results = StanLogisticRegressionSampler.model
        .withData(StanLogisticRegressionSampler.N, xs.length)
        .withData(StanLogisticRegressionSampler.D, xs(0).length)
        .withData(StanLogisticRegressionSampler.x, xs.toSeq.map(t => t.toSeq))
        .withData(StanLogisticRegressionSampler.y, ys.toSeq)
        .withData(StanLogisticRegressionSampler.sigma, sigma)
        .run(chains = 1, method = RunMethod.Sample(samples = iters, warmup=warmup, thin=thin), seed = seed)

    results
      .samples(StanLogisticRegressionSampler.beta)  // get beta samples
      .head  // there is only one chain
      .map(t => t.toArray)  // convert each sample to array
      .toArray  // return an array
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