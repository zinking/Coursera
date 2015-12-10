import sys
import nltk
import math
import time
from collections import defaultdict
import re


START_SYMBOL = '*'
STOP_SYMBOL = 'STOP'
RARE_SYMBOL = '_RARE_'
RARE_WORD_MAX_FREQ = 5
LOG_PROB_OF_ZERO = -1000
LOG_PROB_OF_MINI = -1000000
S1="%s/%s"%(START_SYMBOL,START_SYMBOL)
S2="%s/%s"%(STOP_SYMBOL,STOP_SYMBOL)

# TODO: IMPLEMENT THIS FUNCTION
# Receives a list of tagged sentences and processes each sentence to generate a list of words and a list of tags.
# Each sentence is a string of space separated "WORD/TAG" tokens, with a newline character in the end.
# Remember to include start and stop symbols in yout returned lists, as defined by the constants START_SYMBOL and STOP_SYMBOL.
# brown_words (the list of words) should be a list where every element is a list of the tags of a particular sentence.
# brown_tags (the list of tags) should be a list where every element is a list of the tags of a particular sentence.
def split_wordtags(brown_train):
    brown_words = []
    brown_tags = []
    for sentence in brown_train:

        sentence = "%s %s %s %s"%(S1,S1,sentence.rstrip(),S2)

        #s_tokens = nltk.word_tokenize(sentence)
        s_tokens = sentence.split(" ")
        s_words = []
        s_tags = []
        for token in s_tokens:
            #ss = token.split('/')
            p = token.rfind('/')
            ss0 = token[0:p]
            ss1 = token[p+1:]
            s_words.append(ss0)
            s_tags.append(ss1)
        brown_words.append(s_words)
        brown_tags.append(s_tags)
    return brown_words, brown_tags


# TODO: IMPLEMENT THIS FUNCTION
# This function takes tags from the training data and calculates tag trigram probabilities.
# It returns a python dictionary where the keys are tuples that represent the tag trigram, and the values are the log probability of that trigram
def calc_trigrams(brown_tags):
    atags = []
    for ltags in brown_tags:
        atags.extend(ltags)
    q_values = {}
    bigrams = list(nltk.bigrams(atags))
    trigrams = list(nltk.trigrams(atags))
    bi_count = defaultdict(int)
    for item in bigrams: bi_count[item]+=1
    tri_count = defaultdict(int)
    for item in trigrams: tri_count[item]+=1
    for item,count in tri_count.items():
        k2 = tuple(item[0:2])
        c2 = bi_count[k2]
        p = float(count) / float(c2)
        q_values[item] = math.log(p,2)
    return q_values

# This function takes output from calc_trigrams() and outputs it in the proper format
def q2_output(q_values, filename):
    outfile = open(filename, "w")
    trigrams = q_values.keys()
    trigrams.sort()  
    for trigram in trigrams:
        output = " ".join(['TRIGRAM', trigram[0], trigram[1], trigram[2], str(q_values[trigram])])
        outfile.write(output + '\n')
    outfile.close()


# TODO: IMPLEMENT THIS FUNCTION
# Takes the words from the training data and returns a set of all of the words that occur more than 5 times (use RARE_WORD_MAX_FREQ)
# brown_words is a python list where every element is a python list of the words of a particular sentence.
# Note: words that appear exactly 5 times should be considered rare!
def calc_known(brown_words):
    awords = []
    for lwords in brown_words:
        awords.extend(lwords)
    uni_count = defaultdict(int)
    for item in awords: uni_count[item]+=1
    known_words = set([])
    for w,c in uni_count.items():
        if c > 5 :
            known_words.add(w)
    return known_words

# TODO: IMPLEMENT THIS FUNCTION
# Takes the words from the training data and a set of words that should not be replaced for '_RARE_'
# Returns the equivalent to brown_words but replacing the unknown words by '_RARE_' (use RARE_SYMBOL constant)
def replace_rare(brown_words, known_words):
    brown_words_rare = []
    for lwords in brown_words:
        nlwords = []
        for word in lwords:
            if word in known_words:
                nlwords.append(word)
            else:
                nlwords.append(RARE_SYMBOL)
        brown_words_rare.append(nlwords)
    return brown_words_rare

# This function takes the ouput from replace_rare and outputs it to a file
def q3_output(rare, filename):
    outfile = open(filename, 'w')
    for sentence in rare:
        outfile.write(' '.join(sentence[2:-1]) + '\n')
    outfile.close()


# TODO: IMPLEMENT THIS FUNCTION
# Calculates emission probabilities and creates a set of all possible tags
# The first return value is a python dictionary where each key is a tuple in which the first element is a word
# and the second is a tag, and the value is the log probability of the emission of the word given the tag
# The second return value is a set of all possible tags for this data set
def calc_emission(brown_words_rare, brown_tags):
    e_values = {}
    taglist = set([])
    nsentence = len(brown_tags)
    wordtagcount = defaultdict(int)
    tagcount = defaultdict(int)
    for i in range(0,nsentence):
        wordsent = brown_words_rare[i]
        tagsent  = brown_tags[i]
        nwords = len(wordsent)
        for j in range(0,nwords):
            word = wordsent[j]
            tag = tagsent[j]
            wordtagcount[(word,tag)] += 1
            tagcount[tag] += 1
            taglist.add(tag)
    for t1,c in wordtagcount.items():
        w,t = t1
        pt1 = float(c) / float(tagcount[t])
        e_values[t1] = math.log(pt1,2)
    return e_values, taglist

# This function takes the output from calc_emissions() and outputs it
def q4_output(e_values, filename):
    outfile = open(filename, "w")
    emissions = e_values.keys()
    emissions.sort()  
    for item in emissions:
        output = " ".join([item[0], item[1], str(e_values[item])])
        outfile.write(output + '\n')
    outfile.close()

def get_morphed_rareword(word):
    ####https://github.com/zachguo/HMM-Trigram-Tagger/blob/master/HMM.py
    if not re.search(r'\w',word):
        return '<PUNCS>'
    elif re.search(r'[A-Z]',word):
        return '<CAPITAL>'
    elif re.search(r'\d',word):
        return '<NUM>'
    elif re.search(r'(ion\b|ty\b|ics\b|ment\b|ence\b|ance\b|ness\b|ist\b|ism\b)',word):
        return '<NOUNLIKE>'
    elif re.search(r'(ate\b|fy\b|ize\b|\ben|\bem|ed\b)',word):
        return '<VERBLIKE>'
    elif re.search(r'(\bun|\bin|ble\b|ry\b|ish\b|ious\b|ical\b|\bnon)',word):
        return '<JJLIKE>'
    else:
        return '<OTHER>'


# TODO: IMPLEMENT THIS FUNCTION
# This function takes data to tag (brown_dev_words), a set of all possible tags (taglist), a set of all known words (known_words),
# trigram probabilities (q_values) and emission probabilities (e_values) and outputs a list where every element is a tagged sentence 
# (in the WORD/TAG format, separated by spaces and with a newline in the end, just like our input tagged data)
# brown_dev_words is a python list where every element is a python list of the words of a particular sentence.
# taglist is a set of all possible tags
# known_words is a set of all known words
# q_values is from the return of calc_trigrams()
# e_values is from the return of calc_emissions()
# The return value is a list of tagged sentences in the format "WORD/TAG", separated by spaces. Each sentence is a string with a 
# terminal newline, not a list of tokens. Remember also that the output should not contain the "_RARE_" symbol, but rather the
# original words of the sentence!
def viterbi(brown_dev_words, tagset, known_words, q_values, e_values):
    tagged = []
    taglist = list(tagset)
    #T = len(known_words)
    N = len(taglist)
    for words in brown_dev_words:
        owords = words
        ewords = [START_SYMBOL,START_SYMBOL]
        ewords.extend(words)
        ewords.append(STOP_SYMBOL)
        ewords.append(STOP_SYMBOL)
        words = ewords #enhanced words
        T = len(words)
        vb = {}
        bp = {}


        ##### start of the viterbi algorithm
        STSTATE=0
        word = words[2]
        word = word if word in known_words else get_morphed_rareword(word)
        for tagb in taglist:
            for tagc in taglist:
                qkey = (START_SYMBOL,tagb,tagc)
                qval = q_values.get(qkey,LOG_PROB_OF_ZERO)
                ekey = (word,tagc)
                eval = e_values.get(ekey,LOG_PROB_OF_ZERO)
                nvbkey = (2,tagb,tagc)
                vb[nvbkey] = qval+eval #log prob

        for t in range(3,T):
            word = words[t]
            word = word if word in known_words else get_morphed_rareword(word)
            for tagc in taglist: #N^2 lookup for current tag
                ekey = (word,tagc)
                eval = e_values.get(ekey,LOG_PROB_OF_ZERO)
                for tagb in taglist:
                    prev_a = taglist[0]
                    nvbkey = (t,tagb,tagc)
                    for taga in taglist:
                        ovbkey = (t-1,taga,tagb)
                        ovbval = vb.get(ovbkey,LOG_PROB_OF_MINI)
                        qkey = (taga,tagb,tagc)
                        qval = q_values.get(qkey,LOG_PROB_OF_ZERO)
                        nobst = ovbval + qval + eval
                        if nobst > vb.get(nvbkey,LOG_PROB_OF_MINI):
                            #print "tritag[%s] so1[%s] winning with %g over %g"%(qkey,ekey,nobst,vb.get(nvbkey,LOG_PROB_OF_MINI))
                            vb[nvbkey] = nobst
                            prev_a = taga
                    taga = prev_a
                    obpkey = (t-1,taga,tagb)
                    nbpkey = (t,tagb,tagc)
                    if bp.has_key(obpkey):
                        bp[nbpkey] = bp[obpkey] + [tagc]
                    else:
                        bp[nbpkey] = [tagb,tagc]

        maxst = LOG_PROB_OF_MINI
        lastb = None
        lastc = None
        last = T-1
        for tagb in taglist:
            for tagc in taglist:
                nvbkey = (last,tagb,tagc)
                if vb.has_key(nvbkey) and vb.get(nvbkey) > maxst:
                    maxst = vb[nvbkey]
                    lastc = tagb
                    lastb = tagc

        path = bp[(last,lastb,lastc)]
        #print path
        taggedlist = []
        n = len(owords)
        for i in range(n):
            tgwd = "%s/%s"%(owords[i],path[i])
            taggedlist.append(tgwd)

        sent = " ".join(taggedlist) + "\r\n"
        #print "."
        #print "TAGGED:",sent
        tagged.append(sent)
    return tagged

# This function takes the output of viterbi() and outputs it to file
def q5_output(tagged, filename):
    outfile = open(filename, 'w')
    for sentence in tagged:
        outfile.write(sentence)
    outfile.close()

# TODO: IMPLEMENT THIS FUNCTION
# This function uses nltk to create the taggers described in question 6
# brown_words and brown_tags is the data to be used in training
# brown_dev_words is the data that should be tagged
# The return value is a list of tagged sentences in the format "WORD/TAG", separated by spaces. Each sentence is a string with a 
# terminal newline, not a list of tokens. 
def nltk_tagger(brown_words, brown_tags, brown_dev_words):
    # Hint: use the following line to format data to what NLTK expects for training
    training = [ zip(brown_words[i],brown_tags[i])[2:-2] for i in xrange(0,len(brown_words)) ]

    #from nltk.corpus import brown
    #training = brown.tagged_sents()
    default_tagger = nltk.DefaultTagger('NOUN')
    bigram_tagger = nltk.BigramTagger(train=training, backoff=default_tagger)
    trigram_tagger = nltk.TrigramTagger(train=training, backoff=bigram_tagger)

    # IMPLEMENT THE REST OF THE FUNCTION HERE
    tagged = []

    for words in brown_dev_words:
        wtlist = trigram_tagger.tag(words)
        strlist = map( lambda wt:"%s/%s"%(wt[0],wt[1]), wtlist)
        tagged.append( " ".join(strlist)+"\r\n")
    return tagged

# This function takes the output of nltk_tagger() and outputs it to file
def q6_output(tagged, filename):
    outfile = open(filename, 'w')
    for sentence in tagged:
        outfile.write(sentence)
    outfile.close()

DATA_PATH = 'data/'
OUTPUT_PATH = 'output/'

def main():
    # start timer
    time.clock()

    # open Brown training data
    infile = open(DATA_PATH + "Brown_tagged_train.txt", "r")
    brown_train = infile.readlines()
    infile.close()

    # split words and tags, and add start and stop symbols (question 1)
    brown_words, brown_tags = split_wordtags(brown_train)

    # calculate tag trigram probabilities (question 2)
    q_values = calc_trigrams(brown_tags)

    # question 2 output
    q2_output(q_values, OUTPUT_PATH + 'B2.txt')

    # calculate list of words with count > 5 (question 3)
    known_words = calc_known(brown_words)

    # get a version of brown_words with rare words replace with '_RARE_' (question 3)
    brown_words_rare = replace_rare(brown_words, known_words)

    # question 3 output
    q3_output(brown_words_rare, OUTPUT_PATH + "B3.txt")

    # calculate emission probabilities (question 4)
    e_values, taglist = calc_emission(brown_words_rare, brown_tags)

    # question 4 output
    q4_output(e_values, OUTPUT_PATH + "B4.txt")

    # delete unneceessary data
    del brown_train
    del brown_words_rare

    # open Brown development data (question 5)
    infile = open(DATA_PATH + "Brown_dev.txt", "r")
    brown_dev = infile.readlines()
    infile.close()

    # format Brown development data here
    brown_dev_words = []
    for sentence in brown_dev:
        brown_dev_words.append(sentence.split(" ")[:-1])

    # do viterbi on brown_dev_words (question 5)
    viterbi_tagged = viterbi(brown_dev_words, taglist, known_words, q_values, e_values)

    # question 5 output
    q5_output(viterbi_tagged, OUTPUT_PATH + 'B5.txt')

    # do nltk tagging here
    nltk_tagged = nltk_tagger(brown_words, brown_tags, brown_dev_words)

    # question 6 output
    q6_output(nltk_tagged, OUTPUT_PATH + 'B6.txt')

    # print total time to run Part B
    print "Part B time: " + str(time.clock()) + ' sec'

if __name__ == "__main__": main()
