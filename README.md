# Autocomplete
Autocomplete is a search feature that predicts the user's query and provides suggestions as the user types. It applies N-Gram model using MapReduce to predict the most probable words that might follow a sequence of word text.
![Picture1](https://user-images.githubusercontent.com/49500810/128646685-6832b5d3-4cb4-47e2-9729-9914cac0bc8c.png)

### Dataset
  - From Wiki.
### Model
  1. Build N-Gram library from datasets. (The first MapReduce job)
     - Calculate total count for N-Gram where N could be 2, 3, ...,N.
  2. Predict 1-Gram based on N-Gram. (The second MapReduce job)
     - Build launguage model according to the statistical probability of a word appearing after a phrase. 
     - Select top K words with the highest probabilities and put them into MySQL database.
  3. Predict N-Gram based on N-Gram.  
     - Select top K words in database with the highest probabilities of words appearing after a phrase. 

### Tools
 - Docker: Package applications into a standardized unit and separate your applications from your infrastructure so you can deliver software quickly.
 - MapReduce: MapReduce is a programming model or pattern within the Hadoop framework that is used to processing and generating big data sets with a parallel, distributed algorithm on a cluster. MapReduce program work in two phases, namely, Map and Reduce. Map tasks deal with splitting and mapping of data while Reduce tasks shuffle and reduce the data.
 - MySQL: An open-source relational database management system.
