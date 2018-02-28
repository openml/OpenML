Word2Vec (1 layer MLP + hierarchical softmax)
Pretrained word embeddings (last layer before softmax, a.k.a. bag of neural words): 
https://code.google.com/archive/p/word2vec/
Bin format: word -> latent dimensions

Task: Continuous bag of words (skipgram): words around -> predict word in the middle (or vice versa)

Paragraph2Vec:
https://radimrehurek.com/gensim/models/doc2vec.html

Glove:
http://nlp.stanford.edu/projects/glove/
Data: https://github.com/mfaruqui/eval-word-vectors/tree/master/data/word-sim
words -> word2vec -> predict similarity
Evaluate with Pearson Rank Correlations

Standard classif. tasks:
amazon, imdb, 20newsgroups,...

Tasks:
- Compare representation: glove, cosine similarity with word2vec 
- Classification: 
-> 20 newsgroups + word2vec -> build 
bag-of-words vs mean word2vec
