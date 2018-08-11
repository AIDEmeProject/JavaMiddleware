# Data Exploration Middleware
The data exploration domain is concerned with extracting relevant information for users 
from a big database, in the most efficient fashion. In this software, we implement an 
explore-by-example approach, where users are repeatedly queries for feedback in order
to build a model of his preferences. 

## Active Learning
In order to leverage this explore-by-example approach, we employ techniques from the Active
Learning domain. Some of the algorithms currently supported are:

    - Random Sampling
    - Uncertainty Sampling
    - Simple Margin
    - Version Space bisection
    

## Active Search
Another domain closely related to our work is Active Search. In their approach, instead
of building an accurate model of the user preference, one is looking for retrieving as
many interesting elements as possible from the database under a budget constrain on the
number of interactions with the user.

So far, we have implemented one single algorithm of Active Search, which we call Active Tree Search.
It is described in the references [1] and [2] below.

## References
[1]   Garnett, R., Krishnamurthy, Y., Wang, D., Schneider, J., and Mann, R.
      Bayesian optimal active search on graphs
      Proceedings of the 9th Workshop on Mining and Learning with Graphs, 2011

[2]   Garnett, R., Krishnamurthy, Y., Xiong, X., Schneider, J.
      Bayesian Optimal Active Search and Surveying
      ICML, 2012
        
[3]   Tong, S., Koller, D.
      Support Vector Machine Active Learning with Applications to Text Classification
      Journal of Machine Learning Research (2001) 
