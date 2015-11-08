import math
import nltk
import time
from collections import defaultdict

# Constants to be used by you when you fill the functions
START_SYMBOL = '*'
STOP_SYMBOL = 'STOP'
MINUS_INFINITY_SENTENCE_LOG_PROB = -1000

# TODO: IMPLEMENT THIS FUNCTION
# Calculates unigram, bigram, and trigram probabilities given a training corpus
# training_corpus: is a list of the sentences. Each sentence is a string with tokens separated by spaces, ending in a newline character.
# This function outputs three python dictionaries, where the keys are tuples expressing the ngram and the value is the log probability of that ngram
def calc_probabilities(training_corpus):
    tokens = []
    bigrams = []
    trigrams = []
    for sentence in training_corpus:
        sentence = "%s %s %s %s %s"%(START_SYMBOL,START_SYMBOL,sentence,STOP_SYMBOL,STOP_SYMBOL)
        s_tokens = nltk.word_tokenize(sentence)
        s_bigram_tupples = list(nltk.bigrams(s_tokens))
        s_trigram_tupples = list(nltk.trigrams(s_tokens))
        tokens.extend(s_tokens)
        bigrams.extend(s_bigram_tupples)
        trigrams.extend(s_trigram_tupples)
    uni_count = defaultdict(int)
    for item in tokens: uni_count[(item,)]+=1
    uni_total = len(tokens)
    bi_count = defaultdict(int)
    for item in bigrams: bi_count[item]+=1
    bi_total = len(bigrams)
    tri_count = defaultdict(int)
    for item in trigrams: tri_count[item]+=1
    tri_total = len(trigrams)

    unigram_p = {}
    for item,count in uni_count.items():
        p = float(count) / float(uni_total)
        unigram_p[item] = math.log(p,2)

    bigram_p = {}
    for item,count in bi_count.items():
        k1 = (item[0],)
        c1 = uni_count[k1]
        p = float(count) / float(c1)
        bigram_p[item] = math.log(p,2)

    trigram_p = {}
    for item,count in tri_count.items():
        k2 = tuple(item[0:2])
        c2 = bi_count[k2]
        p = float(count) / float(c2)
        trigram_p[item] = math.log(p,2)

    return unigram_p, bigram_p, trigram_p

# Prints the output for q1
# Each input is a python dictionary where keys are a tuple expressing the ngram, and the value is the log probability of that ngram
def q1_output(unigrams, bigrams, trigrams, filename):
    # output probabilities
    outfile = open(filename, 'w')

    unigrams_keys = unigrams.keys()
    unigrams_keys.sort()
    for unigram in unigrams_keys:
        outfile.write('UNIGRAM ' + unigram[0] + ' ' + str(unigrams[unigram]) + '\n')

    bigrams_keys = bigrams.keys()
    bigrams_keys.sort()
    for bigram in bigrams_keys:
        outfile.write('BIGRAM ' + bigram[0] + ' ' + bigram[1]  + ' ' + str(bigrams[bigram]) + '\n')

    trigrams_keys = trigrams.keys()
    trigrams_keys.sort()    
    for trigram in trigrams_keys:
        outfile.write('TRIGRAM ' + trigram[0] + ' ' + trigram[1] + ' ' + trigram[2] + ' ' + str(trigrams[trigram]) + '\n')

    outfile.close()


# TODO: IMPLEMENT THIS FUNCTION
# Calculates scores (log probabilities) for every sentence
# ngram_p: python dictionary of probabilities of uni-, bi- and trigrams.
# n: size of the ngram you want to use to compute probabilities
# corpus: list of sentences to score. Each sentence is a string with tokens separated by spaces, ending in a newline character.
# This function must return a python list of scores, where the first element is the score of the first sentence, etc. 
def score(ngram_p, n, corpus):
    scores = []
    for sentence in corpus:
        sentence = "%s %s %s %s %s"%(START_SYMBOL,START_SYMBOL,sentence,STOP_SYMBOL,STOP_SYMBOL)
        s_tokens = nltk.word_tokenize(sentence)
        score = 0
        #score = sum( [ ngram_p[ ] for i in range(2,len(s_tokens)-2) ] )
        for i in range(2,len(s_tokens)-2):
            kval = tuple(s_tokens[i-n+1:i+1])
            #score = math.pow(2,ngram_p[kval]) if ngram_p.has_key(kval) else MINUS_INFINITY_SENTENCE_LOG_PROB
            #score += ngram_p[kval] if ngram_p.has_key(kval) else MINUS_INFINITY_SENTENCE_LOG_PROB
            if ngram_p.has_key(kval):
                score += ngram_p[kval]
            else:
                #set entire sentence to this value
                score = MINUS_INFINITY_SENTENCE_LOG_PROB
                break
        scores.append(score)
    return scores

# Outputs a score to a file
# scores: list of scores
# filename: is the output file name
def score_output(scores, filename):
    outfile = open(filename, 'w')
    for score in scores:
        outfile.write(str(score) + '\n')
    outfile.close()

# TODO: IMPLEMENT THIS FUNCTION
# Calculates scores (log probabilities) for every sentence with a linearly interpolated model
# Each ngram argument is a python dictionary where the keys are tuples that express an ngram and the value is the log probability of that ngram
# Like score(), this function returns a python list of scores
def linearscore(unigrams, bigrams, trigrams, corpus):
    scores = []
    for sentence in corpus:
        sentence = "%s %s %s %s %s"%(START_SYMBOL,START_SYMBOL,sentence,STOP_SYMBOL,STOP_SYMBOL)
        s_tokens = nltk.word_tokenize(sentence)
        score = 0
        p1 = 0
        p2 = 0
        p3 = 0
        keyNotFound = False
        for i in range(2,len(s_tokens)-2):
            unikey = (s_tokens[i],)
            if unigrams.has_key(unikey):
                p1=unigrams[unikey]
            else:
                keyNotFound = True
                break

            bikey = tuple(s_tokens[i-1:i+1])
            if bigrams.has_key(bikey):
                p2 = bigrams[bikey]
            else:
                keyNotFound = True
                break

            trikey = tuple(s_tokens[i-2:i+1])
            if trigrams.has_key(trikey):
                p3 = trigrams[trikey]
            else:
                keyNotFound = True
                break

            s1 = (1.0/3.0)*(math.pow(2,p1)+math.pow(2,p2)+math.pow(2,p3))
            score += math.log(s1,2)

        if not keyNotFound :
            scores.append(score)
        else :
            scores.append(MINUS_INFINITY_SENTENCE_LOG_PROB)

    return scores

DATA_PATH = 'data/'
OUTPUT_PATH = 'output/'

# DO NOT MODIFY THE MAIN FUNCTION
def main():
    # start timer
    time.clock()

    # get data
    infile = open(DATA_PATH + 'Brown_train.txt', 'r')
    corpus = infile.readlines()
    infile.close()

    # calculate ngram probabilities (question 1)
    unigrams, bigrams, trigrams = calc_probabilities(corpus)

    # question 1 output
    q1_output(unigrams, bigrams, trigrams, OUTPUT_PATH + 'A1.txt')

    # score sentences (question 2)
    uniscores = score(unigrams, 1, corpus)
    biscores = score(bigrams, 2, corpus)
    triscores = score(trigrams, 3, corpus)

    # question 2 output
    score_output(uniscores, OUTPUT_PATH + 'A2.uni.txt')
    score_output(biscores, OUTPUT_PATH + 'A2.bi.txt')
    score_output(triscores, OUTPUT_PATH + 'A2.tri.txt')

    # linear interpolation (question 3)
    linearscores = linearscore(unigrams, bigrams, trigrams, corpus)

    # question 3 output
    score_output(linearscores, OUTPUT_PATH + 'A3.txt')

    # open Sample1 and Sample2 (question 5)
    infile = open(DATA_PATH + 'Sample1.txt', 'r')
    sample1 = infile.readlines()
    infile.close()
    infile = open(DATA_PATH + 'Sample2.txt', 'r')
    sample2 = infile.readlines()
    infile.close() 

    # score the samples
    sample1scores = linearscore(unigrams, bigrams, trigrams, sample1)
    sample2scores = linearscore(unigrams, bigrams, trigrams, sample2)

    # question 5 output
    score_output(sample1scores, OUTPUT_PATH + 'Sample1_scored.txt')
    score_output(sample2scores, OUTPUT_PATH + 'Sample2_scored.txt')

    # print total time to run Part A
    print "Part A time: " + str(time.clock()) + ' sec'

if __name__ == "__main__": main()
