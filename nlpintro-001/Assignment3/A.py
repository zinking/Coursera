from main import replace_accented
from sklearn import svm
from sklearn import neighbors
import nltk
from collections import defaultdict

# don't change the window size
window_size = 10
import pdb
# A.1
def build_s(data):
    '''
    Compute the context vector for each lexelt
    :param data: dic with the following structure:
        {
			lexelt: [(instance_id, left_context, head, right_context, sense_id), ...],
			...
        }
    :return: dic s with the following structure:
        {
			lexelt: [w1,w2,w3, ...],
			...
        }

    '''
    s = {}

    # implement your code here
    for l,instances in data.items():
        words=set()
        for instance in instances:
            lwords = nltk.word_tokenize(instance[1])[-window_size:]
            rwords = nltk.word_tokenize(instance[3])[:window_size]
            for w in lwords: words.add(w)
            for w in rwords: words.add(w)
        #s[l]=list(words)
        s[l]=words
        pdb.set_trace()

    return s


# A.1
def vectorize(data, s):
    '''
    :param data: list of instances for a given lexelt with the following structure:
        {
			[(instance_id, left_context, head, right_context, sense_id), ...]
        }
    :param s: list of words (features) for a given lexelt: [w1,w2,w3, ...]
    :return: vectors: A dictionary with the following structure
            { instance_id: [w_1 count, w_2 count, ...],
            ...
            }
            labels: A dictionary with the following structure
            { instance_id : sense_id }

    '''
    vectors = {}
    labels = {}

    # implement your code here
    cwords = s
    for instance in data:
        instanceid = instance[0]
        iwords = nltk.word_tokenize(instance[1])+nltk.word_tokenize(instance[3])
        iwordcount = defaultdict(int)
        for iword in iwords:
            if iword in cwords:
                iwordcount[iword]+=1
        wc = [iwordcount[c] for c in cwords]
        pdb.set_trace()
        vectors[instanceid] = wc
        labels[instanceid] = instance[4]

    return vectors, labels


# A.2
def classify(X_train, X_test, y_train):
    '''
    Train two classifiers on (X_train, and y_train) then predict X_test labels

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

    :return: svm_results: a list of tuples (instance_id, label) where labels are predicted by LinearSVC
             knn_results: a list of tuples (instance_id, label) where labels are predicted by KNeighborsClassifier
    '''

    svm_results = []
    knn_results = []

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

    svm_clf = svm.LinearSVC()
    knn_clf = neighbors.KNeighborsClassifier()

    svm_clf.fit(X,Y)
    knn_clf.fit(X,Y)

    svm_pred = svm_clf.predict(XT)
    knn_pred = knn_clf.predict(XT)

    svm_results = zip(XTT,svm_pred)
    knn_results = zip(XTT,knn_pred)

    # implement your code here

    return svm_results, knn_results

# A.3, A.4 output
def print_results(results ,output_file):
    '''

    :param results: A dictionary with key = lexelt and value = a list of tuples (instance_id, label)
    :param output_file: file to write output

    '''

    # implement your code here
    # don't forget to remove the accent of characters using main.replace_accented(input_str)
    # you should sort results on instance_id before printing
    content = ""
    for l,results in results.items():
        ll=replace_accented(l).encode('ascii')
        sresults = sorted(results)
        for result in sresults:
            iid = replace_accented(result[0]).encode('ascii')
            #label = replace_accented(result[1]).encode('ascii')
            label = result[1]
            line = "%s %s %s\n"%(ll,iid,label)
            content+=line
    with open(output_file,'w+') as f:
        f.write(content)

# run part A
def run(train, test, language, knn_file, svm_file):
    s = build_s(train)
    svm_results = {}
    knn_results = {}
    for lexelt in s:
        X_train, y_train = vectorize(train[lexelt], s[lexelt])
        X_test, _ = vectorize(test[lexelt], s[lexelt])
        svm_results[lexelt], knn_results[lexelt] = classify(X_train, X_test, y_train)

    print_results(svm_results, svm_file)
    print_results(knn_results, knn_file)



