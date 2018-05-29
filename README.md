# active_search
Active Search is the subset of the Information Retrieval domain concerned with retrieving as many elements from a target 
subset of a database as possible. The retrieval dynamics work as follow:

  1) Our algorithm / model proposes a new candidate point x
  2) Upon revelation, it is revealed whether x belongs to the target set or not
  3) Update our current model with the new information
  4) Go back to 1 until a certain label budget is reached
  
In this repository, we have implemented a couple techniques:

  - Random Sampling: a baseline method used only for comparison. Basically, it randomly picks a point at every iteration.
  
  - Uncertainty Sampling: a technique imported from the Active Learning domain. The model retrieves at every iteration the
  point it deems the most uncertainty; i.e. whose probability of being a target is the closest to 0.5. 
  
  - Active Seach Tree: technique proposed in [1] and [2], it develops a dynamic programming strategy for the optimal strategy
  for this problem. In addition, a series of optimizations are proposed in order to make this algorithm useful in practive (
  since the complexity of the naive DP approach is exponential in the database size...).

References:

  [1]   Garnett, R., Krishnamurthy, Y., Wang, D., Schneider, J., and Mann, R.
        Bayesian optimal active search on graphs
        Proceedings of the 9th Workshop on Mining and Learning with Graphs, 2011
  
  [2]   Garnett, R., Krishnamurthy, Y., Xiong, X., Schneider, J.
        Bayesian Optimal Active Search and Surveying
        ICML, 2012
