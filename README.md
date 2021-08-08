# Autocomplete
Autocomplete is a search feature that predicts the user's query and provides suggestions as the user types. It applies N-Gram model using MapReduce to predict the most probable words that might follow a sequence of word text.
![Picture1](https://user-images.githubusercontent.com/49500810/128646685-6832b5d3-4cb4-47e2-9729-9914cac0bc8c.png)

### Model
  1. Build N-Gram library from datasets
. 
  2. Build launguage model according to the statistical probabilities of words in N-Gram library. Select top K words with the highest probabilities and put them into MySQL database.

### Tools
 - Docker
 - MapReduce
 - MySQL
