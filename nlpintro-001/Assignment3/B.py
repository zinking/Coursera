import A
from sklearn.feature_extraction import DictVectorizer
import nltk
from nltk.tokenize import RegexpTokenizer,WhitespaceTokenizer
from nltk.corpus import stopwords
from nltk.stem.lancaster import LancasterStemmer

from collections import defaultdict
import math
from sklearn import svm
from sklearn import neighbors
import pdb
from nltk.corpus import wordnet as wn

# You might change the window size
window_size = 15

stops = set(stopwords.words("english"))
st = LancasterStemmer()
stem_lambda = lambda w:st.stem(w)



# B.1.a,b,c,d
def extract_features(data,lexelt):
    '''
    :param data: list of instances for a given lexelt with the following structure:
        {
			[(instance_id, left_context, head, right_context, sense_id), ...]
        }
    :return: features: A dictionary with the following structure
             { instance_id: {f1:count, f2:count,...}
            ...
            }
            labels: A dictionary with the following structure
            { instance_id : sense_id }
    '''
    features = {}
    labels = {}
    # implement your code here
    lem,pos = lexelt.split(".")
    def getsynorms(lem,ppos):
        def get_definition_words(syn):
            dtext = syn.definition()
            dwords = nltk.word_tokenize(dtext)
            lwords = [stem_lambda(w) for w in dwords]
            return lwords
        syns = wn.synsets(lem,pos=ppos)
        r = []
        for syn in syns:
            r.extend(get_definition_words(syn))
            for hsyn in syn.hypernyms():
                r.extend(hsyn.lemma_names())
                r.extend(get_definition_words(hsyn))
        return r
    collw = set(getsynorms(lem,pos))
    context = {}
    for instance in data:
        olwords = nltk.word_tokenize(instance[1])
        orwords = nltk.word_tokenize(instance[3])
        #stem and remove stopwords didn't help much
        lwords = [stem_lambda(w) for w in olwords if w not in stops and w.isalpha() ]
        rwords = [stem_lambda(w) for w in orwords if w not in stops and w.isalpha() ]
        id = instance[0]
        sid = instance[4]
        tword = instance[2]
        words = lwords + [tword] + rwords
        lwwords = lwords[-window_size:]
        rwwords = rwords[:window_size]
        wwords = lwwords + [tword] + rwwords
        #wts = nltk.pos_tag(words)
        collw.update(wwords)
        context[id] = (words,wwords,olwords,orwords,tword)
    for instance in data:
        id = instance[0]
        sense = instance[4]
        words,wword,lwords,rwords,tword = context[id]
        collocounter = defaultdict(int)
        for w in words:
            if w in collw:
                collocounter[w]+=1

        llwords = lwords[-4:]
        rrwords = rwords[:4]
        ll = nltk.pos_tag(llwords)
        rr = nltk.pos_tag(rrwords)

        feature = {}
        for w in collw:
            feature[w] = collocounter[w]

        wordcontext = [
            ll[-3][0] if len(ll)>=3 else '',
            ll[-2][0] if len(ll)>=2 else '',
            ll[-1][0] if len(ll)>=1 else '',
            rr[0][0] if len(rr)>=1 else '',
            rr[1][0] if len(rr)>=2 else '',
            rr[2][0] if len(rr)>=3 else '',
        ]
        tagcontext = [
            ll[-3][1] if len(ll)>=3 else '',
            ll[-2][1] if len(ll)>=2 else '',
            ll[-1][1] if len(ll)>=1 else '',
            rr[0][1] if len(rr)>=1 else '',
            rr[1][1] if len(rr)>=2 else '',
            rr[2][1] if len(rr)>=3 else '',
        ]

        wordfeature={
            'w0':tword,
            'wn2':wordcontext[1],
            'wn1':wordcontext[2],
            'w1':wordcontext[3],
            'w2':wordcontext[4],
            'wn12':"_".join(wordcontext[1:3]),
            'wn23':"_".join(wordcontext[2:4]),
            'wn34':"_".join(wordcontext[3:5]),
            'wn012':"_".join(wordcontext[0:3]),
            'wn123':"_".join(wordcontext[1:4]),
            'wn234':"_".join(wordcontext[2:5]),
            'wn345':"_".join(wordcontext[3:6]),
            'pn3':tagcontext[0],
            'pn2':tagcontext[1],
            'pn1':tagcontext[2],
            'p0':pos,
            'p1':tagcontext[3],
            'p2':tagcontext[4],
            'p3':tagcontext[5],
        }
        feature.update(wordfeature)

        features[id]=feature
        labels[id]=sense

        #ll = [(stem_lambda(w),t) for w,t in ll if w not in stops and t.isalpha()]
        #rr = [(stem_lambda(w),t) for w,t in rr if w not in stops and t.isalpha()]

    return features, labels

# implemented for you
def vectorize(train_features,test_features):
    '''
    convert set of features to vector representation
    :param train_features: A dictionary with the following structure
             { instance_id: {f1:count, f2:count,...}
            ...
            }
    :param test_features: A dictionary with the following structure
             { instance_id: {f1:count, f2:count,...}
            ...
            }
    :return: X_train: A dictionary with the following structure
             { instance_id: [f1_count,f2_count, ...]}
            ...
            }
            X_test: A dictionary with the following structure
             { instance_id: [f1_count,f2_count, ...]}
            ...
            }
    '''
    X_train = {}
    X_test = {}

    vec = DictVectorizer()
    vec.fit(train_features.values())
    for instance_id in train_features:
        X_train[instance_id] = vec.transform(train_features[instance_id]).toarray()[0]

    for instance_id in test_features:
        X_test[instance_id] = vec.transform(test_features[instance_id]).toarray()[0]

    return X_train, X_test

#B.1.e
def feature_selection(X_train,X_test,y_train):
    '''
    Try to select best features using good feature selection methods (chi-square or PMI)
    or simply you can return train, test if you want to select all features
    :param X_train: A dictionary with the following structure
             { instance_id: [f1_count,f2_count, ...]}
            ...
            }
    :param X_test: A dictionary with the following structure
             { instance_id: [f1_count,f2_count, ...]}
            ...
            }
    :param y_train: A dictionary with the following structure
            { instance_id : sense_id }
    :return:
    '''



    # implement your code here

    #return X_train_new, X_test_new
    # or return all feature (no feature selection):
    return X_train, X_test

# B.2
def classify(X_train, X_test, y_train):
    '''
    Train the best classifier on (X_train, and y_train) then predict X_test labels

    :param X_train: A dictionary with the following structure
            { instance_id: [w_1 count, w_2 count, ...],
            ...
            }

    :param X_test: A dictionary with the following structure
            { instance_id: [w_1 count, w_2 count, ...],
            ...
            }

    :param y_train: A dictionary with the following structure
            { instance_id : sense_id }

    :return: results: a list of tuples (instance_id, label) where labels are predicted by the best classifier
    '''


    svm_clf = svm.LinearSVC()
    #knn_clf = neighbors.KNeighborsClassifier(n_neighbors=10)

    XI=X_train.items()
    XI.sort()
    X=[ i[1] for i in XI]
    YI=y_train.items()
    YI.sort()
    Y=[ i[1] for i in YI]

    XTI=X_test.items()
    XTI.sort()
    XT=[i[1] for i in XTI]
    XTT=[i[0] for i in XTI]

    svm_clf.fit(X,Y)
    #knn_clf.fit(X,Y)

    svm_pred = svm_clf.predict(XT)
    #knn_pred = knn_clf.predict(XT)

    svm_results = zip(XTT,svm_pred)
    #knn_results = zip(XTT,knn_pred)

    results = svm_results


    # implement your code here

    return results

# run part B
def run(train, test, language, answer):
    results = {}

    for lexelt in train:

        train_features, y_train = extract_features(train[lexelt],lexelt)
        test_features, _ = extract_features(test[lexelt],lexelt)

        X_train, X_test = vectorize(train_features,test_features)
        X_train_new, X_test_new = feature_selection(X_train, X_test,y_train)
        results[lexelt] = classify(X_train_new, X_test_new,y_train)

    A.print_results(results, answer)