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

## References        
[1]   Tong, S., Koller, D.
      Support Vector Machine Active Learning with Applications to Text Classification
      Journal of Machine Learning Research (2001) 




# Application

## Installation

Install nodejs: https://nodejs.org/en/ 

Install java and maven

## quick start

Launch the Jetty web server

```bash
mvn cean compile && mvn -e exec:java -Dexec.mainClass="application.ApplicationServerMain"
```

Launch the web interface

```bash

cd src/frontend/gui/
npm start

```

Go to localhost:3000


